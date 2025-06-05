package com.example.reservation.service;

import com.example.reservation.model.dto.FacilityDTO;

import java.util.List;

/**
 * 施設関連の業務ロジックを定義するサービスインターフェース
 * このインターフェースを実装するクラスが実際の施設管理機能を提供する
 */
public interface FacilityService {
    /**
     * 指定されたIDに基づいて施設を検索する
     *
     * @param id 検索対象の施設ID
     * @return 該当する施設の情報（FacilityDTO形式）
     * 存在しない場合は実装クラスによって例外がスローされる可能性がある
     */
    FacilityDTO findById(Long id);

    /**
     * システム内の全施設を取得する
     *
     * @return 全施設のリスト（FacilityDTO形式）
     * 施設が存在しない場合は空のリストが返される
     */
    List<FacilityDTO> findAll();

    /**
     * 指定された名前を含む施設を検索する
     *
     * @param name 検索キーワード（施設名の一部）
     * @return 条件に一致する施設のリスト（FacilityDTO形式）
     * 該当する施設がない場合は空のリストが返される
     */
    List<FacilityDTO> findByNameContaining(String name);

    /**
     * 指定された最小収容人数以上の施設を検索する
     *
     * @param minCapacity 最小収容人数（この値以上の収容人数を持つ施設が検索される）
     * @return 条件に一致する施設のリスト（FacilityDTO形式）
     * 該当する施設がない場合は空のリストが返される
     */
    List<FacilityDTO> findByMinCapacity(Integer minCapacity);

    /**
     * 新規施設を作成する
     *
     * @param facilityDTO 作成する施設の情報
     * @return 作成された施設の情報（ID等が付与された状態のFacilityDTO）
     * 作成に失敗した場合は実装クラスによって例外がスローされる可能性がある
     */
    FacilityDTO create(FacilityDTO facilityDTO);

    /**
     * 指定されたIDの施設情報を更新する
     *
     * @param id          更新対象の施設ID
     * @param facilityDTO 更新する施設の情報
     * @return 更新後の施設情報（FacilityDTO形式）
     * 更新対象が存在しない場合は実装クラスによって例外がスローされる可能性がある
     */
    FacilityDTO update(Long id, FacilityDTO facilityDTO);

    /**
     * 指定されたIDの施設を削除する
     *
     * @param id 削除対象の施設ID
     *           該当する施設が存在しない場合は実装クラスによって例外がスローされる可能性がある
     */
    void delete(Long id);
}