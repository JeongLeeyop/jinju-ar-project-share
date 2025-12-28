package com.community.cms.api.comment.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.comment.dto.CommentDto;
import com.community.cms.api.comment.dto.search.CommentSearch;
import com.community.cms.api.comment.service.ClientCommentService;
import com.community.cms.api.comment.service.CommentService;
import com.community.cms.entity.Comment;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/comment")
@AllArgsConstructor
public class ClientCommentController {
    private final ClientCommentService commentService;

    @GetMapping
    public ResponseEntity<Page<CommentDto.Detail>> list(
        @PageableDefault(size = 10, page = 0, direction = Direction.DESC, sort = {"createDate" }) Pageable pageable, @AuthenticationPrincipal SinghaUser authUser,
        CommentSearch commentSearch) {
        return ResponseEntity.ok(commentService.list(pageable, authUser, commentSearch));
    }

    @PostMapping
    public ResponseEntity add(
        @AuthenticationPrincipal UserDetails userDetail,
        @RequestBody @Valid CommentDto.Add addDto) {
        commentService.add(addDto, userDetail);
        return ResponseEntity.ok().build();       
    }

    @PutMapping("{uid}")
    public ResponseEntity update(
        @PathVariable("uid") Comment comment,
        @RequestBody @Valid CommentDto.Update updateDto) {
        commentService.update(comment, updateDto);
        return ResponseEntity.ok().build();       
    }

    @DeleteMapping("{uid}")
    public ResponseEntity delete(@PathVariable("uid") Comment comment, @AuthenticationPrincipal SinghaUser authUser) {
        commentService.delete(comment, authUser);
        return ResponseEntity.ok().build();
    }
}
