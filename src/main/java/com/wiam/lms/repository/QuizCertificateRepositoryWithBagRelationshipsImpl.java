package com.wiam.lms.repository;

import com.wiam.lms.domain.QuizCertificate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class QuizCertificateRepositoryWithBagRelationshipsImpl implements QuizCertificateRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<QuizCertificate> fetchBagRelationships(Optional<QuizCertificate> quizCertificate) {
        return quizCertificate.map(this::fetchStudents).map(this::fetchQuestions);
    }

    @Override
    public Page<QuizCertificate> fetchBagRelationships(Page<QuizCertificate> quizCertificates) {
        return new PageImpl<>(
            fetchBagRelationships(quizCertificates.getContent()),
            quizCertificates.getPageable(),
            quizCertificates.getTotalElements()
        );
    }

    @Override
    public List<QuizCertificate> fetchBagRelationships(List<QuizCertificate> quizCertificates) {
        return Optional.of(quizCertificates).map(this::fetchStudents).map(this::fetchQuestions).orElse(Collections.emptyList());
    }

    QuizCertificate fetchStudents(QuizCertificate result) {
        return entityManager
            .createQuery(
                "select quizCertificate from QuizCertificate quizCertificate left join fetch quizCertificate.students where quizCertificate.id = :id",
                QuizCertificate.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<QuizCertificate> fetchStudents(List<QuizCertificate> quizCertificates) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, quizCertificates.size()).forEach(index -> order.put(quizCertificates.get(index).getId(), index));
        List<QuizCertificate> result = entityManager
            .createQuery(
                "select quizCertificate from QuizCertificate quizCertificate left join fetch quizCertificate.students where quizCertificate in :quizCertificates",
                QuizCertificate.class
            )
            .setParameter("quizCertificates", quizCertificates)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    QuizCertificate fetchQuestions(QuizCertificate result) {
        return entityManager
            .createQuery(
                "select quizCertificate from QuizCertificate quizCertificate left join fetch quizCertificate.questions where quizCertificate.id = :id",
                QuizCertificate.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<QuizCertificate> fetchQuestions(List<QuizCertificate> quizCertificates) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, quizCertificates.size()).forEach(index -> order.put(quizCertificates.get(index).getId(), index));
        List<QuizCertificate> result = entityManager
            .createQuery(
                "select quizCertificate from QuizCertificate quizCertificate left join fetch quizCertificate.questions where quizCertificate in :quizCertificates",
                QuizCertificate.class
            )
            .setParameter("quizCertificates", quizCertificates)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
