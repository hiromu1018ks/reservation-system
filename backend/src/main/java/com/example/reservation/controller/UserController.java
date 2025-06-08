package com.example.reservation.controller;

import com.example.reservation.exception.IllegalOperationException;
import com.example.reservation.model.dto.PasswordChangeDTO;
import com.example.reservation.model.dto.ProfileUpdateDTO;
import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;
import com.example.reservation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * ユーザー関連のHTTPリクエストを処理するコントローラークラス
 * REST APIエンドポイントを提供し、ユーザー情報の取得、登録、削除などの操作を行う
 */
@RestController  // このクラスがRESTコントローラーであることを示す（JSONレスポンスを自動的に生成）
@RequestMapping("/api/users")  // このコントローラーが処理するベースURLパス
@RequiredArgsConstructor  // Lombokアノテーション：finalフィールドを引数に持つコンストラクタを自動生成
public class UserController {
    /**
     * ユーザー関連の業務ロジックを提供するサービスクラス
     * コンストラクタインジェクションによって自動的に注入される
     */
    private final UserService userService;

    /**
     * 全ユーザー情報を取得するエンドポイント
     * HTTPメソッド: GET
     * URL: /api/users
     *
     * @return システムに登録されている全ユーザーのリスト（DTOオブジェクト形式）
     */
    @GetMapping
    public List<UserDTO> getAllUser() {
        return userService.findAll();
    }

    /**
     * 指定されたIDのユーザー情報を取得するエンドポイント
     * HTTPメソッド: GET
     * URL: /api/users/{id}
     *
     * @param id 取得対象のユーザーID（URLパスから抽出）
     * @return 該当するユーザーの情報（DTOオブジェクト形式）
     */
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    /**
     * 指定されたユーザー名のユーザー情報を取得するエンドポイント
     * HTTPメソッド: GET
     * URL: /api/users/username/{username}
     *
     * @param username 取得対象のユーザー名（URLパスから抽出）
     * @return 該当するユーザーの情報（DTOオブジェクト形式）
     */
    @GetMapping("/username/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    /**
     * 新規ユーザーを登録するエンドポイント
     * HTTPメソッド: POST
     * URL: /api/users/register
     *
     * @param registrationDTO ユーザー登録情報（リクエストボディから抽出）
     * @return 登録されたユーザー情報とHTTPステータスコード201（作成成功）を含むレスポンス
     * &#064;Validアノテーションによりバリデーションが実行される
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO createdUser = userService.register(registrationDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);  // HTTP 201 Created ステータスを返す
    }

    /**
     * 指定されたIDのユーザーを削除するエンドポイント
     * HTTPメソッド: DELETE
     * URL: /api/users/{id}
     *
     * @param id 削除対象のユーザーID（URLパスから抽出）
     * @return HTTPステータスコード204（内容なし）を含むレスポンス
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();  // HTTP 204 No Content ステータスを返す
    }

    /**
     * 認証済みユーザーの現在の情報を取得するエンドポイント
     * HTTPメソッド: GET
     * URL: /api/users/me
     *
     * @param authentication 現在ログイン中のユーザー認証情報（Spring Securityにより自動的に提供）
     * @return 現在のユーザー情報（DTOオブジェクト形式）
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserDTO currentUser = userService.findById(userId);
        return ResponseEntity.ok(currentUser);
    }

    /**
     * 認証済みユーザーのプロフィール情報を更新するエンドポイント
     * HTTPメソッド: PUT
     * URL: /api/users/profile
     *
     * @param authentication   現在ログイン中のユーザー認証情報（Spring Securityにより自動的に提供）
     * @param profileUpdateDTO 更新するプロフィール情報（リクエストボディから抽出）
     * @return 更新されたユーザー情報（DTOオブジェクト形式）
     */
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateDTO profileUpdateDTO) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserDTO updatedUser = userService.updateProfile(userId, profileUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 認証済みユーザーのアバター画像をアップロードするエンドポイント
     * HTTPメソッド: POST
     * URL: /api/users/avatar
     *
     * @param authentication 現在ログイン中のユーザー認証情報（Spring Securityにより自動的に提供）
     * @param avatarFile     アップロードされたアバター画像ファイル（マルチパートリクエストから抽出）
     * @return 更新されたユーザー情報（DTOオブジェクト形式）
     * @throws IllegalOperationException ファイルアップロード処理に失敗した場合に発生
     */
    @PostMapping("/avatar")
    public ResponseEntity<UserDTO> uploadAvatar(
            Authentication authentication,
            @RequestParam("avatar") MultipartFile avatarFile) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            UserDTO updatedUser = userService.updateAvatar(userId, avatarFile);
            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) {
            throw new IllegalOperationException("ファイルアップロードに失敗しました");
        }
    }

    /**
     * 認証済みユーザーのパスワードを変更するエンドポイント
     * HTTPメソッド: PUT
     * URL: /api/users/password
     *
     * @param authentication    現在ログイン中のユーザー認証情報（Spring Securityにより自動的に提供）
     * @param passwordChangeDTO 現在のパスワードと新しいパスワード情報（リクエストボディから抽出）
     * @return 処理成功時にHTTPステータスコード200（OK）を含むレスポンス
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        Long userId = getUserIdFromAuthentication(authentication);
        userService.changePassword(userId, passwordChangeDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * 認証情報からユーザーIDを取得するプライベートヘルパーメソッド
     *
     * @param authentication Spring Securityが提供する認証情報
     * @return 認証されたユーザーのID
     * <p>
     * 処理手順:
     * 1. 認証情報からユーザー名を取得
     * 2. ユーザー名を使ってユーザーサービスからユーザー情報を検索
     * 3. 取得したユーザー情報からIDを返却
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        UserDTO user = userService.findByUsername(username);
        return user.getId();
    }
}