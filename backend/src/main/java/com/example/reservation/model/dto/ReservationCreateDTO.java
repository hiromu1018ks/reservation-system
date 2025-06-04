package com.example.reservation.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 予約作成時に使用するデータ転送オブジェクト（DTO）
 * クライアントから送信された予約作成リクエストのデータを受け取るために使用される
 * バリデーション制約を含み、入力データの検証を行う
 */
@Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
public class ReservationCreateDTO {
    /**
     * 施設ID
     * 予約対象となる施設を識別するためのID
     * 必須項目として設定され、nullの場合はバリデーションエラーとなる
     */
    @NotNull(message = "施設IDは必須です")
    private Long facilityId;

    /**
     * 予約開始時間
     * 予約の開始日時を示す
     * 必須項目であり、現在時刻以降の日時である必要がある
     */
    @NotNull(message = "開始時間は必須です")
    @FutureOrPresent(message = "開始時間は現在以降である必要があります")
    private LocalDateTime startTime;

    /**
     * 予約終了時間
     * 予約の終了日時を示す
     * 必須項目であり、現在時刻以降の日時である必要がある
     */
    @NotNull(message = "終了時間は必須です")
    @FutureOrPresent(message = "終了時間は現在以降である必要があります")
    private LocalDateTime endTime;

    /**
     * 予約目的
     * 施設を利用する目的や理由を記述するフィールド
     * 任意項目だが、入力される場合は500文字以内である必要がある
     */
    @Size(max = 500, message = "目的は500文字以内で入力してください")
    private String purpose;
}