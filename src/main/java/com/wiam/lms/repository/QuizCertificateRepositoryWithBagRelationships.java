package com.wiam.lms.repository;

import com.wiam.lms.domain.QuizCertificate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface QuizCertificateRepositoryWithBagRelationships {
    Optional<QuizCertificate> fetchBagRelationships(Optional<QuizCertificate> quizCertificate);

    List<QuizCertificate> fetchBagRelationships(List<QuizCertificate> quizCertificates);

    Page<QuizCertificate> fetchBagRelationships(Page<QuizCertificate> quizCertificates);
}
