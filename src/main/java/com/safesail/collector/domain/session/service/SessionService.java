package com.safesail.collector.domain.session.service;

import com.safesail.collector.domain.session.dto.CreateSessionRequest;
import com.safesail.collector.domain.session.dto.CreateSessionResponse;
import com.safesail.collector.domain.session.dto.EndSessionRequest;
import com.safesail.collector.domain.session.entity.Session;
import com.safesail.collector.domain.session.repository.SessionRepository;
import com.safesail.collector.infra.redis.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CacheService cacheService;

    @Transactional
    public CreateSessionResponse createSession(CreateSessionRequest request) {
        Session session = Session.builder()
                .clientId(request.clientId())
                .scenarioName(request.scenarioName())
                .status("IN_PROGRESS")
                .startedAt(OffsetDateTime.now())
                .build();
        session = sessionRepository.save(session);
        cacheService.setSessionActive(request.clientId(), session.getId());
        return new CreateSessionResponse(session.getId());
    }

    @Transactional
    public void endSession(Long sessionId, EndSessionRequest request) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found: " + sessionId));
        session.setStatus(request.status());
        session.setEndedAt(OffsetDateTime.now());
    }
}
