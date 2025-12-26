package com.community.cms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * SpacePostFile Entity
 * 공간 게시글 첨부파일 엔티티
 */
@Entity
@Table(name = "space_post_file")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePostFile {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String uid;

    /**
     * 순서 (기본값 0)
     */
    @Builder.Default
    @Column(name = "view_order")
    private int viewOrder = 0;

    /**
     * 게시글 (다대일 관계)
     * post_uid 컬럼을 통해 관계 관리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_uid", nullable = false)
    private SpacePost post;

    /**
     * 첨부파일 (다대일 관계)
     * file_uid 컬럼을 통해 관계 관리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_uid", nullable = false)
    private AttachedFile file;
}
