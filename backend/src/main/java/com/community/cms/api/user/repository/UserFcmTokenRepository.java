package com.community.cms.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.community.cms.api.user.dto.UserFcmToken;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, String> {

    @Modifying
    @Query(value = "DELETE FROM UserFcmToken uft WHERE uft.token = ?1 AND uft.userUid = ?2")
    void deleteByTokenAndUserUid(String token, String userUid);
    
    void deleteByUserUid(String userUid);
}
