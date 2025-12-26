package com.community.cms.api.level.controller;

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
import com.community.cms.api.level.dto.ChannelLevelDto;
import com.community.cms.api.level.dto.ChannelLevelSearch;
import com.community.cms.api.level.service.ChannelLevelService;
import com.community.cms.entity2.Authority;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelLevel;
import com.community.cms.entity2.Event;
import com.community.cms.entity.Post;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/channel/level")
@AllArgsConstructor
public class ChannelLevelController {
    private final ChannelLevelService ChannelLevelService;
    private final AttachedFileService attachedFileService;

    @GetMapping("{idx}")
    public ResponseEntity<ChannelLevelDto.detail> detail(@AuthenticationPrincipal SinghaUser authUser,@PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(ChannelLevelService.detail(authUser, idx));
    }
   
    @GetMapping("event")
    public ResponseEntity<List<Event>> event(@AuthenticationPrincipal SinghaUser authUser) {
        return ResponseEntity.ok(ChannelLevelService.event(authUser));
    }

    @GetMapping("authority")
    public ResponseEntity<List<Authority>> authority(@AuthenticationPrincipal SinghaUser authUser) {
        return ResponseEntity.ok(ChannelLevelService.authority(authUser));
    }

    @GetMapping
    public ResponseEntity<Page<ChannelLevelDto.list>> list(@AuthenticationPrincipal SinghaUser authUser, ChannelLevelSearch search, @PageableDefault(direction = Direction.DESC, sort = { "createDate" }) Pageable pageable) {
        return ResponseEntity.ok(ChannelLevelService.list(authUser, search, pageable));
    }
    
    @PostMapping
    public ResponseEntity add(@AuthenticationPrincipal SinghaUser authUser, @RequestBody List<ChannelLevelDto.add> addDtoList) {
        ChannelLevelService.add(authUser, addDtoList);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("{idx}")
    public ResponseEntity delete(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") ChannelLevel Channel) {
        ChannelLevelService.delete(authUser, Channel);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("{idx}")
    public ResponseEntity update(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") ChannelLevel Channel,@RequestBody ChannelLevelDto.update updateDto) {
        ChannelLevelService.update(authUser, Channel, updateDto);
        return ResponseEntity.ok().build();
    }
}
