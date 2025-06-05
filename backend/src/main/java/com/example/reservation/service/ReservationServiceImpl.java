package com.example.reservation.service;

import com.example.reservation.exception.IllegalOperationException;
import com.example.reservation.exception.ResourceNotFoundException;
import com.example.reservation.model.dto.ReservationCreateDTO;
import com.example.reservation.model.dto.ReservationDTO;
import com.example.reservation.model.entity.Facility;
import com.example.reservation.model.entity.Reservation;
import com.example.reservation.model.entity.User;
import com.example.reservation.repository.FacilityRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 予約サービスの実装クラス
 * 予約の作成、検索、更新、削除などの業務ロジックを提供する
 */
@Service
@RequiredArgsConstructor  // Lombokによる依存性注入用コンストラクタ自動生成
public class ReservationServiceImpl implements ReservationService {
    /**
     * 予約リポジトリ - 予約データへのアクセスを提供
     */
    private final ReservationRepository reservationRepository;

    /**
     * ユーザーリポジトリ - ユーザーデータへのアクセスを提供
     */
    private final UserRepository userRepository;

    /**
     * 施設リポジトリ - 施設データへのアクセスを提供
     */
    private final FacilityRepository facilityRepository;

    /**
     * IDによる予約情報の検索
     *
     * @param id 検索する予約のID
     * @return 予約DTOオブジェクト
     * @throws ResourceNotFoundException 予約が見つからない場合に発生
     */
    @Override
    public ReservationDTO findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("予約が見つかりませんでした: " + id));
        return ReservationDTO.fromEntity(reservation);
    }

    /**
     * すべての予約情報を取得
     *
     * @return 全予約のDTOリスト
     */
    @Override
    public List<ReservationDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationDTO::fromEntity)  // エンティティからDTOへの変換
                .collect(Collectors.toList());
    }

    /**
     * 施設IDによる予約情報の検索
     *
     * @param facilityId 施設ID
     * @return 指定された施設の予約DTOリスト
     */
    @Override
    public List<ReservationDTO> findByFacilityId(Long facilityId) {
        return reservationRepository.findByFacilityId(facilityId).stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ユーザーIDによる予約情報の検索
     *
     * @param userId ユーザーID
     * @return 指定されたユーザーの予約DTOリスト
     */
    @Override
    public List<ReservationDTO> findByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 予約ステータスによる予約情報の検索
     * 現在時刻以降の予約のみ対象
     *
     * @param status 検索対象のステータス
     * @return 指定されたステータスの予約DTOリスト
     */
    @Override
    public List<ReservationDTO> findByStatus(Reservation.ReservationStatus status) {
        return reservationRepository.findByStatusAndStartTimeAfter(status, LocalDateTime.now()).stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 新規予約の作成
     *
     * @param createDTO 予約作成用DTOオブジェクト
     * @param userId    予約するユーザーのID
     * @return 作成された予約のDTOオブジェクト
     * @throws ResourceNotFoundException 施設またはユーザーが見つからない場合
     * @throws IllegalArgumentException  予約時間が不正な場合
     * @throws IllegalOperationException 指定された時間枠が既に予約されている場合
     */
    @Override
    public ReservationDTO create(ReservationCreateDTO createDTO, Long userId) {
        // 施設情報の取得
        Facility facility = facilityRepository.findById(createDTO.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("施設が見つかりませんでした: " + createDTO.getFacilityId()));

        // ユーザー情報の取得
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザが見つかりませんでした: " + userId));

        // 終了時間が開始時間より後であることを確認
        if (createDTO.getEndTime().isBefore(createDTO.getStartTime())) {
            throw new IllegalArgumentException("終了時間は開始時間より後である必要があります");
        }

        // 過去の時間に予約できないことを確認
        if (createDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("過去の時間に予約を作成することはできません");
        }

        // 指定された時間枠が利用可能か確認
        if (!isTimeSlotAvailable(createDTO.getFacilityId(), createDTO.getStartTime(), createDTO.getEndTime(), null)) {
            throw new IllegalOperationException("指定された時間枠は既に予約されています");
        }

        // 予約エンティティの作成と設定
        Reservation reservation = new Reservation();
        reservation.setFacility(facility);
        reservation.setUser(user);
        reservation.setStartTime(createDTO.getStartTime());
        reservation.setEndTime(createDTO.getEndTime());
        reservation.setPurpose(createDTO.getPurpose());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);  // 初期ステータスは承認待ち

        // 予約を保存し、DTOに変換して返す
        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationDTO.fromEntity(savedReservation);
    }

    /**
     * 予約ステータスの更新
     *
     * @param id     更新する予約のID
     * @param status 新しい予約ステータス
     * @return 更新された予約のDTOオブジェクト
     * @throws ResourceNotFoundException 予約が見つからない場合
     */
    @Override
    public ReservationDTO updateStatus(Long id, Reservation.ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("予約が見つかりませんでした: " + id));

        // ステータスを更新
        reservation.setStatus(status);
        Reservation updatedReservation = reservationRepository.save(reservation);
        return ReservationDTO.fromEntity(updatedReservation);
    }

    /**
     * 指定された時間枠が予約可能かどうかをチェック
     *
     * @param facilityId           施設ID
     * @param startTime            開始時間
     * @param endTime              終了時間
     * @param excludeReservationId 除外する予約ID（更新時に自身の予約を除外するため）
     * @return 時間枠が利用可能な場合はtrue、既に予約されている場合はfalse
     */
    @Override
    public boolean isTimeSlotAvailable(Long facilityId, LocalDateTime startTime, LocalDateTime endTime, Long excludeReservationId) {
        // 重複する予約を検索
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(facilityId, startTime, endTime);

        // 除外対象のIDが指定されている場合、そのIDの予約を除外
        if (excludeReservationId != null) {
            overlappingReservations = overlappingReservations.stream()
                    .filter(r -> !r.getId().equals(excludeReservationId))
                    .toList();
        }

        // 重複する予約がなければ利用可能
        return overlappingReservations.isEmpty();
    }

    /**
     * 予約の削除
     *
     * @param id 削除する予約のID
     * @throws ResourceNotFoundException 予約が見つからない場合
     */
    @Override
    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("予約が見つかりませんでした: " + id);
        }
        reservationRepository.deleteById(id);
    }
}