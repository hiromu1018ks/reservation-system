package com.example.reservation.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 予約情報を表すエンティティクラス
 * データベースの "reservations" テーブルにマッピングされる
 */
@Entity
@Table(name = "reservations")
@Data // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
public class Reservation {
    /**
     * 予約ID（主キー）
     * 自動採番される
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 予約対象の施設
     * 多対一の関係（多くの予約が一つの施設に対して行われる）
     * LAZY：施設情報は必要になるまで読み込まれない（パフォーマンス向上）
     * facility_idカラムを外部キーとして使用
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    /**
     * 予約したユーザー
     * 多対一の関係（多くの予約が一人のユーザーによって行われる）
     * LAZY：ユーザー情報は必要になるまで読み込まれない（パフォーマンス向上）
     * user_idカラムを外部キーとして使用
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 予約開始日時
     * NULL不可
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 予約終了日時
     * NULL不可
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * 予約目的・理由
     * 任意項目、最大500文字
     */
    @Column(length = 500)
    private String purpose;

    /**
     * 予約状態
     * PENDING：承認待ち、APPROVED：承認済み、REJECTED：拒否、CANCELLED：キャンセル
     * デフォルトはPENDING（承認待ち）
     * 文字列としてデータベースに保存
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

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
     * 予約状態を表す列挙型
     * PENDING: 承認待ち
     * APPROVED: 承認済み
     * REJECTED: 拒否
     * CANCELLED: キャンセル
     */
    public enum ReservationStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }
}