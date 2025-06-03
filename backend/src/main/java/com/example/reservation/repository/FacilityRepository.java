package com.example.reservation.repository;

import com.example.reservation.model.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 施設情報に対するデータアクセス操作を提供するリポジトリインターフェース
 * Spring Data JPAのJpaRepositoryを拡張し、Facilityエンティティに対する基本的なCRUD操作を自動的に提供する
 * 主キーの型はLong型
 */
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    /**
     * 施設名に特定の文字列が含まれる施設を検索するメソッド
     * 大文字・小文字を区別せずに検索を行う
     *
     * @param name 検索対象の文字列（施設名の一部）
     * @return 検索条件に一致する施設のリスト（該当するものがない場合は空のリスト）
     */
    List<Facility> findByNameContainingIgnoreCase(String name);

    /**
     * 指定した収容人数以上の施設を検索するメソッド
     *
     * @param minCapacity 検索する最小収容人数
     * @return 指定した収容人数以上の施設のリスト（該当するものがない場合は空のリスト）
     */
    List<Facility> findByCapacityGreaterThanEqual(Integer minCapacity);
}