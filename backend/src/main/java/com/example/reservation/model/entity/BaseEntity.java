package com.example.reservation.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * すべてのエンティティクラスの基底クラス
 * 共通的な監査フィールド（作成日時、更新日時）を提供する
 * 
 * このクラスを継承することで、すべてのエンティティに統一された
 * タイムスタンプ機能を提供し、コードの重複を防ぐ
 */
@MappedSuperclass
@Data
public abstract class BaseEntity {
    
    /**
     * レコード作成日時
     * エンティティが初めてデータベースに保存された日時を記録
     * 一度設定されると変更されない（updatable = false）
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * レコード最終更新日時
     * エンティティがデータベースで更新される度に自動的に更新される
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * エンティティ保存前に自動実行されるメソッド
     * 新規作成時に作成日時と更新日時を現在時刻に設定する
     * 
     * @PrePersist アノテーションにより、JPA がエンティティを
     * データベースに保存する直前に自動的に呼び出される
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    
    /**
     * エンティティ更新前に自動実行されるメソッド
     * 更新時に更新日時を現在時刻に設定する
     * 
     * @PreUpdate アノテーションにより、JPA がエンティティを
     * データベースで更新する直前に自動的に呼び出される
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}