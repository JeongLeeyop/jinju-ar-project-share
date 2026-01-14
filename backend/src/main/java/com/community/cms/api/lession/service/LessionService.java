package com.community.cms.api.lession.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.lession.dto.LessionDto;
import com.community.cms.api.lession.dto.LessionSearch;
import com.community.cms.api.lession.dto.mapper.LessionMapper;
import com.community.cms.api.lession.repository.LessionFileRepository;
import com.community.cms.api.lession.repository.LessionRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.User;
import com.community.cms.entity2.Lession;
import com.community.cms.fcm.service.PushNotificationService;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface LessionService {
    LessionDto.detail detail(SinghaUser authUser, String uid);
    List<LessionDto.list> list(SinghaUser authUser, LessionSearch search);
    Page<LessionDto.list> list(SinghaUser authUser, LessionSearch search, Pageable pageable);
    void add(SinghaUser authUser, LessionDto.add addDto );
    void update(SinghaUser authUser, Lession lession, LessionDto.update updateDto );
    void delete(SinghaUser authUser, Lession lession);
}

@Service
@AllArgsConstructor
class LessionServiceImpl implements LessionService {
    
    private final LessionRepository lessionRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    
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
    LessionFileRepository lessionFileRepository;

    @Override
    public LessionDto.detail detail(SinghaUser authUser, String uid) {
        User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
        Optional<Lession> optional = lessionRepository.findById(uid);
        // optional.orElseThrow(() -> new NotFoundException(NotFound.엔티티));
        LessionDto.detail dto = LessionMapper.INSTANCE.entityToDetail(optional.get());
        return dto;
    }

    @Override
    public List<LessionDto.list> list(SinghaUser authUser, LessionSearch search) {
        // Optional<User> user = userRepository.findById(authUser.getUser().getUid());
        return lessionRepository.findAll(search.search()).stream().map(entity -> {
            LessionDto.list dto = LessionMapper.INSTANCE.entityToList(entity);
            // if(user.isPresent()){
            //     dto.setMyWatchPercent(lessionRepository.findMyPercentage(entity.getUid(), user.get().getUid()));
            // }
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public Page<LessionDto.list> list(SinghaUser authUser, LessionSearch search, Pageable pageable) {
        // Optional<User> user = userRepository.findById(authUser.getUser().getUid());
        return lessionRepository.findAll(search.search(), pageable).map(entity -> {
            LessionDto.list dto = LessionMapper.INSTANCE.entityToList(entity);
            /* if(user.isPresent()){
                dto.setMyWatchPercent(lessionRepository.findMyPercentage(entity.getUid(), user.get().getUid()));
                if(dto.getMyWatchPercent() == null) dto.setMyWatchPercent(0);
            } */
            return dto;
        });
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, LessionDto.add addDto) {
        Lession entity = null;
        entity = LessionMapper.INSTANCE.addDtoToEntity(addDto);
        lessionRepository.save(entity);
    }
    
    @Transactional
    @Override
    public void update(SinghaUser authUser, Lession lession, LessionDto.update updateDto) {
        // 사용자 조회
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 채널 정보 조회
        com.community.cms.entity2.Channel channel = channelRepository.findByUid(lession.getChannelUid())
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        // 권한 체크: 커뮤니티 관리자만 강좌 수정 가능
        boolean isChannelAdmin = channel.getUserUid() != null && channel.getUserUid().equals(user.getUid());

        if (!isChannelAdmin) {
            throw new RuntimeException("강좌 수정 권한이 없습니다. 커뮤니티 관리자만 수정할 수 있습니다");
        }

        lessionFileRepository.deleteByLessionUid(updateDto.getUid());
        lession = LessionMapper.INSTANCE.updateDtoToEntity(updateDto, lession);
        lessionRepository.save(lession);
    }
    
    @Transactional
    @Override
    public void delete(SinghaUser authUser, Lession lession) {
        // 사용자 조회
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 채널 정보 조회
        com.community.cms.entity2.Channel channel = channelRepository.findByUid(lession.getChannelUid())
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        // 권한 체크: 커뮤니티 관리자만 강좌 삭제 가능
        boolean isChannelAdmin = channel.getUserUid() != null && channel.getUserUid().equals(user.getUid());

        if (!isChannelAdmin) {
            throw new RuntimeException("강좌 삭제 권한이 없습니다. 커뮤니티 관리자만 삭제할 수 있습니다");
        }

        lessionRepository.delete(lession);
    }
}
