package com.community.cms.api.calendar.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.community.cms.api.calendar.dto.CalendarDto;
import com.community.cms.api.calendar.dto.CalendarSearch;
import com.community.cms.api.calendar.service.CalendarService;
import com.community.cms.entity.AttachedFile;
import com.community.cms.entity2.Calendar;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/calendar")
@AllArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;
    private final AttachedFileService attachedFileService;


    @GetMapping
    public ResponseEntity<List<CalendarDto.list>> list(@AuthenticationPrincipal SinghaUser authUser, CalendarSearch search) {
        return ResponseEntity.ok(calendarService.list(authUser, search));
    }
    
    @GetMapping("/page")
    public ResponseEntity<Page<CalendarDto.list>> list(@PageableDefault(size=10, page=0)Pageable pageable, @AuthenticationPrincipal SinghaUser authUser, CalendarSearch search) {
        return ResponseEntity.ok(calendarService.list(authUser, search, pageable));
    }
    
        @GetMapping("{idx}")
    public ResponseEntity<CalendarDto.detail> detail(@AuthenticationPrincipal SinghaUser authUser,@PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(calendarService.detail(authUser, idx));
    }

    @PostMapping
    public ResponseEntity add(@AuthenticationPrincipal SinghaUser authUser, @RequestBody CalendarDto.add addDto) {
        calendarService.add(authUser, addDto);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("{idx}")
    public ResponseEntity delete(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") Calendar Calendar) {
        calendarService.delete(authUser, Calendar);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("{idx}")
    public ResponseEntity update(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") Calendar Calendar,@RequestBody CalendarDto.update updateDto) {
        calendarService.update(authUser, Calendar, updateDto);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/upload")
	public ResponseEntity<AttachedFile> fileUpload(MultipartFile file) {
		return ResponseEntity.ok(attachedFileService.save(file, "calendar"));
	}
}
