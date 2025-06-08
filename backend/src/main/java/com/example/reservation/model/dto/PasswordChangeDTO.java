package com.example.reservation.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * パスワード変更処理で使用するデータ転送オブジェクト（DTO）
 * ユーザーが自身のパスワードを変更する際に必要な情報を保持するクラス
 */
@Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
public class PasswordChangeDTO {

    /**
     * 現在のパスワード
     * ユーザー認証のために必要な現在使用中のパスワード
     * 空白は許可されない（@NotBlank）
     */
    @NotBlank(message = "現在のパスワードは必須です")
    private String currentPassword;

    /**
     * 新しいパスワード
     * ユーザーが設定したい新しいパスワード
     * 空白は許可されず（@NotBlank）、8文字以上100文字以内の長さ制限がある（@Size）
     */
    @NotBlank(message = "新しいパスワードは必須です")
    @Size(min = 8, max = 100, message = "パスワードは8文字以上100文字以内で入力してください")
    private String newPassword;

    /**
     * 新しいパスワード（確認用）
     * 新しいパスワードの入力ミスを防ぐための確認用フィールド
     * 空白は許可されない（@NotBlank）
     * このフィールドとnewPasswordフィールドの値が一致するかはコントローラーやサービスで検証される
     */
    @NotBlank(message = "新しいパスワード（確認）は必須です")
    private String confirmPassword;
}