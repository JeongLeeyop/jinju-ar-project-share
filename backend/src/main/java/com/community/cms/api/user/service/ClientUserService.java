package com.community.cms.api.user.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.security.core.session.SessionInformation;

import com.community.cms.api.channel.dto.ChannelMemberDto;
import com.community.cms.api.channel.dto.mapper.ChannelMemberMapper;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.coupon.repository.CouponRepository;
import com.community.cms.api.coupon.repository.UserCouponRepository;
import com.community.cms.api.event.repository.EventHistoryRepository;
import com.community.cms.api.post.repository.PostRepository;
import com.community.cms.api.shop.repository.ShopRepository;
import com.community.cms.api.station.repository.StationRepository;
import com.community.cms.api.user.dto.ClientUserDto;
import com.community.cms.api.user.dto.mapper.ClientUserMapper;
import com.community.cms.api.user.dto.mapper.UserMapper;
import com.community.cms.api.user.exception.UserDuplicateException;
import com.community.cms.api.user.repository.UserFcmTokenRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.api.user.repository.UserRoleRepository;
import com.community.cms.api.withdraw.repository.WithdrawHistoryRepository;
import com.community.cms.common.exception.BadRequestException;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.BadRequest;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.User;
import com.community.cms.entity.UserRole;
import com.community.cms.entity.WithdrawHistory;
import com.community.cms.entity2.ChannelMember;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

public interface ClientUserService {
    void join(ClientUserDto.join dto);

    ClientUserDto.info info(SinghaUser authUser);
    
	ClientUserDto.channelInfo channelInfo(SinghaUser authUser, String channelUid);

    void updateInfo(User user, ClientUserDto.update updateDto, SinghaUser authUser);
    
	void setShop(Integer idx, SinghaUser authUser);
	
	void setStation(Integer idx, SinghaUser authUser);

	void withdraw(ClientUserDto.withdraw dto, SinghaUser authUser);

	Map<String, String> findLatLon(String address);
}

@Service
@AllArgsConstructor
class ClientUserServiceImpl implements ClientUserService {
    private final UserRepository userRepository;
    
	private final ShopRepository shopRepository;
	
	private final StationRepository stationRepository;

	private final UserRoleRepository userRoleRepository;
	
	private final ChannelMemberRepository channelMemberRepository;
	
	private final ChannelRepository channelRepository;
	
	private final EventHistoryRepository eventHistoryRepository;

	private final TokenStore tokenStore;

	private final WithdrawHistoryRepository withdrawHistoryRepository;

	private final PostRepository postRepository;
	
	private final UserFcmTokenRepository userFcmTokenRepository;	
	
	private final UserService userServce;	

	private final PasswordEncoder passwordEncoder;

	@Autowired
    private SessionRegistry sessionRegistry;

	@Transactional
	@Override
	public void join(ClientUserDto.join dto) {
		User user = ClientUserMapper.INSTANCE.joinDtoToEntity(dto);
		user.setJoinStatus(true);
		user.setPoint(0);
		if (!userServce.userEmailCheck(user.getEmail())) throw new UserDuplicateException("이미 사용중인 아이디 입니다.");
		user.setUserId(user.getEmail());
		user = userRepository.save(user);

		// 무조건 ROLE_CREATOR 권한 부여
		UserRole creatorRole = new UserRole();
		creatorRole.setUserUid(user.getUid());
		creatorRole.setRoleCode("ROLE_CREATOR");
		userRoleRepository.save(creatorRole);

		// ROLE_USER 권한도 함께 부여
		UserRole userRole = new UserRole();
		userRole.setUserUid(user.getUid());
		userRole.setRoleCode("ROLE_USER");
		userRoleRepository.save(userRole);

		// Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName("singha_oauth", user.getUserId());
		// for (OAuth2AccessToken token : tokens) {
			// tokenStore.removeAccessToken(token);
		// }
	}

	@Transactional
	@Override
	public ClientUserDto.info info(SinghaUser authUser) {
		User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
			ClientUserDto.info dto = ClientUserMapper.INSTANCE.entityToInfoDto(user);

			List<SessionInformation> sessions = sessionRegistry.getAllSessions(authUser, false);
			if(sessions.size() > 0) dto.setIsOnline(true);
			else dto.setIsOnline(false);
		return dto;
	}

	@Transactional
	@Override
	public ClientUserDto.channelInfo channelInfo(SinghaUser authUser, String channelDomain) {
		User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
		ClientUserDto.channelInfo dto = ClientUserMapper.INSTANCE.entityToChannelInfoDto(user);
		
		// domain → uid 변환
		String channelUid = channelRepository.findByDomain(channelDomain)
				.map(channel -> channel.getUid())
				.orElse(channelDomain);  // domain이 아니면 uid로 간주
		
		Optional<ChannelMember> optional = channelMemberRepository.findByUserUidAndChannelUid(user.getUid(), channelUid);
		if(optional.isPresent()){
			ChannelMember entity = optional.get();
			ChannelMemberDto.detail memberDto = ChannelMemberMapper.INSTANCE.entityToDetailDto(entity, null);
			dto.setChannelMember(memberDto);
			dto.setUserScoreList(eventHistoryRepository.calculateUserScore(channelUid, user.getUid()));
		}
		return dto;
	}

