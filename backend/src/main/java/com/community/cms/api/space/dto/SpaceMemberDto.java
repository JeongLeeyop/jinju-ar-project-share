package com.community.cms.api.space.dto;

import com.community.cms.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceMemberDto {
    private String uid;
    private String userId;
    private String actualName;
    private String nickName;
    private String profileImage;
    
    public static SpaceMemberDto from(User user) {
        return SpaceMemberDto.builder()
                .uid(user.getUid())
                .userId(user.getUserId())
                .actualName(user.getActualName())
                .nickName(user.getActualName())  // User 엔티티에 nickName 필드가 없으면 actualName 사용
                .profileImage(null)  // User 엔티티에 profileImage 필드 확인 필요
                .build();
    }
}
