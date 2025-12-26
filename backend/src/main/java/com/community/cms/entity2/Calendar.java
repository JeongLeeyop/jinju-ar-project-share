package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

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
public class Calendar implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String title;
    private String content;
    
    private String channelUid;
    
    @ManyToOne
	@JoinColumn(name = "channelUid", insertable = false, updatable = false)
	private Channel channel;

    @CreationTimestamp
    private LocalDateTime createdDate;
}	
	
