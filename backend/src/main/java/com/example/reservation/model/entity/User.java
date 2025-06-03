package com.example.reservation.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ユーザー情報を表すエンティティクラス
 * データベースの "users" テーブルにマッピングされる
 */
@Entity
@Table(name = "users")
@Data // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
public class User {
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
     * レコード作成日時
     * NULL不可、作成後は更新されない
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * レコード最終更新日時
     * NULL不可
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * エンティティ保存前に自動実行されるメソッド
     * 作成日時と更新日時を現在時刻に設定する
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * エンティティ更新前に自動実行されるメソッド
     * 更新日時を現在時刻に設定する
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * ユーザーの役割（権限）を表す列挙型
     * USER: 一般ユーザー
     * ADMIN: 管理者
     */
    public enum Role {
        USER, ADMIN
    }
}