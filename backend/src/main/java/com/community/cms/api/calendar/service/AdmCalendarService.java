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
import com.community.cms.api.calendar.dto.mapper.AdmCalendarMapper;
import com.community.cms.api.calendar.dto.mapper.CalendarMapper;
import com.community.cms.api.calendar.repository.AdmCalendarRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity2.Calendar;
import com.community.cms.fcm.service.PushNotificationService;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface AdmCalendarService {
    List<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search);
    Page<CalendarDto.fullCalendar> list(SinghaUser authUser, CalendarSearch search, Pageable pageable);
    CalendarDto.detail detail(SinghaUser authUser, Integer uid);
    void add(SinghaUser authUser, CalendarDto.add addDto );
    void update(SinghaUser authUser, Calendar Calendar, CalendarDto.update updateDto );
    void delete(SinghaUser authUser, Calendar Calendar);
}

@Service
@AllArgsConstructor
class AdmCalendarServiceImpl implements AdmCalendarService {
    
    private final AdmCalendarRepository admCalendarRepository;
    
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
    public List<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search) {
        return admCalendarRepository.findAll(search.search()).stream().map(entity -> AdmCalendarMapper.INSTANCE.entityToList(entity)).collect(Collectors.toList());
    }
    
    @Override
    public Page<CalendarDto.fullCalendar> list(SinghaUser authUser, CalendarSearch search, Pageable pageable) {
        return admCalendarRepository.findAll(search.search(), pageable).map(entity -> AdmCalendarMapper.INSTANCE.entityToFullCalendar(entity));
    }

    @Override
    public CalendarDto.detail detail(SinghaUser authUser, Integer idx) {
        Optional<Calendar> optional = admCalendarRepository.findById(idx);
        optional.orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
        CalendarDto.detail dto = CalendarMapper.INSTANCE.entityToDetail(optional.get());
        return dto;
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, CalendarDto.add addDto) {
        Calendar entity = null;
        entity = AdmCalendarMapper.INSTANCE.addDtoToEntity(addDto);
        admCalendarRepository.save(entity);
    }
    
    @Override
    public void update(SinghaUser authUser, Calendar Calendar, CalendarDto.update updateDto) {
        Calendar = AdmCalendarMapper.INSTANCE.updateDtoToEntity(updateDto, Calendar);
		admCalendarRepository.save(Calendar);
    }
    
    @Override
    public void delete(SinghaUser authUser, Calendar Calendar) {
		admCalendarRepository.delete(Calendar);
    }    
}
