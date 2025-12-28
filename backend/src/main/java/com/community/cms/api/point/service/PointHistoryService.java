package com.community.cms.api.point.service;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.point.dto.PointHistoryDto;
import com.community.cms.api.point.dto.mapper.PointHistoryMapper;
import com.community.cms.api.point.dto.search.PointHistorySearch;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.PointHistory;
import com.community.cms.entity.User;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface PointHistoryService {
    Page<PointHistoryDto.list> list(SinghaUser authUser, Pageable pageable);
    void addPoint(int point, String reason, String userUid);
}

@Service
@AllArgsConstructor
class PointHistoryServiceImpl implements PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public Page<PointHistoryDto.list> list(SinghaUser authUser, Pageable pageable) {
        PointHistorySearch search = new PointHistorySearch();
        search.setUserUid(authUser.getUser().getUid());
        return pointHistoryRepository.findAll(search.search(), pageable)
            .map(e -> PointHistoryMapper.INSTANCE.entityToListDto(e));
    }

    @Override
    @Transactional
    public void addPoint(int point, String reason, String userUid) {
        User user = userRepository.findById(userUid).orElseThrow(() -> new NotFoundException(NotFound.USER));
        
        // 현재 포인트 계산
        if (user.getPoint() == null) user.setPoint(0);
        int newBalance = user.getPoint() + point;
        
        // 포인트 히스토리 생성
        PointHistory history = PointHistory.builder()
                .userUid(user.getUid())
                .channelUid("default")  // 기본 채널 (추후 확장 가능)
                .pointType("MANUAL")
                .pointAmount(point)
                .currentBalance(newBalance)
                .description(reason)
                .build();
        
        pointHistoryRepository.save(history);

        // 사용자 포인트 업데이트
        user.setPoint(newBalance);
        userRepository.save(user);
    }
}
