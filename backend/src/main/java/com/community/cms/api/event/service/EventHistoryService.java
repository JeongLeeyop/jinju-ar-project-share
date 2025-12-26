package com.community.cms.api.event.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.api.event.dto.mapper.EventHistoryMapper;
import com.community.cms.api.event.repository.EventHistoryRepository;
import com.community.cms.api.level.repository.ChannelLevelRepository;
import com.community.cms.entity2.ChannelLevel;
import com.community.cms.entity2.ChannelLevelSetting;
import com.community.cms.entity2.ChannelMember;
import com.community.cms.entity2.Event;
import com.community.cms.entity2.EventHistory;
import com.community.cms.entity2.EventType;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface EventHistoryService {
    // List<EventHistoryDto.list> list(SinghaUser authUser, EventSearch search);
    // Page<EventHistoryDto.detail> list(SinghaUser authUser, EventSearch search, Pageable pageable);
    // EventHistoryDto.detail detail(SinghaUser authUser, String uid);
    void add(EventHistoryDto.add addDto);
    void visitAdd(EventHistoryDto.add addDto);
    // void update(SinghaUser authUser, Event event, EventHistoryDto.update updateDto );
    void delete(SinghaUser authUser, EventHistory eventHistory);
    EventHistoryDto.rankingAll rankingAll(SinghaUser authUser, String channelUid) ;
    List<EventHistoryDto.calculateRanking> ranking(SinghaUser authUser, String channelUid, int day) ;
}

@Service
@AllArgsConstructor
class EventHistoryServiceImpl implements EventHistoryService {
    
    private final EventHistoryRepository eventHistoryRepository;
    private final ChannelLevelRepository channelLevelRepository;
    private final ChannelMemberRepository channelMemberRepository;

    @Transactional
    @Override
    public void add(EventHistoryDto.add addDto) {
        EventHistory entity = null;
        if(addDto.getEventType().equals(EventType.COMMENT.toString())) {
            if(eventHistoryRepository.findByEventTypeAndCommentUidAndUserUid(EventType.COMMENT,addDto.getCommentUid(), addDto.getUserUid()).isPresent()) return;
        } else if(addDto.getEventType().equals(EventType.LIKE.toString())) {
            if(eventHistoryRepository.findByEventTypeAndPostUidAndUserUid(EventType.LIKE, addDto.getPostUid(), addDto.getUserUid()).isPresent()) return;
        } else if(addDto.getEventType().equals(EventType.POST.toString())) {
            if(eventHistoryRepository.findByEventTypeAndPostUidAndUserUid(EventType.POST,addDto.getPostUid(), addDto.getUserUid()).isPresent()) return;
        } else if(addDto.getEventType().equals(EventType.VIDEO.toString())) {
            if(eventHistoryRepository.findByEventTypeAndVideoIdxAndUserUid(EventType.VIDEO,addDto.getVideoIdx(), addDto.getUserUid()).isPresent()) return;
        }
        entity = EventHistoryMapper.INSTANCE.addDtoToEntity(addDto);
        eventHistoryRepository.save(entity);

        // 승급
        this.levelUp(addDto);
    }

    @Transactional
    @Override
    public void visitAdd(EventHistoryDto.add addDto) {
        EventHistory entity = null;
        if(addDto.getEventType().equals(EventType.VISIT.toString())) {
            // 오늘날짜 출석
            if(eventHistoryRepository.findByChannelUidAndUserUidAndCreateDate(addDto.getChannelUid(), addDto.getUserUid()).isPresent()) return;
        }
        entity = EventHistoryMapper.INSTANCE.addDtoToEntity(addDto);
        eventHistoryRepository.save(entity);

        // 승급
        this.levelUp(addDto);
    }
   
    @Override
    public EventHistoryDto.rankingAll rankingAll(SinghaUser authUser, String channelUid) {
            EventHistoryDto.rankingAll dto = new EventHistoryDto.rankingAll();
            dto.setDay(eventHistoryRepository.calculateRanking(1, channelUid));
            dto.setWeek(eventHistoryRepository.calculateRanking(7, channelUid));
            dto.setMonth(eventHistoryRepository.calculateRanking(30, channelUid));
            dto.setAll(eventHistoryRepository.calculateRankingAllDay(channelUid));
            return dto;
    }

        @Override
        public List<EventHistoryDto.calculateRanking> ranking(SinghaUser authUser, String channelUid, int day) {
            return eventHistoryRepository.calculateRanking(day, channelUid);
        }

    @Override
    public void delete(SinghaUser authUser, EventHistory eventHistory) {
		eventHistoryRepository.delete(eventHistory);
    }

    public void levelUp(EventHistoryDto.add addDto) {
        // 승급
        String channelUid = addDto.getChannelUid();
        String userUid = addDto.getUserUid();

        Optional<ChannelMember> optional = channelMemberRepository.findByUserUidAndChannelUid(userUid, channelUid);
        if(optional.isPresent()){
            ChannelMember channelMember = optional.get();
            if(channelMember.getChannelLevel() != null){
                int level = channelMember.getChannelLevel().getLevel();
                // 레벨 9까지만 승급 가능
                if (level < 10) {
                    Optional<ChannelLevel> optional2 = channelLevelRepository.findByChannelUidAndLevel(channelUid, level + 1);
                    if(optional.isPresent()) {
                        List<ChannelLevelSetting> targetList = optional2.get().getLevelSettingList();
                        
                        int COMMENT = eventHistoryRepository.calculateUserScore(EventType.COMMENT.toString() ,channelUid, userUid);
                        int LIKE = eventHistoryRepository.calculateUserScore(EventType.LIKE.toString() ,channelUid, userUid);
                        int POST = eventHistoryRepository.calculateUserScore(EventType.POST.toString() ,channelUid, userUid);
                        int VIDEO = eventHistoryRepository.calculateUserScore(EventType.VIDEO.toString() ,channelUid, userUid);
                        int VISIT = eventHistoryRepository.calculateUserScore(EventType.VISIT.toString() ,channelUid, userUid);
                        
                        
                        boolean flag = true;
                        for(ChannelLevelSetting target: targetList){
                            if (target.getEventType().equals(EventType.COMMENT.toString()) && target.getGoal() > COMMENT) {
                                flag = false;
                                break;
                            } else if (target.getEventType().equals(EventType.LIKE.toString()) && target.getGoal() > LIKE) {
                                flag = false;
                                break;
                            } else if (target.getEventType().equals(EventType.POST.toString()) && target.getGoal() > POST) {
                                flag = false;
                                break;
                            } else if (target.getEventType().equals(EventType.VIDEO.toString()) && target.getGoal() > VIDEO) {
                                flag = false;
                                break;
                            } else if (target.getEventType().equals(EventType.VISIT.toString()) && target.getGoal() > VISIT) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            channelMember.setChannelLevelIdx(optional2.get().getIdx());
                            channelMember.setLevelUpDate(LocalDateTime.now());
                            channelMemberRepository.save(channelMember);
                        }
                    }
                }
            }
        }
    }
}
