# ベースイメージとしてEclipse Temurin JDK 21（Alpineベース）を使用
# Alpineは軽量なLinuxディストリビューションで、イメージサイズを小さくできる
FROM eclipse-temurin:21-jdk-alpine

# コンテナ内の作業ディレクトリを/appに設定
# 以降のコマンドはこのディレクトリで実行される
WORKDIR /app

# gradleラッパーとビルドファイルをコピー
# ビルド設定のみを先にコピーすることで、依存関係が変わらない限りこのレイヤーはキャッシュされる
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 依存関係をダウンロード（キャッシュレイヤーとして）
# --no-daemonオプションでGradleデーモンを使用せず、コンテナ内でのメモリ使用を抑制
# 依存関係の解決を先に行うことで、ソースコードが変更されてもこのステップはキャッシュから利用される
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# ソースコードをコピー
# ソースコードは頻繁に変更されるため、依存関係の後にコピーしてビルド効率を上げる
COPY src src

# コンテナ起動時に実行されるコマンド
# bootRunタスクでSpring Bootアプリケーションを開発モードで起動
# 開発環境では、ホットリロード機能によりコード変更が即時反映される
CMD ["./gradlew", "bootRun", "--no-daemon"]