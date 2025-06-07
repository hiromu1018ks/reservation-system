package com.example.reservation.controller;

import com.example.reservation.model.dto.AuthDTO;
import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;
import com.example.reservation.security.JwtTokenUtil;
import com.example.reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証関連のエンドポイントを提供するコントローラークラス
 * ユーザー登録（サインアップ）とログイン機能を提供する
 */
@RestController // このクラスがRESTコントローラーであることを示す（JSONレスポンスを返す）
@RequestMapping("/api/auth") // このコントローラーのベースURLパスを設定
@RequiredArgsConstructor // Lombokアノテーション：final/@NonNullフィールドを引数に持つコンストラクタを自動生成
public class AuthController {
    // Spring Securityの認証マネージャー（ユーザー認証処理を担当）
    private final AuthenticationManager authenticationManager;

    // ユーザー関連の業務ロジックを提供するサービス
    private final UserService userService;

    // パスワードをハッシュ化するためのエンコーダー
    private final PasswordEncoder passwordEncoder;

    // JWTトークンの生成と検証を行うユーティリティ
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * ユーザー登録（サインアップ）エンドポイント
     * 新規ユーザーを登録し、成功時にはユーザー情報を返す
     *
     * @param signupRequest ユーザー登録情報（ユーザー名、メールアドレス、パスワード）
     * @return 登録結果のレスポンス
     */
    @PostMapping("/signup") // HTTP POSTメソッドと/api/auth/signupパスにマッピング
    public ResponseEntity<?> registerUser(@RequestBody AuthDTO.SignupRequest signupRequest) {
        // ユーザー名の重複チェック
        // 既に同じユーザー名が登録されている場合はエラーレスポンスを返す
        if (userService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("エラー: このユーザー名は既に使用されています。");
        }

        // メールアドレスの重複チェック
        // 既に同じメールアドレスが登録されている場合はエラーレスポンスを返す
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("エラー: このメールアドレスは既に使用されています。");
        }

        // クライアントから受け取ったSignupRequestをUserRegistrationDTOに変換
        // UserServiceはUserRegistrationDTOを期待するため、この変換が必要
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername(signupRequest.getUsername());
        registrationDTO.setEmail(signupRequest.getEmail());
        registrationDTO.setPassword(signupRequest.getPassword());

        // ユーザーサービスを使用してユーザーを登録
        // パスワードのハッシュ化などはUserServiceで処理される
        UserDTO userDTO = userService.register(registrationDTO);

        // 登録成功メッセージを返す
        return ResponseEntity.ok("ユーザー登録が完了しました。");
    }

    /**
     * ログインエンドポイント
     * ユーザー認証を行い、成功時にはJWTトークンを返す
     *
     * @param loginRequest ログイン情報（ユーザー名、パスワード）
     * @return 認証結果のレスポンス（JWTトークンを含む）
     */
    @PostMapping("/login") // HTTP POSTメソッドと/api/auth/loginパスにマッピング
    public ResponseEntity<?> authenticateUser(@RequestBody AuthDTO.LoginRequest loginRequest) {
        try {
            // 認証を試みる
            // AuthenticationManagerを使用してユーザー名とパスワードの検証を行う
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), // 認証に使用するユーザー名
                            loginRequest.getPassword()  // 認証に使用するパスワード
                    )
            );

            // 認証成功時、SecurityContextに認証情報を設定
            // これにより、同一リクエスト内で認証状態を維持できる
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 認証されたユーザー情報を取得
            // AuthenticationオブジェクトからUserDetailsを取得
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // ユーザー名を使ってユーザー情報を取得
            // 追加のユーザー情報（ID、ロールなど）を取得するため
            UserDTO userDTO = userService.findByUsername(userDetails.getUsername());

            // JWTトークンを生成
            // ユーザー情報とIDを元にJWTトークンを作成
            String jwt = jwtTokenUtil.generateToken(userDetails, userDTO.getId());

            // クライアントに返すレスポンスを作成
            // トークンとユーザー情報を含む
            AuthDTO.TokenResponse tokenResponse = new AuthDTO.TokenResponse(
                    jwt,                    // 生成されたJWTトークン
                    userDTO.getId(),        // ユーザーID
                    userDTO.getUsername(),  // ユーザー名
                    userDTO.getEmail(),     // メールアドレス
                    userDTO.getRole().name() // ユーザーの役割（文字列に変換）
            );

            // 認証成功レスポンスを返す
            return ResponseEntity.ok(tokenResponse);

        } catch (BadCredentialsException e) {
            // 認証失敗時（ユーザー名またはパスワードが不正）のエラーハンドリング
            return ResponseEntity.badRequest().body("エラー: ユーザー名またはパスワードが正しくありません。");
        }
    }
}