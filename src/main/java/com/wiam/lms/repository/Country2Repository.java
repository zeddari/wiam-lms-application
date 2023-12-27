package com.wiam.lms.repository;

import com.wiam.lms.domain.Country2;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Country2 entity.
 */
@SuppressWarnings("unused")
@Repository
public interface Country2Repository extends JpaRepository<Country2, Long> {}
