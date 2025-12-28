package com.community.cms.api.post.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.api.event.service.EventHistoryService;
import com.community.cms.api.activity_log.dto.ActivityLogDto;
import com.community.cms.api.activity_log.service.ActivityLogService;
import com.community.cms.api.post.repository.PostLikeRepository;
import com.community.cms.api.post.repository.PostRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.ActivityLog;
import com.community.cms.entity.Post;
import com.community.cms.entity.PostLike;
import com.community.cms.entity2.EventType;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface ClientPostLikeService {
    Integer like(String postUid, SinghaUser authUser);
}

@Service
@AllArgsConstructor
class ClientPostLikeServiceImpl implements ClientPostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final EventHistoryService eventHistoryService;
    private final ActivityLogService activityLogService;

    @Override
    public Integer like(String postUid, SinghaUser authUser) {
        Post post = postRepository.findById(postUid).orElseThrow(() -> new NotFoundException(NotFound.POST));
        String userUid = authUser.getUser().getUid();
        String userName = authUser.getUser().getActualName();

        Optional<PostLike> optional = postLikeRepository.findByPostUidAndUserUid(postUid, userUid);
        
        if (optional.isPresent()) {
            // ✅ 좋아요 취소
            postLikeRepository.delete(optional.get());
            
            // ✅ Activity Log 추가: 좋아요 취소
            activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                    .userUid(userUid)
                    .userName(userName)
                    .channelUid(post.getChannelUid())
                    .activityType(ActivityLog.ActivityType.LIKE_REMOVED)
                    .description(ActivityLog.generateDescription(ActivityLog.ActivityType.LIKE_REMOVED, post.getWriter(), null))
                    .relatedUid(post.getUid())
                    .relatedName("게시글")
                    .targetUserUid(post.getUserUid())
                    .targetUserName(post.getWriter())
                    .build());
        } else {
            // ✅ 좋아요 추가
            PostLike entity = new PostLike();
            entity.setPostUid(postUid);
            entity.setUserUid(userUid);
            postLikeRepository.save(entity);

            eventHistoryService.add(new EventHistoryDto.add(EventType.LIKE.toString(), userUid, post.getChannelUid(), null, postUid, null, entity.getIdx()));
            
            // ✅ Activity Log 추가: 좋아요
            activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                    .userUid(userUid)
                    .userName(userName)
                    .channelUid(post.getChannelUid())
                    .activityType(ActivityLog.ActivityType.LIKE_ADDED)
                    .description(ActivityLog.generateDescription(ActivityLog.ActivityType.LIKE_ADDED, post.getWriter(), null))
                    .relatedUid(post.getUid())
                    .relatedName("게시글")
                    .targetUserUid(post.getUserUid())
                    .targetUserName(post.getWriter())
                    .build());
            
            return entity.getIdx();
        }
        return null;
    }

    
}
