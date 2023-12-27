package com.wiam.lms.repository;

import com.wiam.lms.domain.SessionJoinMode;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SessionJoinMode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SessionJoinModeRepository extends JpaRepository<SessionJoinMode, Long> {}
