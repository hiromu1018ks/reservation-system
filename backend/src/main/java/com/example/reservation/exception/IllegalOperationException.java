package com.example.reservation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 不正な操作が行われた場合に発生する例外クラス。
 * この例外がスローされると、Spring MVCはHTTP 400（Bad Request）ステータスコードを
 * クライアントに返します。
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalOperationException extends RuntimeException {

    /**
     * 指定されたエラーメッセージを持つ新しいIllegalOperationExceptionを構築します。
     *
     * @param message エラーの詳細を説明するメッセージ文字列
     */
    public IllegalOperationException(String message) {
        super(message);
    }
}