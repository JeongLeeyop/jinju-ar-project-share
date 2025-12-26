package com.community.cms.api.channel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.community.cms.api.channel.dto.ChannelMemberPermissionDto;
import com.community.cms.api.channel.repository.ChannelMemberPermissionRepository;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.common.code.ChannelMemberPermissionType;
import com.community.cms.entity.ChannelMemberPermission;
import com.community.cms.entity2.ChannelMember;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 채널 멤버 권한 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelMemberPermissionService {
    
    private final ChannelMemberPermissionRepository permissionRepository;
    private final ChannelMemberRepository channelMemberRepository;
    
    /**
     * 멤버의 모든 권한 조회
     */
    @Transactional(readOnly = true)
    public ChannelMemberPermissionDto.ListResponse getMemberPermissions(Integer channelMemberIdx) {
        ChannelMember member = channelMemberRepository.findById(channelMemberIdx)
                .orElseThrow(() -> new RuntimeException("채널 멤버를 찾을 수 없습니다"));
        
        List<ChannelMemberPermission> permissions = permissionRepository.findByChannelMemberIdx(channelMemberIdx);
        
        List<ChannelMemberPermissionDto.Detail> permissionDetails = permissions.stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
        
        return ChannelMemberPermissionDto.ListResponse.builder()
                .channelMemberIdx(channelMemberIdx)
                .userUid(member.getUserUid())
                .permissions(permissionDetails)
                .build();
    }
    
    /**
     * 권한 생성 또는 업데이트
     */
    @Transactional
    public ChannelMemberPermissionDto.Detail createOrUpdatePermission(
            ChannelMemberPermissionDto.CreateRequest request,
            String adminUid) {
        
        // 멤버 존재 확인
        ChannelMember member = channelMemberRepository.findById(request.getChannelMemberIdx())
                .orElseThrow(() -> new RuntimeException("채널 멤버를 찾을 수 없습니다"));
        
        // 기존 권한 조회
        ChannelMemberPermission permission = permissionRepository
                .findByChannelMemberIdxAndPermissionType(
                        request.getChannelMemberIdx(),
                        request.getPermissionType()
                )
                .orElse(null);
        
        if (permission == null) {
            // 새로운 권한 생성
            permission = ChannelMemberPermission.builder()
                    .channelMemberIdx(request.getChannelMemberIdx())
                    .permissionType(request.getPermissionType())
                    .hasPermission(request.getHasPermission())
                    .grantedBy(adminUid)
                    .build();
        } else {
            // 기존 권한 업데이트
            permission.setHasPermission(request.getHasPermission());
            permission.setGrantedBy(adminUid);
        }
        
        ChannelMemberPermission saved = permissionRepository.save(permission);
        
        log.info("Permission {} for member {} in channel {}: hasPermission={}",
                request.getPermissionType(),
                request.getChannelMemberIdx(),
                member.getChannelUid(),
                request.getHasPermission());
        
        return toDetailDto(saved);
    }
    
    /**
     * 멤버 권한 일괄 설정
     */
    @Transactional
    public ChannelMemberPermissionDto.ListResponse bulkUpdatePermissions(
            ChannelMemberPermissionDto.BulkUpdateRequest request,
            String adminUid) {
        
        // 멤버 존재 확인
        ChannelMember member = channelMemberRepository.findById(request.getChannelMemberIdx())
                .orElseThrow(() -> new RuntimeException("채널 멤버를 찾을 수 없습니다"));
        
        // 각 권한 처리
        for (ChannelMemberPermissionDto.PermissionItem item : request.getPermissions()) {
            ChannelMemberPermissionDto.CreateRequest createRequest = 
                    new ChannelMemberPermissionDto.CreateRequest(
                            request.getChannelMemberIdx(),
                            item.getPermissionType(),
                            item.getHasPermission()
                    );
            createOrUpdatePermission(createRequest, adminUid);
        }
        
        log.info("Bulk updated {} permissions for member {} in channel {}",
                request.getPermissions().size(),
                request.getChannelMemberIdx(),
                member.getChannelUid());
        
        return getMemberPermissions(request.getChannelMemberIdx());
    }
    
    /**
     * 권한 확인
     */
    @Transactional(readOnly = true)
    public ChannelMemberPermissionDto.CheckResponse checkPermission(
            Integer channelMemberIdx,
            ChannelMemberPermissionType permissionType) {
        
        boolean hasPermission = permissionRepository.hasPermission(channelMemberIdx, permissionType);
        
        return ChannelMemberPermissionDto.CheckResponse.builder()
                .permissionType(permissionType)
                .hasPermission(hasPermission)
                .build();
    }
    
    /**
     * 사용자 UID로 권한 확인
     */
    @Transactional(readOnly = true)
    public ChannelMemberPermissionDto.CheckResponse checkPermissionByUserUid(
            String userUid,
            String channelUid,
            ChannelMemberPermissionType permissionType) {
        
        ChannelMember member = channelMemberRepository.findByUserUidAndChannelUid(userUid, channelUid)
                .orElseThrow(() -> new RuntimeException("채널 멤버를 찾을 수 없습니다"));
        
        return checkPermission(member.getIdx(), permissionType);
    }
    
    /**
     * 권한 삭제
     */
    @Transactional
    public void deletePermission(Long permissionId) {
        ChannelMemberPermission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("권한을 찾을 수 없습니다"));
        
        permissionRepository.delete(permission);
        
        log.info("Deleted permission {} for member {}",
                permission.getPermissionType(),
                permission.getChannelMemberIdx());
    }
    
    /**
     * 멤버의 모든 권한 삭제
     */
    @Transactional
    public void deleteAllMemberPermissions(Integer channelMemberIdx) {
        permissionRepository.deleteByChannelMemberIdx(channelMemberIdx);
        
        log.info("Deleted all permissions for member {}", channelMemberIdx);
    }
    
    /**
     * 모든 권한 타입 조회
     */
    public List<ChannelMemberPermissionType> getAllPermissionTypes() {
        return List.of(ChannelMemberPermissionType.values());
    }
    
    /**
     * Entity를 Detail DTO로 변환
     */
    private ChannelMemberPermissionDto.Detail toDetailDto(ChannelMemberPermission entity) {
        return ChannelMemberPermissionDto.Detail.builder()
                .id(entity.getId())
                .channelMemberIdx(entity.getChannelMemberIdx())
                .permissionType(entity.getPermissionType())
                .permissionTypeName(entity.getPermissionType().getDescription())
                .hasPermission(entity.isHasPermission())
                .grantedBy(entity.getGrantedBy())
                .grantedAt(entity.getGrantedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
