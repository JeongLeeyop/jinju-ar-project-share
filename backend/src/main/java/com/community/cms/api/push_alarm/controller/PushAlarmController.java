package com.community.cms.api.push_alarm.controller;

import com.community.cms.api.push_alarm.dto.PushAlarmDto;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.entity.PushAlarm;
import com.community.cms.oauth.SinghaUser;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/push-alarm")
@AllArgsConstructor
public class PushAlarmController {
    private final PushAlarmService pushAlarmService;

    @GetMapping
    public ResponseEntity<PushAlarmDto.model> list(
        @AuthenticationPrincipal SinghaUser authUser,
        @PageableDefault(
            size=10,
            page=0)
        @SortDefault.SortDefaults({
            @SortDefault(sort = "createDate", direction = Sort.Direction.DESC)
        }) Pageable pageable) {
        return ResponseEntity.ok(pushAlarmService.list(authUser, pageable));
    }

    @GetMapping("count")
    public ResponseEntity<Integer> count(@AuthenticationPrincipal SinghaUser authUser) {
        return ResponseEntity.ok(pushAlarmService.count(authUser));
    }

    @PutMapping("readAll")
    @Transactional
    public ResponseEntity readAll(@AuthenticationPrincipal SinghaUser authUser) {
        pushAlarmService.readAll(authUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable("id") PushAlarm pushAlarm, @AuthenticationPrincipal SinghaUser authUser) {
        pushAlarmService.delete(authUser, pushAlarm);
        return ResponseEntity.ok().build();
    }
}
