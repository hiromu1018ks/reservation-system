package com.example.reservation.exception;

/**
 * ビジネスロジック例外の基底クラス
 * アプリケーション固有のビジネスルール違反を表現する例外の共通基底
 * 
 * この例外クラスを継承することで、より具体的なビジネス例外を定義し、
 * 適切なエラーハンドリングとユーザーフレンドリーなエラーメッセージを提供できる
 */
public class BusinessException extends RuntimeException {
    
    /**
     * エラーコード
     * 例外の種類を識別するためのコード
     */
    private final String errorCode;
    
    /**
     * コンストラクタ（メッセージのみ）
     * 
     * @param message エラーメッセージ
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    /**
     * コンストラクタ（メッセージと原因）
     * 
     * @param message エラーメッセージ
     * @param cause 原因となった例外
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    /**
     * コンストラクタ（エラーコード付き）
     * 
     * @param errorCode エラーコード
     * @param message エラーメッセージ
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * エラーコードを取得
     * 
     * @return エラーコード
     */
    public String getErrorCode() {
        return errorCode;
    }
}