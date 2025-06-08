package com.example.reservation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ファイル配信専用のコントローラー
 * アバター画像などの静的ファイルの配信を担当
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${app.avatar.upload-dir:uploads/avatars}")
    private String uploadDir;

    /**
     * アバター画像を配信するエンドポイント
     * 
     * @param filename ファイル名
     * @return 画像ファイルのレスポンス
     */
    @GetMapping("/avatars/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        try {
            String projectRoot = System.getProperty("user.dir");
            Path filePath = Paths.get(projectRoot, uploadDir, filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(filePath);
            
            // ファイルのMIMEタイプを判定
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * デバッグ用: ファイルパス確認エンドポイント
     */
    @GetMapping("/debug/path")
    public ResponseEntity<String> debugPath() {
        String projectRoot = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectRoot, uploadDir);
        return ResponseEntity.ok("Project root: " + projectRoot + "\nUpload path: " + uploadPath.toString());
    }
}