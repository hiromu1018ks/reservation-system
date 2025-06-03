package com.example.reservation.repository;

import com.example.reservation.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ユーザー情報に対するデータアクセス操作を提供するリポジトリインターフェース
 * Spring Data JPAのJpaRepositoryを拡張し、Userエンティティに対する基本的なCRUD操作を自動的に提供する
 * 主キーの型はLong型
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ユーザー名に基づいてユーザーを検索するメソッド
     * ユーザーが存在しない場合は空のOptionalを返す
     *
     * @param username 検索対象のユーザー名
     * @return 該当するユーザーを含むOptional、存在しない場合は空のOptional
     */
    Optional<User> findByUsername(String username);

    /**
     * メールアドレスに基づいてユーザーを検索するメソッド
     * ユーザーが存在しない場合は空のOptionalを返す
     *
     * @param email 検索対象のメールアドレス
     * @return 該当するユーザーを含むOptional、存在しない場合は空のOptional
     */
    Optional<User> findByEmail(String email);

    /**
     * 指定されたユーザー名が既に存在するかどうかを確認するメソッド
     * ユーザー登録時の重複チェックなどに使用される
     *
     * @param username 存在確認するユーザー名
     * @return 存在する場合はtrue、存在しない場合はfalse
     */
    boolean existsByUsername(String username);

    /**
     * 指定されたメールアドレスが既に存在するかどうかを確認するメソッド
     * ユーザー登録時の重複チェックなどに使用される
     *
     * @param email 存在確認するメールアドレス
     * @return 存在する場合はtrue、存在しない場合はfalse
     */
    boolean existsByEmail(String email);
}