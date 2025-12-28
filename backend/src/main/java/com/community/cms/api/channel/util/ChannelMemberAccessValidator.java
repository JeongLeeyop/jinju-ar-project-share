package com.community.cms.api.channel.util;

import org.springframework.stereotype.Component;

import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.entity2.ChannelMember;

import lombok.RequiredArgsConstructor;

/**
 * 채널 멤버 접근 권한 검증 유틸리티
 */
@Component
@RequiredArgsConstructor
public class ChannelMemberAccessValidator {
    
    private final ChannelMemberRepository channelMemberRepository;
    
    /**
     * 사용자가 채널에 접근 가능한지 확인
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @throws RuntimeException 접근 불가능한 경우
     */
    public void validateChannelAccess(String userUid, String channelUid) {
        ChannelMember member = channelMemberRepository
                .findByUserUidAndChannelUid(userUid, channelUid)
                .orElse(null);
        
        if (member != null && member.isBanned()) {
            throw new RuntimeException("추방된 회원은 해당 커뮤니티에 접근할 수 없습니다.");
        }
    }
    
    /**
     * 사용자가 채널에서 추방되었는지 확인
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @return 추방 여부
     */
    public boolean isBannedFromChannel(String userUid, String channelUid) {
        return channelMemberRepository
                .findByUserUidAndChannelUid(userUid, channelUid)
                .map(ChannelMember::isBanned)
                .orElse(false);
    }
}