	@Transactional
	@Override
	public void updateInfo(User user, ClientUserDto.update updateDto, SinghaUser authUser) {
		// 최신 User 정보 다시 로드
		User freshUser = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
		if (!freshUser.getUid().equals(authUser.getUser().getUid())) {
			throw new NotFoundException(NotFound.USER);
		}
		
		// 비밀번호 변경 로직
		if (updateDto.getCurrentPassword() != null && !updateDto.getCurrentPassword().isEmpty()) {
			// 현재 비밀번호 확인 (최신 User 데이터로 검증)
			if (!passwordEncoder.matches(updateDto.getCurrentPassword(), freshUser.getUserPassword())) {
				throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
			}
			
			// 새 비밀번호 검증
			if (updateDto.getNewPassword() == null || updateDto.getNewPassword().isEmpty()) {
				throw new RuntimeException("새 비밀번호를 입력해주세요.");
			}
			
			if (updateDto.getNewPassword().length() < 8 || updateDto.getNewPassword().length() > 20) {
				throw new RuntimeException("새 비밀번호는 8~20자로 입력해주세요.");
			}
			
			// 새 비밀번호와 새 비밀번호 확인 일치 여부 검증
			if (updateDto.getNewPasswordCheck() == null || updateDto.getNewPasswordCheck().isEmpty()) {
				throw new RuntimeException("새 비밀번호 확인을 입력해주세요.");
			}
			
			if (!updateDto.getNewPassword().equals(updateDto.getNewPasswordCheck())) {
				throw new RuntimeException("새 비밀번호가 일치하지 않습니다.");
			}
			
			// 새 비밀번호 암호화하여 저장
			// setUserPassword()는 자동으로 암호화하므로, 이미 암호화된 값을 저장하려면 setOriginalUserPassword() 사용
			freshUser.setOriginalUserPassword(passwordEncoder.encode(updateDto.getNewPassword()));
		}
		
		// 매퍼를 통해 다른 필드들 업데이트 (userPassword는 무시됨)
		freshUser = ClientUserMapper.INSTANCE.updateDtoToEntity(updateDto, freshUser);
		userRepository.save(freshUser);
	}

	@Override
	public void setShop(Integer idx, SinghaUser authUser) {
		shopRepository.findById(idx).orElseThrow(() -> new NotFoundException(NotFound.SHOP));
		User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
		user.setShopIdx(idx);
		userRepository.save(user);
	}

	@Override
	public void setStation(Integer idx, SinghaUser authUser) {
		stationRepository.findById(idx).orElseThrow(() -> new NotFoundException(NotFound.STATION));
		User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
		user.setStationIdx(idx);
		userRepository.save(user);
	}

	@Transactional
	@Override
	public void withdraw(ClientUserDto.withdraw dto, SinghaUser authUser) {
		// int remainOrderCount = productOrderService.getRemainCount(authUser);
		int remainOrderCount = 0;
		if (remainOrderCount > 0) throw new BadRequestException(BadRequest.REMAIN_ORDER);

		User user = userRepository.findById(authUser.getUser().getUid()).orElseThrow(() -> new NotFoundException(NotFound.USER));
		
		userRoleRepository.deleteByUserUid(user.getUid());
		userFcmTokenRepository.deleteByUserUid(user.getUid());
		
		WithdrawHistory withdrawHistory = new WithdrawHistory();
		withdrawHistory.setReason(dto.getReason());
		withdrawHistory.setUserName(user.getActualName());
		withdrawHistory.setUserUid(user.getUid());
		withdrawHistory.setUserId(user.getUserId());
		withdrawHistoryRepository.save(withdrawHistory);
		
		postRepository.deleteWithdrawUser(user.getUid());
		
		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName("singha_oauth", user.getUserId());
		for (OAuth2AccessToken token : tokens) {
			tokenStore.removeAccessToken(token);
		}
		
		userRepository.delete(user);
	}

    @Override
    public Map<String, String> findLatLon(String address) {
        final String HOST = "http://dapi.kakao.com";
        URI targetUrl = UriComponentsBuilder.fromUriString(HOST+"/v2/local/search/address.json")
            .queryParam("query", address)
            .queryParam("page", "1")
            .queryParam("size", "10")
            .build()
            .encode(StandardCharsets.UTF_8).toUri();
        RestTemplate template = new RestTemplate();
        
        HttpHeaders headers  = new HttpHeaders();
        headers.add("Authorization", "KakaoAK ce5081b76da6325966891111d1231612");
        HttpEntity entity = new HttpEntity<>(headers); 
        
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<String>() {};
        
        ResponseEntity<String> response = template.exchange(targetUrl, HttpMethod.GET, entity, responseType);
        String responseBody = response.getBody();
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray jsonArray = jsonObject.getJSONArray("documents");
           
			Map<String, String> map = new HashMap<String, String>();
			if (jsonArray.length() > 0) {
                JSONObject addressData = jsonArray.getJSONObject(0);
                if (addressData.has("y")) map.put("Lat",addressData.getString("y"));
                if (addressData.has("x")) map.put("Lon",addressData.getString("x"));
            }
			return map;
        } catch (Exception e) {
			e.printStackTrace();
        }
		return null;
    }

}