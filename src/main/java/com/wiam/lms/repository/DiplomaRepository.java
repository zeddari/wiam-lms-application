package com.wiam.lms.repository;

import com.wiam.lms.domain.Diploma;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Diploma entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DiplomaRepository extends JpaRepository<Diploma, Long> {}
