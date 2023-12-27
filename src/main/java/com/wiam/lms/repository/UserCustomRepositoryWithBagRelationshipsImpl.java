package com.wiam.lms.repository;

import com.wiam.lms.domain.UserCustom;
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
public class UserCustomRepositoryWithBagRelationshipsImpl implements UserCustomRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<UserCustom> fetchBagRelationships(Optional<UserCustom> userCustom) {
        return userCustom.map(this::fetchExams);
    }

    @Override
    public Page<UserCustom> fetchBagRelationships(Page<UserCustom> userCustoms) {
        return new PageImpl<>(fetchBagRelationships(userCustoms.getContent()), userCustoms.getPageable(), userCustoms.getTotalElements());
    }

    @Override
    public List<UserCustom> fetchBagRelationships(List<UserCustom> userCustoms) {
        return Optional.of(userCustoms).map(this::fetchExams).orElse(Collections.emptyList());
    }

    UserCustom fetchExams(UserCustom result) {
        return entityManager
            .createQuery(
                "select userCustom from UserCustom userCustom left join fetch userCustom.exams where userCustom.id = :id",
                UserCustom.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<UserCustom> fetchExams(List<UserCustom> userCustoms) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, userCustoms.size()).forEach(index -> order.put(userCustoms.get(index).getId(), index));
        List<UserCustom> result = entityManager
            .createQuery(
                "select userCustom from UserCustom userCustom left join fetch userCustom.exams where userCustom in :userCustoms",
                UserCustom.class
            )
            .setParameter("userCustoms", userCustoms)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
