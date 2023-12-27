package com.wiam.lms.repository;

import com.wiam.lms.domain.QuizCertificateType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuizCertificateType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuizCertificateTypeRepository extends JpaRepository<QuizCertificateType, Long> {}
