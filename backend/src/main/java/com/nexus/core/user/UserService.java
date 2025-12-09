package com.nexus.core.user;

import com.nexus.core.user.dto.UserDTO;
import com.nexus.core.user.dto.UserInfoDTO;
import com.nexus.core.user.entity.User;

import java.util.List;

public interface UserService {

    /**
     * 회원 정보 조회 (PK 기준)
     * - 추후 다른 프로필 테이블과의 조인을 고려해 확장 예정
     *
     * @param userIdx 회원 고유번호 (PK)
     * @return 회원 엔티티(Optional)
     */
    UserInfoDTO getUserInfo(Long userIdx);

    /**
     * 키워드를 기반으로 회원 목록을 조회
     * 현재 로그인한 사용자 정보 제외
     * 검색 기능에 사용중
     *
     * @param keyword 검색 키워드 (이름, 아이디 등)
     * @return 조건에 부합하는 회원 목록
     */
    public List<UserInfoDTO> searchUsers(String keyword, Long currentUserIdx);

    /**
     * 아이디 사용 가능 여부 확인
     *
     * - 회원가입 시 중복된 아이디(userId)가 존재하는지 검사
     * - true → 사용 가능한 아이디
     * - false → 이미 존재하는 아이디
     *
     * @param userId 중복 여부를 확인할 사용자 아이디
     * @return 사용 가능 여부
     */
    boolean isUserIdAvailble(String userId);

    /**
     * 이메일 사용 가능 여부 확인
     *
     * - 회원가입 시 이메일 중복을 방지하기 위한 검사
     * - true → 사용 가능한 이메일
     * - false → 이미 등록된 이메일
     *
     * @param email 중복 여부를 확인할 이메일 주소
     * @return 사용 가능 여부
     */
    boolean isEmailAvailable(String email);

    /**
     * 회원가입 처리
     *
     * - 아이디, 이메일 중복 여부 확인 후 저장
     * - 비밀번호는 필요한 경우 암호화 후 저장
     *
     * @param
     * @return 가입 성공 시 User 엔티티
     */
    User signup(UserDTO userDTO);
}
