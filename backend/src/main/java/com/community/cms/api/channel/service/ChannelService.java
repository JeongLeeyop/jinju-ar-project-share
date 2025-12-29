package com.community.cms.api.channel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.channel.dto.ChannelMemberDto;
import com.community.cms.api.channel.dto.ChannelQuestionDto;
import com.community.cms.api.channel.dto.ChannelSearch;
import com.community.cms.api.channel.dto.mapper.ChannelMapper;
import com.community.cms.api.channel.dto.mapper.ChannelMemberQuestionMapper;
import com.community.cms.api.channel.repository.ChannelImageRepository;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.channel.repository.ChannelQuestionRepository;
// import com.community.cms.api.channel.dto.mapper.ChannelMapper;
// import com.community.cms.api.channel.dto.mapper.ChannelUserMapper;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.api.event.repository.EventHistoryRepository;
import com.community.cms.api.event.service.EventHistoryService;
import com.community.cms.api.level.repository.ChannelLevelDefaultRepository;
import com.community.cms.api.level.repository.ChannelLevelRepository;
import com.community.cms.api.level.repository.ChannelLevelSettingDefaultRepository;
import com.community.cms.api.level.repository.ChannelLevelSettingRepository;
import com.community.cms.api.new_alarm.dto.NewAlarmDto;
import com.community.cms.api.new_alarm.service.NewAlarmService;
import com.community.cms.api.channel.repository.ChannelUseCategoryRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.point.service.PointHistoryService;
import com.community.cms.api.push_alarm.dto.PushAlarmDto;
import com.community.cms.api.push_alarm.service.PushAlarmService;
import com.community.cms.api.user.exception.UserAccessDeniedException;
import com.community.cms.api.user.exception.UserNotFoundException;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.AlreadyJoinChannelException;
import com.community.cms.common.exception.DomainDuplicateException;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.NotVaildJoinChannelException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.NewAlarm;
// import com.community.cms.entity.ChannelUser;
import com.community.cms.entity.User;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelLevel;
import com.community.cms.entity2.ChannelLevelDefault;
import com.community.cms.entity2.ChannelLevelSetting;
import com.community.cms.entity2.ChannelLevelSettingDefault;
import com.community.cms.entity2.ChannelMember;
import com.community.cms.entity2.ChannelQuestion;
import com.community.cms.entity2.EventType;
import com.community.cms.fcm.service.PushNotificationService;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface ChannelService {
    ChannelDto.detail detail(SinghaUser authUser, String uid);

    ChannelDto.detail domainDetail(SinghaUser authUser, String domain);

    List<ChannelDto.detail> list(SinghaUser authUser, ChannelSearch search);

    Page<ChannelDto.detail> list(SinghaUser authUser, ChannelSearch search, Pageable pageable);

    ChannelDto.myList myList(SinghaUser authUser);

    void add(SinghaUser authUser, ChannelDto.add addDto);

    void update(SinghaUser authUser, Channel Channel, ChannelDto.update updateDto);

    void delete(SinghaUser authUser, Channel Channel);

    boolean checkDomainDuplicate(String domain);

    void join(SinghaUser authUser, ChannelMemberDto.add addDto);

    boolean validateCode(Channel channel, String code);

    Long getMemberCountByDomain(String domain);
}

