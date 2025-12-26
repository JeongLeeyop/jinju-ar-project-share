package com.community.cms.api.push_alarm.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.community.cms.entity.User;

import lombok.Data;

public class PushAlarmDto {
    @Data
    public static class Add {
        private String userUid;
        private String title;
        private String content;
        private String link;
		private List<User> userUidList = new ArrayList<User>();
    }

    @Data
    public static class model {
        private int newCount;
        private Page<Detail> alarmList;
    }

    @Data
    public static class Detail {
        private Integer id;
        private String title;
        private String content;
        private String link;
        private LocalDateTime createDate;
    }
}
