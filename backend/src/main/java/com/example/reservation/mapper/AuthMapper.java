package com.example.reservation.mapper;
import com.example.reservation.model.dto.AuthDTO;
import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;

/**
 * 認証関連のデータ変換を行うマッパーインターフェース
 * 認証処理で使用される異なるデータモデル間の変換ロジックを定義する
 */
public interface AuthMapper {
    
    /**
     * サインアップリクエストをユーザー登録DTOに変換するメソッド
     *
     * @param request クライアントから受け取ったサインアップリクエスト情報
     * @return 内部処理用に変換されたユーザー登録DTO
     * 
     * このメソッドは外部からのリクエストデータを内部処理用の形式に変換することで、
     * コントローラーとサービス層の間でデータ形式の違いを吸収する役割を果たす
     */
    UserRegistrationDTO toRegistrationDTO(AuthDTO.SignupRequest request);
    
    /**
     * JWTトークンとユーザー情報からトークンレスポンスを生成するメソッド
     *
     * @param jwt 認証サーバーで生成されたJWTトークン文字列
     * @param user 認証されたユーザーの情報を含むDTO
     * @return クライアントに返却する認証トークンレスポンス
     * 
     * このメソッドは認証成功時に、生成されたJWTトークンとユーザー情報を組み合わせて
     * クライアントに返すためのレスポンスオブジェクトを作成する
     */
    AuthDTO.TokenResponse toTokenResponse(String jwt, UserDTO user);
}