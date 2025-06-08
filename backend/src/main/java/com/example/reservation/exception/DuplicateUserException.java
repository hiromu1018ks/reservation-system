package com.example.reservation.exception;

/**
 * ユーザー重複例外クラス
 * ユーザー名またはメールアドレスが既に存在する場合に発生する例外
 * 
 * この例外は新規ユーザー登録時に、既存のユーザーと重複するデータが
 * 検出された場合に使用される
 */
public class DuplicateUserException extends BusinessException {
    
    /**
     * コンストラクタ
     * 
     * @param message 重複に関する詳細メッセージ
     */
    public DuplicateUserException(String message) {
        super("DUPLICATE_USER", message);
    }
    
    /**
     * ユーザー名重複のファクトリメソッド
     * 
     * @param username 重複したユーザー名
     * @return ユーザー名重複例外
     */
    public static DuplicateUserException forUsername(String username) {
        return new DuplicateUserException(
            String.format("ユーザー名 '%s' は既に使用されています", username)
        );
    }
    
    /**
     * メールアドレス重複のファクトリメソッド
     * 
     * @param email 重複したメールアドレス
     * @return メールアドレス重複例外
     */
    public static DuplicateUserException forEmail(String email) {
        return new DuplicateUserException(
            String.format("メールアドレス '%s' は既に使用されています", email)
        );
    }
}