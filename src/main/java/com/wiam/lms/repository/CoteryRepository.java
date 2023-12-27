package com.wiam.lms.repository;

import com.wiam.lms.domain.Cotery;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cotery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CoteryRepository extends JpaRepository<Cotery, Long> {}
