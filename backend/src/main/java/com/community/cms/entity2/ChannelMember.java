package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.CreationTimestamp;

import com.community.cms.entity.User;
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
public class ChannelMember implements Serializable {
    // 카테고리 고유값
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    // 유저 고유값
	private String userUid;
	
    // 채널 고유값
    private String channelUid;

    // @Column(insertable = false, updatable = false)
    private Integer channelLevelIdx;

    private String introduce;

    private boolean approvalStatus;
    
    // 생성일 
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate;
    
    private LocalDateTime levelUpDate;

    // 채널 질문 목록
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "channelMemberIdx", nullable = false)
	private List<ChannelMemberQuestion> questionList;

    // Jackson 순환 참조 방지: ChannelMember → ChannelLevel → ... 방지
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "channelLevelIdx",  insertable = false, updatable = false)
    private ChannelLevel channelLevel;

    // Jackson 순환 참조 방지: ChannelMember → Channel → ChannelMember 방지
    @JsonIgnore
    @ManyToOne
	@JoinColumn(name = "channelUid", insertable = false, updatable = false)
	private Channel channel;
    
    // Jackson 순환 참조 방지: ChannelMember → User → ChannelMember 방지
    @JsonIgnore
    @ManyToOne
	@JoinColumn(name = "userUid", insertable = false, updatable = false)
	private User user;

    /* 
     * TODO
     * 채널탈퇴
     */
}	
	