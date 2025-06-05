package com.example.reservation.service;

import com.example.reservation.model.dto.ReservationCreateDTO;
import com.example.reservation.model.dto.ReservationDTO;
import com.example.reservation.model.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 予約サービスインターフェース
 * 予約に関する各種ビジネスロジックを定義する
 */
public interface ReservationService {
    /**
     * IDによる予約情報取得
     * 指定されたIDの予約情報を取得する
     *
     * @param id 取得する予約のID
     * @return 予約情報DTO
     */
    ReservationDTO findById(Long id);

    /**
     * すべての予約情報取得
     * システム内のすべての予約情報を取得する
     *
     * @return 予約情報DTOのリスト
     */
    List<ReservationDTO> findAll();

    /**
     * 施設IDによる予約情報取得
     * 指定された施設IDに関連する予約情報をすべて取得する
     *
     * @param facilityId 施設ID
     * @return 指定された施設の予約情報DTOのリスト
     */
    List<ReservationDTO> findByFacilityId(Long facilityId);

    /**
     * ユーザーIDによる予約情報取得
     * 指定されたユーザーIDに関連する予約情報をすべて取得する
     *
     * @param userId ユーザーID
     * @return 指定されたユーザーの予約情報DTOのリスト
     */
    List<ReservationDTO> findByUserId(Long userId);

    /**
     * 予約状態による予約情報取得
     * 指定された予約状態（承認待ち、承認済み、拒否、キャンセルなど）に一致する予約情報を取得する
     *
     * @param status 予約状態
     * @return 指定された状態の予約情報DTOのリスト
     */
    List<ReservationDTO> findByStatus(Reservation.ReservationStatus status);

    /**
     * 予約情報の新規作成
     * 指定された予約作成DTOとユーザーIDを使用して新しい予約を作成する
     *
     * @param createDTO 予約作成情報DTO
     * @param userId    予約を作成するユーザーのID
     * @return 作成された予約情報DTO
     */
    ReservationDTO create(ReservationCreateDTO createDTO, Long userId);

    /**
     * 予約状態の更新
     * 指定された予約IDの予約状態を更新する
     *
     * @param id     更新する予約のID
     * @param status 新しい予約状態
     * @return 更新された予約情報DTO
     */
    ReservationDTO updateStatus(Long id, Reservation.ReservationStatus status);

    /**
     * 予約時間枠の利用可能性確認
     * 指定された施設、時間枠が予約可能かどうかを確認する
     * 既存の予約をチェックするときに特定の予約を除外することができる
     *
     * @param facilityId           施設ID
     * @param startTime            開始時間
     * @param endTime              終了時間
     * @param excludeReservationId 確認時に除外する予約ID（予約更新時に自分自身を除外するため）
     * @return 時間枠が利用可能な場合はtrue、そうでない場合はfalse
     */
    boolean isTimeSlotAvailable(Long facilityId, LocalDateTime startTime, LocalDateTime endTime, Long excludeReservationId);

    /**
     * 予約の削除
     * 指定されたIDの予約を削除する
     *
     * @param id 削除する予約のID
     */
    void delete(Long id);
}