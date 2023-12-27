package com.wiam.lms.repository;

import com.wiam.lms.domain.ProgressionMode;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProgressionMode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProgressionModeRepository extends JpaRepository<ProgressionMode, Long> {}
