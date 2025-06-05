package com.example.reservation.service;

import com.example.reservation.exception.ResourceNotFoundException;
import com.example.reservation.model.dto.FacilityDTO;
import com.example.reservation.model.entity.Facility;
import com.example.reservation.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 施設管理のビジネスロジックを実装するサービスクラス
 * 施設のCRUD操作と検索機能を提供します
 */
@Service // Springのサービスコンポーネントとして登録するアノテーション
@RequiredArgsConstructor // Lombokアノテーション：finalフィールドのみを引数とするコンストラクタを自動生成
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository; // 施設情報の永続化を担当するリポジトリ

    /**
     * 指定されたIDの施設を検索します
     *
     * @param id 施設ID
     * @return 施設情報DTO
     * @throws ResourceNotFoundException 施設が見つからない場合
     */
    @Override
    public FacilityDTO findById(Long id) {
        // リポジトリからIDで施設を検索し、見つからない場合は例外をスロー
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("施設が見つかりませんでした: " + id));
        // エンティティからDTOに変換して返却
        return FacilityDTO.fromEntity(facility);
    }

    /**
     * すべての施設を取得します
     *
     * @return 施設情報DTOのリスト
     */
    @Override
    public List<FacilityDTO> findAll() {
        // リポジトリからすべての施設を取得
        List<Facility> facilities = facilityRepository.findAll();
        // 各エンティティをDTOに変換しリストとして返却（Java 16以降のtoListメソッドを使用）
        return facilities.stream().map(FacilityDTO::fromEntity).toList();
    }

    /**
     * 指定された名前を含む施設を検索します（大文字小文字を区別しない）
     *
     * @param name 検索する施設名
     * @return 該当する施設情報DTOのリスト
     */
    @Override
    public List<FacilityDTO> findByNameContaining(String name) {
        // 名前に特定の文字列を含む施設をリポジトリから検索（大文字小文字を区別しない）
        List<Facility> facilities = facilityRepository.findByNameContainingIgnoreCase(name);
        // 検索結果の各エンティティをDTOに変換しリストとして返却
        return facilities.stream().map(FacilityDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * 指定された収容人数以上の施設を検索します
     *
     * @param minCapacity 最小収容人数
     * @return 該当する施設情報DTOのリスト
     */
    @Override
    public List<FacilityDTO> findByMinCapacity(Integer minCapacity) {
        // 指定された収容人数以上の施設をリポジトリから検索
        List<Facility> facilities = facilityRepository.findByCapacityGreaterThanEqual(minCapacity);
        // 検索結果の各エンティティをDTOに変換しリストとして返却
        return facilities.stream().map(FacilityDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * 新しい施設を作成します
     *
     * @param facilityDTO 作成する施設の情報
     * @return 作成された施設の情報DTO
     */
    @Override
    @Transactional // トランザクション管理を有効化するアノテーション（処理中にエラーが発生した場合、変更をロールバック）
    public FacilityDTO create(FacilityDTO facilityDTO) {
        // DTOからエンティティへの変換
        Facility facility = new Facility();
        facility.setName(facilityDTO.getName());        // 施設名を設定
        facility.setDescription(facilityDTO.getDescription()); // 説明を設定
        facility.setCapacity(facilityDTO.getCapacity());    // 収容人数を設定
        facility.setLocation(facilityDTO.getLocation());    // 場所を設定
        facility.setImageUrl(facilityDTO.getImageUrl());    // 画像URLを設定

        // データベースへの保存（created_atとupdated_atは@PrePersistで自動設定される）
        Facility savedFacility = facilityRepository.save(facility);
        // 保存されたエンティティをDTOに変換して返却
        return FacilityDTO.fromEntity(savedFacility);
    }

    /**
     * 指定されたIDの施設情報を更新します
     * 現在は実装されていません（null返却）
     *
     * @param id          更新する施設のID
     * @param facilityDTO 更新する施設の情報
     * @return 更新された施設の情報DTO
     */
    @Override
    public FacilityDTO update(Long id, FacilityDTO facilityDTO) {
        return null; // 未実装（実装時には既存の施設を取得し、値を更新した後保存する処理が必要）
    }

    /**
     * 指定されたIDの施設を削除します
     * 現在は実装されていません
     *
     * @param id 削除する施設のID
     */
    @Override
    public void delete(Long id) {
        // 未実装（実装時には指定IDの施設を削除する処理が必要）
    }
}