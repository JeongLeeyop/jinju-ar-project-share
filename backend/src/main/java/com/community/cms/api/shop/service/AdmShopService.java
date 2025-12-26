package com.community.cms.api.shop.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.community.cms.api.shop.dto.AdmShopDto;
import com.community.cms.api.shop.dto.AdmShopPickupTimeDto;
import com.community.cms.api.shop.dto.mapper.AdmShopMapper;
import com.community.cms.api.shop.dto.search.AdmShopSearch;
import com.community.cms.api.shop.repository.ShopRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.api.user.service.UserService;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.Shop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

public interface AdmShopService {
    Page<AdmShopDto.list> list(Pageable pageable, AdmShopSearch search);
    AdmShopDto.detail detail(Integer idx);
    void add(AdmShopDto.add addDto);
    void update(Integer idx, AdmShopDto.update updateDto);
    void withdraw(Integer idx);
    void setLanLon(Shop entity); // 위도,경도 적용
    List<AdmShopDto.list> listAll();
}

@Service
@AllArgsConstructor
@Slf4j
class AdmShopServiceImpl implements AdmShopService {
    private final ShopRepository shopRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public Page<AdmShopDto.list> list(Pageable pageable, AdmShopSearch search) {
        return shopRepository.findAll(search.search(), pageable).map(e -> AdmShopMapper.INSTANCE.entityToListDto(e));
    }

    @Override
    public AdmShopDto.detail detail(Integer idx) {
        Shop entity = shopRepository.findById(idx).orElseThrow(() -> new NotFoundException(NotFound.SHOP));
        // AdmShopDto.detail dto = AdmShopMapper.INSTANCE.entityToDetailDto(entity);
        AdmShopDto.detail dto = null;
        return dto;
    }

    @Transactional
    @Override
    public void add(AdmShopDto.add addDto) {
        // Shop entity = AdmShopMapper.INSTANCE.addDtoToEntity(addDto);
        // if (StringUtils.hasText(entity.getAddress())) setLanLon(entity);
        // savePickupTimes(entity, addDto.getPickupTimes());
        // shopRepository.save(entity);
    }

    @Transactional
    @Override
    public void update(Integer idx, AdmShopDto.update updateDto) {
        Shop entity = shopRepository.findById(idx).orElseThrow(() -> new NotFoundException(NotFound.SHOP));
        // shopPickupTimeRepository.deleteByShopIdx(idx);

        // entity = AdmShopMapper.INSTANCE.updateDtoToEntity(updateDto, entity);
        if (StringUtils.hasText(entity.getAddress())) setLanLon(entity);

        // entity.getPickupTimes().clear();

        savePickupTimes(entity, updateDto.getPickupTimes());
        shopRepository.save(entity);
    }

    @Transactional
    @Override
    public void withdraw(Integer idx) {
        Shop entity = shopRepository.findById(idx).orElseThrow(() -> new NotFoundException(NotFound.SHOP));
        entity.setWithdrawDate(LocalDateTime.now());
        entity.setWithdrawStatus(true);
        shopRepository.save(entity);
    }

    @Override
    public void setLanLon(Shop shop) {
        final String HOST = "http://dapi.kakao.com";
        URI targetUrl = UriComponentsBuilder.fromUriString(HOST+"/v2/local/search/address.json")
            .queryParam("query", shop.getAddress())
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
            if (jsonArray.length() > 0) {
                JSONObject addressData = jsonArray.getJSONObject(0);
                if (addressData.has("y")) shop.setLat(addressData.getString("y"));
                if (addressData.has("x")) shop.setLon(addressData.getString("x"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void savePickupTimes(Shop entity, AdmShopPickupTimeDto.save saveDto) {
    }

    @Override
    public List<AdmShopDto.list> listAll() {
        return shopRepository.findByWithdrawStatus(false)
            .stream().map(e -> AdmShopMapper.INSTANCE.entityToListDto(e))
            .collect(Collectors.toList());
    }
}
