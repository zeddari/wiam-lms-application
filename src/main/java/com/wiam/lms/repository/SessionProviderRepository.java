package com.wiam.lms.repository;

import com.wiam.lms.domain.SessionProvider;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SessionProvider entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SessionProviderRepository extends JpaRepository<SessionProvider, Long> {}
