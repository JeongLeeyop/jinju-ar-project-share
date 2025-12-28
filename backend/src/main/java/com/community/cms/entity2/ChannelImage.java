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
public class ChannelImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    private String fileUid;

    private Integer viewOrder;

    @Column(insertable = false, updatable = false)
    private String channelUid;

    @Enumerated(EnumType.STRING)
	private ChannelImageType imageType;

    @ManyToOne
    @JoinColumn(name = "channelUid", insertable = false, updatable = false)
    private Channel channel;
    
    @ManyToOne
	@JoinColumn(name = "fileUid", insertable = false, updatable = false)
	private AttachedFile file;
}	
	