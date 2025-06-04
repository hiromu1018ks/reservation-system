package com.example.reservation.model.dto;

import com.example.reservation.model.entity.Facility;
import lombok.Data;

/**
 * 施設情報をクライアント側やビジネスロジックで扱いやすい形に変換するためのDTOクラス
 * データベースエンティティとは異なり、表示に必要な情報のみを保持する
 */
@Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
public class FacilityDTO {
    /**
     * 施設ID
     * データベース上の一意識別子
     */
    private Long id;

    /**
     * 施設名
     * 施設を識別するための名称
     */
    private String name;

    /**
     * 施設の説明
     * 施設の詳細情報や特徴を説明するテキスト
     */
    private String description;

    /**
     * 施設の収容人数
     * 施設が収容可能な最大人数
     */
    private Integer capacity;

    /**
     * 施設の場所・住所
     * 施設の物理的な所在地情報
     */
    private String location;

    /**
     * 施設の画像URL
     * 施設の外観や内装を表示するための画像へのリンク
     */
    private String imageUrl;

    /**
     * 施設エンティティからDTOオブジェクトを生成する静的ファクトリメソッド
     * このメソッドを使用することで、エンティティからDTOへの変換処理を一箇所に集約できる
     *
     * @param facility 変換元となる施設エンティティ
     * @return 生成されたFacilityDTOオブジェクト
     */
    public static FacilityDTO fromEntity(Facility facility) {
        // 新しいDTOオブジェクトを生成
        FacilityDTO dto = new FacilityDTO();
        // 施設エンティティの各フィールド値をDTOにコピー
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setDescription(facility.getDescription());
        dto.setCapacity(facility.getCapacity());
        dto.setLocation(facility.getLocation());
        dto.setImageUrl(facility.getImageUrl());
        // 作成したDTOを返却
        return dto;
    }
}