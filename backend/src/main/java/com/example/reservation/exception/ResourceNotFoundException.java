package com.example.reservation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * リソースが見つからない場合に発生する例外クラス。
 * この例外がスローされると、Spring MVCはHTTP 404（Not Found）ステータスコードを
 * クライアントに返します。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * 指定されたエラーメッセージを持つ新しいResourceNotFoundExceptionを構築します。
     *
     * @param message エラーの詳細を説明するメッセージ文字列
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}