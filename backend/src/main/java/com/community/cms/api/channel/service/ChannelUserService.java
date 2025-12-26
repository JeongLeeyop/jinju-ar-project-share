// package com.community.cms.api.channel.service;

// import java.time.LocalDateTime;
// import java.time.temporal.ChronoUnit;

// import javax.transaction.Transactional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.community.cms.api.channel.dto.ChannelUserDto;
// // import com.community.cms.api.channel.dto.mapper.ChannelUserMapper;
// import com.community.cms.api.channel.exception.UserAlreadyJoinException;
// import com.community.cms.api.channel.repository.ChannelRepository;
// import com.community.cms.api.channel.repository.ChannelUserRepository;
// import com.community.cms.api.point.repository.PointHistoryRepository;
// import com.community.cms.api.point.service.PointHistoryService;
// import com.community.cms.api.push_alarm.service.PushAlarmService;
// import com.community.cms.api.user.repository.UserFcmTokenRepository;
// import com.community.cms.common.exception.NotFoundException;
// import com.community.cms.common.exception.code.NotFound;
// import com.community.cms.entity2.Channel;
// // import com.community.cms.entity.ChannelUser;
// import com.community.cms.entity.User;
// import com.community.cms.fcm.service.PushNotificationService;
// import com.community.cms.oauth.SinghaUser;

// import lombok.AllArgsConstructor;

// public interface ChannelUserService {
//     void add(SinghaUser authUser, ChannelUserDto.add addDto );
// }

// @Service
// @AllArgsConstructor
// class ChannelUserServiceImpl implements ChannelUserService {
    
//     private final ChannelUserRepository channelUserRepository;
//     private final ChannelRepository channelRepository;
    
//     @Transactional
//     @Override
//     public void add(SinghaUser authUser, ChannelUserDto.add addDto) {
//         // ChannelUser entity = null;
//         User user = authUser.getUser();
//         if(user == null) new NotFoundException(NotFound.USER);
//         int cnt = channelUserRepository.countByUserUidAndChannelUid(user.getUid(), addDto.getChannelUid());
//         if(cnt > 0) throw new UserAlreadyJoinException();
//         addDto.setUserUid(user.getUid());
//         Channel Channel = channelRepository.findById(addDto.getChannelUid()).orElseThrow(() -> new NotFoundException(NotFound.Channel));
//         Integer totalDay = Channel.getTotalDay();
//         LocalDateTime now = LocalDateTime.now();
//         LocalDateTime futureDateTime = now.plus(totalDay, ChronoUnit.DAYS);
//         addDto.setDueDate(futureDateTime);
//         entity = ChannelUserMapper.INSTANCE.addDtoToEntity(addDto);
//         channelUserRepository.save(entity);
//     }
// }
