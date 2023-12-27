package com.wiam.lms.repository;

import com.wiam.lms.domain.SessionLink;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SessionLink entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SessionLinkRepository extends JpaRepository<SessionLink, Long> {}
