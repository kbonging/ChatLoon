package com.nexus.core.user;

import com.nexus.core.security.custom.CustomUser;
import com.nexus.core.user.dto.UserDTO;
import com.nexus.core.user.dto.UserInfoDTO;
import com.nexus.core.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal CustomUser customUser){
        log.info("/api/users/info 호출 - customUser: {}", customUser);

        UserInfoDTO userInfo = userService.getUserInfo(customUser.getUser().getUserIdx());

        if(userInfo != null) return ResponseEntity.ok(userInfo);

        return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }


    @GetMapping("/search")
    public List<UserInfoDTO> searchUsers(@RequestParam("keyword") String keyword){
        log.info("/api/users/search -> searchUsers() 호출");
        List<UserInfoDTO> resultList = userService.searchUsers(keyword);
        log.info("resultList : {}", resultList.toString());

        return resultList;
    }

    @GetMapping("/check-id")
    public ResponseEntity<?> checkUserId(@RequestParam String userId){
        boolean available = userService.isUserIdAvailble(userId);

        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email){
        boolean available = userService.isEmailAvailable(email);

        return ResponseEntity.ok(Map.of("available", available));
    }

    /**
     * 회원가입
     *
     * - userDTO 받아 User 생성
     * - UserAuth 테이블에 ROLE_USER 기본 권한 추가
     * - 비밀번호는 암호화 후 저장
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.signup(userDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "회원가입 성공!"
            ));
        } catch (IllegalArgumentException e) {
            // 중복 아이디 / 이메일 등 유효성 실패 시
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
