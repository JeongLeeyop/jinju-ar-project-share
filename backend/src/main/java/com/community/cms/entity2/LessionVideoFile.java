package com.community.cms.entity2;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.community.cms.entity.AttachedFile;

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
public class LessionVideoFile implements Serializable {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Integer idx;

	@Column(insertable = false, updatable = false)
	private Integer videoIdx;

	private String fileUid;

	// @Enumerated(EnumType.STRING)
	// private LessionVideoFileType fileType;
	
	@Column(insertable = false, updatable = false)
	private int viewOrder;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "videoIdx", insertable = false, updatable = false)
	private LessionVideo lessionVideo;

	@ManyToOne
	@JoinColumn(name = "fileUid", insertable = false, updatable = false)
	private AttachedFile file;
}	
	