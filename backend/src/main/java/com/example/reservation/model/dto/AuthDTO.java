package com.example.reservation.model.dto;

import lombok.Data;

/**
 * 認証関連のデータ転送オブジェクト（DTO）を格納するクラス
 * サインアップ、ログイン、トークン応答などの認証処理に必要なデータ構造を定義する
 */
public class AuthDTO {
    /**
     * ユーザー登録（サインアップ）リクエスト用のDTOクラス
     * クライアントからのユーザー登録情報を受け取るために使用される
     */
    @Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
    public static class SignupRequest {
        /**
         * ユーザー名
         * システムへのログインや識別に使用される名前
         */
        private String username;

        /**
         * パスワード
         * ユーザー認証に使用される秘密情報
         */
        private String password;

        /**
         * メールアドレス
         * ユーザーの連絡先として使用され、アカウント確認などにも利用される
         */
        private String email;
    }

    /**
     * ログインリクエスト用のDTOクラス
     * クライアントからのログイン情報を受け取るために使用される
     */
    @Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
    public static class LoginRequest {
        /**
         * ユーザー名
         * ログイン時の識別子として使用される
         */
        private String username;

        /**
         * パスワード
         * ユーザー認証に使用される秘密情報
         */
        private String password;
    }

    /**
     * 認証トークン応答用のDTOクラス
     * 認証成功時にクライアントに返されるJWTトークンと関連ユーザー情報を含む
     */
    @Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
    public static class TokenResponse {
        /**
         * JWT認証トークン
         * クライアントが以降のリクエストで認証に使用する
         */
        private String token;

        /**
         * トークンタイプ
         * デフォルトで"Bearer"に設定される
         */
        private String type = "Bearer";

        /**
         * ユーザーID
         * データベース上のユーザーの一意識別子
         */
        private Long Id;

        /**
         * ユーザー名
         * 認証されたユーザーの名前
         */
        private String username;

        /**
         * メールアドレス
         * 認証されたユーザーのメールアドレス
         */
        private String email;

        /**
         * ユーザーの役割（権限）
         * ユーザーの権限レベルを示す（例：USER、ADMIN）
         */
        private String role;

        /**
         * JWTレスポンスを初期化するコンストラクタ
         * 認証トークンと関連するユーザー情報を設定する
         *
         * @param token    生成されたJWTトークン
         * @param id       ユーザーID
         * @param username ユーザー名
         * @param email    ユーザーのメールアドレス
         * @param role     ユーザーの役割（権限）
         */
        public TokenResponse(String token, Long id, String username, String email, String role) {
            this.token = token;
            this.Id = id;
            this.username = username;
            this.email = email;
            this.role = role;
        }

        /**
         * デフォルトコンストラクタ
         * Lombokの@Dataアノテーションと併用するために必要
         */
        public TokenResponse() {
        }
    }
}
