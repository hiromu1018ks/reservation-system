package com.example.reservation.controller;

import com.example.reservation.mapper.AuthMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証関連のエンドポイントを提供するコントローラークラス
 * ユーザー登録（サインアップ）とログイン機能を提供する
 * <p>
 * このクラスはクライアント（フロントエンド）からのHTTPリクエストを受け取り、
 * ユーザー認証に関する処理を行う入り口となる
 */
@RestController // このクラスがRESTコントローラーであることを示す（JSONレスポンスを返す）
@RequestMapping("/api/auth") // このコントローラーのベースURLパスを設定
@RequiredArgsConstructor // Lombokアノテーション：final/@NonNullフィールドを引数に持つコンストラクタを自動生成
public class AuthController {
    // Spring Securityの認証マネージャー（ユーザー認証処理を担当）
    // ユーザー名とパスワードの検証を行い、認証の成功・失敗を判定する
    private final AuthenticationManager authenticationManager;

    // ユーザー関連の業務ロジックを提供するサービス
    // ユーザーの登録、検索などの処理を担当する
    private final UserService userService;

    // JWTトークンの生成と検証を行うユーティリティ
    // JSON Web Token（JWT）の生成・管理を担当し、認証済みユーザーの識別に使用する
    private final JwtTokenUtil jwtTokenUtil;

    // 認証関連のデータ変換を行うマッパー
    // DTOオブジェクト間の変換処理を担当し、データの形式変換を行う
    private final AuthMapper authMapper;

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
        // データベース内に同じユーザー名が存在しないことを確認する
        if (userService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("エラー: このユーザー名は既に使用されています。");
        }

        // メールアドレスの重複チェック
        // 既に同じメールアドレスが登録されている場合はエラーレスポンスを返す
        // データベース内に同じメールアドレスが存在しないことを確認する
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("エラー: このメールアドレスは既に使用されています。");
        }

        // AuthMapper を使ってリクエストデータをサービス層で使用するDTOに変換する
        // コントローラーとサービス層の間のデータ形式の差異を吸収するために使用
        UserRegistrationDTO registrationDTO = authMapper.toRegistrationDTO(signupRequest);

        // ユーザーサービスを使用してユーザーを登録
        // パスワードのハッシュ化などはUserServiceで処理される
        // データベースへの保存処理もUserService内で行われる
        UserDTO userDTO = userService.register(registrationDTO);

        // 登録成功メッセージを返す
        // HTTP 200 OKステータスとともにメッセージボディを返す
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
            // UsernamePasswordAuthenticationTokenは認証情報を保持するオブジェクト
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), // 認証に使用するユーザー名
                            loginRequest.getPassword()  // 認証に使用するパスワード
                    )
            );

            // 認証成功時、SecurityContextに認証情報を設定
            // これにより、同一リクエスト内で認証状態を維持できる
            // Spring Securityのセキュリティコンテキストに認証情報を格納
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 認証されたユーザー情報を取得
            // AuthenticationオブジェクトからUserDetailsを取得
            // UserDetailsはSpring Securityが認証に使用するユーザー情報インターフェース
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // ユーザー名を使ってユーザー情報を取得
            // 追加のユーザー情報（ID、ロールなど）を取得するため
            // データベースからより詳細なユーザー情報を取得
            UserDTO userDTO = userService.findByUsername(userDetails.getUsername());

            // JWTトークンを生成
            // ユーザー情報とIDを元にJWTトークンを作成
            // このトークンはクライアント側で保存され、以降のリクエストの認証に使用される
            String jwt = jwtTokenUtil.generateToken(userDetails, userDTO.getId());

            // 認証トークンとユーザー情報を含むレスポンスオブジェクトを作成
            // AuthMapperを使用してレスポンスデータを適切な形式に変換
            AuthDTO.TokenResponse tokenResponse = authMapper.toTokenResponse(jwt, userDTO);

            // 認証成功レスポンスを返す
            // HTTP 200 OKステータスとともに、トークンとユーザー情報を含むJSONを返す
            return ResponseEntity.ok(tokenResponse);

        } catch (BadCredentialsException e) {
            // 認証失敗時（ユーザー名またはパスワードが不正）のエラーハンドリング
            // パスワードが間違っている場合や、ユーザーが存在しない場合に発生する例外を捕捉
            // HTTP 400 Bad Requestステータスとともにエラーメッセージを返す
            return ResponseEntity.badRequest().body("エラー: ユーザー名またはパスワードが正しくありません。");
        }
    }
}