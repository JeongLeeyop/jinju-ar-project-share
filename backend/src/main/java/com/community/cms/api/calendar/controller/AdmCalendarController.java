package com.community.cms.api.calendar.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.community.cms.api.calendar.dto.CalendarDto;
import com.community.cms.api.calendar.dto.CalendarSearch;
import com.community.cms.api.calendar.service.AdmCalendarService;
import com.community.cms.entity2.Calendar;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/calendar")
@PreAuthorize("hasAnyRole('ROLE_CREATOR')")
@AllArgsConstructor
public class AdmCalendarController {
    private final AdmCalendarService admCalendarService;
    private final AttachedFileService attachedFileService;

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<Page<CalendarDto.fullCalendar>> list(@AuthenticationPrincipal SinghaUser authUser, CalendarSearch search, @PageableDefault(direction = Direction.DESC, sort = { "createDate" }) Pageable pageable) {
        pageable = PageRequest.of(0, Integer.MAX_VALUE);
        return ResponseEntity.ok(admCalendarService.list(authUser, search, pageable));
    }
    
    @PostMapping
    public ResponseEntity add(@AuthenticationPrincipal SinghaUser authUser, @RequestBody CalendarDto.add addDto) {
        admCalendarService.add(authUser, addDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{idx}")
    public ResponseEntity<CalendarDto.detail> detail(@AuthenticationPrincipal SinghaUser authUser,@PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(admCalendarService.detail(authUser, idx));
    }
    
    @DeleteMapping("{idx}")
    public ResponseEntity delete(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") Calendar Calendar) {
        admCalendarService.delete(authUser, Calendar);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("{idx}")
    public ResponseEntity update(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") Calendar Calendar,@RequestBody CalendarDto.update updateDto) {
        admCalendarService.update(authUser, Calendar, updateDto);
        return ResponseEntity.ok().build();
    }

	@PostMapping("/upload")
	public ResponseEntity<AttachedFileDto.detail> fileUpload(MultipartFile file) {
		return ResponseEntity.ok(AttachedFileMapper.INSTANCE.entityToDetailDto(attachedFileService.save(file, "Calendar")));
	}
}
