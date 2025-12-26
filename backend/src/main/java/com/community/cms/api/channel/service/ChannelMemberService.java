package com.community.cms.api.channel.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import com.community.cms.api.channel.dto.ChannelMemberDto;
import com.community.cms.api.channel.dto.ChannelMemberSearch;
import com.community.cms.api.channel.dto.mapper.ChannelMemberMapper;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.push_alarm.dto.PushAlarmDto;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.dto.UserDto;
import com.community.cms.api.user.dto.mapper.UserMapper;
import com.community.cms.entity.User;
import com.community.cms.entity2.ChannelMember;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface ChannelMemberService {
    Page<ChannelMemberDto.detail> list(SinghaUser authUser, ChannelMemberSearch search, Pageable pageable);
    ChannelMemberDto.count userCount(SinghaUser authUser, ChannelMemberSearch search);
    void approval(ChannelMember channelMember, SinghaUser authUser);
}

@Service
@AllArgsConstructor
class ChannelMemberServiceImpl implements ChannelMemberService {
    
    ChannelMemberRepository channelMemberRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private PushAlarmService pushAlarmService;
    
    @Override
    public Page<ChannelMemberDto.detail> list(SinghaUser authUser, ChannelMemberSearch search, Pageable pageable) {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        List<String> userList = allPrincipals.stream().filter(principal -> principal instanceof SinghaUser).map(principal -> {
        SinghaUser principalMap = (SinghaUser) principal;
            return principalMap.getUser().getUid();
        }).collect(Collectors.toList());
        if (search.getIsOnline()) search.setUserUidList(userList.toArray(new String[userList.size()]));
        Page<ChannelMemberDto.detail> list = channelMemberRepository.findAll(search.search(), pageable).map(entity -> ChannelMemberMapper.INSTANCE.entityToDetailDto(entity, userList)); 
        return list;
    }

    public ChannelMemberDto.count userCount(SinghaUser authUser, ChannelMemberSearch search) {
            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            List<String> userList = allPrincipals.stream().filter(principal -> principal instanceof SinghaUser).map(principal -> {
                SinghaUser principalMap = (SinghaUser) principal;
                    return principalMap.getUser().getUid();
                }).collect(Collectors.toList());
            ChannelMemberDto.count dto = new ChannelMemberDto.count();
            search.setIsOnline(false);
            dto.setOnlineCount(channelMemberRepository.countByChannelUidAndUserUidInAndApprovalStatus(search.getChannelUid(), userList, true));

            search.setIsHolding(false);
            dto.setTotalCount(channelMemberRepository.count(search.search()));
            
            search.setIsHolding(true);
            dto.setHoldingCount(channelMemberRepository.count(search.search()));
            return dto;
    }

    public void approval(ChannelMember channelMember, SinghaUser authUser) {
        channelMember.setApprovalStatus(true);
        channelMemberRepository.save(channelMember);

        // 사용자에게
        PushAlarmDto.Add pushAlarmDto = new PushAlarmDto.Add();
        pushAlarmDto.setUserUid(channelMember.getUserUid());
        pushAlarmDto.setTitle("커뮤니티 가입 완료");
        pushAlarmDto.setContent("[" + channelMember.getChannel().getName() + "] 커뮤니티에 가입 되셨습니다.");
        pushAlarmDto.setLink("/channel/" + channelMember.getChannel().getDomain());
        pushAlarmDto.setUserUidList(null);
        pushAlarmService.add(pushAlarmDto);
    }
}
