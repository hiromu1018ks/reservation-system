package com.example.reservation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Webアプリケーションの設定を管理するクラス
 * CORS設定と静的ファイル配信設定を統合管理
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * アップロードされたファイルの保存ディレクトリ
     */
    @Value("${app.avatar.upload-dir:uploads/avatars}")
    private String uploadDir;

    /**
     * CORSの設定を行う
     * フロントエンドからのリクエストを許可する
     *
     * @param registry CORS設定を登録するためのレジストリ
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")  // すべてのエンドポイントに対してCORSを有効化
                .allowedOrigins("http://localhost:5173", "http://localhost:5174", "http://localhost:5175")  // フロントエンドのオリジンを許可
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")  // 許可するHTTPメソッド
                .allowedHeaders("*")  // すべてのヘッダーを許可
                .allowCredentials(true);  // クレデンシャル（Cookie等）の送信を許可
    }

    /**
     * 静的リソースハンドラーの設定
     * アップロードされたファイルにHTTPアクセスできるようにする
     *
     * @param registry リソースハンドラーのレジストリ
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // プロジェクトルートからの絶対パス指定
        String projectRoot = System.getProperty("user.dir");
        
        // uploadsディレクトリ全体を静的リソースとして配信
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + projectRoot + "/uploads/")
                .setCachePeriod(3600); // 1時間キャッシュ
        
        // アバター専用のハンドラー（より具体的な設定）
        registry.addResourceHandler("/api/files/avatars/**")
                .addResourceLocations("file:" + projectRoot + "/" + uploadDir + "/")
                .setCachePeriod(3600); // 1時間キャッシュ
    }
}
