package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
public class LessionVideoTimeLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    private String time;
    
    private String description;

	@Column(insertable = false, updatable = false)
    private int videoIdx;
    
    @Column(insertable = false, updatable = false)
    private Integer viewOrder;
    
    @CreationTimestamp
    private LocalDateTime createDate;
}	
	