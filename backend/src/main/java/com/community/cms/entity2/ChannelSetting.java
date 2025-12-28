package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class ChannelSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

/*     private String name;
    
    private String tel;

    private String postCode;

    private String address;

    private String addressDetail;

    private String lat;

    private String lon;

    private LocalTime openTime;

    private LocalTime closeTime;

    private LocalDate holidayStartDate;
    
    private LocalDate holidayEndDate;

    @CreationTimestamp
    private LocalDateTime createDate; */
}	
	