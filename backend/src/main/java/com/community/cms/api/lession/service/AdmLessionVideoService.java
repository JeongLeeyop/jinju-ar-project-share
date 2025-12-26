package com.community.cms.api.lession.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.lession.dto.LessionVideoDto;
import com.community.cms.api.lession.dto.LessionVideoSearch;
import com.community.cms.api.lession.dto.mapper.LessionVideoMapper;
import com.community.cms.api.lession.repository.LessionVideoFileRepository;
import com.community.cms.api.lession.repository.LessionVideoRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.User;
import com.community.cms.entity2.LessionVideo;
import com.community.cms.fcm.service.PushNotificationService;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface AdmLessionVideoService {
    LessionVideoDto.detail detail(SinghaUser authUser, Integer uid);
    List<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search);
    Page<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search, Pageable pageable);
    void add(SinghaUser authUser, LessionVideoDto.add addDto );
    void update(SinghaUser authUser, LessionVideo lession, LessionVideoDto.update updateDto );
    void delete(SinghaUser authUser, LessionVideo lession);
}

@Service
@AllArgsConstructor
class AdmLessionVideoServiceImpl implements AdmLessionVideoService {
    
    private final LessionVideoRepository lessionVideoRepository;
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
    LessionVideoFileRepository lessionVideoFileRepository;

    @Override
    public LessionVideoDto.detail detail(SinghaUser authUser, Integer uid) {
        User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
        Optional<LessionVideo> optional = lessionVideoRepository.findById(uid);
        // optional.orElseThrow(() -> new NotFoundException(NotFound.엔티티));
        LessionVideoDto.detail dto = LessionVideoMapper.INSTANCE.entityToDetail(optional.get());
        return dto;
    }

    @Override
    public List<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search) {
        return lessionVideoRepository.findAll(search.search()).stream().map(entity -> LessionVideoMapper.INSTANCE.entityToList(entity)).collect(Collectors.toList());
    }
    
    @Override
    public Page<LessionVideoDto.list> list(SinghaUser authUser, LessionVideoSearch search, Pageable pageable) {
        User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
        return lessionVideoRepository.findAll(search.search(), pageable).map(entity -> LessionVideoMapper.INSTANCE.entityToList(entity));
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, LessionVideoDto.add addDto) {
        LessionVideo entity = null;
        entity = LessionVideoMapper.INSTANCE.addDtoToEntity(addDto);
        entity.setViewOrder(0);
        lessionVideoRepository.save(entity);
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
