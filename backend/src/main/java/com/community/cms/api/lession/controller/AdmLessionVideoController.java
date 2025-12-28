package com.community.cms.api.lession.controller;
                                                                                                                                                                                                                                                                                                                                                                                                                  import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.community.cms.api.attached_file.service.AttachedFileService;
import com.community.cms.api.lession.dto.LessionVideoDto;
import com.community.cms.api.lession.dto.LessionVideoSearch;
import com.community.cms.api.lession.service.AdmLessionVideoService;
import com.community.cms.entity.AttachedFile;
import com.community.cms.entity2.LessionVideo;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/lession/video")
@PreAuthorize("hasAnyRole('ROLE_CREATOR')")
@AllArgsConstructor
public class AdmLessionVideoController {
    private final AdmLessionVideoService lessionService;
    private final AttachedFileService attachedFileService;
	
    // @GetMapping
    // public ResponseEntity<List<LessionVideoDto.list>> list(@AuthenticationPrincipal SinghaUser authUser, LessionVideoSearch search) {
    //     return ResponseEntity.ok(lessionService.list(authUser, search));
    // }

    @GetMapping
    public ResponseEntity<Page<LessionVideoDto.list>> list(
        @PageableDefault(size=10, page=0)Pageable pageable,
        @AuthenticationPrincipal SinghaUser authUser, LessionVideoSearch search) {
        return ResponseEntity.ok(lessionService.list(authUser, search, pageable));
    }

    @GetMapping("{idx}")
    public ResponseEntity<LessionVideoDto.detail> detail(@AuthenticationPrincipal SinghaUser authUser,@PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(lessionService.detail(authUser, idx));
    }

    @PostMapping
    public ResponseEntity add(@AuthenticationPrincipal SinghaUser authUser, @RequestBody LessionVideoDto.add addDto) {
        lessionService.add(authUser, addDto);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("{idx}")
    public ResponseEntity delete(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") LessionVideo LessionVideo) {
        lessionService.delete(authUser, LessionVideo);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("{idx}")
    public ResponseEntity update(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") LessionVideo LessionVideo,@RequestBody LessionVideoDto.update updateDto) {
        lessionService.update(authUser, LessionVideo, updateDto);
        return ResponseEntity.ok().build();
    }
    
    @PreAuthorize("permitAll()")    
    @PostMapping("/upload")
	public ResponseEntity<AttachedFile> fileUpload(MultipartFile file) {
		return ResponseEntity.ok(attachedFileService.save(file, "LessionVideo"));
	}
}
