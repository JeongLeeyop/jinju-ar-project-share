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
import com.community.cms.api.point.dto.PointHistoryDto;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.point.service.PointService;
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
import lombok.extern.slf4j.Slf4j;

public interface LessionVideoService {
    LessionVideoDto.detail detail(SinghaUser authUser, Integer idx);

    List<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search);

    Page<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search, Pageable pageable);

    void add(SinghaUser authUser, LessionVideoDto.add addDto);

    void saveWatchHistory(SinghaUser authUser, LessionVideoDto.watchHistory dto);

    void update(SinghaUser authUser, LessionVideo lession, LessionVideoDto.update updateDto);

    void delete(SinghaUser authUser, LessionVideo lession);
}

@Slf4j
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
    PointService pointService;

    @Autowired
    UserFcmTokenRepository userFcmTokenRepository;

    @Autowired
    LessionVideoFileRepository lessionVideoFileRepository;

    @Autowired
    LessionVideoWatchHistoryRepository lessionVideoWatchHistoryRepository;

    @Override
    public LessionVideoDto.detail detail(SinghaUser authUser, Integer idx) {
        Optional<LessionVideo> optional = lessionVideoRepository.findById(idx);
        LessionVideoDto.detail dto = LessionVideoMapper.INSTANCE.entityToDetail(optional.get());

        // 로그인한 사용자의 시청 기록 조회
        if (authUser != null && authUser.getUser() != null) {
            User user = userRepository.findById(authUser.getUser().getUid())
                    .orElseThrow(() -> new NotFoundException(NotFound.USER));
            
            Optional<LessionVideoWatchHistory> watchHistory = lessionVideoWatchHistoryRepository
                    .findByVideoIdxAndUserUid(idx, user.getUid());
            
            if (watchHistory.isPresent()) {
                dto.setMyWatchPercent(watchHistory.get().getPercent());
                dto.setLastWatchSecond(watchHistory.get().getLastWatchSecond());
                dto.setLastWatchUpdate(watchHistory.get().getUpdateDate());
            }
        }
        
        return dto;
    }

    @Override
    public List<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search) {
        Optional<User> user = userRepository.findById(authUser.getUser().getUid());
        return lessionVideoRepository.findAll(search.search()).stream().map(entity -> {
            LessionVideoDto.list dto = LessionVideoMapper.INSTANCE.entityToList(entity);
            if (user.isPresent()) {
                Optional<LessionVideoWatchHistory> watchHistory = lessionVideoWatchHistoryRepository
                        .findByVideoIdxAndUserUid(entity.getIdx(), user.get().getUid());
                if (watchHistory.isPresent()) {
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
        return lessionVideoRepository.findAll(search.search(), pageable).map(entity -> {
            LessionVideoDto.list dto = LessionVideoMapper.INSTANCE.entityToList(entity);
            
            // 로그인한 사용자의 시청 기록 조회
            if (authUser != null && authUser.getUser() != null) {
                Optional<User> user = userRepository.findById(authUser.getUser().getUid());
                if (user.isPresent()) {
                    Optional<LessionVideoWatchHistory> watchHistory = lessionVideoWatchHistoryRepository
                            .findByVideoIdxAndUserUid(entity.getIdx(), user.get().getUid());
                    if (watchHistory.isPresent()) {
                        dto.setMyWatchPercent(watchHistory.get().getPercent());
                        dto.setLastWatchSecond(watchHistory.get().getLastWatchSecond());
                        dto.setLastWatchUpdate(watchHistory.get().getUpdateDate());
                    }
                }
            }
            
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
        // Check if authUser is null or authUser.getUser() is null
        if (authUser == null || authUser.getUser() == null) {
            throw new NotFoundException(NotFound.USER);
        }
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));
        
        log.info("saveWatchHistory called: userUid={}, videoIdx={}, percent={}, channelUid={}", 
                user.getUid(), dto.getVideoIdx(), dto.getPercent(), dto.getChannelUid());
        
        Optional<LessionVideoWatchHistory> optional = lessionVideoWatchHistoryRepository
                .findByVideoIdxAndUserUid(dto.getVideoIdx(), user.getUid());
        
        // 이전에 100% 완료한 적이 있는지 체크 (중복 적립 방지)
        boolean wasCompleted = optional.isPresent() && optional.get().getPercent() == 100;
        
        log.info("Previous watch history: exists={}, wasCompleted={}", optional.isPresent(), wasCompleted);
        
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

        log.info("Checking point eligibility: percent={}, wasCompleted={}, shouldGivePoint={}", 
                dto.getPercent(), wasCompleted, (dto.getPercent() == 100 && !wasCompleted));

        if (dto.getPercent() == 100 && !wasCompleted) {
            log.info("100% completion detected - starting point award process");
            
            // 이벤트 기록
            eventHistoryService.add(new EventHistoryDto.add(EventType.VIDEO.toString(), user.getUid(),
                    dto.getChannelUid(), entity.getIdx(), null, null, null));
            
            // 포인트 적립 (강의 수강 완료) - 중복 적립 방지, 일일 제한 체크
            try {
                // 강의 정보 조회
                Optional<LessionVideo> videoOptional = lessionVideoRepository.findById(dto.getVideoIdx());
                if (videoOptional.isPresent()) {
                    LessionVideo video = videoOptional.get();
                    
                    log.info("Attempting to add point for COURSE_COMPLETE: userUid={}, channelUid={}, videoIdx={}, videoTitle={}", 
                            user.getUid(), dto.getChannelUid(), video.getIdx(), video.getTitle());
                    
                    PointHistoryDto result = pointService.addPointForEventWithValidation(
                            user.getUid(),
                            dto.getChannelUid(),
                            "COURSE_COMPLETE",
                            "강의 수강 완료: " + video.getTitle(),
                            String.valueOf(video.getIdx()), // 강의 ID를 참조로 사용
                            0,  // 강의는 글자수 체크 불필요
                            null  // 본인 컨텐츠 체크 불필요
                    );
                    
                    if (result != null) {
                        log.info("Point added for COURSE_COMPLETE: userUid={}, amount={}", user.getUid(), result.getPointAmount());
                    } else {
                        log.info("Point NOT added for COURSE_COMPLETE (validation failed): userUid={}", user.getUid());
                    }
                } else {
                    log.warn("Video not found for videoIdx={}", dto.getVideoIdx());
                }
            } catch (Exception e) {
                log.error("Failed to add point for course completion: {}", e.getMessage(), e);
            }
        } else {
            log.info("Skipping point award: percent={}, wasCompleted={}", dto.getPercent(), wasCompleted);
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
