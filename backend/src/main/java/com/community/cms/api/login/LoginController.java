package com.community.cms.api.login;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.community.cms.entity.User;
import com.community.cms.entity.UserRole;
import com.community.cms.oauth.SinghaUser;
import com.community.cms.oauth.soical.apple.AppleApi;
import com.community.cms.oauth.soical.apple.dto.AppleLoginInfo;
import com.community.cms.oauth.soical.naver.NaverApi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
    @PreAuthorize("permitAll()")
public class LoginController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private NaverApi naverApi;

    @Autowired
    private AppleApi appleApi;

    @Autowired
    private SessionRegistry sessionRegistry;

    @PostMapping(value = "/oauth/token")
    public ResponseEntity postAccessToken(
            @RequestHeader(name = "x-auth-token", required = false) String xAuthToken,
            @RequestParam Map<String, String> parameters,
            Principal principal
    ) {
        parameters.putIfAbsent("grant_type", "password");
        if (xAuthToken != null) {
            parameters.put("provider_token", xAuthToken);
        }
        try {
            return tokenEndpoint.postAccessToken(principal, parameters);
        } catch (HttpRequestMethodNotSupportedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InvalidGrantException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            // return ResponseEntity.badRequest().body(e);
        } catch (InternalAuthenticationServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        
    }

    @GetMapping(value = "/oauth/logout")
    public void logout(@AuthenticationPrincipal SinghaUser authUser) {
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(authUser, false);
        for (SessionInformation session : sessions) {
            session.expireNow(); 
            sessionRegistry.removeSessionInformation(session.getSessionId());
        }
    }

    @GetMapping(value = "/oauth/updateOnline")
    public void updateOnline(@AuthenticationPrincipal SinghaUser authUser) {
        User user = authUser.getUser();
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		List<UserRole> roles = user.getRoles();
		roles.forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getRole().getRoleCode())));
        sessionRegistry.registerNewSession(authUser.getUsername(), new SinghaUser(user, authorities));
    }

    @GetMapping(value = "/oauth/naver/access")
    public ResponseEntity socialLogin(
            @RequestParam("code") String code
    ) {
        return ResponseEntity.ok(naverApi.access(code));
    }

    @GetMapping(value = "/oauth/naver/login")
    public ResponseEntity naverAccess(
            @RequestParam("code") String code
    ) {
        return ResponseEntity.ok(naverApi.token(code));
    }

    @GetMapping(value = "/oauth/naver/me")
    public ResponseEntity naverMe(@RequestParam("accessToken") String access_token) {
        return ResponseEntity.ok(naverApi.me(access_token));
    }
    
    @PostMapping(value = "/oauth/apple/login")
    public ResponseEntity appleLogin(@RequestBody AppleLoginInfo dto) {
        return ResponseEntity.ok(appleApi.token(dto));
    }

    @GetMapping(value = "/api/me")
    public ResponseEntity<Object> me(Principal principal) {
        return ResponseEntity.ok(principal);
    }
}
