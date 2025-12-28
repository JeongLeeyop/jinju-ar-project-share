package com.community.cms.api.new_alarm.service;

import java.util.List;

import org.springframework.stereotype.Service;

// import com.community.cms.api.post.model.dto.PostDto;
import com.community.cms.api.new_alarm.dto.NewAlarmDto;
import com.community.cms.api.new_alarm.repository.NewAlarmRepository;
import com.community.cms.api.user.repository.UserRepository;
// import com.community.cms.config.WebUtilConfig;
import com.community.cms.entity.NewAlarm;
import com.community.cms.entity.User;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface NewAlarmService {
    Integer count(SinghaUser authUser);
    void add(NewAlarmDto.Add addDto);
    void check(SinghaUser authUser, String postUid);
}

@Service
@AllArgsConstructor
class NewAlarmServiceImpl implements NewAlarmService {
    private final NewAlarmRepository newAlarmRepository;
    private final UserRepository userRepository;

    @Override
    public Integer count(SinghaUser authUser) {
        if (authUser != null) {
            return (int) newAlarmRepository.count(authUser.getUser().getUid());
        }
        return null;
    }

    @Override
    public void add(NewAlarmDto.Add item) {
        if(item.getPostUid() != null){
            List<User> userList = userRepository.findAll();
            for(User user : userList){
                NewAlarm data = new NewAlarm();
                data.setPostUid(item.getPostUid());
                data.setType(item.getType());
                data.setCheckStatus(false);
                data.setUserUid(user.getUid());
                newAlarmRepository.save(data);
            }
        } 
    }

    @Override
    public void check(SinghaUser authUser, String postUid) {
       newAlarmRepository.check(authUser.getUser().getUid(),postUid);        
    }
}