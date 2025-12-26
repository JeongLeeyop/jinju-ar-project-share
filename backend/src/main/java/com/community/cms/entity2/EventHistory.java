package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class EventHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    // 출석, 댓글, 포스트, 좋아요, 강의시청
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    // TO-DO 삭제 됬을때 고려

    private String channelUid;
    private String userUid;
    private Integer videoIdx;
    private String postUid;
    private String commentUid;
    private Integer likeIdx;

    @CreationTimestamp
    private LocalDateTime createDate;
}	
	