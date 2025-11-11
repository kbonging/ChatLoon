package com.nexus.core.user;

import com.nexus.core.user.dto.UserAuthDTO;
import com.nexus.core.user.dto.UserInfoDTO;
import com.nexus.core.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public UserInfoDTO getUserInfo(Long userIdx) {
        return userRepository.findByUserIdxWithAuthList(userIdx)
                .map(user -> {
                    UserInfoDTO dto = new UserInfoDTO();
                    dto.setUserIdx(user.getUserIdx());
                    dto.setUserId(user.getUserId());
                    dto.setNickname(user.getNickname());
                    dto.setEmail(user.getEmail());
                    dto.setProfileImg(user.getProfileImg());
                    dto.setEnabled(user.getIsEnabled());
                    dto.setCreatedAt(user.getCreatedAt());
                    dto.setUpdatedAt(user.getUpdatedAt());
                    dto.setAuthList(
                            user.getAuthList().stream()
                                    .map(auth -> new UserAuthDTO(auth.getUser().getUserIdx(), auth.getAuth()))
                                    .collect(Collectors.toList())
                    );
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userIdx=" + userIdx));
    }
}
