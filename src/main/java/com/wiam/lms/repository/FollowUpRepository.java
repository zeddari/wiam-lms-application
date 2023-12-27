package com.wiam.lms.repository;

import com.wiam.lms.domain.FollowUp;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FollowUp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {}
