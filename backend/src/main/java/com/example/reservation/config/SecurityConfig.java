package com.example.reservation.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Securityの設定クラス
 * アプリケーション全体のセキュリティ設定を管理します
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * セキュリティフィルターチェーンの設定
     * HTTPリクエストに対する認証・認可の設定を行います
     *
     * @param http HttpSecurityオブジェクト
     * @return 設定済みのSecurityFilterChain
     * @throws Exception 設定中にエラーが発生した場合
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORSの設定を適用
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // CSRF保護を無効化（RESTful APIのため）
            .csrf(csrf -> csrf.disable())
            // エンドポイントごとのアクセス制御を設定
            .authorizeHttpRequests(authz -> authz
                // 認証不要のエンドポイント
                .requestMatchers("/api/users/register","/api/auth/**").permitAll()
                // 一般ユーザーと管理者がアクセス可能なエンドポイント
                .requestMatchers("/api/facilities/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/reservations/**").hasAnyRole("USER", "ADMIN")
                // 管理者専用エンドポイント
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // その他のエンドポイントは認証が必要
                .anyRequest().authenticated()
            )
            // セッション管理をステートレスに設定（JWT認証のため）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * CORSの設定
     * フロントエンド（localhost:3000）からのクロスオリジンリクエストを許可します
     * 認証情報（クッキー等）の送信を許可し、必要なHTTPメソッドとヘッダーを設定します
     *
     * @return CORS設定を提供するCorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // フロントエンドのオリジンのみを許可
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // 必要なHTTPメソッドを許可
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // すべてのヘッダーを許可
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 認証情報の送信を許可
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * パスワードエンコーダーの設定
     * BCryptアルゴリズムを使用してパスワードのハッシュ化を行います
     *
     * @return BCryptPasswordEncoderのインスタンス
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
