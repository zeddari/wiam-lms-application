package com.wiam.lms.repository;

import com.wiam.lms.domain.CoteryHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CoteryHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CoteryHistoryRepository extends JpaRepository<CoteryHistory, Long> {}
