package com.nexus.core.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        // JWT 쿠키 삭제
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);  // HTTPS라면 true
        cookie.setPath("/");
        cookie.setMaxAge(0);       // 즉시 만료
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }
}
