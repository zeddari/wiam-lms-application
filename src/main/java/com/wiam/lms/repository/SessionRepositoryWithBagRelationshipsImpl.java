package com.wiam.lms.repository;

import com.wiam.lms.domain.Session;
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
public class SessionRepositoryWithBagRelationshipsImpl implements SessionRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Session> fetchBagRelationships(Optional<Session> session) {
        return session.map(this::fetchProfessors).map(this::fetchEmployees).map(this::fetchLinks);
    }

    @Override
    public Page<Session> fetchBagRelationships(Page<Session> sessions) {
        return new PageImpl<>(fetchBagRelationships(sessions.getContent()), sessions.getPageable(), sessions.getTotalElements());
    }

    @Override
    public List<Session> fetchBagRelationships(List<Session> sessions) {
        return Optional
            .of(sessions)
            .map(this::fetchProfessors)
            .map(this::fetchEmployees)
            .map(this::fetchLinks)
            .orElse(Collections.emptyList());
    }

    Session fetchProfessors(Session result) {
        return entityManager
            .createQuery("select session from Session session left join fetch session.professors where session.id = :id", Session.class)
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<Session> fetchProfessors(List<Session> sessions) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, sessions.size()).forEach(index -> order.put(sessions.get(index).getId(), index));
        List<Session> result = entityManager
            .createQuery("select session from Session session left join fetch session.professors where session in :sessions", Session.class)
            .setParameter("sessions", sessions)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Session fetchEmployees(Session result) {
        return entityManager
            .createQuery("select session from Session session left join fetch session.employees where session.id = :id", Session.class)
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<Session> fetchEmployees(List<Session> sessions) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, sessions.size()).forEach(index -> order.put(sessions.get(index).getId(), index));
        List<Session> result = entityManager
            .createQuery("select session from Session session left join fetch session.employees where session in :sessions", Session.class)
            .setParameter("sessions", sessions)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Session fetchLinks(Session result) {
        return entityManager
            .createQuery("select session from Session session left join fetch session.links where session.id = :id", Session.class)
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<Session> fetchLinks(List<Session> sessions) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, sessions.size()).forEach(index -> order.put(sessions.get(index).getId(), index));
        List<Session> result = entityManager
            .createQuery("select session from Session session left join fetch session.links where session in :sessions", Session.class)
            .setParameter("sessions", sessions)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
