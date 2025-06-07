package com.example.reservation.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API レスポンスの統一形式を提供するクラス
 * すべてのREST APIエンドポイントで一貫したレスポンス形式を使用するために定義
 * 
 * @param <T> レスポンスデータの型パラメータ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    
    /**
     * リクエストの成功/失敗を示すフラグ
     * true: 成功、false: 失敗
     */
    private boolean success;
    
    /**
     * レスポンスメッセージ
     * 成功時は成功メッセージ、失敗時はエラーメッセージを格納
     */
    private String message;
    
    /**
     * レスポンスデータ
     * 実際のAPIレスポンスデータを格納（ジェネリクス型）
     */
    private T data;
    
    /**
     * レスポンス生成時刻
     * レスポンスが生成された日時を記録
     */
    private LocalDateTime timestamp;
    
    /**
     * 成功レスポンスを生成するファクトリメソッド
     * 
     * @param <T> データの型
     * @param data レスポンスデータ
     * @return 成功レスポンス
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            true, 
            "Success", 
            data, 
            LocalDateTime.now()
        );
    }
    
    /**
     * 成功レスポンス（メッセージ付き）を生成するファクトリメソッド
     * 
     * @param <T> データの型
     * @param message 成功メッセージ
     * @param data レスポンスデータ
     * @return 成功レスポンス
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
            true, 
            message, 
            data, 
            LocalDateTime.now()
        );
    }
    
    /**
     * エラーレスポンスを生成するファクトリメソッド
     * 
     * @param message エラーメッセージ
     * @return エラーレスポンス
     */
    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(
            false, 
            message, 
            null, 
            LocalDateTime.now()
        );
    }
}