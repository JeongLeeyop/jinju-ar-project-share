package com.community.cms.api.channel.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.attached_file.service.AttachedFileService;
import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.channel.dto.ChannelMemberDto;
import com.community.cms.api.channel.dto.ChannelMemberSearch;
import com.community.cms.api.channel.dto.ChannelSearch;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.channel.service.ChannelMemberService;
import com.community.cms.api.post.dto.PostDto;
import com.community.cms.entity.Post;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelMember;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/channel-member")
@AllArgsConstructor
public class ChannelMemberController {
    private final ChannelMemberService channelMemberService;
    private final ChannelRepository channelRepository;

    @GetMapping
    public ResponseEntity<Page<ChannelMemberDto.detail>> list(
        @PageableDefault(size=10, page=0)Pageable pageable,
        @AuthenticationPrincipal SinghaUser authUser, ChannelMemberSearch search) {
        // domain → uid 변환
        if (search.getChannelUid() != null) {
            String actualChannelUid = channelRepository.findByDomain(search.getChannelUid())
                    .map(Channel::getUid)
                    .orElse(search.getChannelUid());
            search.setChannelUid(actualChannelUid);
        }
        return ResponseEntity.ok(channelMemberService.list(authUser, search, pageable));
    }

    @GetMapping("userCount")
    public ResponseEntity<ChannelMemberDto.count> userCount(
        @AuthenticationPrincipal SinghaUser authUser, ChannelMemberSearch search) {
        return ResponseEntity.ok(channelMemberService.userCount(authUser, search));
    }
    
    @PutMapping("approval/{uid}")
    public ResponseEntity approval(
        @AuthenticationPrincipal SinghaUser authUser,
			@PathVariable("uid") ChannelMember channelMember) {
        channelMemberService.approval(channelMember, authUser);
        return ResponseEntity.ok().build();
    }
}
