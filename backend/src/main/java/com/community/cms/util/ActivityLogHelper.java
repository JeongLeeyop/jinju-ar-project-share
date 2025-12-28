package com.community.cms.util;

import com.community.cms.entity.ActivityLog;
import com.community.cms.api.activity_log.dto.ActivityLogDto;
import com.community.cms.api.activity_log.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 활동 로그 헬퍼 클래스
 * 전역적으로 사용 가능한 유틸리티 메서드 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityLogHelper {

    private final ActivityLogService activityLogService;

    /**
     * 공간 초대 로그
     */
    public void logSpaceInvited(String userUid, String userName, String channelUid, 
                                String channelName, String spaceName) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.SPACE_INVITED)
                .description(spaceName + " 공간에 초대되었습니다")
                .relatedName(spaceName)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 공간 개설 로그
     */
    public void logSpaceCreated(String userUid, String userName, String channelUid,
                                String channelName, String spaceUid, String spaceName) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.SPACE_CREATED)
                .description(spaceName + " 공간을 개설하였습니다")
                .relatedUid(spaceUid)
                .relatedName(spaceName)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 공간 참여 로그
     */
    public void logSpaceJoined(String userUid, String userName, String channelUid,
                               String channelName, String spaceUid, String spaceName) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.SPACE_JOINED)
                .description(spaceName + " 공간에 참여하였습니다")
                .relatedUid(spaceUid)
                .relatedName(spaceName)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 게시글 작성 로그
     */
    public void logPostCreated(String userUid, String userName, String channelUid,
                               String channelName, String postUid, String postTitle) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.POST_CREATED)
                .description("게시글을 작성하였습니다")
                .relatedUid(postUid)
                .relatedName(postTitle)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 댓글 작성 로그
     */
    public void logCommentCreated(String userUid, String userName, String channelUid,
                                  String channelName, String postUid, String postTitle,
                                  String postAuthorUid, String postAuthorName) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.COMMENT_CREATED)
                .description(postAuthorName + "님 게시글에 댓글을 작성하였습니다")
                .relatedUid(postUid)
                .relatedName(postTitle)
                .targetUserUid(postAuthorUid)
                .targetUserName(postAuthorName)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 좋아요 로그
     */
    public void logLikeAdded(String userUid, String userName, String channelUid,
                            String channelName, String postUid, String postTitle,
                            String postAuthorUid, String postAuthorName) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.LIKE_ADDED)
                .description(postAuthorName + "님 게시글에 좋아요를 눌렀습니다")
                .relatedUid(postUid)
                .relatedName(postTitle)
                .targetUserUid(postAuthorUid)
                .targetUserName(postAuthorName)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 커뮤니티 가입 로그
     */
    public void logCommunityJoined(String userUid, String userName, String channelUid,
                                   String channelName) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.COMMUNITY_JOINED)
                .description(channelName + " 커뮤니티에 가입하였습니다")
                .relatedName(channelName)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 일정 생성 로그
     */
    public void logEventCreated(String userUid, String userName, String channelUid,
                               String channelName, String eventUid, String eventTitle) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.EVENT_CREATED)
                .description("'" + eventTitle + "' 일정을 생성하였습니다")
                .relatedUid(eventUid)
                .relatedName(eventTitle)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 일정 참여 로그
     */
    public void logEventJoined(String userUid, String userName, String channelUid,
                              String channelName, String eventUid, String eventTitle) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.EVENT_JOINED)
                .description("'" + eventTitle + "' 일정에 참여하였습니다")
                .relatedUid(eventUid)
                .relatedName(eventTitle)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 상품 등록 로그
     */
    public void logProductCreated(String userUid, String userName, String channelUid,
                                 String channelName, String productUid, String productName) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.PRODUCT_CREATED)
                .description("'" + productName + "' 상품을 등록하였습니다")
                .relatedUid(productUid)
                .relatedName(productName)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 강의 생성 로그
     */
    public void logLessonCreated(String userUid, String userName, String channelUid,
                                String channelName, String lessonUid, String lessonTitle) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.LESSON_CREATED)
                .description("'" + lessonTitle + "' 강의를 생성하였습니다")
                .relatedUid(lessonUid)
                .relatedName(lessonTitle)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 영상 시청 로그
     */
    public void logVideoWatched(String userUid, String userName, String channelUid,
                               String channelName, String videoUid, String videoTitle) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.VIDEO_WATCHED)
                .description("'" + videoTitle + "' 영상을 시청하였습니다")
                .relatedUid(videoUid)
                .relatedName(videoTitle)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 영상 시청 완료 로그
     */
    public void logVideoCompleted(String userUid, String userName, String channelUid,
                                 String channelName, String videoUid, String videoTitle) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.VIDEO_COMPLETED)
                .description("'" + videoTitle + "' 영상 시청을 완료하였습니다")
                .relatedUid(videoUid)
                .relatedName(videoTitle)
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * R포인트 획득 로그
     */
    public void logRPointEarned(String userUid, String userName, String channelUid,
                               String channelName, int points, String reason) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.RPOINT_EARNED)
                .description(points + " R포인트를 획득하였습니다 (" + reason + ")")
                .relatedName(reason)
                .metaData("{\"points\": " + points + ", \"reason\": \"" + reason + "\"}")
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * R포인트 사용 로그
     */
    public void logRPointUsed(String userUid, String userName, String channelUid,
                             String channelName, int points, String reason) {
        ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .channelName(channelName)
                .activityType(ActivityLog.ActivityType.RPOINT_USED)
                .description(points + " R포인트를 사용하였습니다 (" + reason + ")")
                .relatedName(reason)
                .metaData("{\"points\": " + points + ", \"reason\": \"" + reason + "\"}")
                .build();
        
        activityLogService.logActivity(createReq);
    }

    /**
     * 커스텀 활동 로그 (범용)
     */
    public void logCustomActivity(String activityType, String userUid, String userName,
                                  String channelUid, String channelName,
                                  String relatedUid, String relatedName, String description) {
        try {
            // activityType 문자열을 ActivityLog.ActivityType enum으로 변환
            ActivityLog.ActivityType type = ActivityLog.ActivityType.valueOf(activityType);
            
            ActivityLogDto.CreateReq createReq = ActivityLogDto.CreateReq.builder()
                    .userUid(userUid)
                    .userName(userName)
                    .channelUid(channelUid)
                    .channelName(channelName)
                    .activityType(type)
                    .description(description)
                    .relatedUid(relatedUid)
                    .relatedName(relatedName)
                    .build();
            
            activityLogService.logActivity(createReq);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 활동 타입: {}", activityType, e);
        }
    }
}
