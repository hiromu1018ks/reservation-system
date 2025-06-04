package com.example.reservation.model.dto;

import com.example.reservation.model.entity.Reservation;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 予約情報のデータ転送オブジェクト（DTO）
 * エンティティとプレゼンテーション層の間でデータをやり取りするために使用される
 * Lombokの@Dataアノテーションにより、getter、setter、equals、hashCode、toStringメソッドが自動生成される
 */
@Data
public class ReservationDTO {
    /**
     * 予約ID
     */
    private Long id;

    /**
     * 施設ID
     */
    private Long facilityId;

    /**
     * 施設名
     */
    private String facilityName;

    /**
     * ユーザーID
     */
    private Long userId;

    /**
     * ユーザー名
     */
    private String username;

    /**
     * 予約開始日時
     */
    private LocalDateTime startTime;

    /**
     * 予約終了日時
     */
    private LocalDateTime endTime;

    /**
     * 予約目的・理由
     */
    private String purpose;

    /**
     * 予約状態（PENDING: 承認待ち、APPROVED: 承認済み、REJECTED: 拒否、CANCELLED: キャンセル）
     */
    private Reservation.ReservationStatus status;

    /**
     * Reservationエンティティからデータ転送オブジェクトを生成するファクトリメソッド
     *
     * @param reservation 変換元のReservationエンティティ
     * @return 変換されたReservationDTO
     */
    public static ReservationDTO fromEntity(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        // 予約IDを設定
        dto.setId(reservation.getId());
        // 施設IDを設定（予約に関連付けられた施設から取得）
        dto.setFacilityId(reservation.getFacility().getId());
        // 施設名を設定（予約に関連付けられた施設から取得）
        dto.setFacilityName(reservation.getFacility().getName());
        // ユーザーIDを設定（予約に関連付けられたユーザーから取得）
        dto.setUserId(reservation.getUser().getId());
        // ユーザー名を設定（予約に関連付けられたユーザーから取得）
        dto.setUsername(reservation.getUser().getUsername());
        // 予約開始日時を設定
        dto.setStartTime(reservation.getStartTime());
        // 予約終了日時を設定
        dto.setEndTime(reservation.getEndTime());
        // 予約目的を設定
        dto.setPurpose(reservation.getPurpose());
        // 予約状態を設定
        dto.setStatus(reservation.getStatus());

        return dto;
    }
}