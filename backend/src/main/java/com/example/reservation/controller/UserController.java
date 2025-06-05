package com.example.reservation.controller;

import com.example.reservation.model.dto.UserDTO;
import com.example.reservation.model.dto.UserRegistrationDTO;
import com.example.reservation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ユーザー関連のHTTPリクエストを処理するコントローラークラス
 * REST APIエンドポイントを提供し、ユーザー情報の取得、登録、削除などの操作を行う
 */
@RestController  // このクラスがRESTコントローラーであることを示す（JSONレスポンスを自動的に生成）
@RequestMapping("/api/users")  // このコントローラーが処理するベースURLパス
@RequiredArgsConstructor  // Lombokアノテーション：finalフィールドを引数に持つコンストラクタを自動生成
public class UserController {
    /**
     * ユーザー関連の業務ロジックを提供するサービスクラス
     * コンストラクタインジェクションによって自動的に注入される
     */
    private final UserService userService;

    /**
     * 全ユーザー情報を取得するエンドポイント
     * HTTPメソッド: GET
     * URL: /api/users
     *
     * @return システムに登録されている全ユーザーのリスト（DTOオブジェクト形式）
     */
    @GetMapping
    public List<UserDTO> getAllUser() {
        return userService.findAll();
    }

    /**
     * 指定されたIDのユーザー情報を取得するエンドポイント
     * HTTPメソッド: GET
     * URL: /api/users/{id}
     *
     * @param id 取得対象のユーザーID（URLパスから抽出）
     * @return 該当するユーザーの情報（DTOオブジェクト形式）
     */
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    /**
     * 指定されたユーザー名のユーザー情報を取得するエンドポイント
     * HTTPメソッド: GET
     * URL: /api/users/username/{username}
     *
     * @param username 取得対象のユーザー名（URLパスから抽出）
     * @return 該当するユーザーの情報（DTOオブジェクト形式）
     */
    @GetMapping("/username/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    /**
     * 新規ユーザーを登録するエンドポイント
     * HTTPメソッド: POST
     * URL: /api/users/register
     *
     * @param registrationDTO ユーザー登録情報（リクエストボディから抽出）
     * @return 登録されたユーザー情報とHTTPステータスコード201（作成成功）を含むレスポンス
     * &#064;Validアノテーションによりバリデーションが実行される
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO createdUser = userService.register(registrationDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);  // HTTP 201 Created ステータスを返す
    }

    /**
     * 指定されたIDのユーザーを削除するエンドポイント
     * HTTPメソッド: DELETE
     * URL: /api/users/{id}
     *
     * @param id 削除対象のユーザーID（URLパスから抽出）
     * @return HTTPステータスコード204（内容なし）を含むレスポンス
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();  // HTTP 204 No Content ステータスを返す
    }
}