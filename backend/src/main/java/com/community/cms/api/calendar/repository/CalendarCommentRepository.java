package com.community.cms.api.calendar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.community.cms.entity2.CalendarComment;

/**
 * 일정 댓글 Repository
 */
@Repository
public interface CalendarCommentRepository extends JpaRepository<CalendarComment, Long> {
    
    /**
     * 특정 일정의 모든 댓글 조회 (삭제되지 않은 것만, 최신순)
     */
    @Query("SELECT cc FROM CalendarComment cc " +
           "WHERE cc.calendarIdx = :calendarIdx AND cc.isDeleted = false " +
           "ORDER BY cc.createdAt DESC")
    List<CalendarComment> findByCalendarIdxAndNotDeleted(@Param("calendarIdx") Integer calendarIdx);
    
    /**
     * 특정 일정의 댓글 조회 (페이징, 삭제되지 않은 것만)
     */
    @Query("SELECT cc FROM CalendarComment cc " +
           "WHERE cc.calendarIdx = :calendarIdx AND cc.isDeleted = false " +
           "ORDER BY cc.createdAt DESC")
    Page<CalendarComment> findByCalendarIdxAndNotDeleted(
            @Param("calendarIdx") Integer calendarIdx, 
            Pageable pageable);
    
    /**
     * 일정의 댓글 수 조회 (삭제되지 않은 것만)
     */
    @Query("SELECT COUNT(cc) FROM CalendarComment cc " +
           "WHERE cc.calendarIdx = :calendarIdx AND cc.isDeleted = false")
    int countByCalendarIdxAndNotDeleted(@Param("calendarIdx") Integer calendarIdx);
    
    /**
     * 특정 일정의 모든 댓글 삭제
     */
    void deleteByCalendarIdx(Integer calendarIdx);
}

