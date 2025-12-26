package com.community.cms.api.lession.controller;
                                                                                                                                                                                                                                                                                                                                                                                                                  import java.util.List;

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
import com.community.cms.api.lession.dto.LessionDto;
import com.community.cms.api.lession.dto.LessionSearch;
import com.community.cms.api.lession.service.LessionService;
import com.community.cms.entity.AttachedFile;
import com.community.cms.entity2.Lession;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/lession")
@AllArgsConstructor
public class LessionController {
    private final LessionService lessionService;
    private final AttachedFileService attachedFileService;
	
    // @GetMapping
    // public ResponseEntity<List<LessionDto.list>> list(@AuthenticationPrincipal SinghaUser authUser, LessionSearch search) {
    //     return ResponseEntity.ok(lessionService.list(authUser, search));
    // }

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<Page<LessionDto.list>> list(
        @PageableDefault(size=10, page=0)Pageable pageable,
        @AuthenticationPrincipal SinghaUser authUser, LessionSearch search) {
        return ResponseEntity.ok(lessionService.list(authUser, search, pageable));
    }

    @GetMapping("{uid}")
    public ResponseEntity<LessionDto.detail> detail(@AuthenticationPrincipal SinghaUser authUser,@PathVariable("uid") String uid) {
        return ResponseEntity.ok(lessionService.detail(authUser, uid));
    }

    @PostMapping
    public ResponseEntity add(@AuthenticationPrincipal SinghaUser authUser, @RequestBody LessionDto.add addDto) {
        lessionService.add(authUser, addDto);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("{uid}")
    public ResponseEntity delete(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("uid") Lession Lession) {
        lessionService.delete(authUser, Lession);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("{uid}")
    public ResponseEntity update(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("uid") Lession Lession,@RequestBody LessionDto.update updateDto) {
        lessionService.update(authUser, Lession, updateDto);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/upload")
	public ResponseEntity<AttachedFile> fileUpload(MultipartFile file) {
		return ResponseEntity.ok(attachedFileService.save(file, "Lession"));
	}
}
