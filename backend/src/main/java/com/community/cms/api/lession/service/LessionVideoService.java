package com.community.cms.api.lession.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.api.event.service.EventHistoryService;
import com.community.cms.api.lession.dto.LessionVideoDto;
import com.community.cms.api.lession.dto.LessionVideoSearch;
import com.community.cms.api.lession.dto.mapper.LessionVideoMapper;
import com.community.cms.api.lession.dto.mapper.LessionVideoWatchHistoryMapper;
import com.community.cms.api.lession.repository.LessionVideoFileRepository;
import com.community.cms.api.lession.repository.LessionVideoRepository;
import com.community.cms.api.lession.repository.LessionVideoWatchHistoryRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.User;
import com.community.cms.entity2.EventType;
import com.community.cms.entity2.LessionVideo;
import com.community.cms.entity2.LessionVideoWatchHistory;
import com.community.cms.fcm.service.PushNotificationService;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface LessionVideoService {
    LessionVideoDto.detail detail(SinghaUser authUser, Integer idx);
    List<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search);
    Page<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search, Pageable pageable);
    void add(SinghaUser authUser, LessionVideoDto.add addDto );
    void saveWatchHistory(SinghaUser authUser, LessionVideoDto.watchHistory dto );
    void update(SinghaUser authUser, LessionVideo lession, LessionVideoDto.update updateDto );
    void delete(SinghaUser authUser, LessionVideo lession);
}

@Service
@AllArgsConstructor
class LessionVideoServiceImpl implements LessionVideoService {
    
    private final LessionVideoRepository lessionVideoRepository;
    private final UserRepository userRepository;
    
    @Autowired
    PushAlarmService pushAlarmService;
    
    @Autowired
    PushNotificationService pushNotificationService;
    
    @Autowired
    EventHistoryService eventHistoryService;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    PointHistoryService pointHistoryService;
    
    @Autowired
    UserFcmTokenRepository userFcmTokenRepository;

    @Autowired
    LessionVideoFileRepository lessionVideoFileRepository;
    
    @Autowired
    LessionVideoWatchHistoryRepository lessionVideoWatchHistoryRepository;

    @Override
    public LessionVideoDto.detail detail(SinghaUser authUser, Integer idx) {
        // User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
        Optional<LessionVideo> optional = lessionVideoRepository.findById(idx);
        LessionVideoDto.detail dto = LessionVideoMapper.INSTANCE.entityToDetail(optional.get());
        
        Optional<LessionVideoWatchHistory> watchHistory = lessionVideoWatchHistoryRepository.findByVideoIdxAndUserUid(idx, "f6743300-3921-454e-b0ae-92b90314419f");
        // Optional<LessionVideoWatchHistory> watchHistory = lessionVideoWatchHistoryRepository.findByVideoIdxAndUserUid(idx, user.getUid());
        if(watchHistory.isPresent()) {
            dto.setMyWatchPercent(watchHistory.get().getPercent());
            dto.setLastWatchSecond(watchHistory.get().getLastWatchSecond());
        }
        return dto;
    }

    @Override
    public List<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search) {
        Optional<User> user = userRepository.findById(authUser.getUser().getUid());
        return lessionVideoRepository.findAll(search.search()).stream().map(entity -> {
            LessionVideoDto.list dto = LessionVideoMapper.INSTANCE.entityToList(entity);
               if(user.isPresent()){
                    Optional<LessionVideoWatchHistory> watchHistory = lessionVideoWatchHistoryRepository.findByVideoIdxAndUserUid(entity.getIdx(), user.get().getUid());
                    if(watchHistory.isPresent()) {
                        dto.setMyWatchPercent(watchHistory.get().getPercent());
                        dto.setLastWatchSecond(watchHistory.get().getLastWatchSecond());
                        dto.setLastWatchUpdate(watchHistory.get().getUpdateDate());
                    }
}
                return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public Page<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search, Pageable pageable) {
            // Optional<User> user = userRepository.findById(authUser.getUser().getUid());
        return lessionVideoRepository.findAll(search.search(), pageable).map(entity -> {
            LessionVideoDto.list dto = LessionVideoMapper.INSTANCE.entityToList(entity);
            /* if(user.isPresent()){
                Optional<LessionVideoWatchHistory> watchHistory = lessionVideoWatchHistoryRepository.findByVideoIdxAndUserUid(entity.getIdx(), user.get().getUid());
                if(watchHistory.isPresent()) {
                    dto.setMyWatchPercent(watchHistory.get().getPercent());
                    dto.setLastWatchSecond(watchHistory.get().getLastWatchSecond());
                    dto.setLastWatchUpdate(watchHistory.get().getUpdateDate());
                }
            } */
                return dto;
            });
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, LessionVideoDto.add addDto) {
        LessionVideo entity = null;
        entity = LessionVideoMapper.INSTANCE.addDtoToEntity(addDto);
        lessionVideoRepository.save(entity);
    }
    
    @Transactional
    @Override
    public void saveWatchHistory(SinghaUser authUser, LessionVideoDto.watchHistory dto) {
        User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
        Optional<LessionVideoWatchHistory> optional = lessionVideoWatchHistoryRepository.findByVideoIdxAndUserUid(dto.getVideoIdx(), user.getUid());
        LessionVideoWatchHistory entity;
        if (optional.isPresent()) {
            entity = optional.get();
            if (entity.getPercent() >= dto.getPercent()) {
                dto.setPercent(entity.getPercent()); // 퍼센트가 더 작을경우 업데이트 하지 않음
            }
            LessionVideoWatchHistoryMapper.INSTANCE.updateDtoToEntity(dto, entity);
        } else {
            entity = LessionVideoWatchHistoryMapper.INSTANCE.dtoToEntity(dto);
        }
        entity.setUserUid(user.getUid());
        entity.setUpdateDate(LocalDateTime.now());
        lessionVideoWatchHistoryRepository.save(entity);

        if(dto.getPercent() == 100) {
			eventHistoryService.add(new EventHistoryDto.add(EventType.VIDEO.toString(), user.getUid(), dto.getChannelUid(), entity.getIdx(), null, null, null));
        }
    }
    
    @Transactional
    @Override
    public void update(SinghaUser authUser, LessionVideo lession, LessionVideoDto.update updateDto) {
        lessionVideoFileRepository.deleteByvideoIdx(updateDto.getIdx());
        lession = LessionVideoMapper.INSTANCE.updateDtoToEntity(updateDto, lession);
		lessionVideoRepository.save(lession);
    }
    
    @Override
    public void delete(SinghaUser authUser, LessionVideo lession) {
		lessionVideoRepository.delete(lession);
    }
}
