package com.community.cms.api.activity.dto;

import com.community.cms.entity.Activity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Activity DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {

    private Long id;
    private String type;
    private String userUid;
    private String userName;
    private String channelUid;
    private String spaceUid;
    private String description;

    @JsonFormat(pattern = "yyyy.MM.dd")
    private LocalDateTime createdAt;

    /**
     * Entity to DTO
     */
    public static ActivityDto from(Activity activity) {
        return ActivityDto.builder()
                .id(activity.getId())
                .type(activity.getType())
                .userUid(activity.getUserUid())
                .userName(activity.getUserName())
                .channelUid(activity.getChannelUid())
                .spaceUid(activity.getSpaceUid())
                .description(activity.getDescription())
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
