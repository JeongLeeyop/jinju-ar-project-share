package com.community.cms.api.post.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.community.cms.api.post.dto.PostTagDto;
import com.community.cms.api.post.repository.query.PostTagQuery;

import lombok.AllArgsConstructor;

public interface PostTagService {
    List<PostTagDto.best> list();
}

@Service
@AllArgsConstructor
class PostTagServiceImpl implements PostTagService {
    private final PostTagQuery postTagQuery;

    @Override
    public List<PostTagDto.best> list() {
        return postTagQuery.list();
    }

    
}