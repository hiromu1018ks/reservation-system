package com.example.reservation.security;

import com.example.reservation.model.entity.User;
import com.example.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * カスタム認証処理を行うサービスクラス
 * Spring Securityの認証プロセスで使用されるユーザー情報をデータベースから取得する
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    /**
     * ユーザー情報へのアクセスを提供するリポジトリ
     */
    private final UserRepository userRepository;

    /**
     * ユーザー名に基づいてユーザー詳細を読み込むメソッド
     * Spring Securityの認証プロセスから呼び出される
     *
     * @param username 認証に使用するユーザー名
     * @return カスタムユーザー詳細オブジェクト
     * @throws UsernameNotFoundException ユーザーが見つからない場合に投げられる例外
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // データベースからユーザー情報を検索
        // 見つからない場合は例外をスロー
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));

        // 検索結果からカスタムユーザー詳細オブジェクトを生成して返す
        return new CustomUserDetails(user);
    }

    /**
     * アプリケーション固有のユーザー情報をSpring Securityで使用できる形式に変換するクラス
     * UserDetailsインターフェースを実装し、認証と認可に必要な情報を提供する
     */
    public static class CustomUserDetails implements UserDetails {
        /**
         * データベースから取得したユーザーエンティティ
         */
        private final User user;

        /**
         * ユーザーに付与された権限のリスト
         */
        private final List<GrantedAuthority> authorities;

        /**
         * コンストラクタ - ユーザーエンティティからUserDetails実装を生成
         *
         * @param user データベースから取得したユーザー情報
         */
        public CustomUserDetails(User user) {
            this.user = user;
            this.authorities = new ArrayList<>();
            // ユーザーのロールを権限として設定
            // Spring Securityの規約に従い「ROLE_」プレフィックスを付加
            this.authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }

        /**
         * ユーザーに付与された権限のコレクションを返す
         *
         * @return 権限のコレクション
         */
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        /**
         * 認証に使用するパスワードを返す
         *
         * @return ハッシュ化されたパスワード
         */
        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }

        /**
         * 認証に使用するユーザー名を返す
         *
         * @return ユーザー名
         */
        @Override
        public String getUsername() {
            return user.getUsername();
        }

        /**
         * アカウントの有効期限が切れていないかを確認
         *
         * @return true：有効期限内、false：期限切れ
         * 現在の実装では常にtrueを返す（有効期限管理なし）
         */
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        /**
         * アカウントがロックされていないかを確認
         *
         * @return true：ロックされていない、false：ロック中
         * 現在の実装では常にtrueを返す（アカウントロック機能なし）
         */
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        /**
         * 認証情報（パスワード）の有効期限が切れていないかを確認
         *
         * @return true：有効期限内、false：期限切れ
         * 現在の実装では常にtrueを返す（パスワード期限管理なし）
         */
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        /**
         * ユーザーが有効かどうかを確認
         *
         * @return true：有効、false：無効
         * 現在の実装では常にtrueを返す（ユーザー無効化機能なし）
         */
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}