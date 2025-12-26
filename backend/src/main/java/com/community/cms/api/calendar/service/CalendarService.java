package com.community.cms.api.calendar.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.calendar.dto.CalendarDto;
import com.community.cms.api.calendar.dto.CalendarSearch;
import com.community.cms.api.calendar.dto.mapper.CalendarMapper;
import com.community.cms.api.calendar.repository.CalendarRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.User;
import com.community.cms.entity2.Calendar;
import com.community.cms.fcm.service.PushNotificationService;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface CalendarService {
    CalendarDto.detail detail(SinghaUser authUser, Integer idx);
    List<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search);
    Page<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search, Pageable pageable);
    void add(SinghaUser authUser, CalendarDto.add addDto );
    void update(SinghaUser authUser, Calendar Calendar, CalendarDto.update updateDto );
    void delete(SinghaUser authUser, Calendar Calendar);
}

@Service
@AllArgsConstructor
class CalendarServiceImpl implements CalendarService {
    
    private final CalendarRepository calendarRepository;
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

    @Override
    public CalendarDto.detail detail(SinghaUser authUser, Integer idx) {
        User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
        Optional<Calendar> optional = calendarRepository.findById(idx);
        // optional.orElseThrow(() -> new NotFoundException(NotFound.엔티티));
        CalendarDto.detail dto = CalendarMapper.INSTANCE.entityToDetail(optional.get());
        return dto;
    }

    @Override
    public List<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search) {
        return calendarRepository.findAll(search.search()).stream().map(entity -> CalendarMapper.INSTANCE.entityToList(entity)).collect(Collectors.toList());
    }
    
    @Override
    public Page<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search, Pageable pageable) {
        return calendarRepository.findAll(search.search(), pageable).map(entity -> CalendarMapper.INSTANCE.entityToList(entity));
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, CalendarDto.add addDto) {
        Calendar entity = null;
        entity = CalendarMapper.INSTANCE.addDtoToEntity(addDto);
        calendarRepository.save(entity);
    }
    
    @Override
    public void update(SinghaUser authUser, Calendar Calendar, CalendarDto.update updateDto) {
        Calendar = CalendarMapper.INSTANCE.updateDtoToEntity(updateDto, Calendar);
		calendarRepository.save(Calendar);
    }
    
    @Override
    public void delete(SinghaUser authUser, Calendar Calendar) {
		calendarRepository.delete(Calendar);
    }
}
