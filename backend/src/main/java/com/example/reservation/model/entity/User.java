package com.example.reservation.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ユーザー情報を表すエンティティクラス
 * データベースの "users" テーブルにマッピングされる
 */
@Entity
@Table(name = "users")
@Data // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
@EqualsAndHashCode(callSuper = false) // スーパークラスのequals/hashCodeを呼び出さない
public class User extends BaseEntity {
    /**
     * ユーザーID（主キー）
     * 自動採番される
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ユーザー名
     * NULL不可、一意制約あり、最大長50文字
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * メールアドレス
     * NULL不可、一意制約あり、最大長100文字
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * パスワードハッシュ
     * セキュリティのため平文ではなくハッシュ化して保存
     * NULL不可
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * ユーザーの役割（権限）
     * USER：一般ユーザー、ADMIN：管理者
     * 文字列としてデータベースに保存される
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;


    /**
     * ユーザーの役割（権限）を表す列挙型
     * USER: 一般ユーザー
     * ADMIN: 管理者
     */
    public enum Role {
        USER, ADMIN
    }

    /**
     * 表示名
     * ユーザーがアプリケーション内で表示される名前
     * 実名やニックネームなど、ユーザー自身が設定できる
     * 最大100文字まで設定可能
     */
    @Column(length = 100)
    private String displayName;

    /**
     * プロフィール文
     * ユーザー自身の紹介文や経歴などを記述するフィールド
     * ユーザープロフィールページなどで表示される
     * 最大500文字まで設定可能
     */
    @Column(length = 500)
    private String bio;

    /**
     * アバター画像のパス
     * ユーザーのプロフィール画像ファイルへのパス情報を保持
     * サーバー上のファイルパスまたはURL形式で保存
     * 最大255文字まで設定可能
     */
    @Column(length = 255)
    private String avatarPath;

    /**
     * 電話番号
     * ユーザーの連絡先として使用される電話番号
     * 国際番号形式やハイフンを含めて最大20文字まで設定可能
     * アカウント確認や二要素認証などに使用される場合もある
     */
    @Column(length = 20)
    private String phoneNumber;
}