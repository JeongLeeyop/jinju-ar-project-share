package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.community.cms.entity.AttachedFile;
import com.community.cms.entity.Post;

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
public class LessionFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    
    @Column(insertable = false, updatable = false)
    private String lessionUid;

    private String fileUid;
    
    @CreationTimestamp
    private LocalDateTime createDate;

    @ManyToOne
	@JoinColumn(name = "fileUid", insertable = false, updatable = false)
	private AttachedFile file;

	@Column(insertable = false, updatable = false)
    private Integer viewOrder;
}	
	