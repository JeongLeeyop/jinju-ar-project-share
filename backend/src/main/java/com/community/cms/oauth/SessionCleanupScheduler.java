package com.community.cms.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SessionCleanupScheduler {

    @Autowired
    private SessionRegistry sessionRegistry;

    private final long SESSION_TIMEOUT = 15; // 15분
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startSessionCleanupTask() {
        scheduler.scheduleAtFixedRate(this::removeExpiredSessions, 0, 1, TimeUnit.MINUTES);
    }

    private void removeExpiredSessions() {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for (Object principal : allPrincipals) {
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
            for (SessionInformation session : sessions) {
                Date lastRequestDate = session.getLastRequest();
                Instant lastRequestInstant = lastRequestDate.toInstant();
                LocalDateTime lastRequestTime = LocalDateTime.ofInstant(lastRequestInstant, ZoneId.systemDefault());
                if (lastRequestTime.plusMinutes(SESSION_TIMEOUT).isBefore(LocalDateTime.now())) {
                    session.expireNow();
                    sessionRegistry.removeSessionInformation(session.getSessionId());
                }
            }
        }
    }
}
