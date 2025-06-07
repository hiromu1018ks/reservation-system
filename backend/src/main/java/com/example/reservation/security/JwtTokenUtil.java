package com.example.reservation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWTトークンの生成と検証を行うユーティリティクラス
 * JWT（JSON Web Token）は、ユーザー認証や情報交換に使用される標準的なトークン形式です
 */
@Component
public class JwtTokenUtil {

    // application.propertiesからJWTの秘密鍵を読み込みます
    // この秘密鍵はトークンの署名と検証に使用されます
    @Value("${app.jwt.secret}")
    private String secret;

    // application.propertiesからJWTの有効期限を読み込みます
    // トークンが有効な期間をミリ秒単位で指定します
    @Value("${app.jwt.expiration}")
    private Long expiration;

    /**
     * トークンからユーザー名を取得するメソッド
     *
     * @param token JWTトークン
     * @return トークンに含まれるユーザー名
     * <p>
     * 処理の流れ：
     * 1. トークンからClaims（クレーム）を取得します
     * 2. Claimsからsubject（ユーザー名）を取得して返します
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * トークンから指定されたクレームを取得するメソッド
     *
     * @param token          JWTトークン
     * @param claimsResolver 取得したいクレームを指定する関数
     * @return 指定されたクレームの値
     * <p>
     * 処理の流れ：
     * 1. トークンからすべてのクレームを取得します
     * 2. 指定された関数を使用して、必要なクレームの値を抽出します
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * トークンからユーザーIDを取得するメソッド
     *
     * @param token JWTトークン
     * @return トークンに含まれるユーザーID
     * <p>
     * 処理の流れ：
     * 1. トークンからすべてのクレームを取得します
     * 2. "userId"という名前のクレームから値を取得します
     * 3. 文字列として取得した値をLong型に変換して返します
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.parseLong(claims.get("userId", String.class));
    }

    /**
     * トークンからすべてのクレームを取得するメソッド
     *
     * @param token JWTトークン
     * @return トークンに含まれるすべてのクレーム
     * <p>
     * 処理の流れ：
     * 1. JWTパーサーを構築します
     * 2. 秘密鍵を設定します
     * 3. トークンを解析してクレームを取得します
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 署名キーを生成するメソッド
     *
     * @return 署名に使用するキー
     * <p>
     * 処理の流れ：
     * 1. 秘密鍵の文字列をバイト配列に変換します
     * 2. HMAC-SHAアルゴリズム用のキーを生成します
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * トークンの有効期限を確認するメソッド
     *
     * @param token JWTトークン
     * @return トークンが期限切れの場合はtrue、有効な場合はfalse
     * <p>
     * 処理の流れ：
     * 1. トークンから有効期限を取得します
     * 2. 現在時刻と比較して、期限切れかどうかを判定します
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * トークンから有効期限を取得するメソッド
     *
     * @param token JWTトークン
     * @return トークンの有効期限
     * <p>
     * 処理の流れ：
     * 1. トークンからClaimsを取得します
     * 2. Claimsから有効期限（expiration）を取得して返します
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * ユーザー情報からトークンを生成するメソッド
     *
     * @param userDetails ユーザー情報
     * @param userId      ユーザーID
     * @return 生成されたJWTトークン
     * <p>
     * 処理の流れ：
     * 1. クレームのマップを作成します
     * 2. ユーザーIDをクレームに追加します
     * 3. トークンを生成して返します
     */
    public String generateToken(UserDetails userDetails, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * トークンを生成する内部メソッド
     *
     * @param claims  トークンに含めるクレーム
     * @param subject トークンの主体（ユーザー名）
     * @return 生成されたJWTトークン
     * <p>
     * 処理の流れ：
     * 1. JWTビルダーを作成します
     * 2. クレームを設定します
     * 3. 主体（ユーザー名）を設定します
     * 4. 発行時刻を設定します
     * 5. 有効期限を設定します
     * 6. 秘密鍵で署名します
     * 7. トークンを生成して返します
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * トークンの有効性を検証するメソッド
     *
     * @param token       JWTトークン
     * @param userDetails ユーザー情報
     * @return トークンが有効な場合はtrue、無効な場合はfalse
     * <p>
     * 処理の流れ：
     * 1. トークンからユーザー名を取得します
     * 2. 取得したユーザー名と引数のユーザー名が一致するか確認します
     * 3. トークンが期限切れでないことを確認します
     * 4. 両方の条件が満たされていればtrueを返します
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
