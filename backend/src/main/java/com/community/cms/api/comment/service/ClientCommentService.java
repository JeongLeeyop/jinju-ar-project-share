package com.community.cms.api.comment.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.community.cms.api.comment.dto.CommentDto;
import com.community.cms.api.comment.dto.mapper.CommentMapper;
import com.community.cms.api.comment.dto.search.CommentSearch;
import com.community.cms.api.comment.exception.CommentNotFoundException;
import com.community.cms.api.comment.repository.CommentRepository;
import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.api.event.service.EventHistoryService;
import com.community.cms.api.activity_log.dto.ActivityLogDto;
import com.community.cms.api.activity_log.service.ActivityLogService;
import com.community.cms.api.post.exception.PostNotFoundException;
import com.community.cms.api.post.repository.PostRepository;
import com.community.cms.api.user.exception.UserNotFoundException;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.BadRequestException;
import com.community.cms.entity.ActivityLog;
import com.community.cms.entity.Comment;
import com.community.cms.entity.Post;
import com.community.cms.entity.User;
import com.community.cms.entity2.EventType;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface ClientCommentService {
    Page<CommentDto.Detail> list(Pageable pageable, SinghaUser authUser, CommentSearch commentSearch);
    void add(CommentDto.Add addDto, SinghaUser userDetail);
    void update(Comment comment, CommentDto.Update updateDto);
    void delete(Comment comment, SinghaUser authUser);
}

@Service
@AllArgsConstructor
class ClientCommentServiceImpl implements ClientCommentService {
    private final EventHistoryService eventHistoryService;
    private final ActivityLogService activityLogService;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Page<CommentDto.Detail> list(Pageable pageable, SinghaUser authUser, CommentSearch commentSearch) {
        if (authUser == null) throw new UserNotFoundException("잘못된 접근입니다.");
        return commentRepository.findAll(commentSearch.search(), pageable).map(data -> CommentMapper.INSTANCE.entityToDetailDto(data, authUser.getUser().getUid()));
    }

    @Override
    @Transactional
    public void add(CommentDto.Add addDto, @AuthenticationPrincipal SinghaUser userDetail) {
        if (userDetail == null || userDetail.getUsername() == null) throw new UserNotFoundException("잘못된 접근입니다.");
        User user = userRepository.findByUserId(userDetail.getUsername().toString()).orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다."));

        Comment comment = CommentMapper.INSTANCE.addDtoToEntity(addDto);
        Post post = postRepository.findById(addDto.getPostUid()).orElseThrow(() -> new PostNotFoundException());
        comment.setUserUid(user.getUid());
        comment.setPostUid(post.getUid());
        comment.setDeleteStatus(false);
        
        if (!StringUtils.hasText(addDto.getWriter())) comment.setWriter(user.getActualName());
        if (StringUtils.hasText(addDto.getPassword())) comment.setPassword(passwordEncoder.encode(addDto.getPassword()));

        if (StringUtils.hasText(addDto.getParentUid())) {
            Comment parentComment = commentRepository.findById(addDto.getParentUid()).orElseThrow(() -> new CommentNotFoundException());
            comment.setParentUid(parentComment.getUid());
            comment.setViewOrder(parentComment.getViewOrder());
            comment.setDepth(parentComment.getDepth() + 1);
        } else {
            int viewOrder = 1;
            Optional<Comment> optional = commentRepository.findTopByOrderByViewOrderDesc();
            if (optional.isPresent()) viewOrder = comment.getViewOrder() + 1;
            comment.setParentUid(null);
            comment.setViewOrder(viewOrder);
            comment.setDepth(1);
        }

        commentRepository.save(comment);
        eventHistoryService.add(new EventHistoryDto.add(EventType.COMMENT.toString(), user.getUid(), addDto.getChannelUid(), null, null, comment.getUid(), null));

        // ✅ Activity Log 추가: 댓글 작성
        activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                .userUid(user.getUid())
                .userName(user.getActualName())
                .channelUid(addDto.getChannelUid())
                .activityType(ActivityLog.ActivityType.COMMENT_CREATED)
                .description(ActivityLog.generateDescription(ActivityLog.ActivityType.COMMENT_CREATED, post.getWriter(), null))
                .relatedUid(post.getUid())
                .relatedName("게시글")
                .targetUserUid(post.getUserUid())
                .targetUserName(post.getWriter())
                .build());
    }

    @Override
    public void update(Comment comment, CommentDto.Update updateDto) {
        comment = CommentMapper.INSTANCE.updateDtoToEntity(updateDto, comment);
        if (StringUtils.hasText(updateDto.getPassword())) comment.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        
        commentRepository.save(comment);
    }

    @Override
    public void delete(Comment comment, SinghaUser authUser) {
        commentRepository.delete(comment);        
    }

}
