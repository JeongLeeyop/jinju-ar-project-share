package com.community.cms.api.channel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.community.cms.api.attached_file.service.AttachedFileService;
import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.channel.dto.ChannelMemberDto;
import com.community.cms.api.channel.dto.ChannelSearch;
import com.community.cms.api.channel.service.ChannelService;
import com.community.cms.entity.AttachedFile;
import com.community.cms.entity2.Channel;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/channel")
@AllArgsConstructor
public class ChannelController {
    private final ChannelService channelService;
    private final AttachedFileService attachedFileService;

    @GetMapping("{uid}")
    public ResponseEntity<ChannelDto.detail> detail(@AuthenticationPrincipal SinghaUser authUser,@PathVariable("uid") String uid) {
        return ResponseEntity.ok(channelService.detail(authUser, uid));
    }
	
    // @GetMapping
    // public ResponseEntity<List<ChannelDto.detail>> list(@AuthenticationPrincipal SinghaUser authUser, ChannelSearch search) {
        // return ResponseEntity.ok(channelService.list(authUser, search));
    // }

    @GetMapping
    public ResponseEntity<Page<ChannelDto.detail>> list(
        @PageableDefault(size=10, page=0)Pageable pageable,
        @AuthenticationPrincipal SinghaUser authUser, ChannelSearch search) {
        return ResponseEntity.ok(channelService.list(authUser, search, pageable));
    }

    @GetMapping("domain/{domain}")
    public ResponseEntity<ChannelDto.detail> domainDetail(@AuthenticationPrincipal SinghaUser authUser,@PathVariable("domain") String domain) {
        return ResponseEntity.ok(channelService.domainDetail(authUser, domain));
    }
    
    @GetMapping("my")
    public ResponseEntity<ChannelDto.myList> myList(@AuthenticationPrincipal SinghaUser authUser) {
        return ResponseEntity.ok(channelService.myList(authUser));
    }
    
    @GetMapping("community")
    public ResponseEntity<Page<ChannelDto.detail>> communityList(@AuthenticationPrincipal SinghaUser authUser, ChannelSearch search, @PageableDefault(
					size=10,
					page=0,
					direction = Direction.DESC,
					sort = {"createDate"}) Pageable pageable) {
        return ResponseEntity.ok(channelService.list(authUser, search, pageable));
    }

    @PostMapping
    public ResponseEntity add(@AuthenticationPrincipal SinghaUser authUser, @RequestBody ChannelDto.add addDto) {
        channelService.add(authUser, addDto);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("{idx}")
    public ResponseEntity delete(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") Channel Channel) {
        channelService.delete(authUser, Channel);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("{idx}")
    public ResponseEntity update(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") Channel Channel,@RequestBody ChannelDto.update updateDto) {
        channelService.update(authUser, Channel, updateDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("domain/check/{domain}")
    public boolean checkDomainDuplicate(@PathVariable("domain") String domain) {
        return channelService.checkDomainDuplicate(domain);
    }

    @PostMapping("join")
    public ResponseEntity join(@AuthenticationPrincipal SinghaUser authUser, @RequestBody ChannelMemberDto.add addDto) {
        channelService.join(authUser, addDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("code/{uid}")
    public ResponseEntity<Boolean> validateCode(@PathVariable("uid") Channel channel, String code) {
        return ResponseEntity.ok(channelService.validateCode(channel, code));
    }

    @GetMapping("domain/{domain}/member-count")
    public ResponseEntity<Long> getMemberCountByDomain(@PathVariable("domain") String domain) {
        return ResponseEntity.ok(channelService.getMemberCountByDomain(domain));
    }

    // @PutMapping
    // public ResponseEntity updateSecretStatus(@AuthenticationPrincipal SinghaUser authUser, @RequestBody ChannelDto.updateSecretStatus updateDto) {
    //     channelService.updateSecretStatus(authUser, updateDto);
    //     return ResponseEntity.ok().build();
    // }
}
