package com.wiam.lms.repository;

import com.wiam.lms.domain.Enrolement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Enrolement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnrolementRepository extends JpaRepository<Enrolement, Long> {}
