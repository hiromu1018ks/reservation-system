package com.example.reservation.controller;

import com.example.reservation.model.dto.ReservationCreateDTO;
import com.example.reservation.model.dto.ReservationDTO;
import com.example.reservation.model.entity.Reservation;
import com.example.reservation.security.CustomUserDetailsService;
import com.example.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 予約管理のためのRESTコントローラー
 * 予約の作成、取得、更新、削除などの操作を提供する
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    /**
     * 予約サービス
     * コンストラクタインジェクションによって注入される
     */
    private final ReservationService reservationService;

    /**
     * すべての予約を取得する
     * HTTP GETリクエスト: /api/reservations
     *
     * @return 予約DTOのリスト
     */
    @GetMapping
    public List<ReservationDTO> getAllReservations() {
        return reservationService.findAll();
    }

    /**
     * 指定されたIDの予約を取得する
     * HTTP GETリクエスト: /api/reservations/{id}
     *
     * @param id 取得する予約のID
     * @return 予約DTO
     */
    @GetMapping("/{id}")
    public ReservationDTO getReservationById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    /**
     * 指定された施設IDに関連する予約をすべて取得する
     * HTTP GETリクエスト: /api/reservations/facility/{facilityId}
     *
     * @param facilityId 施設ID
     * @return 予約DTOのリスト
     */
    @GetMapping("/facility/{facilityId}")
    public List<ReservationDTO> getReservationsByFacility(@PathVariable Long facilityId) {
        return reservationService.findByFacilityId(facilityId);
    }

    /**
     * 指定されたユーザーIDに関連する予約をすべて取得する
     * HTTP GETリクエスト: /api/reservations/user/{userId}
     *
     * @param userId ユーザーID
     * @return 予約DTOのリスト
     */
    @GetMapping("/user/{userId}")
    public List<ReservationDTO> getReservationsByUser(@PathVariable Long userId) {
        return reservationService.findByUserId(userId);
    }

    /**
     * 指定されたステータスを持つ予約をすべて取得する
     * HTTP GETリクエスト: /api/reservations/status/{status}
     *
     * @param status 予約ステータス（文字列形式）
     * @return 予約DTOのリスト
     */
    @GetMapping("/status/{status}")
    public List<ReservationDTO> getReservationsByStatus(@PathVariable String status) {
        return reservationService.findByStatus(Reservation.ReservationStatus.valueOf(status.toUpperCase()));
    }

    /**
     * 新しい予約を作成する
     * HTTP POSTリクエスト: /api/reservations
     * 認証されたユーザーの情報を使用して予約を作成する
     *
     * @param createDTO      予約作成に必要なデータを含むDTO
     * @param authentication 認証情報
     * @return 作成された予約のDTOと201 Created HTTPステータス
     */
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationCreateDTO createDTO,
            Authentication authentication) {
        // 認証されたユーザーの情報からユーザーIDを取得
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = extractUserIdFromUserDetails(userDetails);
        ReservationDTO createdReservation = reservationService.create(createDTO, userId);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    /**
     * 予約のステータスを更新する
     * HTTP PATCHリクエスト: /api/reservations/{id}/status
     *
     * @param id     更新する予約のID
     * @param status 新しい予約ステータス
     * @return 更新された予約DTO
     */
    @PatchMapping("/{id}/status")
    public ReservationDTO updateReservationStatus(
            @PathVariable Long id,
            @RequestParam Reservation.ReservationStatus status) {
        return reservationService.updateStatus(id, status);
    }

    /**
     * 指定されたIDの予約を削除する
     * HTTP DELETEリクエスト: /api/reservations/{id}
     *
     * @param id 削除する予約のID
     * @return 204 No Content HTTPステータス
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * UserDetailsからユーザーIDを抽出するヘルパーメソッド
     * CustomUserDetailsからユーザーIDを取得する
     *
     * @param userDetails 認証されたユーザーの詳細情報
     * @return ユーザーID
     */
    private Long extractUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetailsService.CustomUserDetails) {
            CustomUserDetailsService.CustomUserDetails customUserDetails = 
                (CustomUserDetailsService.CustomUserDetails) userDetails;
            return customUserDetails.getUser().getId();
        }
        throw new IllegalArgumentException("Unsupported UserDetails type");
    }
}