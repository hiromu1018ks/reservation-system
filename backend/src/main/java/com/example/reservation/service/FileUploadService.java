package com.example.reservation.service;

import com.example.reservation.config.FileUploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * ファイルアップロード機能を提供するサービスクラス
 * 主にユーザーアバター画像のアップロードを処理します
 */
@Service // このクラスをSpringのサービスコンポーネントとして登録します
@RequiredArgsConstructor // Lombokの機能で、finalフィールドを引数に持つコンストラクタを自動生成します
public class FileUploadService {
    /**
     * ファイルアップロードに関する設定情報を保持するプロパティクラス
     * アップロード先ディレクトリなどの情報が含まれています
     */
    private final FileUploadProperties fileUploadProperties;

    /**
     * ユーザーのアバター画像をアップロードし、保存するメソッド
     *
     * @param file   アップロードされたファイル（MultipartFile形式）
     * @param userId アバター画像の所有者となるユーザーID
     * @return 保存されたファイルのパス（文字列形式）
     * @throws IOException ファイル操作中にエラーが発生した場合
     */
    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        // ファイルのバリデーションを実行（空でないか、サイズ制限内か、許可された形式か）
        validateFile(file);

        // プロジェクトルートからの絶対パスでアップロード先ディレクトリを作成
        String projectRoot = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectRoot, fileUploadProperties.getUploadDir());

        // アップロードディレクトリが存在しない場合は作成
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // オリジナルのファイル名から拡張子を抽出
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // ユーザーIDとランダムなUUIDを組み合わせて一意のファイル名を生成
        // これにより、同じユーザーが複数回アップロードしても重複しません
        String newFilename = userId + "_" + UUID.randomUUID().toString() + extension;

        // 最終的な保存先のパスを作成
        Path filePath = uploadPath.resolve(newFilename);

        // ファイルをコピー（同名ファイルがある場合は上書き）
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Web経由でアクセス可能な相対パスを返却
        return "uploads/avatars/" + newFilename;
    }

    /**
     * アップロードされたファイルが有効かどうかを検証するメソッド
     * 空のファイル、サイズ超過、非対応形式の場合に例外をスローします
     *
     * @param file 検証対象のファイル
     * @throws IllegalArgumentException バリデーションに失敗した場合
     */
    private void validateFile(MultipartFile file) {
        // ファイルが空でないかチェック
        if (file.isEmpty()) {
            throw new IllegalArgumentException("ファイルが選択されていません");
        }

        // ファイルサイズが5MB以下かチェック (5 * 1024 * 1024 = 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("ファイルサイズが大きすぎます");
        }

        // ファイルの種類（Content-Type）が許可されているかチェック
        String contentType = file.getContentType();
        if (!isAllowedImageType(contentType)) {
            throw new IllegalArgumentException("許可されていないファイル形式です");
        }
    }

    /**
     * 指定されたContent-Typeが許可された画像形式かどうかを判定するメソッド
     *
     * @param contentType チェックするContent-Type
     * @return 許可された形式の場合はtrue、それ以外はfalse
     */
    private boolean isAllowedImageType(String contentType) {
        // null以外で、かつjpeg、png、gifのいずれかの形式かをチェック
        return contentType != null &&
                (contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif"));
    }
}