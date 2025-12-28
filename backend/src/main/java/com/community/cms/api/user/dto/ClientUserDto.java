package com.community.cms.api.user.dto;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.community.cms.api.channel.dto.ChannelMemberDto;
import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.api.level.dto.ChannelLevelDto;

import lombok.Data;

public class ClientUserDto {
    @Data
    public static class join {
        @NotBlank(message = "성명을 입력하세요.")
        private String actualName;
        
        // @NotBlank(message = "연락처를 입력하세요.")
        private String concatNumber;

        private LocalDate birth;

        @NotBlank(message = "이메일을 입력하세요.")
        @Email(message = "이메일 형식으로 입력하세요.")
        private String email;

        @NotBlank(message = "패스워드를 입력하세요.")
        @Size(min = 8, max = 20, message = "패스워드는 최소8자, 최대20자 입니다.")
        private String userPassword;

        private String iconFileUid;

        private String postCode;

        // @NotBlank(message = "주소를 입력하세요.")
        private String address;

        private String addressDetail;

        private String lat;
        
        private String lon;

        // @NotNull(message = "성별을 선택하세요.")
        private Integer gender;

        private boolean dietExperience;

        // @NotBlank(message = "키를 입력하세요.")
        private String height;

        // @NotBlank(message = "체중을 입력하세요.")
        private String weight;

        // @NotBlank(message = "목표 체중을 입력하세요.")
        private String goalWeight;

        private LocalDate goalDate;


        private int activityLevel;

        // @NotBlank(message = "식단관리 목적을 입력하세요.")
        private String dietPurpose;

        // @NotBlank(message = "식단제공 시 유의사항을 입력하세요.")
        private String dietPrecaution;
        
        private boolean marketingStatus;
        
        private String role;
    }

    @Data
    public static class info {
        private String uid;
        private String userId;
        private String actualName;
        private String concatNumber;
        private String email;
        private String iconFileUid;
        private Integer point;
        private Boolean isOnline;
    }

    @Data
    public static class channelInfo {
        private String uid;
        private String userId;
        private String actualName;
        private String email;
        private String iconFileUid;
        private Integer point;
        private ChannelMemberDto.detail channelMember;
        private List<EventHistoryDto.userScore> userScoreList;
    }

    @Data
    public static class update {
        private String actualName;
        private String email;
        private String iconFileUid;
    }

    @Data
    public static class withdraw {
        private String reason;
    }
}
