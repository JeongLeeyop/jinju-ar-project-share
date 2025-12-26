package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
@SQLDelete(sql = "UPDATE channel_question SET delete_status = true WHERE idx = ?")
@Where(clause = "delete_status=false")
public class ChannelQuestion implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

	// 질문
	private String title;

    // 순서
	@Column(insertable = false, updatable = false)
	private int viewOrder;

    @Column(insertable = false, updatable = false)
    private String channelUid;

    private boolean deleteStatus = Boolean.FALSE;
	
    // 생성일 
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate;
}	
	