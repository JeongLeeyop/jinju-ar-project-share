package com.community.cms.entity2;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class ChannelUseCategory implements Serializable {
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	private String uid;

	// 게시판 고유값
	@Column(insertable = false, updatable = false)
	private String channelUid;

	// 카테고리 고유값
	private String categoryUid;
	
	// 순서
	@Column(insertable = false, updatable = false)
	private int viewOrder;

	@ManyToOne
	@JoinColumn(name = "categoryUid", insertable = false, updatable = false)
	private ChannelCategory category;
}	
	