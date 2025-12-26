package com.community.cms.fcm.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.user.dto.UserFcmTokenDto;
import com.community.cms.api.user.service.UserFcmTokenService;
import com.community.cms.entity.User;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/fcm-token")
@AllArgsConstructor
public class FcmTokenController {
    private UserFcmTokenService userFcmTokenService;

    @PostMapping
    public ResponseEntity saveFcmToken(
        @RequestBody @Valid UserFcmTokenDto.Save saveDto,
        @AuthenticationPrincipal SinghaUser authUser,
        BindingResult result) {
        if(result.hasErrors()) return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
        userFcmTokenService.saveUserFcmToken(saveDto, authUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{token}")
    public ResponseEntity deleteFcmToken(
        @PathVariable("token") String token,
        @AuthenticationPrincipal SinghaUser authUser) {
        userFcmTokenService.deleteUserFcmToken(token, authUser);
        return ResponseEntity.ok().build();
    }
}
