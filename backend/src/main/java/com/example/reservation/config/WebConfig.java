package com.example.reservation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Webアプリケーションの設定を管理するクラス
 * 主にCORS（Cross-Origin Resource Sharing）の設定を担当
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * CORSの設定を行う
     * フロントエンド（localhost:3000）からのリクエストを許可する
     *
     * @param registry CORS設定を登録するためのレジストリ
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")  // すべてのエンドポイントに対してCORSを有効化
            .allowedOrigins("http://localhost:3000")  // フロントエンドのオリジンを許可
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")  // 許可するHTTPメソッド
            .allowedHeaders("*")  // すべてのヘッダーを許可
            .allowCredentials(true);  // クレデンシャル（Cookie等）の送信を許可
    }
}
