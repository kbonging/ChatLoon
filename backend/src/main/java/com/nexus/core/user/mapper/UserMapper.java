package com.nexus.core.user.mapper;

import com.nexus.core.user.dto.UserAuthDTO;
import com.nexus.core.user.dto.UserDTO;
import com.nexus.core.user.dto.UserInfoDTO;
import com.nexus.core.user.entity.User;


public class UserMapper {

    public static UserInfoDTO toDTO(User user) {
        if (user == null) return null;

        UserInfoDTO dto = new UserInfoDTO();
        dto.setUserIdx(user.getUserIdx());
        dto.setUserId(user.getUserId());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setProfileImg(user.getProfileImg());
        dto.setEnabled(user.getIsEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        if (user.getAuthList() != null) {
            dto.setAuthList(
                    user.getAuthList().stream()
                            .map(auth -> new UserAuthDTO(auth.getUser().getUserIdx(), auth.getAuth()))
                            .toList()
            );
        }

        return dto;
    }

    public static User toEntity(UserDTO dto, String encodePassword){
        if(dto==null) return null;

        return User.builder()
                .userId(dto.getUserId())
                .userPw(encodePassword)
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .profileImg("/images/profile/default.png")
                .build();
    }
}

