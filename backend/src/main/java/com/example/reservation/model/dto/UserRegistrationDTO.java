package com.example.reservation.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ユーザー登録時に使用するDTOクラス
 * クライアントからのユーザー登録リクエストデータを受け取るために使用される
 */
@Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
public class UserRegistrationDTO {
    /**
     * ユーザー名
     * ユーザーのログインIDや表示名として使用される
     * 必須入力項目であり、3文字以上50文字以下の制約がある
     */
    @NotBlank(message = "ユーザー名は必須です")  // 空文字やnullを許可しない
    @Size(min = 3, max = 50, message = "ユーザー名は3〜50文字以下にしてください")  // 文字数の制約
    private String username;

    /**
     * メールアドレス
     * ユーザーの連絡先として使用され、システム通知の送信先になる
     * 必須入力項目であり、有効なメールアドレス形式である必要がある
     */
    @NotBlank(message = "メールアドレスは必須です")  // 空文字やnullを許可しない
    @Email(message = "有効なメールアドレスを入力してください")  // メールアドレス形式のバリデーション
    private String email;

    /**
     * パスワード
     * ユーザー認証に使用される
     * 必須入力項目であり、セキュリティのため6文字以上の制約がある
     * 注：このDTOでは平文で受け取り、実際のデータベース保存時にはハッシュ化される
     */
    @NotBlank(message = "パスワードは必須です")  // 空文字やnullを許可しない
    @Size(min = 6, message = "パスワードは6文字以上で入力してください")  // 最小文字数の制約
    private String password;
}