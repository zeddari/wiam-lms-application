package com.wiam.lms.repository;

import com.wiam.lms.domain.QuizCertificate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuizCertificate entity.
 *
 * When extending this class, extend QuizCertificateRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface QuizCertificateRepository extends QuizCertificateRepositoryWithBagRelationships, JpaRepository<QuizCertificate, Long> {
    default Optional<QuizCertificate> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<QuizCertificate> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<QuizCertificate> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
