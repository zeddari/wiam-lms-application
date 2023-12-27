package com.wiam.lms.repository;

import com.wiam.lms.domain.SessionMode;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SessionMode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SessionModeRepository extends JpaRepository<SessionMode, Long> {}
