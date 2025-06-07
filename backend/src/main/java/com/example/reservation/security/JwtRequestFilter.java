package com.example.reservation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWTトークンを使用した認証フィルター
 * リクエストごとにJWTトークンを検証し、認証情報をセキュリティコンテキストに設定する
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    /**
     * ユーザー情報を取得するためのサービス
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * JWTトークンの生成と検証を行うユーティリティ
     */
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * リクエストごとに一度だけ実行されるフィルターメソッド
     * Authorization ヘッダーからJWTトークンを抽出し、検証を行う
     *
     * @param request     HTTPリクエスト
     * @param response    HTTPレスポンス
     * @param filterChain フィルターチェーン
     * @throws ServletException サーブレット例外
     * @throws IOException      入出力例外
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // リクエストヘッダーから「Authorization」の値を取得
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        // Authorization ヘッダーが存在し、「Bearer 」で始まる場合の処理
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // 「Bearer 」の部分を除去してトークン部分を取得（7文字分をスキップ）
            jwtToken = requestTokenHeader.substring(7);
            try {
                // トークンからユーザー名を抽出
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                // トークンの解析に失敗した場合はエラーログを出力
                logger.error("トークンの解析に失敗しました: " + e.getMessage());
            }
        } else {
            // Authorization ヘッダーが不正な形式の場合は警告ログを出力
            logger.warn("JWT トークンが Bearer で始まっていないか、トークンがありません");
        }

        // ユーザー名が取得でき、かつ現在のセキュリティコンテキストに認証情報がない場合
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // ユーザー名を使ってユーザー詳細を取得
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // トークンが有効かどうか検証
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                // 認証トークンを作成（パスワードはnull、権限はユーザー詳細から取得）
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 認証トークンに現在のリクエスト情報を詳細として設定
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // セキュリティコンテキストに認証情報を設定
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // フィルターチェーンの次のフィルターに処理を渡す
        filterChain.doFilter(request, response);
    }
}