package com.example.reservation.service;

import com.example.reservation.exception.DuplicateUserException;
import com.example.reservation.model.dto.UserRegistrationDTO;
import com.example.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ユーザー関連のバリデーションを行うサービスクラス
 * ユーザー登録や更新時のビジネスルール検証を担当
 * 
 * このサービスを分離することで、バリデーション処理の再利用性と
 * テスト容易性を向上させる
 */
@Service
@RequiredArgsConstructor
public class UserValidationService {
    
    private final UserRepository userRepository;
    
    /**
     * ユーザー登録データのバリデーションを実行
     * 
     * @param dto ユーザー登録DTO
     * @throws DuplicateUserException ユーザー名またはメールアドレスが重複している場合
     */
    public void validateUserRegistration(UserRegistrationDTO dto) {
        validateUsernameUniqueness(dto.getUsername());
        validateEmailUniqueness(dto.getEmail());
        validatePasswordStrength(dto.getPassword());
    }
    
    /**
     * ユーザー名の一意性を検証
     * 
     * @param username 検証するユーザー名
     * @throws DuplicateUserException ユーザー名が既に存在する場合
     */
    public void validateUsernameUniqueness(String username) {
        if (userRepository.existsByUsername(username)) {
            throw DuplicateUserException.forUsername(username);
        }
    }
    
    /**
     * メールアドレスの一意性を検証
     * 
     * @param email 検証するメールアドレス
     * @throws DuplicateUserException メールアドレスが既に存在する場合
     */
    public void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw DuplicateUserException.forEmail(email);
        }
    }
    
    /**
     * パスワード強度の検証
     * 
     * @param password 検証するパスワード
     * @throws IllegalArgumentException パスワードが要件を満たさない場合
     */
    public void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("パスワードは8文字以上である必要があります");
        }
        
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("パスワードには英字と数字の両方が含まれている必要があります");
        }
    }
}