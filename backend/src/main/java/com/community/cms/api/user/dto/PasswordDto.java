package com.community.cms.api.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 관련 DTO
 */
public class PasswordDto {

    /**
     * 임시 비밀번호 발급 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TempPasswordRequest {
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식으로 입력해주세요.")
        private String email;

        @NotBlank(message = "핸드폰 번호를 입력해주세요.")
        private String concatNumber;
    }

    /**
     * 임시 비밀번호 발급 응답 DTO
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TempPasswordResponse {
        private boolean success;
        private String message;
        private String maskedPhone;
    }

    /**
     * 이메일 찾기 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindEmailRequest {
        @NotBlank(message = "이름을 입력해주세요.")
        private String actualName;

        @NotBlank(message = "핸드폰 번호를 입력해주세요.")
        private String concatNumber;
    }

    /**
     * 이메일 찾기 응답 DTO
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindEmailResponse {
        private boolean found;
        private String maskedEmail;
        private String message;
    }
}
