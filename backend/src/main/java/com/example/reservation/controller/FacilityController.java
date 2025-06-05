package com.example.reservation.controller;

import com.example.reservation.model.dto.FacilityDTO;
import com.example.reservation.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 施設情報に関するHTTPリクエストを処理するRESTコントローラ
 * '/api/facilitiesエンドポイントへのリクエストを処理する
 */
@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {
    /**
     * 施設関連の業務ロジックを実行するサービスクラス
     * コンストラクタインジェクションによって自動的に注入される
     */
    private final FacilityService facilityService;

    /**
     * 全ての施設情報を取得するエンドポイント
     * GET /api/facilities
     *
     * @return 全施設のリスト
     */
    @GetMapping
    public List<FacilityDTO> getAllFacilities() {
        return facilityService.findAll();
    }

    /**
     * 指定されたIDの施設情報を取得するエンドポイント
     * GET /api/facilities/{id}
     *
     * @param id 取得したい施設のID
     * @return 指定されたIDの施設情報
     */
    @GetMapping("/{id}")
    public FacilityDTO getFacilityById(@PathVariable Long id) {
        return facilityService.findById(id);
    }

    /**
     * 施設を検索するエンドポイント
     * 名前または最小収容人数で検索可能
     * GET /api/facilities/search?name=xxx
     * GET /api/facilities/search?minCapacity=xxx
     * パラメータが指定されない場合は全施設を返す
     *
     * @param name        検索する施設名（部分一致）
     * @param minCapacity 最小収容人数
     * @return 検索条件に合致する施設のリスト
     */
    @GetMapping("/search")
    public List<FacilityDTO> searchFacilities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minCapacity) {
        // 名前が指定された場合は名前で検索
        if (name != null && !name.isEmpty()) {
            return facilityService.findByNameContaining(name);
            // 最小収容人数が指定された場合は収容人数で検索
        } else if (minCapacity != null) {
            return facilityService.findByMinCapacity(minCapacity);
            // どちらも指定されていない場合は全施設を返す
        } else {
            return facilityService.findAll();
        }
    }

    /**
     * 新しい施設を作成するエンドポイント
     * POST /api/facilities
     * リクエストボディにFacilityDTOのJSONを含める必要がある
     *
     * @param facilityDTO 作成する施設の情報
     * @return 作成された施設情報とHTTPステータス201(Created)
     */
    @PostMapping
    public ResponseEntity<FacilityDTO> createFacility(@Valid @RequestBody FacilityDTO facilityDTO) {
        FacilityDTO createdFacility = facilityService.create(facilityDTO);
        return new ResponseEntity<>(createdFacility, HttpStatus.CREATED);
    }

    /**
     * 施設情報を更新するエンドポイント
     * PUT /api/facilities/{id}
     * リクエストボディに更新後のFacilityDTOのJSONを含める必要がある
     *
     * @param id          更新する施設のID
     * @param facilityDTO 更新後の施設情報
     * @return 更新された施設情報
     */
    @PutMapping("/{id}")
    public FacilityDTO updateFacility(
            @PathVariable Long id,
            @Valid @RequestBody FacilityDTO facilityDTO) {
        return facilityService.update(id, facilityDTO);
    }

    /**
     * 施設を削除するエンドポイント
     * DELETE /api/facilities/{id}
     *
     * @param id 削除する施設のID
     * @return HTTPステータス204(No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Long id) {
        facilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}