package com.wiam.lms.repository;

import com.wiam.lms.domain.Session;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface SessionRepositoryWithBagRelationships {
    Optional<Session> fetchBagRelationships(Optional<Session> session);

    List<Session> fetchBagRelationships(List<Session> sessions);

    Page<Session> fetchBagRelationships(Page<Session> sessions);
}
