package com.wiam.lms.repository;

import com.wiam.lms.domain.SessionType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SessionType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SessionTypeRepository extends JpaRepository<SessionType, Long> {}
