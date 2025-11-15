package com.nexus.core.user;

import com.nexus.core.security.custom.CustomUser;
import com.nexus.core.user.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

}
