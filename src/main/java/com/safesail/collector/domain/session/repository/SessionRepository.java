package com.safesail.collector.domain.session.repository;

import com.safesail.collector.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {}
