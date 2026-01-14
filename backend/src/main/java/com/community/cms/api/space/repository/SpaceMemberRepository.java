package com.community.cms.api.space.repository;

import com.community.cms.entity.SpaceMember;
import com.community.cms.entity.SpaceMember.SpaceMemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SpaceMember Repository
 */
@Repository
public interface SpaceMemberRepository extends JpaRepository<SpaceMember, SpaceMemberId> {

    /**
     * 공간 멤버 여부 확인
     */
    boolean existsBySpaceUidAndUserUid(String spaceUid, String userUid);

    /**
     * 공간 멤버 조회
     */
    Optional<SpaceMember> findBySpaceUidAndUserUid(String spaceUid, String userUid);

    /**
     * 공간별 멤버 목록 조회
     */
    List<SpaceMember> findBySpaceUidOrderByJoinedAtDesc(String spaceUid);

    /**
     * 사용자별 가입 공간 목록 조회
     */
    List<SpaceMember> findByUserUidOrderByJoinedAtDesc(String userUid);
    
    /**
     * 사용자별 가입 공간 목록 조회
     */
    List<SpaceMember> findByUserUid(String userUid);

    /**
     * 공간별 멤버 수 조회
     */
    long countBySpaceUid(String spaceUid);

    /**
     * 공간 멤버 삭제
     */
    void deleteBySpaceUidAndUserUid(String spaceUid, String userUid);

    /**
     * 공간별 멤버 조회
     */
    List<SpaceMember> findBySpaceUid(String spaceUid);
    
    /**
     * 공간별 멤버 조회 (페이지네이션)
     */
    Page<SpaceMember> findBySpaceUid(String spaceUid, Pageable pageable);
}
