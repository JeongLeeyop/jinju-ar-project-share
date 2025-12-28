package com.community.cms.api.calendar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.community.cms.entity2.CalendarLike;

/**
 * 일정 좋아요 Repository
 */
@Repository
public interface CalendarLikeRepository extends JpaRepository<CalendarLike, Long> {
    
    /**
     * 일정의 좋아요 수 조회
     */
    @Query("SELECT COUNT(cl) FROM CalendarLike cl WHERE cl.calendarIdx = :calendarIdx")
    int countByCalendarIdx(@Param("calendarIdx") Integer calendarIdx);
    
    /**
     * 특정 사용자의 좋아요 여부 확인
     */
    Optional<CalendarLike> findByCalendarIdxAndUserUid(Integer calendarIdx, String userUid);
    
    /**
     * 특정 사용자가 좋아요 했는지 확인
     */
    boolean existsByCalendarIdxAndUserUid(Integer calendarIdx, String userUid);
    
    /**
     * 특정 일정의 모든 좋아요 삭제
     */
    void deleteByCalendarIdx(Integer calendarIdx);
}

