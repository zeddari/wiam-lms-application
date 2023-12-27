package com.wiam.lms.repository;

import com.wiam.lms.domain.Tickets;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tickets entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long> {}
