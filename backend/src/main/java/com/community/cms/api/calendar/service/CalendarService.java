package com.community.cms.api.calendar.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.activity_log.dto.ActivityLogDto;
import com.community.cms.api.activity_log.service.ActivityLogService;
import com.community.cms.api.calendar.dto.CalendarDto;
import com.community.cms.api.calendar.dto.CalendarSearch;
import com.community.cms.api.calendar.dto.ScheduleParticipantDto;
import com.community.cms.api.calendar.dto.mapper.CalendarMapper;
import com.community.cms.api.calendar.repository.CalendarRepository;
import com.community.cms.api.calendar.repository.ScheduleParticipantRepository;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.channel.service.ChannelMemberPermissionService;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.code.ChannelMemberPermissionType;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.ActivityLog;
import com.community.cms.entity.PointHistory;
import com.community.cms.entity.User;
import com.community.cms.entity2.Calendar;
import com.community.cms.entity2.ChannelMember;
import com.community.cms.entity2.ScheduleParticipant;
import com.community.cms.entity2.ScheduleParticipant.ParticipantStatus;
import com.community.cms.fcm.service.PushNotificationService;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface CalendarService {
    CalendarDto.detail detail(SinghaUser authUser, Integer idx);

    List<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search);

    Page<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search, Pageable pageable);

    void add(SinghaUser authUser, CalendarDto.add addDto);

    void update(SinghaUser authUser, Calendar calendar, CalendarDto.update updateDto);

    void delete(SinghaUser authUser, Calendar calendar);

    // New methods for schedule participation
    CalendarDto.JoinResult joinEvent(SinghaUser authUser, Integer calendarIdx);

    void cancelParticipation(SinghaUser authUser, Integer calendarIdx);

    List<ScheduleParticipantDto.ListItem> getParticipants(Integer calendarIdx);

    void grantPointToParticipant(SinghaUser authUser, Integer participantIdx);
    
    // 내 일정 관리 메서드
    List<CalendarDto.MyScheduleItem> getMyRegisteredSchedules(SinghaUser authUser, String channelUid);
    
    List<CalendarDto.MyScheduleItem> getMyParticipatedSchedules(SinghaUser authUser, String channelUid);
    
    void cancelMySchedule(SinghaUser authUser, Integer calendarIdx);
}

