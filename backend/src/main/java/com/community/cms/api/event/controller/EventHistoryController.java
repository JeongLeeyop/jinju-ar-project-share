package com.community.cms.api.event.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.api.event.service.EventHistoryService;
import com.community.cms.entity2.Channel;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/event/history")
@AllArgsConstructor
public class EventHistoryController {
    private final EventHistoryService eventHistoryService;
    private final ChannelRepository channelRepository;

    @GetMapping("ranking/{uid}")
    public ResponseEntity<EventHistoryDto.rankingAll> rankingAll(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("uid") String channelDomain) {
        // domain → uid 변환
        String channelUid = channelRepository.findByDomain(channelDomain)
                .map(Channel::getUid)
                .orElse(channelDomain);  // domain이 아니면 uid로 간주
        
        return ResponseEntity.ok(eventHistoryService.rankingAll(authUser, channelUid));
    }

    @GetMapping("ranking/{uid}/{day}")
    public ResponseEntity<List<EventHistoryDto.calculateRanking>> ranking(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("uid") String channelDomain,  @PathVariable("day") int day) {
        // domain → uid 변환
        String channelUid = channelRepository.findByDomain(channelDomain)
                .map(Channel::getUid)
                .orElse(channelDomain);  // domain이 아니면 uid로 간주
        
        return ResponseEntity.ok(eventHistoryService.ranking(authUser, channelUid, day));
    }

}
