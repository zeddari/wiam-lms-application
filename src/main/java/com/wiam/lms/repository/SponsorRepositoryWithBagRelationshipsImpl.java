package com.wiam.lms.repository;

import com.wiam.lms.domain.Sponsor;
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
public class SponsorRepositoryWithBagRelationshipsImpl implements SponsorRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Sponsor> fetchBagRelationships(Optional<Sponsor> sponsor) {
        return sponsor.map(this::fetchStudents);
    }

    @Override
    public Page<Sponsor> fetchBagRelationships(Page<Sponsor> sponsors) {
        return new PageImpl<>(fetchBagRelationships(sponsors.getContent()), sponsors.getPageable(), sponsors.getTotalElements());
    }

    @Override
    public List<Sponsor> fetchBagRelationships(List<Sponsor> sponsors) {
        return Optional.of(sponsors).map(this::fetchStudents).orElse(Collections.emptyList());
    }

    Sponsor fetchStudents(Sponsor result) {
        return entityManager
            .createQuery("select sponsor from Sponsor sponsor left join fetch sponsor.students where sponsor.id = :id", Sponsor.class)
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<Sponsor> fetchStudents(List<Sponsor> sponsors) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, sponsors.size()).forEach(index -> order.put(sponsors.get(index).getId(), index));
        List<Sponsor> result = entityManager
            .createQuery("select sponsor from Sponsor sponsor left join fetch sponsor.students where sponsor in :sponsors", Sponsor.class)
            .setParameter("sponsors", sponsors)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
