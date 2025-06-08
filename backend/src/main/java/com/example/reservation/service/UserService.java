package com.example.reservation.service;

import com.example.reservation.model.dto.PasswordChangeDTO;
import com.example.reservation.model.dto.ProfileUpdateDTO;
import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    /**
     * 指定されたユーザー名が既にデータベースに存在するかをチェックするメソッド
     *
     * @param username チェック対象のユーザー名
     * @return 指定されたユーザー名が既に存在する場合はtrue、存在しない場合はfalse
     * <p>
     * このメソッドは主にユーザー登録時のバリデーションで使用され、
     * 重複するユーザー名の登録を防止するために利用されます。
     * Spring Data JPAによって自動的に実装され、クエリは内部で生成されます。
     */
    boolean existsByUsername(String username);

    /**
     * 指定されたメールアドレスが既にデータベースに存在するかをチェックするメソッド
     *
     * @param email チェック対象のメールアドレス
     * @return 指定されたメールアドレスが既に存在する場合はtrue、存在しない場合はfalse
     * <p>
     * このメソッドは主にユーザー登録時のバリデーションで使用され、
     * 同じメールアドレスを持つ複数のアカウント作成を防止するために利用されます。
     * Spring Data JPAによって自動的に実装され、メソッド名からクエリが生成されます。
     */
    boolean existsByEmail(String email);

    /**
     * ユーザープロファイル情報を更新するメソッド
     *
     * @param userId           更新対象のユーザーID
     * @param profileUpdateDTO 更新するプロファイル情報を含むDTO
     *                         （名前、メールアドレスなどの更新可能な項目を含む）
     * @return 更新後のユーザー情報を含むUserDTOオブジェクト
     * @throws IllegalArgumentException 指定されたIDのユーザーが存在しない場合や
     *                                  更新内容が無効な場合（例：既に使用されているメールアドレス）
     */
    UserDTO updateProfile(Long userId, ProfileUpdateDTO profileUpdateDTO);

    /**
     * ユーザーのアバター画像（プロフィール画像）を更新するメソッド
     *
     * @param userId     アバターを更新するユーザーのID
     * @param avatarFile アップロードされた新しいアバター画像ファイル
     *                   （MultipartFileとして受け取り、適切な形式に処理される）
     * @return 更新後のユーザー情報を含むUserDTOオブジェクト
     * @throws IllegalArgumentException      指定されたIDのユーザーが存在しない場合
     * @throws IOException                   ファイル処理中にエラーが発生した場合
     * @throws UnsupportedMediaTypeException サポートされていないファイル形式の場合
     */
    UserDTO updateAvatar(Long userId, MultipartFile avatarFile) throws IOException;

    /**
     * ユーザーのパスワードを変更するメソッド
     *
     * @param userId            パスワードを変更するユーザーのID
     * @param passwordChangeDTO パスワード変更情報を含むDTO
     *                          （現在のパスワードと新しいパスワードを含む）
     * @throws IllegalArgumentException    指定されたIDのユーザーが存在しない場合
     * @throws InvalidCredentialsException 現在のパスワードが一致しない場合
     * @throws InvalidPasswordException    新しいパスワードが要件を満たさない場合
     *                                     （例：最小長さ、複雑さの要件）
     */
    void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO);
}