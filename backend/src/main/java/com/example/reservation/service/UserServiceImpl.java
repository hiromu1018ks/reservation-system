package com.example.reservation.service;

import com.example.reservation.exception.DuplicateUserException;
import com.example.reservation.exception.IllegalOperationException;
import com.example.reservation.exception.ResourceNotFoundException;
import com.example.reservation.model.dto.PasswordChangeDTO;
import com.example.reservation.model.dto.ProfileUpdateDTO;
import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;
import com.example.reservation.model.entity.User;
import com.example.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * ユーザー関連のビジネスロジックを実装するサービスクラス
 * UserServiceインターフェースを実装し、ユーザー情報の取得・登録・削除などの機能を提供する
 */
@Slf4j
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
     * ファイルアップロード処理を担当するサービス
     * ユーザーのアバター画像などのアップロード処理を行う
     */
    private final FileUploadService fileUploadService;

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
                .orElseThrow(() -> new ResourceNotFoundException("ユーザが見つかりません。"));
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
                .orElseThrow(() -> new ResourceNotFoundException("ユーザが見つかりません。"));
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

    /**
     * ユーザーのプロフィール情報を更新する
     *
     * @param userId           ユーザーID
     * @param profileUpdateDTO 更新するプロフィール情報を含むDTO
     * @return 更新後のユーザー情報DTO
     * @throws ResourceNotFoundException 指定されたIDのユーザーが存在しない場合
     * @throws DuplicateUserException    更新するメールアドレスが他のユーザーで既に使用されている場合
     */
    @Override
    @Transactional
    public UserDTO updateProfile(Long userId, ProfileUpdateDTO profileUpdateDTO) {
        // 指定されたIDのユーザーを取得（存在しない場合は例外をスロー）
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

        // メールアドレスの変更がある場合、重複チェックを行う
        if (profileUpdateDTO.getEmail() != null &&
                !profileUpdateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(profileUpdateDTO.getEmail())) {
                throw new DuplicateUserException("このメールアドレスは既に使用されています");
            }
        }

        // 各フィールドが指定されている場合のみ更新
        if (profileUpdateDTO.getDisplayName() != null) {
            user.setDisplayName(profileUpdateDTO.getDisplayName());
        }
        if (profileUpdateDTO.getEmail() != null) {
            user.setEmail(profileUpdateDTO.getEmail());
        }
        if (profileUpdateDTO.getBio() != null) {
            user.setBio(profileUpdateDTO.getBio());
        }
        if (profileUpdateDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(profileUpdateDTO.getPhoneNumber());
        }

        // 更新したユーザー情報を保存し、DTOに変換して返却
        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }

    /**
     * ユーザーのアバター画像を更新する
     *
     * @param userId     ユーザーID
     * @param avatarFile アップロードされたアバター画像ファイル
     * @return 更新後のユーザー情報DTO
     * @throws ResourceNotFoundException 指定されたIDのユーザーが存在しない場合
     * @throws IOException               ファイル操作中にエラーが発生した場合
     */
    @Override
    @Transactional
    public UserDTO updateAvatar(Long userId, MultipartFile avatarFile) throws IOException {
        // 指定されたIDのユーザーを取得（存在しない場合は例外をスロー）
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

        // 既存のアバター画像がある場合は削除
        if (user.getAvatarPath() != null) {
            try {
                Files.deleteIfExists(Paths.get(user.getAvatarPath()));
            } catch (IOException e) {
                // 削除に失敗した場合はログに記録するが処理は続行
                log.warn("古いアバター画像の削除に失敗しました: {}", e.getMessage());
            }
        }

        // 新しいアバター画像をアップロード
        String avatarPath = fileUploadService.uploadAvatar(avatarFile, userId);
        user.setAvatarPath(avatarPath);

        // 更新したユーザー情報を保存し、DTOに変換して返却
        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }

    /**
     * ユーザーのパスワードを変更する
     *
     * @param userId            ユーザーID
     * @param passwordChangeDTO パスワード変更情報を含むDTO
     * @throws ResourceNotFoundException 指定されたIDのユーザーが存在しない場合
     * @throws IllegalOperationException 現在のパスワードが正しくない場合や、新パスワードと確認パスワードが一致しない場合
     */
    @Override
    @Transactional
    public void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO) {
        // 指定されたIDのユーザーを取得（存在しない場合は例外をスロー）
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

        // 新しいパスワードと確認パスワードが一致するか検証
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            throw new IllegalOperationException("新しいパスワードと確認パスワードが一致しません");
        }

        // 現在のパスワードが正しいか検証
        if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalOperationException("現在のパスワードが正しくありません");
        }

        // 新しいパスワードをハッシュ化して保存
        String hashedNewPassword = passwordEncoder.encode(passwordChangeDTO.getNewPassword());
        user.setPasswordHash(hashedNewPassword);
        userRepository.save(user);
    }
}