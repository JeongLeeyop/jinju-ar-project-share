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

import com.community.cms.entity.User;

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

    private String location; // 진행 장소
    private Integer points; // R포인트 (유료/획득)
    private String eventType; // free, paid, earn
    private String writerUid; // 작성자 UID

    @ManyToOne
    @JoinColumn(name = "channelUid", insertable = false, updatable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "writerUid", insertable = false, updatable = false)
    private User writer;

    @CreationTimestamp
    private LocalDateTime createdDate;
}