@Service
@AllArgsConstructor
class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    // private final ChannelUserRepository channelUserRepository;
    // private final ChannelRecordRepository channelRecordRepository;
    private final UserRepository userRepository;

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
    ChannelMemberRepository channelMemberRepository;

    @Autowired
    ChannelImageRepository channelImageRepository;

    @Autowired
    EventHistoryRepository eventHistoryRepository;

    @Autowired
    EventHistoryService eventHistoryService;

    @Autowired
    ChannelLevelRepository channelLevelRepository;

    @Autowired
    ChannelLevelDefaultRepository channelLevelDefaultRepository;

    @Autowired
    ChannelLevelSettingRepository channelLevelSettingRepository;

    @Autowired
    ChannelLevelSettingDefaultRepository channelLevelSettingDefaultRepository;

    @Autowired
    ChannelUseCategoryRepository channelUseCateogryRepository;

    @Autowired
    ChannelQuestionRepository channelQuestionRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Override
    public ChannelDto.detail detail(SinghaUser authUser, String uid) {
        // if(authUser == null){throw new UserNotFoundException();}
        Optional<Channel> optional = channelRepository.findById(uid);
        optional.orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
        ChannelDto.detail dto = ChannelMapper.INSTANCE.entityToDetail(optional.get());

        // 개설자 이름 설정
        Optional<User> creator = userRepository.findById(dto.getUserUid());
        if (creator.isPresent()) {
            dto.setCreatorName(creator.get().getActualName());
        }

        // 카테고리 이름 설정 (첫 번째 카테고리)
        if (dto.getCategoryList() != null && !dto.getCategoryList().isEmpty()) {
            // categoryList의 첫 번째 항목에서 카테고리 이름 추출
            if (dto.getCategoryList().get(0).getCategory() != null) {
                dto.setCategoryName(dto.getCategoryList().get(0).getCategory().getName());
            }
        }

        // 현재 로그인된 유저의 가입 유무를 확인
        if (authUser != null) {
            Optional<ChannelMember> channelMember = channelMemberRepository
                    .findByUserUidAndChannelUid(authUser.getUser().getUid(), dto.getUid());
            dto.setMyJoinStatus(channelMember.isPresent());
            dto.setMyApprovalStatus(channelMember.isPresent() && channelMember.get().isApprovalStatus());
        }
        // ChannelUser result =
        // channelUserRepository.findByUserUidAndChannelUid(authUser.getUser().getUid(),
        // uid);
        // if (result != null) {
        // dto.setUserJoinStatus(true);
        // dto.setChannelUser(ChannelUserMapper.INSTANCE.entityToDetail(result));
        // } else dto.setUserJoinStatus(false);

        // int cnt2 = channelRecordRepository.countTodayChannel(user.getUid(), uid);
        // if(cnt2 > 0) dto.setTodayWriteStatus(true);
        // else dto.setTodayWriteStatus(false);

        return dto;
    }

    @Override
    public ChannelDto.detail domainDetail(SinghaUser authUser, String domain) {
        Optional<Channel> optional = channelRepository.findByDomain(domain);
        optional.orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
        ChannelDto.detail dto = ChannelMapper.INSTANCE.entityToDetail(optional.get());

        // 개설자 이름 설정
        Optional<User> creator = userRepository.findById(dto.getUserUid());
        if (creator.isPresent()) {
            dto.setCreatorName(creator.get().getActualName());
            dto.setUserId(creator.get().getUserId());
        }

        // 카테고리 이름 설정 (첫 번째 카테고리)
        if (dto.getCategoryList() != null && !dto.getCategoryList().isEmpty()) {
            // categoryList의 첫 번째 항목에서 카테고리 이름 추출
            if (dto.getCategoryList().get(0).getCategory() != null) {
                dto.setCategoryName(dto.getCategoryList().get(0).getCategory().getName());
            }
        }

        // 현재 로그인된 유저의 가입 유무를 확인
        if (authUser != null) {
            Optional<ChannelMember> channelMember = channelMemberRepository
                    .findByUserUidAndChannelUid(authUser.getUser().getUid(), dto.getUid());
            dto.setMyJoinStatus(channelMember.isPresent());
            dto.setMyApprovalStatus(channelMember.isPresent() && channelMember.get().isApprovalStatus());
            dto.setMyChannelStatus(dto.getUserUid().equals(authUser.getUser().getUid()));
            
            // TO-DO 소개페이지는 카운트 안되게 해야함
            eventHistoryService.visitAdd(new EventHistoryDto.add(EventType.VISIT.toString(),
                    authUser.getUser().getUid(), dto.getUid(), null, null, null, null));
        }
        return dto;
    }

    /*
     * TODO
     * 가입여부 : myJoinStatus 처리
     */
    @Override
    public List<ChannelDto.detail> list(SinghaUser authUser, ChannelSearch search) {
        // search.setUserUid(authUser.getUser().getUid());
        return null;
        // return channelRepository.findAll(search.search()).stream().map(entity ->
        // ChannelMapper.INSTANCE.entityToDetailMyFlag(entity,
        // search)).collect(Collectors.toList());
    }

    @Override
    public Page<ChannelDto.detail> list(SinghaUser authUser, ChannelSearch search, Pageable pageable) {
        return channelRepository.findAll(search.search(), pageable)
                .map(entity -> ChannelMapper.INSTANCE.entityToDetail(entity));
    }

    @Override
    public ChannelDto.myList myList(SinghaUser authUser) {
        if (authUser == null)
            throw new UserNotFoundException("잘못된 접근입니다.");
        ChannelDto.myList dto = new ChannelDto.myList();
        dto.setJoinList(channelRepository.findByChannelMemberUserUid(authUser.getUser().getUid()).stream()
                .map(entity -> ChannelMapper.INSTANCE.entityToList(entity)).collect(Collectors.toList()));
        dto.setMyList(channelRepository.findByUserUid(authUser.getUser().getUid()).stream()
                .map(entity -> ChannelMapper.INSTANCE.entityToList(entity)).collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    @Override
    public void add(SinghaUser authUser, ChannelDto.add addDto) {
        // 모든 회원이 커뮤니티 생성 가능
        if (!this.checkDomainDuplicate(addDto.getDomain())) {
            throw new DomainDuplicateException();
        }
        Channel entity = null;
        User user = authUser.getUser();
        entity = ChannelMapper.INSTANCE.addDtoToEntity(addDto);
        entity.setUserUid(user.getUid());
        entity.setDeleteStatus(false); // 기본적으로 보이도록 설정
        entity = channelRepository.save(entity);

        // 기본레벨 세팅
        List<ChannelLevelDefault> levelList = channelLevelDefaultRepository.findAllByUseYnOrderByLevel(true);
        for (ChannelLevelDefault levelDefault : levelList) {
            ChannelLevel level = new ChannelLevel();
            level.setChannelUid(entity.getUid());
            level.setDescription(levelDefault.getDescription());
            level.setIcon(levelDefault.getIcon());
            level.setLevel(levelDefault.getLevel());
            level.setName(levelDefault.getName());

            level = channelLevelRepository.save(level);

            List<ChannelLevelSettingDefault> settingList = channelLevelSettingDefaultRepository
                    .findAllByUseYnAndLevel(true, levelDefault.getLevel());
            for (ChannelLevelSettingDefault settingDefault : settingList) {
                ChannelLevelSetting setting = new ChannelLevelSetting();
                setting.setChannelLevelIdx(level.getIdx());
                setting.setEventType(settingDefault.getEventType());
                setting.setGoal(settingDefault.getGoal());

                channelLevelSettingRepository.save(setting);
            }
        }
    }

    @Transactional
    @Override
    public void update(SinghaUser authUser, Channel entity, ChannelDto.update updateDto) {
        // 모든 회원이 커뮤니티 수정 가능
        /*
         * TODO
         * 수정할 채널의 소유자 uid와 현재 로그인한 사용자의 uid와 일치하지 않을 경우 예외
         */
        channelImageRepository.deleteByChannelUid(entity.getUid());
        channelUseCateogryRepository.deleteByChannelUid(entity.getUid());

        entity = ChannelMapper.INSTANCE.updateDtoToEntity(updateDto, entity);

        if (updateDto.isQuestionChangeFlag()) {
            List<ChannelQuestion> existingQuestions = entity.getQuestionList(); // 기존 질문 리스트
            List<ChannelQuestionDto.update> dtoList = updateDto.getQuestionList();
            existingQuestions.clear();

            int index = 0;
            for (ChannelQuestionDto.update dto : dtoList) {
                ChannelQuestion cq = new ChannelQuestion();
                cq.setChannelUid(entity.getUid());
                cq.setTitle(dto.getTitle());
                cq.setViewOrder(index++);
                existingQuestions.add(cq);
            }
        }

        // TODO updateDate추가
        // entity.setUpdateDate(LocalDateTime.now());
        channelRepository.save(entity);
    }

    @Override
    public void delete(SinghaUser authUser, Channel Channel) {
        channelRepository.delete(Channel);
    }

    @Override
    public boolean checkDomainDuplicate(String domain) {
        return channelRepository.findByDomain(domain).isEmpty();
    }

    @Transactional
    @Override
    public void join(SinghaUser authUser, ChannelMemberDto.add addDto) {
        if (authUser == null)
            throw new UserNotFoundException();
        if (channelMemberRepository.findByUserUidAndChannelUid(authUser.getUser().getUid(), addDto.getChannelUid())
                .isPresent()) {
            throw new AlreadyJoinChannelException();
        }
        Channel channel = channelRepository.findById(addDto.getChannelUid())
                .orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
        if (channel.getUserUid().equals(authUser.getUser().getUid())) {
            throw new NotVaildJoinChannelException("본인의 커뮤니티에는 가입할 수 없습니다.");
        }
        ChannelMember entity = new ChannelMember();
        entity.setChannelUid(addDto.getChannelUid());
        entity.setUserUid(authUser.getUser().getUid());
        entity.setIntroduce(addDto.getIntroduce());
        entity.setQuestionList(ChannelMemberQuestionMapper.INSTANCE.addListDtoToEntityList(addDto.getQuestionList()));

        // 승인전
        entity.setApprovalStatus(false);

        // 해당 채널의 설정된 레벨1로 이동
        Optional<ChannelLevel> channelLevel = channelLevelRepository.findByChannelUidAndLevel(addDto.getChannelUid(),
                1);
        if (channelLevel.isPresent()) {
            entity.setChannelLevelIdx(channelLevel.get().getIdx());
        }
        channelMemberRepository.save(entity);

        // 크리에이터에게 알람 전송
        PushAlarmDto.Add pushAlarmDto = new PushAlarmDto.Add();
        pushAlarmDto.setUserUid(channel.getUserUid());
        pushAlarmDto.setTitle("커뮤니티 가입 요청");
        pushAlarmDto.setContent("[" + channel.getName() + "] 커뮤니티에 가입 요청이 들어왔습니다.");
        pushAlarmDto.setLink("/creator/" + channel.getDomain() + "/memberSetting");
        pushAlarmDto.setUserUidList(null);
        pushAlarmService.add(pushAlarmDto);

        // 사용자에게
        PushAlarmDto.Add pushAlarmDto2 = new PushAlarmDto.Add();
        pushAlarmDto2.setUserUid(authUser.getUser().getUid());
        pushAlarmDto2.setTitle("커뮤니티 가입 신청");
        pushAlarmDto2.setContent("[" + channel.getName() + "] 커뮤니티에 가입을 신청하셨습니다.");
        pushAlarmDto2.setLink("/channel/" + channel.getDomain());
        pushAlarmDto2.setUserUidList(null);
        pushAlarmService.add(pushAlarmDto2);
    }

    @Override
    public boolean validateCode(Channel channel, String code) {
        return channel.getPassword().equals(code);
    };

    @Override
    public Long getMemberCountByDomain(String domain) {
        Optional<Channel> channelOpt = channelRepository.findByDomain(domain);
        if (channelOpt.isPresent()) {
            Channel channel = channelOpt.get();
            // Count approved members only
            return channelMemberRepository.countByChannelUidAndApprovalStatus(channel.getUid(), true);
        }
        return 0L;
    }

    // @Override
    // public void updateSecretStatus(SinghaUser authUser,
    // ChannelDto.updateSecretStatus updateDto){
    // Optional<Channel> optional = channelRepository.findById(updateDto.getIdx());
    // if (optional.isPresent()) {
    // Channel entity = optional.get();
    // if(authUser.getUser().getUid().equals(entity.getUserUid())){
    // entity.setSecretStatus(updateDto.isSecretStatus());
    // channelRepository.save(entity);
    // }else throw new BadRequestException(BadRequest.NOT_MINE);
    // } else throw new NotFoundException(NotFound.Channel);
    // }

    private void clearRelation(Channel entity) {
        entity.getImagesList().forEach(x -> x.setChannel(null));
    }
}
