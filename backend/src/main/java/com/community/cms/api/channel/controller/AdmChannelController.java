package com.community.cms.api.channel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.community.cms.api.attached_file.dto.AttachedFileDto;
import com.community.cms.api.attached_file.dto.mapper.AttachedFileMapper;
import com.community.cms.api.attached_file.service.AttachedFileService;
import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.channel.dto.ChannelSearch;
import com.community.cms.api.channel.service.AdmChannelService;
import com.community.cms.api.channel.service.ChannelService;
import com.community.cms.entity2.Channel;
import com.community.cms.entity.Post;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/channel")
@PreAuthorize("hasAnyRole('ROLE_CREATOR')")
@AllArgsConstructor
public class AdmChannelController {
    private final AdmChannelService admChannelService;
    private final AttachedFileService attachedFileService;

    @GetMapping("{uid}")
    public ResponseEntity<ChannelDto.detail> detail(@AuthenticationPrincipal SinghaUser authUser,@PathVariable("uid") String uid) {
        return ResponseEntity.ok(admChannelService.detail(authUser, uid));
    }

    @GetMapping
    public ResponseEntity<Page<ChannelDto.detail>> list(@AuthenticationPrincipal SinghaUser authUser, ChannelSearch search, @PageableDefault(direction = Direction.DESC, sort = { "createDate" }) Pageable pageable) {
        return ResponseEntity.ok(admChannelService.list(authUser, search, pageable));
    }
    
    @PostMapping
    public ResponseEntity add(@AuthenticationPrincipal SinghaUser authUser, @RequestBody ChannelDto.add addDto) {
        admChannelService.add(authUser, addDto);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("{uid}")
    public ResponseEntity delete(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("uid") Channel Channel) {
        admChannelService.delete(authUser, Channel);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("{uid}")
    public ResponseEntity update(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("uid") Channel Channel,@RequestBody ChannelDto.update updateDto) {
        admChannelService.update(authUser, Channel, updateDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("permitAll()")
	@PostMapping("/upload")
	public ResponseEntity<AttachedFileDto.detail> fileUpload(MultipartFile file) {
		return ResponseEntity.ok(AttachedFileMapper.INSTANCE.entityToDetailDto(attachedFileService.save(file, "Channel")));
	}
    
    // @PutMapping
    // public ResponseEntity updateSecretStatus(@AuthenticationPrincipal SinghaUser authUser, @RequestBody ChannelDto.updateSecretStatus updateDto) {
    //     admChannelService.updateSecretStatus(authUser, updateDto);
    //     return ResponseEntity.ok().build();
    // }
}
