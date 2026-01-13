package com.community.cms.api.channel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.community.cms.api.channel.dto.ChannelMemberPermissionDto;
import com.community.cms.api.channel.repository.ChannelMemberPermissionRepository;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.common.code.ChannelMemberPermissionType;
import com.community.cms.entity.ChannelMemberPermission;
import com.community.cms.entity2.Channel;
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
    private final ChannelRepository channelRepository;
    
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
     * 권한 검증 (권한 없으면 예외 발생)
     * 커뮤니티 관리자는 모든 권한을 가집니다.
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @param permissionType 필요한 권한 타입
     * @throws RuntimeException 권한이 없는 경우
     */
    @Transactional(readOnly = true)
    public void validatePermission(String userUid, String channelUid, ChannelMemberPermissionType permissionType) {
        // 커뮤니티 관리자인지 확인
        if (isChannelAdmin(userUid, channelUid)) {
            log.debug("✅ 커뮤니티 관리자로 권한 자동 승인: userUid={}, permissionType={}", userUid, permissionType);
            return;
        } 
        
        // 채널 멤버 조회
        ChannelMember member = channelMemberRepository.findByUserUidAndChannelUid(userUid, channelUid)
                .orElseThrow(() -> new RuntimeException("채널 멤버가 아닙니다"));
        
        // 추방된 회원인지 확인
        if (member.isBanned()) {
            throw new RuntimeException("추방된 회원은 이 기능을 사용할 수 없습니다");
        }
        
        // 권한 확인
        boolean hasPermission = permissionRepository.hasPermission(member.getIdx(), permissionType);
        if (!hasPermission) {
            throw new RuntimeException(permissionType.getDescription() + " 권한이 없습니다");
        }
        
        log.debug("✅ 권한 검증 통과: userUid={}, permissionType={}", userUid, permissionType);
    }
    
    /**
     * 권한 여부만 확인 (예외 발생 없음)
     * 커뮤니티 관리자는 모든 권한을 가집니다.
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @param permissionType 확인할 권한 타입
     * @return 권한 여부
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(String userUid, String channelUid, ChannelMemberPermissionType permissionType) {
        // 커뮤니티 관리자인지 확인
        if (isChannelAdmin(userUid, channelUid)) {
            return true;
        }
        
        // 채널 멤버 조회
        ChannelMember member = channelMemberRepository.findByUserUidAndChannelUid(userUid, channelUid)
                .orElse(null);
        
        if (member == null) {
            return false;
        }
        
        // 추방된 회원인지 확인
        if (member.isBanned()) {
            return false;
        }
        
        return permissionRepository.hasPermission(member.getIdx(), permissionType);
    }
    
    /**
     * 채널 관리자인지 확인
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @return 관리자 여부
     */
    @Transactional(readOnly = true)
    public boolean isChannelAdmin(String userUid, String channelUid) {
        if (userUid == null || channelUid == null) {
            return false;
        }
        
        // 채널 정보 조회
        Channel channel = channelRepository.findByUid(channelUid).orElse(null);
        if (channel == null) {
            return false;
        }
        
        // 채널 관리자(생성자) UID와 비교
        return userUid.equals(channel.getUserUid());
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
    
    /**
     * 현재 사용자의 모든 권한 조회 (채널 기준)
     * 커뮤니티 관리자는 모든 권한을 가집니다.
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @return 권한 목록 (permissionType -> hasPermission)
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getAllUserPermissions(String userUid, String channelUid) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        java.util.Map<String, Boolean> permissions = new java.util.HashMap<>();
        
        // 커뮤니티 관리자 여부 확인
        boolean isAdmin = isChannelAdmin(userUid, channelUid);
        result.put("isChannelAdmin", isAdmin);
        
        // 커뮤니티 관리자인 경우 모든 권한 true
        if (isAdmin) {
            for (ChannelMemberPermissionType type : ChannelMemberPermissionType.values()) {
                permissions.put(type.name(), true);
            }
            result.put("permissions", permissions);
            return result;
        }
        
        // 채널 멤버 조회
        ChannelMember member = channelMemberRepository.findByUserUidAndChannelUid(userUid, channelUid)
                .orElse(null);
        
        if (member == null) {
            // 채널 멤버가 아닌 경우 모든 권한 false
            for (ChannelMemberPermissionType type : ChannelMemberPermissionType.values()) {
                permissions.put(type.name(), false);
            }
            result.put("permissions", permissions);
            result.put("isMember", false);
            return result;
        }
        
        result.put("isMember", true);
        
        // 추방된 회원인 경우 모든 권한 false
        if (member.isBanned()) {
            for (ChannelMemberPermissionType type : ChannelMemberPermissionType.values()) {
                permissions.put(type.name(), false);
            }
            result.put("permissions", permissions);
            result.put("isBanned", true);
            return result;
        }
        
        result.put("isBanned", false);
        
        // 각 권한 타입별로 권한 확인
        for (ChannelMemberPermissionType type : ChannelMemberPermissionType.values()) {
            boolean hasPermission = permissionRepository.hasPermission(member.getIdx(), type);
            permissions.put(type.name(), hasPermission);
        }
        
        result.put("permissions", permissions);
        return result;
    }
}
