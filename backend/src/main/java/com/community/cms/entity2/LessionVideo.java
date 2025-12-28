package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class LessionVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    
    @CreationTimestamp
    private LocalDateTime createDate;

    private String title;

    private String description;
    private String content;

    private String urlCode;

    private int viewCount;

    private String lessionUid;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lessionUid", insertable = false, updatable = false)
	private Lession lession;

    private Integer viewOrder;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "videoIdx", nullable = false)
	@OrderColumn(name = "viewOrder")
    private List<LessionVideoFile> fileList;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "videoIdx", nullable = false)
	@OrderColumn(name = "viewOrder")
    private List<LessionVideoTimeLine> timeLineList;
}	
	