package com.community.cms.api.sms.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sms_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36, unique = true)
    private String uid;

    @Column(length = 20)
    private String recipient;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 20)
    private String status;

    @Column(length = 10)
    private String smsType;

    private LocalDateTime sentAt;

    @Column(length = 100)
    private String senderName;

    @Column(length = 100)
    private String senderUid;

    @PrePersist
    public void prePersist() {
        if (this.uid == null) {
            this.uid = UUID.randomUUID().toString();
        }
    }
}
