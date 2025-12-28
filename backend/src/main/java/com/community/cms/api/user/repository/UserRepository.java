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
     * 채널 멤버 중 특정 공간에 속하지 않은 사용자 조회 (본인 제외)
     * @param channelUid 채널 UID (domain)
     * @param spaceUid 공간 UID
     * @param adminUid 본인 UID (제외할 사용자)
     * @param search 검색어 (actualName)
     * @return 초대 가능한 사용자 목록
     */
    @Query(value = "SELECT DISTINCT u.* FROM user u " +
           "INNER JOIN channel_member cm ON u.uid = cm.user_uid " +
           "INNER JOIN channel c ON cm.channel_uid = c.uid " +
           "WHERE c.domain = :channelDomain " +
           "AND cm.approval_status = 1 " +
           "AND u.uid != :adminUid " +
           "AND u.uid NOT IN (" +
           "  SELECT sm.user_uid FROM space_member sm WHERE sm.space_uid = :spaceUid" +
           ") " +
           "AND (COALESCE(:search, '') = '' OR LOWER(u.actual_name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY u.actual_name", 
           nativeQuery = true)
    List<User> findChannelMembersNotInSpace(
            @Param("channelDomain") String channelDomain, 
            @Param("spaceUid") String spaceUid, 
            @Param("adminUid") String adminUid,
            @Param("search") String search);
}
