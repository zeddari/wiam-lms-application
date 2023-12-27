package com.wiam.lms.repository;

import com.wiam.lms.domain.LeaveRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LeaveRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {}
