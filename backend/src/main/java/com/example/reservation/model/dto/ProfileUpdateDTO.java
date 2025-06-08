package com.example.reservation.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ユーザープロファイルの更新情報を保持するDTOクラス
 * クライアントからのプロファイル更新リクエストを受け取るために使用される
 */
@Data
public class ProfileUpdateDTO {

    /**
     * 表示名
     * アプリケーション上で表示されるユーザーの名前
     * 最大100文字まで設定可能
     */
    @Size(max = 100, message = "表示名は100文字以内で入力してください")
    private String displayName;

    /**
     * メールアドレス
     * ユーザーの連絡先として使用されるメールアドレス
     * 有効なメールアドレス形式である必要があり、最大100文字まで設定可能
     */
    @Email(message = "有効なメールアドレス形式で入力してください")
    @Size(max = 100, message = "メールアドレスは100文字以内で入力してください")
    private String email;

    /**
     * 自己紹介文
     * ユーザーのプロフィールページに表示される自己紹介
     * 最大500文字まで設定可能
     */
    @Size(max = 500, message = "自己紹介文は500文字以内で入力してください")
    private String bio;

    /**
     * 電話番号
     * ユーザーの連絡先として使用される電話番号
     * 最大20文字まで設定可能
     */
    @Size(max = 20, message = "電話番号は20文字以内で入力してください")
    private String phoneNumber;
}