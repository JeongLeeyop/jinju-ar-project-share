package com.community.cms.api.level.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.channel.repository.ChannelImageRepository;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.level.dto.ChannelLevelDto;
import com.community.cms.api.level.dto.ChannelLevelSearch;
import com.community.cms.api.level.dto.mapper.ChannelLevelMapper;
import com.community.cms.api.level.repository.AuthorityRepository;
import com.community.cms.api.level.repository.ChannelLevelRepository;
import com.community.cms.api.level.repository.EventRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.exception.UserAccessDeniedException;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity2.Authority;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelLevel;
import com.community.cms.entity2.Event;
import com.community.cms.fcm.service.PushNotificationService;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface ChannelLevelService {
    ChannelLevelDto.detail detail(SinghaUser authUser, Integer idx);
    List<ChannelLevelDto.list> list(SinghaUser authUser, ChannelLevelSearch search);
    Page<ChannelLevelDto.list> list(SinghaUser authUser, ChannelLevelSearch search, Pageable pageable);
    List<Event> event(SinghaUser authUser);
    List<Authority> authority(SinghaUser authUser);
    void add(SinghaUser authUser, List<ChannelLevelDto.add> addDtoList );
    void update(SinghaUser authUser, ChannelLevel Channel, ChannelLevelDto.update updateDto );
    void delete(SinghaUser authUser, ChannelLevel Channel);
}

@Service
@AllArgsConstructor
class ChannelLevelServiceImpl implements ChannelLevelService {
    
private final ChannelLevelRepository channelLevelRepository;
private final UserRepository userRepository;
    
    @Autowired
    PushAlarmService pushAlarmService;
    
    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    PointHistoryService pointHistoryService;
    
    @Autowired
    UserFcmTokenRepository userFcmTokenRepository;
    
    @Autowired
    ChannelMemberRepository channelMemberRepository;

    @Autowired
    ChannelImageRepository channelImageRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Override
    public ChannelLevelDto.detail detail(SinghaUser authUser, Integer idx) {
        Optional<ChannelLevel> optional = channelLevelRepository.findById(idx);
        optional.orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
        ChannelLevelDto.detail dto = ChannelLevelMapper.INSTANCE.entityToDetail(optional.get());
        
        return dto;
    }


    @Override
    public List<ChannelLevelDto.list> list(SinghaUser authUser, ChannelLevelSearch search) {
        // search.setUserUid(authUser.getUser().getUid());
        return null;
        // return channelLevelRepository.findAll(search.search()).stream().map(entity -> ChannelLevelMapper.INSTANCE.entityToDetailMyFlag(entity, search)).collect(Collectors.toList());
    }
    
    @Override
    public Page<ChannelLevelDto.list> list(SinghaUser authUser, ChannelLevelSearch search, Pageable pageable) {
        return channelLevelRepository.findAll(search.search(), pageable).map(entity -> ChannelLevelMapper.INSTANCE.entityToList(entity));
    }
    
    @Override
    public List<Event> event(SinghaUser authUser) {
        return eventRepository.findAll();
    }
    
    @Override
    public List<Authority> authority(SinghaUser authUser) {
        return authorityRepository.findAll();
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, List<ChannelLevelDto.add> addDtoList) {
        if (!authUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_CREATOR"))) {
            throw new UserAccessDeniedException();
        }
        
        // TODO 한번에 입력하는 방법
        if(addDtoList.size() > 0) channelLevelRepository.deleteByChannelUid(addDtoList.get(0).getChannelUid());
        for(ChannelLevelDto.add dto : addDtoList) {
            ChannelLevel entity = null;
            entity = ChannelLevelMapper.INSTANCE.addDtoToEntity(dto);
            channelLevelRepository.save(entity);
        }
    }
    
    @Transactional
    @Override
    public void update(SinghaUser authUser, ChannelLevel entity, ChannelLevelDto.update updateDto) {
        if (!authUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_CREATOR"))) {
            throw new UserAccessDeniedException();
        }
        /* 
         * TODO
         * 수정할 채널의 소유자 uid와 현재 로그인한 사용자의 uid와 일치하지 않을 경우 예외
         */
        // channelImageRepository.deleteByChannelUid(entity.getIdx());
            // entity = ChannelLevelMapper.INSTANCE.updateDtoToEntity(updateDto, entity);
        // TODO updateDate추가
        // entity.setUpdateDate(LocalDateTime.now());
		// channelLevelRepository.save(entity);
    }
    
    @Override
    public void delete(SinghaUser authUser, ChannelLevel Channel) {
		channelLevelRepository.delete(Channel);
    }

    private void clearRelation(Channel entity) {
        entity.getImagesList().forEach(x -> x.setChannel(null));
    }
}
