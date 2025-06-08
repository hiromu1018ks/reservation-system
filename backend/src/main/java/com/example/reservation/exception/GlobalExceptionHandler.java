package com.example.reservation.exception;

import com.example.reservation.model.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * アプリケーション全体の例外を処理するグローバル例外ハンドラー。
 * 各例外タイプに応じて適切なHTTPステータスコードとエラーメッセージを返します。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * リソースが見つからない場合の例外を処理します。
     * HTTP 404 Not Foundステータスを返します。
     *
     * @param ex 発生したResourceNotFoundException
     * @return エラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    /**
     * 不正な操作が試みられた場合の例外を処理します。
     * HTTP 400 Bad Requestステータスを返します。
     *
     * @param ex 発生したIllegalOperationException
     * @return エラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<Map<String, String>> handleIllegalOperationException(IllegalOperationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * 不正な引数が渡された場合の例外を処理します。
     * HTTP 400 Bad Requestステータスを返します。
     *
     * @param ex 発生したIllegalArgumentException
     * @return エラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * バリデーション例外を処理します。
     * 各フィールドのバリデーションエラーメッセージを収集し、
     * HTTP 400 Bad Requestステータスを返します。
     *
     * @param ex 発生したMethodArgumentNotValidException
     * @return フィールド名とエラーメッセージのマップを含むResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * その他の予期しない例外を処理します。
     * HTTP 500 Internal Server Errorステータスを返します。
     *
     * @param ex 発生した例外
     * @return エラーメッセージを含むResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "予期しないエラーが発生しました: " + ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ファイルアップロード関連の例外処理
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex) {

        ApiResponse<Void> response = new ApiResponse<>(
                false,
                "ファイルサイズが大きすぎます",
                null,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 一般的なIOException の処理
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Void>> handleIOException(IOException ex) {
        ApiResponse<Void> response = new ApiResponse<>(
                false,
                "ファイル操作でエラーが発生しました",
                null,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}