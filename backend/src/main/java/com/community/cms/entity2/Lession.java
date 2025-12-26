package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

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
public class Lession implements Serializable {

    @Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String uid;

    private String name;
    
    /* 
     * TODO 
     * 1. 에디터 사용으로 강의 소개 페이지 구현
     * 2. section 테이블 사용
     */
    private String description;

    private boolean privateStatus;

    private String channelUid;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "lessionUid", nullable = false)
	@OrderColumn(name = "viewOrder")
    private List<LessionFile> fileList;
    
    @OneToMany(mappedBy = "lessionUid" , cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "viewOrder")
    private List<LessionVideo> videoList;

    // private String address;

    // private String addressDetail;

    // private String lat;

    // private String lon;

    // private LocalTime openTime;

    // private LocalTime closeTime;

    // private LocalDate holidayStartDate;
    
    // private LocalDate holidayEndDate;

    @CreationTimestamp
    private LocalDateTime createDate;
}	
	
