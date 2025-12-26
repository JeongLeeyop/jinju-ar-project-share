package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.community.cms.entity.BoardCategory;

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
public class ChannelCategory implements Serializable {
    // 카테고리 고유값
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String uid;

	// 카테고리 이름
	private String name;
	
	// 카테고리 설명
	private String descript;

    // 부모 카테고리 고유값
    private String parentUid;
	
    // depth
    private int depth;
    
    // 순서
    private int viewOrder;
    
    // 생성일 
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate;

    // 하위 데이터 리스트
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "parentUid")
    @OrderColumn(name = "viewOrder")
    private List<ChannelCategory> children = new ArrayList<ChannelCategory>();
}	
	