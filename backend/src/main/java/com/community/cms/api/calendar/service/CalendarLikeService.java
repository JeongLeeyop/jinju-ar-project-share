package com.community.cms.api.calendar.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.community.cms.api.activity_log.dto.ActivityLogDto;
import com.community.cms.api.activity_log.service.ActivityLogService;
import com.community.cms.api.calendar.dto.CalendarLikeDto;
import com.community.cms.api.calendar.repository.CalendarLikeRepository;
import com.community.cms.api.calendar.repository.CalendarRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.ActivityLog;
import com.community.cms.entity.User;
import com.community.cms.entity2.Calendar;
import com.community.cms.entity2.CalendarLike;
import com.community.cms.oauth.SinghaUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 일정 좋아요 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarLikeService {

    private final CalendarLikeRepository likeRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    /**
     * 좋아요 토글 (좋아요 추가 또는 취소)
     */
    @Transactional
    public CalendarLikeDto.ToggleResult toggleLike(SinghaUser authUser, Integer calendarIdx) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));

        Calendar calendar = calendarRepository.findById(calendarIdx)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        Optional<CalendarLike> existingLike = likeRepository.findByCalendarIdxAndUserUid(calendarIdx, user.getUid());

        boolean isLiked;
        String message;

        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
            isLiked = false;
            message = "좋아요를 취소했습니다.";

            // Log activity
            activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                    .userUid(user.getUid())
                    .userName(user.getActualName())
                    .channelUid(calendar.getChannelUid())
                    .activityType(ActivityLog.ActivityType.LIKE_REMOVED)
                    .description("'" + calendar.getTitle() + "' 일정 좋아요를 취소하였습니다")
                    .relatedUid(String.valueOf(calendarIdx))
                    .relatedName(calendar.getTitle())
                    .build());

            log.info("Calendar like removed: calendarIdx={}, userUid={}", calendarIdx, user.getUid());
        } else {
            // 좋아요 추가
            CalendarLike like = CalendarLike.builder()
                    .calendarIdx(calendarIdx)
                    .userUid(user.getUid())
                    .build();
            likeRepository.save(like);
            isLiked = true;
            message = "좋아요를 눌렀습니다.";

            // Log activity
            activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                    .userUid(user.getUid())
                    .userName(user.getActualName())
                    .channelUid(calendar.getChannelUid())
                    .activityType(ActivityLog.ActivityType.LIKE_ADDED)
                    .description("'" + calendar.getTitle() + "' 일정에 좋아요를 눌렀습니다")
                    .relatedUid(String.valueOf(calendarIdx))
                    .relatedName(calendar.getTitle())
                    .targetUserUid(calendar.getWriterUid())
                    .build());

            log.info("Calendar like added: calendarIdx={}, userUid={}", calendarIdx, user.getUid());
        }

        int likeCount = likeRepository.countByCalendarIdx(calendarIdx);

        return CalendarLikeDto.ToggleResult.builder()
                .success(true)
                .message(message)
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }

    /**
     * 좋아요 정보 조회
     */
    public CalendarLikeDto.LikeInfo getLikeInfo(SinghaUser authUser, Integer calendarIdx) {
        String userUid = authUser.getUser().getUid();
        boolean isLiked = likeRepository.existsByCalendarIdxAndUserUid(calendarIdx, userUid);
        int likeCount = likeRepository.countByCalendarIdx(calendarIdx);

        return CalendarLikeDto.LikeInfo.builder()
                .calendarIdx(calendarIdx)
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}

