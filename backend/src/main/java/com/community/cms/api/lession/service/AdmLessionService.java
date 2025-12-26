package com.community.cms.api.lession.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.lession.dto.LessionDto;
import com.community.cms.api.lession.dto.LessionSearch;
import com.community.cms.api.lession.dto.mapper.AdmLessionMapper;
import com.community.cms.api.lession.dto.mapper.LessionMapper;
import com.community.cms.api.lession.repository.AdmLessionRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity2.Lession;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface AdmLessionService {
    List<LessionDto.list> list(SinghaUser authUser, LessionSearch search);
    Page<LessionDto.detail> list(SinghaUser authUser, LessionSearch search, Pageable pageable);
    LessionDto.detail detail(SinghaUser authUser, String uid);
    void add(SinghaUser authUser, LessionDto.add addDto );
    void update(SinghaUser authUser, Lession lession, LessionDto.update updateDto );
    void delete(SinghaUser authUser, Lession lession);
}

@Service
@AllArgsConstructor
class AdmLessionServiceImpl implements AdmLessionService {
    
    private final AdmLessionRepository admLessionRepository;
    
    @Autowired
    PushAlarmService pushAlarmService;
    
    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    PointHistoryService pointHistoryService;
    
    @Autowired
    UserFcmTokenRepository userFcmTokenRepository;

    @Override
    public List<LessionDto.list> list(SinghaUser authUser, LessionSearch search) {
        return admLessionRepository.findAll(search.search()).stream().map(entity -> AdmLessionMapper.INSTANCE.entityToList(entity)).collect(Collectors.toList());
    }
    
    @Override
    public Page<LessionDto.detail> list(SinghaUser authUser, LessionSearch search, Pageable pageable) {
        return admLessionRepository.findAll(search.search(), pageable).map(entity -> AdmLessionMapper.INSTANCE.entityToDetail(entity));
    }

    @Override
    public LessionDto.detail detail(SinghaUser authUser, String uid) {
        Optional<Lession> optional = admLessionRepository.findById(uid);
        optional.orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
        LessionDto.detail dto = LessionMapper.INSTANCE.entityToDetail(optional.get());
        return dto;
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, LessionDto.add addDto) {
        Lession entity = null;
        entity = AdmLessionMapper.INSTANCE.addDtoToEntity(addDto);
        admLessionRepository.save(entity);
    }
    
    @Override
    public void update(SinghaUser authUser, Lession lession, LessionDto.update updateDto) {
        lession = AdmLessionMapper.INSTANCE.updateDtoToEntity(updateDto, lession);
		admLessionRepository.save(lession);
    }
    
    @Override
    public void delete(SinghaUser authUser, Lession lession) {
		admLessionRepository.delete(lession);
    }    
}
