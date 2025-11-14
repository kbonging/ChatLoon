package com.nexus.core.user;

import com.nexus.core.user.dto.UserAuthDTO;
import com.nexus.core.user.dto.UserDTO;
import com.nexus.core.user.dto.UserInfoDTO;
import com.nexus.core.user.entity.User;
import com.nexus.core.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userIdx=" + userIdx));
    }

    @Override
    public List<UserInfoDTO> searchUsers(String keyword) {
        List<User> users = userRepository.searchByKeyword(keyword);

        return users.stream()
                .map(u -> UserMapper.toDTO(u))
                .collect(Collectors.toList());

    }
}
