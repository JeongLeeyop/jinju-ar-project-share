package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;

import com.community.cms.entity.AttachedFile;
import com.community.cms.entity.BoardSkin;

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
@SQLDelete(sql = "UPDATE channel_member_question SET delete_status = true WHERE idx = ?")
@Where(clause = "delete_status=false")
public class ChannelMemberQuestion implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idx;

	private String channelQuestionIdx;

	// 카테고리 고유값
	private String answer;

	private boolean deleteStatus = Boolean.FALSE;

	@Column(insertable = false, updatable = false)
	private Integer channelMemberIdx;

	@CreationTimestamp
	private LocalDateTime createDate;

	@OneToOne
	@JoinColumn(name = "channelQuestionIdx", insertable = false, updatable = false)
	private ChannelQuestion channelQuestion;
}	
	