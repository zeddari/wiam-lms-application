package com.wiam.lms.repository;

import com.wiam.lms.domain.Sponsoring;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Sponsoring entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SponsoringRepository extends JpaRepository<Sponsoring, Long> {}
