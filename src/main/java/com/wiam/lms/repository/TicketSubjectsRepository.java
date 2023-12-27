package com.wiam.lms.repository;

import com.wiam.lms.domain.TicketSubjects;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketSubjects entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketSubjectsRepository extends JpaRepository<TicketSubjects, Long> {}
