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
    private final com.community.cms.api.calendar.repository.CalendarLikeRepository calendarLikeRepository;
    private final com.community.cms.api.calendar.repository.CalendarCommentRepository calendarCommentRepository;
    private final com.community.cms.api.calendar.repository.ScheduleParticipantRepository participantRepository;
    
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
        String userUid = authUser.getUser().getUid();
        return admCalendarRepository.findAll(search.search()).stream().map(entity -> {
            CalendarDto.list dto = AdmCalendarMapper.INSTANCE.entityToList(entity);
            
            // Add participant count
            dto.setParticipantCount(
                    participantRepository.countByCalendarIdxAndStatus(entity.getIdx(), 
                            com.community.cms.entity2.ScheduleParticipant.ParticipantStatus.REGISTERED));
            dto.setIsParticipating(participantRepository.existsByCalendarIdxAndUserUidAndStatus(entity.getIdx(),
                    userUid, com.community.cms.entity2.ScheduleParticipant.ParticipantStatus.REGISTERED));
            
            // Add like count and status
            dto.setLikeCount(calendarLikeRepository.countByCalendarIdx(entity.getIdx()));
            dto.setIsLiked(calendarLikeRepository.existsByCalendarIdxAndUserUid(entity.getIdx(), userUid));
            
            // Add comment count
            dto.setCommentCount(calendarCommentRepository.countByCalendarIdxAndNotDeleted(entity.getIdx()));
            
            if (entity.getWriter() != null) {
                dto.setWriterName(entity.getWriter().getActualName());
            }
            
            return dto;
        }).collect(Collectors.toList());
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
        
        String userUid = authUser.getUser().getUid();
        
        // Add participation status
        dto.setIsParticipating(participantRepository.existsByCalendarIdxAndUserUidAndStatus(
                idx, userUid, com.community.cms.entity2.ScheduleParticipant.ParticipantStatus.REGISTERED));

        // Add participant count
        dto.setParticipantCount(participantRepository.countByCalendarIdxAndStatus(
                idx, com.community.cms.entity2.ScheduleParticipant.ParticipantStatus.REGISTERED));

        // Add like count and status
        dto.setLikeCount(calendarLikeRepository.countByCalendarIdx(idx));
        dto.setIsLiked(calendarLikeRepository.existsByCalendarIdxAndUserUid(idx, userUid));

        // Add comment count
        dto.setCommentCount(calendarCommentRepository.countByCalendarIdxAndNotDeleted(idx));

        // Add writer name if available
        Calendar calendar = optional.get();
        if (calendar.getWriter() != null) {
            dto.setWriterName(calendar.getWriter().getActualName());
        }
        
        return dto;
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, CalendarDto.add addDto) {
        Calendar entity = null;
        entity = AdmCalendarMapper.INSTANCE.addDtoToEntity(addDto);
        admCalendarRepository.save(entity);
    }
    
    @Transactional
    @Override
    public void update(SinghaUser authUser, Calendar calendar, CalendarDto.update updateDto) {
        // @MappingTarget을 사용하여 기존 엔티티를 업데이트
        AdmCalendarMapper.INSTANCE.updateDtoToEntity(updateDto, calendar);
        // JPA는 @Transactional 내에서 변경을 자동으로 감지하므로 save() 호출 불필요
        // 하지만 명시적으로 save()를 호출해도 문제없음
        admCalendarRepository.save(calendar);
    }
    
    @Override
    public void delete(SinghaUser authUser, Calendar Calendar) {
		admCalendarRepository.delete(Calendar);
    }    
}
