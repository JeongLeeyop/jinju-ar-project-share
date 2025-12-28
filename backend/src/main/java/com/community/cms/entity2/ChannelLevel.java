package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.CreationTimestamp;

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
public class ChannelLevel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    private String channelUid;
    
    private int level;
    
    private String name;
    
    private String description;
    
    private String icon;
    
    // 조건 목록
     @OneToMany(mappedBy = "channelLevelIdx", cascade = CascadeType.ALL, orphanRemoval = true)
     private List<ChannelLevelSetting> levelSettingList; 
     
     // 권한 목록
     @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
     @JoinColumn(name = "channelLevelIdx", nullable = false)
     private List<ChannelLevelAuthority> levelAuthorityList; 

    @OneToMany(mappedBy = "channelLevelIdx", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    //  @JoinColumn(name = "channelLevelIdx", nullable = false)
     private List<ChannelMember> channelMemberList; 

    @CreationTimestamp
    private LocalDateTime createDate;
}	
	