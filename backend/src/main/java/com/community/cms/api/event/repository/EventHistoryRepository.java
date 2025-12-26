package com.community.cms.api.event.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.entity2.EventHistory;
import com.community.cms.entity2.EventType;

public interface EventHistoryRepository extends JpaRepository<EventHistory, Integer>, QuerydslPredicateExecutor<EventHistory> {
    List<EventHistory> findAll(Predicate search);

    @Query(value="SELECT * FROM event_history WHERE event_type = 'VISIT' AND channel_uid = ?1 AND user_uid = ?2 AND LEFT(create_date, 10) = LEFT(now(), 10) ", nativeQuery=true)
    Optional<EventHistory> findByChannelUidAndUserUidAndCreateDate(String channelUid, String userUid);
    
    Optional<EventHistory> findByEventTypeAndPostUidAndUserUid(EventType eventType, String postUid, String userUid);
    Optional<EventHistory> findByEventTypeAndCommentUidAndUserUid(EventType eventType, String commentUid, String userUid);
    Optional<EventHistory> findByEventTypeAndLikeIdxAndUserUid(EventType eventType,Integer likeIdx, String userUid);
    Optional<EventHistory> findByEventTypeAndVideoIdxAndUserUid(EventType eventType,Integer videoIdx, String userUid);


    @Query(value="SELECT COUNT(*) FROM event_history WHERE event_type = ?1 AND channel_uid = ?2 AND user_uid = ?3", nativeQuery=true)
    int calculateUserScore(String eventType, String channelUid, String userUid);

    @Query(value="SELECT event_type as eventType, COUNT(*) as count FROM event_history WHERE channel_uid = ?1 AND user_uid = ?2 GROUP BY event_type;", nativeQuery=true)
    List<EventHistoryDto.userScore> calculateUserScore(String channelUid, String userUid);
    
    @Query(value="select a.user_uid as userUid, a.actual_name as actualName, a.level as level, sum(a.point) as point from  \n" +
    "           (select eh.user_uid, (count(*) * e.point) as point, u.actual_name, cl.name as level from event_history eh \n" +
    "         left join event e on eh.event_type = e.event_type left join user u on u.uid = eh.user_uid \n" +
    "         left join channel_member cm on cm.user_uid = u.uid And cm.channel_uid = eh.channel_uid \n" +
    "                                 left join channel_level cl on cl.idx = cm.channel_level_idx \n" +
    "         where eh.create_date between DATE_SUB(now(), INTERVAL ?1 DAY) AND now() AND eh.channel_uid = ?2 \n" +
    "         group by eh.user_uid, eh.event_type, e.point, u.actual_name, cl.name) a group by user_uid, level order by point desc limit 0, 7", nativeQuery=true)
    List<EventHistoryDto.calculateRanking> calculateRanking(int day, String channelUid);

    @Query(value="select a.user_uid as userUid, a.actual_name as actualName, a.level as level, sum(a.point) as point from  \n" +
    "           (select eh.user_uid, (count(*) * e.point) as point, u.actual_name, cl.name as level from event_history eh \n" +
    "         left join event e on eh.event_type = e.event_type left join user u on u.uid = eh.user_uid \n" +
    "         left join channel_member cm on cm.user_uid = u.uid And cm.channel_uid = eh.channel_uid \n" +
    "                                 left join channel_level cl on cl.idx = cm.channel_level_idx \n" +
    "         where eh.channel_uid = ?1 \n" +
    "         group by eh.user_uid, eh.event_type, e.point, u.actual_name, cl.name) a group by user_uid, level order by point desc limit 0, 7", nativeQuery=true)
    List<EventHistoryDto.calculateRanking> calculateRankingAllDay(String channelUid);
}

