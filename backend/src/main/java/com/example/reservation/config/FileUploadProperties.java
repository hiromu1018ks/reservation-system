package com.example.reservation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ファイルアップロード機能の設定プロパティを管理するクラス
 * アプリケーションのプロパティファイル（application.properties/yml）から
 * 「app.avatar」プレフィックスを持つ設定値を読み込みます
 */
@Component  // このクラスをSpringのコンポーネントとして登録します
@ConfigurationProperties(prefix = "app.avatar")  // application.propertiesから「app.avatar」プレフィックスの設定を読み込みます
@Data  // Lombokのアノテーションで、getter、setter、toString、equalsなどのメソッドを自動生成します
public class FileUploadProperties {
    /**
     * アバター画像がアップロードされる保存先ディレクトリのパス
     * デフォルト値は「uploads/avatars」です
     */
    private String uploadDir = "uploads/avatars";

    /**
     * アップロードされるファイルの最大サイズ
     * デフォルト値は「5MB」です
     */
    private String maxSize = "5MB";

    /**
     * アップロードを許可するファイルのMIMEタイプのリスト
     * デフォルト値は「image/jpeg, image/png, image/gif」です
     * これにより、JPG、PNG、GIF形式の画像ファイルのみがアップロード可能になります
     */
    private String allowedTypes = "image/jpeg,image/png,image/gif";
}