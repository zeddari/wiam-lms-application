package com.wiam.lms.repository;

import com.wiam.lms.domain.Question2;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Question2 entity.
 */
@SuppressWarnings("unused")
@Repository
public interface Question2Repository extends JpaRepository<Question2, Long> {}
