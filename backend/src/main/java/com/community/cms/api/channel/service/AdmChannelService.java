package com.community.cms.api.channel.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.channel.dto.ChannelSearch;
import com.community.cms.api.channel.dto.mapper.AdmChannelMapper;
import com.community.cms.api.channel.dto.mapper.ChannelMapper;
import com.community.cms.api.channel.repository.AdmChannelRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.level.repository.ChannelLevelDefaultRepository;
import com.community.cms.api.level.repository.ChannelLevelRepository;
import com.community.cms.api.level.repository.ChannelLevelSettingDefaultRepository;
import com.community.cms.api.level.repository.ChannelLevelSettingRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.dto.PushAlarmDto;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.dto.UserFcmToken;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelLevel;
import com.community.cms.entity2.ChannelLevelDefault;
import com.community.cms.entity2.ChannelLevelSetting;
import com.community.cms.entity2.ChannelLevelSettingDefault;
import com.community.cms.entity.User;
import com.community.cms.fcm.model.PushNotificationRequest;
import com.community.cms.fcm.service.PushNotificationService;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface AdmChannelService {
    ChannelDto.detail detail(SinghaUser authUser, String idx);
    List<ChannelDto.detail> list(SinghaUser authUser, ChannelSearch search);
    Page<ChannelDto.detail> list(SinghaUser authUser, ChannelSearch search, Pageable pageable);
    void add(SinghaUser authUser, ChannelDto.add addDto );
    void update(SinghaUser authUser, Channel Channel, ChannelDto.update updateDto );
    void delete(SinghaUser authUser, Channel Channel);
}

@Service
@AllArgsConstructor
class AdmChannelServiceImpl implements AdmChannelService {
    
    private final AdmChannelRepository admChannelRepository;
    
    @Autowired
    PushAlarmService pushAlarmService;
    
    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    PointHistoryService pointHistoryService;
    
    @Autowired
    UserFcmTokenRepository userFcmTokenRepository;

    @Autowired
    ChannelLevelRepository channelLevelRepository;
    
    @Autowired
    ChannelLevelDefaultRepository channelLevelDefaultRepository;

    @Autowired
    ChannelLevelSettingRepository channelLevelSettingRepository;
    
    @Autowired
    ChannelLevelSettingDefaultRepository channelLevelSettingDefaultRepository;

    @Override
    public ChannelDto.detail detail(SinghaUser authUser, String uid) {
        // search.setUserUid(authUser.getUser().getUid());
        Optional<Channel> optional = admChannelRepository.findById(uid);
        optional.orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
        ChannelDto.detail dto = ChannelMapper.INSTANCE.entityToDetail(optional.get());
        return dto;
    }

    @Override
    public List<ChannelDto.detail> list(SinghaUser authUser, ChannelSearch search) {
        return admChannelRepository.findAll(search.search()).stream().map(entity -> ChannelMapper.INSTANCE.entityToDetail(entity)).collect(Collectors.toList());
    }
    
    @Override
    public Page<ChannelDto.detail> list(SinghaUser authUser, ChannelSearch search, Pageable pageable) {
        return admChannelRepository.findAll(search.search(), pageable).map(entity -> ChannelMapper.INSTANCE.entityToDetail(entity));
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, ChannelDto.add addDto) {
        Channel entity = null;
        User user = authUser.getUser();
        entity = AdmChannelMapper.INSTANCE.addDtoToEntity(addDto);
        entity.setUserUid(user.getUid());
        // if (!StringUtils.hasText(addDto.getWriter())){
        //     StringBuilder sb = new StringBuilder(user.getActualName());
        //     for (int i = 1; i < user.getActualName().length(); i++) {
        //         sb.setCharAt(i, '*');
        //     }
        //     entity.setWriter(sb.toString());
        // }
        admChannelRepository.save(entity);

        // 기본레벨 세팅
        List<ChannelLevelDefault> levelList = channelLevelDefaultRepository.findAllByUseYnOrderByLevel(true);
        for(ChannelLevelDefault levelDefault : levelList) {
            ChannelLevel level = new ChannelLevel();
            level.setChannelUid(entity.getUid());
            level.setDescription(levelDefault.getDescription());
            level.setIcon(levelDefault.getIcon());
            level.setLevel(levelDefault.getLevel());
            level.setName(levelDefault.getName());

            channelLevelRepository.save(level);

            List<ChannelLevelSettingDefault> settingList = channelLevelSettingDefaultRepository.findAllByUseYnAndLevel(true, levelDefault.getLevel());
            for(ChannelLevelSettingDefault settingDefault : settingList) {
                ChannelLevelSetting setting = new ChannelLevelSetting();
                setting.setChannelLevelIdx(level.getIdx());
                setting.setEventType(settingDefault.getEventType());
                setting.setGoal(settingDefault.getGoal());

                channelLevelSettingRepository.save(setting);
            }
        }
    }
    
    @Override
    public void update(SinghaUser authUser, Channel Channel, ChannelDto.update updateDto) {
        Channel = AdmChannelMapper.INSTANCE.updateDtoToEntity(updateDto, Channel);
		admChannelRepository.save(Channel);
    }
    
    @Override
    public void delete(SinghaUser authUser, Channel Channel) {
		admChannelRepository.delete(Channel);
    }

    // @Override
    // public void updateSecretStatus(SinghaUser authUser, ChannelDto.updateSecretStatus updateDto){
    //     Optional<Channel> optional = admChannelRepository.findById(updateDto.getIdx());
    //     if (optional.isPresent()) {
    //         Channel entity = optional.get();
    //         if(authUser.getUser().getUid().equals(entity.getUserUid())){
    //             entity.setSecretStatus(updateDto.isSecretStatus());
    //             admChannelRepository.save(entity);
    //         }else throw new BadRequestException(BadRequest.NOT_MINE);
    //     } else throw new NotFoundException(NotFound.Channel);
    // }
    
    private void sendPushAlarm(User user, Object obj) {

        String content = null;
        String link = null;

        ChannelDto.add addDto = (ChannelDto.add) obj;
        content = "신규 챌린지가 등록되었습니다.";
        link = "/Channel/?tfseDate=";

        String title = "지금 바로 웨일리잇 앱에서";

        UserFcmToken fcmToken = userFcmTokenRepository.findById(user.getUid()).orElse(null);

        // 토큰이 있으면 FCM 알림 전송
        if(fcmToken!=null){
            PushNotificationRequest pushRequest = new PushNotificationRequest(title,content,link,fcmToken.getToken(),null);
            pushNotificationService.sendPushNotificationToToken(pushRequest);
        }

        // 푸쉬알람 저장
        PushAlarmDto.Add pushAlarmDto = new PushAlarmDto.Add();
        pushAlarmDto.setUserUid(user.getUid());
        pushAlarmDto.setTitle(title);
        pushAlarmDto.setContent(content);
        pushAlarmDto.setLink(link);
        pushAlarmDto.setUserUidList(null);
        pushAlarmService.add(pushAlarmDto);
    }
}
