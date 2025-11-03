package com.nexus.core.security.jwt.filter;

import com.nexus.core.security.jwt.constants.JwtConstants;
import com.nexus.core.security.jwt.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtRequestFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * jwt 요청 필터
     * - request > headers > Authorization (JWT)
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(JwtConstants.TOKEN_HEADER);
        log.info("Authorization : {}", header);
        log.info("##doFilterInternal## - userId : {}", request.getParameter("userId"));
        log.info("##doFilterInternal## - userPw : {}", request.getParameter("userPw"));

        String jwt = null;

        // 쿠키에서 JWT 가져오기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    log.info("쿠키 JWT 사용");
                    break;
                }
            }
        }

        // JWT 검증 후 인증 처리
        if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("유효한 JWT 토큰입니다.");
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
