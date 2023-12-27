package com.wiam.lms.repository;

import com.wiam.lms.domain.DiplomaType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DiplomaType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DiplomaTypeRepository extends JpaRepository<DiplomaType, Long> {}
