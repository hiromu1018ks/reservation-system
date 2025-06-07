# 予約システム開発ガイドライン

このドキュメントは予約システムの開発に関する重要な情報を提供します。プロジェクトの構成、ビルド方法、テスト方法、および開発のベストプラクティスについて説明します。

## ビルド・設定手順

### プロジェクト構成

このプロジェクトは以下のコンポーネントで構成されています：

- **バックエンド**: Spring Boot 3.5.0 + Java 21 + Gradle
- **フロントエンド**: React 19 + TypeScript + Vite
- **データベース**: PostgreSQL 15

### 環境構築

#### Docker Composeを使用した開発環境の起動

```bash
# プロジェクトルートディレクトリで実行
docker-compose up -d
```

これにより、以下のサービスが起動します：
- PostgreSQLデータベース (ポート: 5432)
- バックエンドサーバー (ポート: 8080)
- フロントエンド開発サーバー (ポート: 5173)

#### バックエンドの個別ビルド

```bash
cd backend
./gradlew build
```

#### フロントエンドの個別ビルド

```bash
cd frontend
npm install
npm run build
```

### 環境変数

バックエンドの主要な環境変数：

```
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/reservation
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_PROFILES_ACTIVE=dev
```

## テスト情報

### バックエンドテスト

#### テスト実行方法

すべてのテストを実行：

```bash
cd backend
./gradlew test
```

特定のテストクラスのみ実行：

```bash
./gradlew test --tests "com.example.reservation.service.UserServiceTest"
```

特定のテストメソッドのみ実行：

```bash
./gradlew test --tests "com.example.reservation.service.UserServiceTest.registerUser_Success"
```

#### テスト作成ガイドライン

1. **テストクラスの配置場所**:
   - テストクラスは `src/test/java` ディレクトリ内に、テスト対象のクラスと同じパッケージ構造で配置します。
   - 例: `com.example.reservation.service.UserService` のテストは `src/test/java/com/example/reservation/service/UserServiceTest.java` に配置します。

2. **テストクラスの命名規則**:
   - テストクラス名は、テスト対象のクラス名に `Test` を付加します。
   - 例: `UserService` のテストクラスは `UserServiceTest` とします。

3. **テストメソッドの命名規則**:
   - テストメソッド名は、テスト対象のメソッド名とテストシナリオを組み合わせます。
   - 例: `registerUser_Success`, `registerUser_DuplicateUsername`

4. **モックの使用**:
   - 外部依存（データベース、外部API等）はモック化してテストを実行します。
   - Mockitoを使用してモックオブジェクトを作成します。

#### テスト例

以下は `UserService` のテスト例です：

```java
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDTO registrationDTO;
    private User savedUser;

    @BeforeEach
    void setUp() {
        // テストデータの準備
        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setPassword("password123");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setPasswordHash("hashedPassword");
        savedUser.setRole(User.Role.USER);
    }

    @Test
    void registerUser_Success() {
        // モックの設定
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // テスト対象メソッドの実行
        UserDTO result = userService.register(registrationDTO);

        // 検証
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(User.Role.USER, result.getRole());

        // メソッドの呼び出し回数を検証
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }
}
```

### フロントエンドテスト

フロントエンドのテストフレームワークは現在設定されていません。必要に応じて以下のパッケージを追加してください：

```bash
cd frontend
npm install --save-dev vitest @testing-library/react @testing-library/jest-dom
```

## 開発情報

### コーディング規約

#### バックエンド（Java）

1. **命名規則**:
   - クラス名: パスカルケース (例: `UserService`)
   - メソッド名・変数名: キャメルケース (例: `findByUsername`)
   - 定数: 大文字のスネークケース (例: `MAX_RETRY_COUNT`)

2. **コメント**:
   - クラス、メソッド、フィールドには日本語のJavadocコメントを記述します。
   - 複雑なロジックには適宜インラインコメントを追加します。

3. **例外処理**:
   - 適切な例外クラスを使用し、意味のあるメッセージを設定します。
   - リソースの解放を確実に行うために try-with-resources を使用します。

#### フロントエンド（TypeScript/React）

1. **コンポーネント構造**:
   - 関数コンポーネントとReact Hooksを使用します。
   - 大きなコンポーネントは小さな再利用可能なコンポーネントに分割します。

2. **状態管理**:
   - ローカル状態には `useState` を使用します。
   - コンポーネント間で共有する状態には Context API を使用します。

### データベース設計

- テーブル名は複数形で小文字のスネークケース (例: `users`, `reservations`)
- 主キーは `id` という名前の自動採番カラム
- 外部キーは `<テーブル名>_id` という命名規則 (例: `user_id`)
- 作成日時・更新日時は `created_at`, `updated_at` というカラム名

### セキュリティ

- パスワードは必ずハッシュ化して保存 (BCryptを使用)
- JWTトークンを使用した認証
- Spring Securityによる認可制御
- クロスサイトリクエストフォージェリ（CSRF）対策

### デバッグ方法

#### バックエンド

1. ログの確認:
   ```bash
   docker-compose logs -f backend
   ```

2. リモートデバッグの設定:
   - IntelliJ IDEAでリモートデバッグ設定を作成
   - ポート: 5005
   - コマンドライン引数: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`

#### フロントエンド

1. ブラウザの開発者ツールを使用
2. Reactデバッグツール拡張機能を使用