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
        // 作成したDTOを返却
        return dto;
    }
}