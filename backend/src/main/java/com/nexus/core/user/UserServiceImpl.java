package com.nexus.core.user;

import com.nexus.core.user.dto.UserAuthDTO;
import com.nexus.core.user.dto.UserDTO;
import com.nexus.core.user.dto.UserInfoDTO;
import com.nexus.core.user.entity.User;
import com.nexus.core.user.entity.UserAuth;
import com.nexus.core.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserInfoDTO getUserInfo(Long userIdx) {
        return userRepository.findByUserIdxWithAuthList(userIdx)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userIdx=" + userIdx));
    }

    @Override
    public List<UserInfoDTO> searchUsers(String keyword, Long currentUserIdx) {
        List<User> users = userRepository.searchByKeyword(keyword, currentUserIdx);

        return users.stream()
                .map(u -> UserMapper.toDTO(u))
                .collect(Collectors.toList());

    }

    @Override
    public boolean isUserIdAvailble(String userId) {
        return !userRepository.existsByUserId(userId);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    public User signup(UserDTO userDTO) {
        // 1. 중복 체크
        if(!isUserIdAvailble(userDTO.getUserId())){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if(!isEmailAvailable(userDTO.getEmail())){
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodePw = passwordEncoder.encode(userDTO.getUserPw());

        // 3. User 엔티티 생성
        User user = UserMapper.toEntity(userDTO, encodePw);
        
        // 4. 기본 권한 추가
        UserAuth roleUser = new UserAuth();
        roleUser.setAuth("ROLE_USER");
        roleUser.setUser(user);
        
        user.setAuthList(List.of(roleUser));

        // 5. DB저장
        User resultUser = userRepository.save(user);
        log.info("회원가입 User정보 : {}", resultUser);
        return resultUser;
    }
}
