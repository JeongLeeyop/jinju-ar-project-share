package com.community.cms.api.channel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.community.cms.common.code.ChannelMemberPermissionType;
import com.community.cms.entity.ChannelMemberPermission;

/**
 * 채널 멤버 권한 Repository
 */
@Repository
public interface ChannelMemberPermissionRepository extends JpaRepository<ChannelMemberPermission, Long> {
    
    /**
     * 채널 멤버의 모든 권한 조회
     */
    List<ChannelMemberPermission> findByChannelMemberIdx(Integer channelMemberIdx);
    
    /**
     * 채널 멤버의 특정 권한 조회
     */
    Optional<ChannelMemberPermission> findByChannelMemberIdxAndPermissionType(
            Integer channelMemberIdx, 
            ChannelMemberPermissionType permissionType
    );
    
    /**
     * 채널 멤버의 권한 보유 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM ChannelMemberPermission p " +
           "WHERE p.channelMemberIdx = :channelMemberIdx " +
           "AND p.permissionType = :permissionType " +
           "AND p.hasPermission = true")
    boolean hasPermission(
            @Param("channelMemberIdx") Integer channelMemberIdx,
            @Param("permissionType") ChannelMemberPermissionType permissionType
    );
    
    /**
     * 채널의 특정 권한을 가진 멤버 수 조회
     */
    @Query("SELECT COUNT(p) FROM ChannelMemberPermission p " +
           "WHERE p.channelMemberIdx IN " +
           "(SELECT cm.idx FROM ChannelMember cm WHERE cm.channelUid = :channelUid) " +
           "AND p.permissionType = :permissionType " +
           "AND p.hasPermission = true")
    long countMembersWithPermission(
            @Param("channelUid") String channelUid,
            @Param("permissionType") ChannelMemberPermissionType permissionType
    );
    
    /**
     * 채널 멤버의 권한 삭제
     */
    void deleteByChannelMemberIdx(Integer channelMemberIdx);
}
