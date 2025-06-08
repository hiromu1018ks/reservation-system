package com.example.reservation.model.dto;

import com.example.reservation.model.entity.User;
import lombok.Data;

/**
 * ユーザー情報をクライアント側やビジネスロジックで扱いやすい形に変換するためのDTOクラス
 * エンティティとは異なり、必要な情報のみを保持する（パスワードなどの機密情報は含まない）
 */
@Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
public class UserDTO {
    /**
     * ユーザーID
     * データベース上の一意識別子
     */
    private Long id;

    /**
     * ユーザー名
     * ユーザーのログイン時やシステム内での表示に使用される名前
     */
    private String username;

    /**
     * メールアドレス
     * ユーザーの連絡先として使用されるメールアドレス
     */
    private String email;

    /**
     * ユーザーの役割（権限）
     * USER（一般ユーザー）またはADMIN（管理者）の値を持つ
     */
    private User.Role role;

    /**
     * 表示名
     * ユーザーがアプリケーション内で表示される名前
     */
    private String displayName;

    /**
     * 自己紹介文
     * ユーザーのプロフィールページに表示される自己紹介
     */
    private String bio;

    /**
     * アバター画像のパス
     * ユーザーのプロフィール画像ファイルへのパス情報
     */
    private String avatarPath;

    /**
     * 電話番号
     * ユーザーの連絡先として使用される電話番号
     */
    private String phoneNumber;

    /**
     * 作成日時
     * ユーザーアカウントが作成された日時
     */
    private String createdAt;

    /**
     * 更新日時
     * ユーザー情報が最後に更新された日時
     */
    private String updatedAt;

    /**
     * ユーザーエンティティからDTOオブジェクトを生成する静的ファクトリメソッド
     *
     * @param user 変換元となるユーザーエンティティ
     * @return 生成されたUserDTOオブジェクト
     */
    public static UserDTO fromEntity(User user) {
        // 新しいDTOオブジェクトを生成
        UserDTO dto = new UserDTO();
        // ユーザーエンティティの各フィールド値をDTOにコピー
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDisplayName(user.getDisplayName());
        dto.setBio(user.getBio());
        dto.setAvatarPath(user.getAvatarPath());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        dto.setUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        // 作成したDTOを返却
        return dto;
    }
}