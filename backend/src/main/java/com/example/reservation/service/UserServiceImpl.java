package com.example.reservation.service;

import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;
import com.example.reservation.model.entity.User;
import com.example.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ユーザー関連のビジネスロジックを実装するサービスクラス
 * UserServiceインターフェースを実装し、ユーザー情報の取得・登録・削除などの機能を提供する
 */
@Service  // Springのサービスコンポーネントとして登録
@RequiredArgsConstructor  // Lombokによる必須フィールドを引数とするコンストラクタを自動生成
public class UserServiceImpl implements UserService {
    /**
     * ユーザー情報のデータアクセスを担当するリポジトリ
     * コンストラクタインジェクションにより注入される
     */
    private final UserRepository userRepository;

    /**
     * パスワードのハッシュ化を担当するエンコーダー
     * セキュリティのため、パスワードは平文ではなくハッシュ化して保存する
     */
    private final PasswordEncoder passwordEncoder;
    
    /**
     * ユーザー関連のバリデーションを担当するサービス
     * ビジネスルールの検証処理を分離して管理
     */
    private final UserValidationService userValidationService;

    /**
     * 指定されたIDのユーザー情報をDTOとして取得する
     *
     * @param id 取得するユーザーのID
     * @return ユーザー情報DTO
     * @throws IllegalArgumentException 指定されたIDのユーザーが存在しない場合
     */
    @Override
    public UserDTO findById(Long id) {
        // リポジトリからユーザーを検索し、存在しない場合は例外をスロー
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ユーザが見つかりません。"));
        // エンティティをDTOに変換して返却
        return UserDTO.fromEntity(user);
    }

    /**
     * 指定されたユーザー名のユーザー情報をDTOとして取得する
     *
     * @param username 取得するユーザーのユーザー名
     * @return ユーザー情報DTO
     * @throws IllegalArgumentException 指定されたユーザー名のユーザーが存在しない場合
     */
    @Override
    public UserDTO findByUsername(String username) {
        // リポジトリからユーザー名でユーザーを検索し、存在しない場合は例外をスロー
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("ユーザが見つかりません。"));
        // エンティティをDTOに変換して返却
        return UserDTO.fromEntity(user);
    }

    /**
     * 全てのユーザー情報をDTOのリストとして取得する
     *
     * @return ユーザー情報DTOのリスト
     */
    @Override
    public List<UserDTO> findAll() {
        // 全ユーザーを取得し、各エンティティをDTOに変換してリストとして返却
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    /**
     * 新規ユーザーを登録する
     *
     * @param registrationDTO ユーザー登録情報を含むDTO
     * @return 登録されたユーザー情報DTO
     * @throws IllegalArgumentException ユーザー名またはメールアドレスが既に使用されている場合
     */
    @Override
    @Transactional  // トランザクション管理を有効化
    public UserDTO register(UserRegistrationDTO registrationDTO) {
        // バリデーションサービスを使用してビジネスルールを検証
        userValidationService.validateUserRegistration(registrationDTO);

        // 新規ユーザーエンティティの作成
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        // パスワードはハッシュ化して保存
        user.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));
        // デフォルト権限は一般ユーザー
        user.setRole(User.Role.USER);

        // ユーザーをデータベースに保存
        User savedUser = userRepository.save(user);
        // 保存されたエンティティをDTOに変換して返却
        return UserDTO.fromEntity(savedUser);
    }

    /**
     * 指定されたIDのユーザーを削除する
     *
     * @param id 削除するユーザーのID
     * @throws IllegalArgumentException 指定されたIDのユーザーが存在しない場合
     */
    @Override
    public void deleteById(Long id) {
        // 削除対象ユーザーの存在確認
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("ユーザーが見つかりませんでした: " + id);
        }
        // ユーザーを削除
        userRepository.deleteById(id);
    }

    /**
     * 指定されたユーザー名が既に存在するかどうかを確認する
     *
     * @param username 確認するユーザー名
     * @return ユーザー名が既に存在する場合はtrue、存在しない場合はfalse
     */
    @Override
    public boolean existsByUsername(String username) {
        // ユーザーリポジトリを使用して、指定されたユーザー名が既にデータベースに存在するかを確認
        // 存在する場合はtrueを返し、存在しない場合はfalseを返す
        return userRepository.existsByUsername(username);
    }

    /**
     * 指定されたメールアドレスが既に存在するかどうかを確認する
     *
     * @param email 確認するメールアドレス
     * @return メールアドレスが既に存在する場合はtrue、存在しない場合はfalse
     */
    @Override
    public boolean existsByEmail(String email) {
        // ユーザーリポジトリを使用して、指定されたメールアドレスが既にデータベースに存在するかを確認
        // 存在する場合はtrueを返し、存在しない場合はfalseを返す
        return userRepository.existsByEmail(email);
    }
}