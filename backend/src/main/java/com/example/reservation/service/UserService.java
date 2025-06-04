package com.example.reservation.service;

import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;

import java.util.List;

/**
 * ユーザー関連の業務ロジックを定義するサービスインターフェース
 * このインターフェースを実装するクラスが実際のユーザー管理機能を提供する
 */
public interface UserService {
    /**
     * 指定されたIDに基づいてユーザーを検索する
     *
     * @param id 検索対象のユーザーID
     * @return 該当するユーザーの情報（UserDTO形式）
     * 存在しない場合は実装クラスによって例外がスローされる可能性がある
     */
    UserDTO findById(Long id);

    /**
     * 指定されたユーザー名に基づいてユーザーを検索する
     *
     * @param username 検索対象のユーザー名
     * @return 該当するユーザーの情報（UserDTO形式）
     * 存在しない場合は実装クラスによって例外がスローされる可能性がある
     */
    UserDTO findByUsername(String username);

    /**
     * システム内の全ユーザーを取得する
     *
     * @return 全ユーザーのリスト（UserDTO形式）
     * ユーザーが存在しない場合は空のリストが返される
     */
    List<UserDTO> findAll();

    /**
     * 新規ユーザーを登録する
     *
     * @param registrationDTO ユーザー登録情報（ユーザー名、メール、パスワードなどを含む）
     * @return 登録された新規ユーザーの情報（UserDTO形式）
     * 登録に失敗した場合は実装クラスによって例外がスローされる可能性がある
     */
    UserDTO register(UserRegistrationDTO registrationDTO);

    /**
     * 指定されたIDのユーザーを削除する
     *
     * @param id 削除対象のユーザーID
     *           該当するユーザーが存在しない場合は実装クラスによって例外がスローされる可能性がある
     */
    void deleteById(Long id);
}