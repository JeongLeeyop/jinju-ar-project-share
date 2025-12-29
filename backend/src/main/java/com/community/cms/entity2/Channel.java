package com.community.cms.entity2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
							
						
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Channel implements Serializable {
    @Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String uid;

    private String name;
    private String introduce;
    private boolean privateStatus;
    private Integer price;
    private String domain;
    private String userUid;
    private String password;
    
    // 삭제 상태 (DB에서 직접 관리, false=보임, true=숨김)
    private boolean deleteStatus = false;
    
    // private String tags;
    // private String basicInfo;
    // private String breakTimeStart;
    // private String breakTimeEnd;
    // private String holidayDayNum;
    // private String operationTimeInfo;
    // private String equipmentInfo;
    // private String parkingInfo;
    // private String locationInfo;
    // private String surroundInfo;
    // private String etcInfo;
    // private String entranceGuide;
    // private String lat;
    // private String lng;
    // private String reserveUrl;
    // private String mediaUrl;
    // private String courseInfo;
    // private String refundPolicyInfo;
	
    // 채널 카테고리 목록
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "channelUid", nullable = false)
    @OrderColumn(name = "viewOrder")
	private List<ChannelUseCategory> categoryList;
    
    // 채널 질문 목록
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "channelUid", nullable = false)
    @OrderColumn(name = "viewOrder")
	private List<ChannelQuestion> questionList;

    // Jackson 순환 참조 방지: Channel → ChannelImage → ... 방지 (이미지는 별도 API로 조회)
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "channelUid", nullable = false)
    @OrderBy(value = "viewOrder Asc")
    private List<ChannelImage> imagesList = new ArrayList<ChannelImage>();

    // Jackson 순환 참조 방지: Channel → ChannelMember → User → ChannelMember 방지
    @JsonIgnore
    @OneToMany(mappedBy = "channelUid", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChannelMember> channelMember = new ArrayList<ChannelMember>();

    /* 
     * WARNING
     * 채널탈퇴 조건 추가
     */
    @Formula("(SELECT COUNT(*) FROM channel_member cm WHERE cm.channel_uid = uid AND cm.approval_status = true)")
	private int memberCount;

    // public void setPassword(String password) {
    //     if (StringUtils.hasText(password)) {
	// 		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	// 		this.password = passwordEncoder.encode(password);
	// 	}
    // }
}
	