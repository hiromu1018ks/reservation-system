package com.example.reservation.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 施設情報を表すエンティティクラス
 * データベースの "facilities" テーブルにマッピングされる
 */
@Entity
@Table(name = "facilities")
@Data // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
public class Facility {
    /**
     * 施設ID（主キー）
     * 自動採番される
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 施設名
     * NULL不可、最大長100文字
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 施設の説明
     * 任意項目
     */
    @Column
    private String description;

    /**
     * 施設の収容人数
     * 任意項目
     */
    @Column
    private Integer capacity;

    /**
     * 施設の場所・住所
     * 任意項目
     */
    @Column
    private String location;

    /**
     * 施設の画像URL
     * 任意項目
     */
    @Column(name = "image_url")
    private String imageUrl;

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
}