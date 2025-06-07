package com.example.reservation.mapper;

import com.example.reservation.model.dto.AuthDTO;
import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;
import org.springframework.stereotype.Component;

/**
 * 認証関連のデータ変換（マッピング）を実装するクラス
 * DTOオブジェクト間の変換処理を担当し、データの形式を適切に変換する
 */
@Component  // このクラスをSpringのコンポーネントとして登録し、DIコンテナで管理できるようにする
public class AuthMapperImpl implements AuthMapper {

    /**
     * サインアップリクエスト（AuthDTO.SignupRequest）をユーザー登録DTO（UserRegistrationDTO）に変換する
     * クライアントから受け取ったサインアップ情報をシステム内部で処理しやすい形式に変換する
     *
     * @param request サインアップリクエスト情報を含むDTOオブジェクト
     * @return 変換されたユーザー登録用DTOオブジェクト
     */
    @Override
    public UserRegistrationDTO toRegistrationDTO(AuthDTO.SignupRequest request) {
        // 新しいユーザー登録DTOオブジェクトを作成
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();

        // サインアップリクエストの各フィールドをユーザー登録DTOにコピー
        registrationDTO.setUsername(request.getUsername());  // ユーザー名を設定
        registrationDTO.setEmail(request.getEmail());        // メールアドレスを設定
        registrationDTO.setPassword(request.getPassword());  // パスワードを設定

        // 変換後のDTOを返却
        return registrationDTO;
    }

    /**
     * JWTトークンとユーザー情報をトークン応答（TokenResponse）に変換する
     * 認証成功時にクライアントに返す応答データを生成する
     *
     * @param jwt  生成されたJWT認証トークン
     * @param user ユーザー情報を含むDTOオブジェクト
     * @return 認証トークン応答DTOオブジェクト
     */
    @Override
    public AuthDTO.TokenResponse toTokenResponse(String jwt, UserDTO user) {
        // 新しいトークン応答オブジェクトを生成して返却
        return new AuthDTO.TokenResponse(
                jwt,                    // 生成されたJWTトークン
                user.getId(),           // ユーザーID
                user.getUsername(),     // ユーザー名
                user.getEmail(),        // メールアドレス
                user.getRole().name()   // ユーザーの役割（列挙型の文字列表現に変換）
        );
    }
}