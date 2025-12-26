package com.community.cms.api.user.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity.User;
import com.community.cms.oauth.soical.SocialType;

@Repository
public interface UserRepository extends JpaRepository<User, String>, QuerydslPredicateExecutor<User>{
	Optional<User> findByUserId(String userId);
	Optional<User> findByEmail(String email);

	Page<User> findAll(Predicate search, Pageable pageable);

	Optional<User> findByUid(String uid);

    Optional<User> findByProviderAndProviderId(SocialType provider, String providerId);

    List<User> findByRolesRoleCode(String roleUid);

    Optional<User> findByUserIdAndUserPassword(String username, String password);

    List<User> findAllByBirth(LocalDate birth);

    /**
     * 채널 멤버 중 특정 공간에 속하지 않은 사용자 조회
     * @param channelUid 채널 UID
     * @param spaceUid 공간 UID
     * @param search 검색어 (actualName)
     * @return 초대 가능한 사용자 목록
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN ChannelMember cm ON u.uid = cm.userUid " +
           "WHERE cm.channelUid = :channelUid " +
           "AND cm.approvalStatus = true " +
           "AND u.uid NOT IN (" +
           "  SELECT sm.user.uid FROM SpaceMember sm WHERE sm.space.uid = :spaceUid" +
           ") " +
           "AND (:search IS NULL OR :search = '' OR LOWER(u.actualName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY u.actualName")
    List<User> findChannelMembersNotInSpace(
            @Param("channelUid") String channelUid, 
            @Param("spaceUid") String spaceUid, 
            @Param("search") String search);
}
