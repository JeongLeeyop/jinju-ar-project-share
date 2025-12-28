package com.community.cms.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log", indexes = {
    @Index(name = "idx_user_channel_created", columnList = "user_uid, channel_uid, created_at"),
    @Index(name = "idx_channel_created", columnList = "channel_uid, created_at"),
    @Index(name = "idx_user_created", columnList = "user_uid, created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "user_uid", nullable = false, length = 100)
    private String userUid;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "channel_uid", nullable = false, length = 100)
    private String channelUid;

    @Column(name = "channel_name", length = 200)
    private String channelName;

    @Column(name = "activity_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "related_uid", length = 100)
    private String relatedUid;

    @Column(name = "related_name", length = 200)
    private String relatedName;

    @Column(name = "target_user_uid", length = 100)
    private String targetUserUid;

    @Column(name = "target_user_name", length = 100)
    private String targetUserName;

    @Column(name = "meta_data", columnDefinition = "TEXT")
    private String metaData; // JSON 형태로 추가 정보 저장

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ActivityType {
        // 공간 관련
        SPACE_INVITED("공간에 초대되었습니다"),
        SPACE_CREATED("공간을 개설하였습니다"),
        SPACE_JOINED("공간에 참여하였습니다"),
        SPACE_MEMBERS_INVITED("공간에 다중 사용자를 초대하였습니다"),
        
        // 게시글 관련
        POST_CREATED("게시글을 작성하였습니다"),
        POST_UPDATED("게시글을 수정하였습니다"),
        POST_DELETED("게시글을 삭제하였습니다"),
        
        // 댓글 관련
        COMMENT_CREATED("댓글을 작성하였습니다"),
        COMMENT_UPDATED("댓글을 수정하였습니다"),
        COMMENT_DELETED("댓글을 삭제하였습니다"),
        
        // 좋아요 관련
        LIKE_ADDED("좋아요를 눌렀습니다"),
        LIKE_REMOVED("좋아요를 취소하였습니다"),
        
        // 커뮤니티 관련
        COMMUNITY_JOINED("커뮤니티에 가입하였습니다"),
        COMMUNITY_LEFT("커뮤니티를 탈퇴하였습니다"),
        
        // 일정 관련
        EVENT_CREATED("일정을 생성하였습니다"),
        EVENT_JOINED("일정에 참여하였습니다"),
        EVENT_CANCELLED("일정 참여를 취소하였습니다"),
        
        // 장터 관련
        PRODUCT_CREATED("상품을 등록하였습니다"),
        PRODUCT_PURCHASED("상품을 구매하였습니다"),
        TRADE_STARTED("거래를 시작하였습니다"),
        TRADE_COMPLETED("거래를 완료하였습니다"),
        TRADE_CANCELLED("거래를 취소하였습니다"),
        REQUEST_APPLIED("요청에 지원하였습니다"),
        
        // 강의 관련
        LESSON_CREATED("강의를 생성하였습니다"),
        VIDEO_UPLOADED("영상을 업로드하였습니다"),
        VIDEO_WATCHED("영상을 시청하였습니다"),
        VIDEO_COMPLETED("영상 시청을 완료하였습니다"),
        
        // 랭킹 관련
        RANK_UP("랭킹이 상승하였습니다"),
        RANK_DOWN("랭킹이 하락하였습니다"),
        
        // R포인트 관련
        RPOINT_EARNED("R포인트를 획득하였습니다"),
        RPOINT_USED("R포인트를 사용하였습니다");

        private final String defaultMessage;

        ActivityType(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }

    /**
     * 설명 생성 헬퍼 메서드
     */
    public static String generateDescription(ActivityType type, String targetName, String relatedName) {
        switch (type) {
            case SPACE_INVITED:
                return relatedName + " 공간에 초대되었습니다";
            case SPACE_CREATED:
                return relatedName + " 공간을 개설하였습니다";
            case SPACE_JOINED:
                return relatedName + " 공간에 참여하였습니다";
            case POST_CREATED:
                return "게시글을 작성하였습니다";
            case COMMENT_CREATED:
                if (targetName != null) {
                    return targetName + "님 게시글에 댓글을 작성하였습니다";
                }
                return "댓글을 작성하였습니다";
            case LIKE_ADDED:
                if (targetName != null) {
                    return targetName + "님 게시글에 좋아요를 눌렀습니다";
                }
                return "좋아요를 눌렀습니다";
            case EVENT_CREATED:
                return "'" + relatedName + "' 일정을 생성하였습니다";
            case EVENT_JOINED:
                return "'" + relatedName + "' 일정에 참여하였습니다";
            case PRODUCT_CREATED:
                return "'" + relatedName + "' 상품을 등록하였습니다";
            case LESSON_CREATED:
                return "'" + relatedName + "' 강의를 생성하였습니다";
            case VIDEO_WATCHED:
                return "'" + relatedName + "' 영상을 시청하였습니다";
            default:
                return type.getDefaultMessage();
        }
    }
}