@Service
@AllArgsConstructor
class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ScheduleParticipantRepository participantRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelRepository channelRepository;
    private final com.community.cms.api.calendar.repository.CalendarLikeRepository calendarLikeRepository;
    private final com.community.cms.api.calendar.repository.CalendarCommentRepository calendarCommentRepository;

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
    ChannelMemberPermissionService permissionService;

    @Autowired
    ActivityLogService activityLogService;

    @Override
    public CalendarDto.detail detail(SinghaUser authUser, Integer idx) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));
        Optional<Calendar> optional = calendarRepository.findById(idx);
        CalendarDto.detail dto = CalendarMapper.INSTANCE.entityToDetail(optional.get());

        // Add participation status
        boolean isParticipating = participantRepository.existsByCalendarIdxAndUserUidAndStatus(
                idx, user.getUid(), ParticipantStatus.REGISTERED);
        dto.setIsParticipating(isParticipating);

        // Add participant count
        int participantCount = participantRepository.countByCalendarIdxAndStatus(idx, ParticipantStatus.REGISTERED);
        dto.setParticipantCount(participantCount);

        // Add like count and status
        int likeCount = calendarLikeRepository.countByCalendarIdx(idx);
        dto.setLikeCount(likeCount);
        boolean isLiked = calendarLikeRepository.existsByCalendarIdxAndUserUid(idx, user.getUid());
        dto.setIsLiked(isLiked);

        // Add comment count
        int commentCount = calendarCommentRepository.countByCalendarIdxAndNotDeleted(idx);
        dto.setCommentCount(commentCount);

        // Add writer name if available
        Calendar calendar = optional.get();
        if (calendar.getWriter() != null) {
            dto.setWriterName(calendar.getWriter().getActualName());
        }

        return dto;
    }

    @Override
    public List<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search) {
        String userUid = authUser.getUser().getUid();
        return calendarRepository.findAll(search.search()).stream().map(entity -> {
            CalendarDto.list dto = CalendarMapper.INSTANCE.entityToList(entity);
            dto.setParticipantCount(
                    participantRepository.countByCalendarIdxAndStatus(entity.getIdx(), ParticipantStatus.REGISTERED));
            dto.setIsParticipating(participantRepository.existsByCalendarIdxAndUserUidAndStatus(entity.getIdx(),
                    userUid, ParticipantStatus.REGISTERED));
            
            // Add like count and status
            dto.setLikeCount(calendarLikeRepository.countByCalendarIdx(entity.getIdx()));
            dto.setIsLiked(calendarLikeRepository.existsByCalendarIdxAndUserUid(entity.getIdx(), userUid));
            
            // Add comment count
            dto.setCommentCount(calendarCommentRepository.countByCalendarIdxAndNotDeleted(entity.getIdx()));
            
            // Add writer name
            if (entity.getWriter() != null) {
                dto.setWriterName(entity.getWriter().getActualName());
            } else if (entity.getWriterUid() != null) {
                // Fetch writer if not loaded
                userRepository.findById(entity.getWriterUid()).ifPresent(writer -> {
                    dto.setWriterName(writer.getActualName());
                });
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<CalendarDto.list> list(SinghaUser authUser, CalendarSearch search, Pageable pageable) {
        String userUid = authUser.getUser().getUid();
        return calendarRepository.findAll(search.search(), pageable).map(entity -> {
            CalendarDto.list dto = CalendarMapper.INSTANCE.entityToList(entity);
            dto.setParticipantCount(
                    participantRepository.countByCalendarIdxAndStatus(entity.getIdx(), ParticipantStatus.REGISTERED));
            dto.setIsParticipating(participantRepository.existsByCalendarIdxAndUserUidAndStatus(entity.getIdx(),
                    userUid, ParticipantStatus.REGISTERED));
            
            // Add like count and status
            dto.setLikeCount(calendarLikeRepository.countByCalendarIdx(entity.getIdx()));
            dto.setIsLiked(calendarLikeRepository.existsByCalendarIdxAndUserUid(entity.getIdx(), userUid));
            
            // Add comment count
            dto.setCommentCount(calendarCommentRepository.countByCalendarIdxAndNotDeleted(entity.getIdx()));
            
            // Add writer name with fallback
            if (entity.getWriter() != null) {
                String writerName = entity.getWriter().getActualName();
                if (writerName == null || writerName.trim().isEmpty()) {
                    writerName = entity.getWriter().getUserId();
                }
                dto.setWriterName(writerName != null ? writerName : "작성자");
            } else if (entity.getWriterUid() != null) {
                userRepository.findById(entity.getWriterUid()).ifPresent(writer -> {
                    String writerName = writer.getActualName();
                    if (writerName == null || writerName.trim().isEmpty()) {
                        writerName = writer.getUserId();
                    }
                    dto.setWriterName(writerName != null ? writerName : "작성자");
                });
            }
            
            return dto;
        });
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, CalendarDto.add addDto) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));

        // Check if user is channel admin (커뮤니티 관리자는 모든 기능 수행 가능)
        com.community.cms.entity2.Channel channel = channelRepository.findById(addDto.getChannelUid())
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다."));
        
        boolean isChannelAdmin = channel.getUserUid().equals(user.getUid());

        // If not admin, check if user is a member and has permission
        if (!isChannelAdmin) {
            // Check permission (SCHEDULE_CREATE)
            Optional<ChannelMember> memberOpt = channelMemberRepository.findByUserUidAndChannelUid(user.getUid(),
                    addDto.getChannelUid());
            if (memberOpt.isEmpty() || !memberOpt.get().isApprovalStatus()) {
                throw new RuntimeException("채널 멤버가 아니거나 승인되지 않았습니다.");
            }
            
            try {
                var permissionCheck = permissionService.checkPermission(memberOpt.get().getIdx(),
                        ChannelMemberPermissionType.SCHEDULE_CREATE);
                if (!permissionCheck.isHasPermission()) {
                    throw new RuntimeException("일정 생성 권한이 없습니다.");
                }
            } catch (RuntimeException e) {
                if (e.getMessage().contains("일정 생성 권한이 없습니다")) {
                    throw e;
                }
                // If permission not set, allow by default
            }
        }

        Calendar entity = CalendarMapper.INSTANCE.addDtoToEntity(addDto);
        entity.setWriterUid(authUser.getUser().getUid());
        calendarRepository.save(entity);

        // Log activity
        activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                .userUid(authUser.getUser().getUid())
                .userName(authUser.getUser().getActualName())
                .channelUid(addDto.getChannelUid())
                .activityType(ActivityLog.ActivityType.EVENT_CREATED)
                .description(ActivityLog.generateDescription(ActivityLog.ActivityType.EVENT_CREATED, null,
                        addDto.getTitle()))
                .relatedUid(String.valueOf(entity.getIdx()))
                .relatedName(addDto.getTitle())
                .build());
    }

    @Override
    public void update(SinghaUser authUser, Calendar calendar, CalendarDto.update updateDto) {
        calendar = CalendarMapper.INSTANCE.updateDtoToEntity(updateDto, calendar);
        calendarRepository.save(calendar);
    }

    @Override
    public void delete(SinghaUser authUser, Calendar calendar) {
        calendarRepository.delete(calendar);
    }

    @Transactional
    @Override
    public CalendarDto.JoinResult joinEvent(SinghaUser authUser, Integer calendarIdx) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));

        Calendar calendar = calendarRepository.findById(calendarIdx)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        // Check if already participating
        if (participantRepository.existsByCalendarIdxAndUserUidAndStatus(calendarIdx, user.getUid(),
                ParticipantStatus.REGISTERED)) {
            return CalendarDto.JoinResult.builder()
                    .success(false)
                    .message("이미 참여 중인 일정입니다.")
                    .build();
        }

        // Check if user is channel member
        Optional<ChannelMember> memberOpt = channelMemberRepository.findByUserUidAndChannelUid(user.getUid(),
                calendar.getChannelUid());
        if (memberOpt.isEmpty() || !memberOpt.get().isApprovalStatus()) {
            return CalendarDto.JoinResult.builder()
                    .success(false)
                    .message("채널 멤버가 아니거나 승인되지 않았습니다.")
                    .build();
        }

        // Check if user is channel admin (community admin has all permissions)
        var channel = channelRepository.findByUid(calendar.getChannelUid())
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다."));
        
        boolean isChannelAdmin = channel.getUserUid().equals(user.getUid());
        
        // If not admin, check SCHEDULE_PARTICIPATE permission
        if (!isChannelAdmin) {
            try {
                var permissionCheck = permissionService.checkPermission(memberOpt.get().getIdx(),
                        ChannelMemberPermissionType.SCHEDULE_PARTICIPATE);
                if (!permissionCheck.isHasPermission()) {
                    return CalendarDto.JoinResult.builder()
                            .success(false)
                            .message("일정 참여 권한이 없습니다.")
                            .build();
                }
            } catch (Exception e) {
                // If permission not set, allow by default
            }
        }

        String eventType = calendar.getEventType() != null ? calendar.getEventType() : "free";
        Integer points = calendar.getPoints() != null ? calendar.getPoints() : 0;
        Integer currentBalance = user.getPoint() != null ? user.getPoint() : 0;

        // Handle paid type - deduct points
        if ("paid".equals(eventType) && points > 0) {
            if (currentBalance < points) {
                return CalendarDto.JoinResult.builder()
                        .success(false)
                        .message("포인트가 부족합니다. 필요: " + points + ", 보유: " + currentBalance)
                        .currentBalance(currentBalance)
                        .build();
            }

            // Deduct points
            int newBalance = currentBalance - points;
            user.setPoint(newBalance);
            userRepository.save(user);

            // Save point history
            PointHistory history = PointHistory.builder()
                    .userUid(user.getUid())
                    .channelUid(calendar.getChannelUid())
                    .pointType("SCHEDULE")
                    .pointAmount(-points)
                    .currentBalance(newBalance)
                    .description("일정 참여: " + calendar.getTitle())
                    .referenceId(String.valueOf(calendarIdx))
                    .build();
            pointHistoryRepository.save(history);

            currentBalance = newBalance;
        }

        // Create participant record
        ScheduleParticipant participant = ScheduleParticipant.builder()
                .calendarIdx(calendarIdx)
                .userUid(user.getUid())
                .channelUid(calendar.getChannelUid())
                .pointsAmount(points)
                .status(ParticipantStatus.REGISTERED)
                .pointGranted("earn".equals(eventType) ? false : null)
                .build();
        participantRepository.save(participant);

        // Log activity
        activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                .userUid(user.getUid())
                .userName(user.getActualName())
                .channelUid(calendar.getChannelUid())
                .activityType(ActivityLog.ActivityType.EVENT_JOINED)
                .description(ActivityLog.generateDescription(ActivityLog.ActivityType.EVENT_JOINED, null,
                        calendar.getTitle()))
                .relatedUid(String.valueOf(calendarIdx))
                .relatedName(calendar.getTitle())
                .build());

        if ("paid".equals(eventType)) {
            return CalendarDto.JoinResult.builder()
                    .success(true)
                    .message("일정에 참여하였습니다.")
                    .pointsUsed(points)
                    .currentBalance(currentBalance)
                    .build();
        } else if ("earn".equals(eventType)) {
            return CalendarDto.JoinResult.builder()
                    .success(true)
                    .message("일정에 참여하였습니다. 일정 완료 후 포인트가 지급됩니다.")
                    .pointsToEarn(points)
                    .currentBalance(currentBalance)
                    .build();
        } else {
            return CalendarDto.JoinResult.builder()
                    .success(true)
                    .message("일정에 참여하였습니다.")
                    .currentBalance(currentBalance)
                    .build();
        }
    }

    @Transactional
    @Override
    public void cancelParticipation(SinghaUser authUser, Integer calendarIdx) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));

        Calendar calendar = calendarRepository.findById(calendarIdx)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        ScheduleParticipant participant = participantRepository.findByCalendarIdxAndUserUidAndStatus(
                calendarIdx, user.getUid(), ParticipantStatus.REGISTERED)
                .orElseThrow(() -> new RuntimeException("참여 정보를 찾을 수 없습니다."));

        // Check if cancellation is allowed (24 hours before)
        LocalDateTime eventStart = calendar.getStartDate();
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilEvent = ChronoUnit.HOURS.between(now, eventStart);

        if (hoursUntilEvent < 24) {
            throw new RuntimeException("일정 시작 24시간 전까지만 취소가 가능합니다.");
        }

        // Refund points for paid type
        String eventType = calendar.getEventType() != null ? calendar.getEventType() : "free";
        if ("paid".equals(eventType) && participant.getPointsAmount() != null && participant.getPointsAmount() > 0) {
            Integer currentBalance = user.getPoint() != null ? user.getPoint() : 0;
            int newBalance = currentBalance + participant.getPointsAmount();
            user.setPoint(newBalance);
            userRepository.save(user);

            // Save refund point history
            PointHistory history = PointHistory.builder()
                    .userUid(user.getUid())
                    .channelUid(calendar.getChannelUid())
                    .pointType("SCHEDULE_REFUND")
                    .pointAmount(participant.getPointsAmount())
                    .currentBalance(newBalance)
                    .description("일정 참여 취소 환불: " + calendar.getTitle())
                    .referenceId(String.valueOf(calendarIdx))
                    .build();
            pointHistoryRepository.save(history);
        }

        // Update participant status
        participant.setStatus(ParticipantStatus.CANCELLED);
        participant.setCancelledAt(LocalDateTime.now());
        participantRepository.save(participant);

        // Log activity
        activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                .userUid(user.getUid())
                .userName(user.getActualName())
                .channelUid(calendar.getChannelUid())
                .activityType(ActivityLog.ActivityType.EVENT_CANCELLED)
                .description("'" + calendar.getTitle() + "' 일정 참여를 취소하였습니다")
                .relatedUid(String.valueOf(calendarIdx))
                .relatedName(calendar.getTitle())
                .build());
    }

    @Override
    public List<ScheduleParticipantDto.ListItem> getParticipants(Integer calendarIdx) {
        List<ScheduleParticipant> participants = participantRepository.findByCalendarIdxAndStatus(
                calendarIdx, ParticipantStatus.REGISTERED);

        return participants.stream().map(p -> {
            User user = userRepository.findById(p.getUserUid()).orElse(null);
            return ScheduleParticipantDto.ListItem.builder()
                    .idx(p.getIdx())
                    .userUid(p.getUserUid())
                    .userName(user != null ? user.getActualName() : "Unknown")
                    .userPhone(user != null ? user.getConcatNumber() : "")
                    .userProfileImage(user != null ? user.getIconFileUid() : null)
                    .status(p.getStatus())
                    .pointGranted(p.getPointGranted())
                    .createdAt(p.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void grantPointToParticipant(SinghaUser authUser, Integer participantIdx) {
        ScheduleParticipant participant = participantRepository.findById(participantIdx)
                .orElseThrow(() -> new RuntimeException("참여자를 찾을 수 없습니다."));

        if (Boolean.TRUE.equals(participant.getPointGranted())) {
            throw new RuntimeException("이미 포인트가 지급되었습니다.");
        }

        Calendar calendar = calendarRepository.findById(participant.getCalendarIdx())
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        // Verify this is an earn type event
        if (!"earn".equals(calendar.getEventType())) {
            throw new RuntimeException("획득 타입 일정에서만 포인트 지급이 가능합니다.");
        }

        // Check if auth user is the calendar creator
        if (!calendar.getWriterUid().equals(authUser.getUser().getUid())) {
            throw new RuntimeException("일정 작성자만 포인트를 지급할 수 있습니다.");
        }

        User participantUser = userRepository.findById(participant.getUserUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));

        Integer pointsToGrant = participant.getPointsAmount() != null ? participant.getPointsAmount() : 0;
        if (pointsToGrant > 0) {
            Integer currentBalance = participantUser.getPoint() != null ? participantUser.getPoint() : 0;
            int newBalance = currentBalance + pointsToGrant;
            participantUser.setPoint(newBalance);
            userRepository.save(participantUser);

            // Save point history
            PointHistory history = PointHistory.builder()
                    .userUid(participantUser.getUid())
                    .channelUid(calendar.getChannelUid())
                    .pointType("SCHEDULE_EARN")
                    .pointAmount(pointsToGrant)
                    .currentBalance(newBalance)
                    .description("일정 참여 포인트 획득: " + calendar.getTitle())
                    .referenceId(String.valueOf(calendar.getIdx()))
                    .build();
            pointHistoryRepository.save(history);

            // Log activity
            activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                    .userUid(participantUser.getUid())
                    .userName(participantUser.getActualName())
                    .channelUid(calendar.getChannelUid())
                    .activityType(ActivityLog.ActivityType.RPOINT_EARNED)
                    .description("'" + calendar.getTitle() + "' 일정 참여로 " + pointsToGrant + " R포인트를 획득하였습니다")
                    .relatedUid(String.valueOf(calendar.getIdx()))
                    .relatedName(calendar.getTitle())
                    .build());
        }

        // Update participant record
        participant.setPointGranted(true);
        participant.setPointGrantedAt(LocalDateTime.now());
        participant.setPointGrantedBy(authUser.getUser().getUid());
        participantRepository.save(participant);
    }
    
    @Override
    public List<CalendarDto.MyScheduleItem> getMyRegisteredSchedules(SinghaUser authUser, String channelUid) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));
        
        // 내가 등록한 일정 조회
        List<Calendar> calendars = calendarRepository.findByWriterUidAndChannelUidOrderByStartDateDesc(
                user.getUid(), channelUid);
        
        return calendars.stream().map(calendar -> {
            int participantCount = participantRepository.countByCalendarIdxAndStatus(
                    calendar.getIdx(), ParticipantStatus.REGISTERED);
            
            String status = getScheduleStatus(calendar);
            
            return CalendarDto.MyScheduleItem.builder()
                    .idx(calendar.getIdx())
                    .title(calendar.getTitle())
                    .startDate(calendar.getStartDate())
                    .endDate(calendar.getEndDate())
                    .location(calendar.getLocation())
                    .participantCount(participantCount)
                    .maxParticipants(calendar.getMaxParticipants())
                    .status(status)
                    .eventType(calendar.getEventType())
                    .points(calendar.getPoints())
                    .hostUid(user.getUid())
                    .hostName(user.getActualName())
                    .canCancel(!"completed".equals(status) && !"cancelled".equals(status))
                    .build();
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<CalendarDto.MyScheduleItem> getMyParticipatedSchedules(SinghaUser authUser, String channelUid) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));
        
        // 내가 참여한 일정 조회 (REGISTERED 상태)
        List<ScheduleParticipant> participants = participantRepository.findByUserUidAndChannelUidAndStatusOrderByCreatedAtDesc(
                user.getUid(), channelUid, ParticipantStatus.REGISTERED);
        
        return participants.stream().map(participant -> {
            Calendar calendar = calendarRepository.findById(participant.getCalendarIdx()).orElse(null);
            if (calendar == null) return null;
            
            User host = userRepository.findById(calendar.getWriterUid()).orElse(null);
            String hostName = host != null ? host.getActualName() : "작성자";
            
            String status = getScheduleStatus(calendar);
            
            // 취소 가능 기한 계산 (일정 시작 24시간 전)
            LocalDateTime cancelDeadline = calendar.getStartDate().minusHours(24);
            boolean canCancel = LocalDateTime.now().isBefore(cancelDeadline) && "upcoming".equals(status);
            
            return CalendarDto.MyScheduleItem.builder()
                    .idx(calendar.getIdx())
                    .title(calendar.getTitle())
                    .startDate(calendar.getStartDate())
                    .endDate(calendar.getEndDate())
                    .location(calendar.getLocation())
                    .participantCount(participantRepository.countByCalendarIdxAndStatus(
                            calendar.getIdx(), ParticipantStatus.REGISTERED))
                    .maxParticipants(calendar.getMaxParticipants())
                    .status(status)
                    .eventType(calendar.getEventType())
                    .points(calendar.getPoints())
                    .hostUid(calendar.getWriterUid())
                    .hostName(hostName)
                    .participatedAt(participant.getCreatedAt())
                    .cancelDeadline(cancelDeadline)
                    .canCancel(canCancel)
                    .build();
        }).filter(item -> item != null).collect(Collectors.toList());
    }
    
    @Transactional
    @Override
    public void cancelMySchedule(SinghaUser authUser, Integer calendarIdx) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));
        
        Calendar calendar = calendarRepository.findById(calendarIdx)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        
        // 작성자 본인만 취소 가능
        if (!calendar.getWriterUid().equals(user.getUid())) {
            // 채널 관리자인지 확인
            var channel = channelRepository.findByUid(calendar.getChannelUid())
                    .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다."));
            
            if (!channel.getUserUid().equals(user.getUid())) {
                throw new RuntimeException("일정 작성자 또는 채널 관리자만 취소할 수 있습니다.");
            }
        }
        
        // 이미 시작된 일정은 취소 불가
        if (LocalDateTime.now().isAfter(calendar.getStartDate())) {
            throw new RuntimeException("이미 시작된 일정은 취소할 수 없습니다.");
        }
        
        // 참여자들에게 포인트 환불 (paid 타입인 경우)
        if ("paid".equals(calendar.getEventType())) {
            List<ScheduleParticipant> participants = participantRepository.findByCalendarIdxAndStatus(
                    calendarIdx, ParticipantStatus.REGISTERED);
            
            for (ScheduleParticipant participant : participants) {
                if (participant.getPointsAmount() != null && participant.getPointsAmount() > 0) {
                    User participantUser = userRepository.findById(participant.getUserUid()).orElse(null);
                    if (participantUser != null) {
                        Integer currentBalance = participantUser.getPoint() != null ? participantUser.getPoint() : 0;
                        int newBalance = currentBalance + participant.getPointsAmount();
                        participantUser.setPoint(newBalance);
                        userRepository.save(participantUser);
                        
                        // 포인트 히스토리 기록
                        PointHistory history = PointHistory.builder()
                                .userUid(participantUser.getUid())
                                .channelUid(calendar.getChannelUid())
                                .pointType("SCHEDULE_CANCELLED_REFUND")
                                .pointAmount(participant.getPointsAmount())
                                .currentBalance(newBalance)
                                .description("일정 취소로 인한 환불: " + calendar.getTitle())
                                .referenceId(String.valueOf(calendarIdx))
                                .build();
                        pointHistoryRepository.save(history);
                    }
                }
                
                // 참여자 상태 변경
                participant.setStatus(ParticipantStatus.CANCELLED);
                participant.setCancelledAt(LocalDateTime.now());
                participantRepository.save(participant);
            }
        }
        
        // 일정 삭제 (또는 상태 변경)
        calendarRepository.delete(calendar);
        
        // 활동 로그 기록
        activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                .userUid(user.getUid())
                .userName(user.getActualName())
                .channelUid(calendar.getChannelUid())
                .activityType(ActivityLog.ActivityType.EVENT_CANCELLED)
                .description("'" + calendar.getTitle() + "' 일정을 취소하였습니다")
                .relatedUid(String.valueOf(calendarIdx))
                .relatedName(calendar.getTitle())
                .build());
    }
    
    /**
     * 일정 상태 계산
     */
    private String getScheduleStatus(Calendar calendar) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(calendar.getStartDate())) {
            return "upcoming";
        } else if (now.isAfter(calendar.getEndDate())) {
            return "completed";
        } else {
            return "ongoing";
        }
    }
}
