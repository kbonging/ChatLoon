package com.nexus.core.user;

import com.nexus.core.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자 아이디(userId)로 회원 정보를 조회하면서
     * 권한 목록(authList)을 함께 페치 조인(FETCH JOIN)으로 로딩
     *
     * - 로그인 시 UserDetailsService에서 사용
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.authList WHERE u.userId = :userId")
    Optional<User> findByUserIdWithAuthList(@Param("userId") String userId);

    /**
     * 회원 고유번호(userIdx)로 사용자 단일 조회
     *
     * - 회원 정보 조회 시 사용
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.authList WHERE u.userIdx = :userIdx")
    Optional<User> findByUserIdxWithAuthList(@Param("userIdx") Long userIdx);

    /**
     * 닉네임 또는 아이디로 검색
     *
     * - 아이디 OR 닉네임 검색 시 사용
     * */
    @Query("SELECT u FROM User u WHERE (u.nickname LIKE %:keyword% OR u.userId LIKE %:keyword%) AND u.userIdx != :currentUserIdx AND u.isEnabled = true")
    List<User> searchByKeyword(@Param("keyword") String keyword, @Param("currentUserIdx") Long currentUserIdx);

    /**
     * 아이디 중복 여부 확인
     *
     * - userId가 이미 DB에 존재하는지 체크
     * - true면 이미 존재하는 아이디
     */
    boolean existsByUserId(String userId);

    /**
     * 이메일 중복 여부 확인
     *
     * - email이 이미 DB에 존재하는지 체크
     * - true면 이미 사용 중인 이메일
     */
    boolean existsByEmail(String email);
}
