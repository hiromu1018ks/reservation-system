package com.example.reservation.service;

import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;
import com.example.reservation.model.entity.User;
import com.example.reservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserServiceImplのテストクラス
 * Mockitoを使用してUserRepositoryとPasswordEncoderをモック化し、
 * UserServiceImplの各メソッドの動作を検証します
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDTO registrationDTO;
    private User savedUser;

    @BeforeEach
    void setUp() {
        // テストデータの準備
        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setPassword("password123");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setPasswordHash("hashedPassword");
        savedUser.setRole(User.Role.USER);
    }

    /**
     * ユーザー登録機能のテスト
     * 正常にユーザーが登録できることを検証します
     */
    @Test
    void registerUser_Success() {
        // モックの設定
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // テスト対象メソッドの実行
        UserDTO result = userService.register(registrationDTO);

        // 検証
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(User.Role.USER, result.getRole());

        // メソッドの呼び出し回数を検証
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    /**
     * ユーザー名重複時のエラー処理のテスト
     * 既に存在するユーザー名で登録を試みた場合、例外がスローされることを検証します
     */
    @Test
    void registerUser_DuplicateUsername() {
        // モックの設定 - ユーザー名が既に存在する場合
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // テスト対象メソッドの実行と例外の検証
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(registrationDTO)
        );

        // 例外メッセージの検証
        assertEquals("このユーザー名は既に使用されています", exception.getMessage());

        // save()メソッドが呼ばれていないことを検証
        verify(userRepository, never()).save(any(User.class));
    }
}