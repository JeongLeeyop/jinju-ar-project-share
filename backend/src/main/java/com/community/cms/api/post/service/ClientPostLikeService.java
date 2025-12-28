package com.community.cms.api.post.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.api.event.service.EventHistoryService;
import com.community.cms.api.post.repository.PostLikeRepository;
import com.community.cms.api.post.repository.PostRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
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

    @Override
    public Integer like(String postUid, SinghaUser authUser) {
        Post post = postRepository.findById(postUid).orElseThrow(() -> new NotFoundException(NotFound.POST));
        String userUid = authUser.getUser().getUid();

        Optional<PostLike> optional = postLikeRepository.findByPostUidAndUserUid(postUid, userUid);
        
        if (optional.isPresent()) {
            postLikeRepository.delete(optional.get());
        } else {
            PostLike entity = new PostLike();
            entity.setPostUid(postUid);
            entity.setUserUid(userUid);
            postLikeRepository.save(entity);

            eventHistoryService.add(new EventHistoryDto.add(EventType.LIKE.toString(),userUid, post.getChannelUid(), null, postUid, null, entity.getIdx()));
            return entity.getIdx();
        }
        return null;
    }

    
}
