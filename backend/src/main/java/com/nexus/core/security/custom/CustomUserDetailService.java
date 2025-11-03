package com.nexus.core.security.custom;

import com.nexus.core.user.UserRepository;
import com.nexus.core.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("login - loadUserByUsername : {}", username);

        // ✅ JPA 메서드로 조회 (Optional 처리)
        User user = userRepository.findByUserIdWithAuthList(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        log.info("user : {}", user);

        // user -> CustomUser
        CustomUser customUser = new CustomUser(user);

        log.info("customUser : ");

        return customUser;
    }
}
