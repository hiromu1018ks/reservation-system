package com.example.reservation.repository;

import com.example.reservation.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 予約情報に関するデータアクセスを提供するリポジトリインターフェース
 * Spring Data JPAを使用してCRUD操作を実装
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 施設IDに基づいて予約を検索するメソッド
     * 指定された施設に関連するすべての予約を返す
     *
     * @param facilityId 検索対象の施設ID
     * @return 指定された施設に関連する予約のリスト
     */
    List<Reservation> findByFacilityId(Long facilityId);

    /**
     * ユーザーIDに基づいて予約を検索するメソッド
     * 指定されたユーザーが行ったすべての予約を返す
     *
     * @param userId 検索対象のユーザーID
     * @return 指定されたユーザーに関連する予約のリスト
     */
    List<Reservation> findByUserId(Long userId);

    /**
     * 指定された時間範囲と施設IDに基づいて重複する予約を検索するメソッド
     * 承認済み（APPROVED）の予約のみを対象とする
     * 予約時間が重複するかどうかは、新しい予約の開始時間が既存の予約の終了時間以前かつ
     * 新しい予約の終了時間が既存の予約の開始時間以降かどうかで判断
     *
     * @param facilityId 検索対象の施設ID
     * @param startTime  予約開始時間
     * @param endTime    予約終了時間
     * @return 重複する予約のリスト
     */
    @Query("SELECT r FROM Reservation r WHERE r.facility.id = :facilityId " +
            "AND ((r.startTime <= :endTime AND r.endTime >= :startTime) " +
            "AND r.status = 'APPROVED')")
    List<Reservation> findOverlappingReservations(
            @Param("facilityId") Long facilityId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 指定されたステータスと開始時間以降の予約を検索するメソッド
     * 特定のステータス（例：承認待ち）で、指定された時間以降に開始する予約を取得するのに有用
     *
     * @param status    検索対象の予約ステータス
     * @param startTime この時間以降に開始する予約を検索
     * @return 条件に一致する予約のリスト
     */
    List<Reservation> findByStatusAndStartTimeAfter(
            Reservation.ReservationStatus status,
            LocalDateTime startTime);
}