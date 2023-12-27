package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Tickets;
import com.wiam.lms.repository.TicketsRepository;
import com.wiam.lms.repository.search.TicketsSearchRepository;
import com.wiam.lms.web.rest.errors.BadRequestAlertException;
import com.wiam.lms.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.wiam.lms.domain.Tickets}.
 */
@RestController
@RequestMapping("/api/tickets")
@Transactional
public class TicketsResource {

    private final Logger log = LoggerFactory.getLogger(TicketsResource.class);

    private static final String ENTITY_NAME = "tickets";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketsRepository ticketsRepository;

    private final TicketsSearchRepository ticketsSearchRepository;

    public TicketsResource(TicketsRepository ticketsRepository, TicketsSearchRepository ticketsSearchRepository) {
        this.ticketsRepository = ticketsRepository;
        this.ticketsSearchRepository = ticketsSearchRepository;
    }

    /**
     * {@code POST  /tickets} : Create a new tickets.
     *
     * @param tickets the tickets to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tickets, or with status {@code 400 (Bad Request)} if the tickets has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Tickets> createTickets(@Valid @RequestBody Tickets tickets) throws URISyntaxException {
        log.debug("REST request to save Tickets : {}", tickets);
        if (tickets.getId() != null) {
            throw new BadRequestAlertException("A new tickets cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tickets result = ticketsRepository.save(tickets);
        ticketsSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/tickets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tickets/:id} : Updates an existing tickets.
     *
     * @param id the id of the tickets to save.
     * @param tickets the tickets to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tickets,
     * or with status {@code 400 (Bad Request)} if the tickets is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tickets couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tickets> updateTickets(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Tickets tickets
    ) throws URISyntaxException {
        log.debug("REST request to update Tickets : {}, {}", id, tickets);
        if (tickets.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tickets.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Tickets result = ticketsRepository.save(tickets);
        ticketsSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tickets.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tickets/:id} : Partial updates given fields of an existing tickets, field will ignore if it is null
     *
     * @param id the id of the tickets to save.
     * @param tickets the tickets to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tickets,
     * or with status {@code 400 (Bad Request)} if the tickets is not valid,
     * or with status {@code 404 (Not Found)} if the tickets is not found,
     * or with status {@code 500 (Internal Server Error)} if the tickets couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tickets> partialUpdateTickets(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Tickets tickets
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tickets partially : {}, {}", id, tickets);
        if (tickets.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tickets.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tickets> result = ticketsRepository
            .findById(tickets.getId())
            .map(existingTickets -> {
                if (tickets.getTitle() != null) {
                    existingTickets.setTitle(tickets.getTitle());
                }
                if (tickets.getDescription() != null) {
                    existingTickets.setDescription(tickets.getDescription());
                }
                if (tickets.getJustifDoc() != null) {
                    existingTickets.setJustifDoc(tickets.getJustifDoc());
                }
                if (tickets.getJustifDocContentType() != null) {
                    existingTickets.setJustifDocContentType(tickets.getJustifDocContentType());
                }
                if (tickets.getDateTicket() != null) {
                    existingTickets.setDateTicket(tickets.getDateTicket());
                }
                if (tickets.getDateProcess() != null) {
                    existingTickets.setDateProcess(tickets.getDateProcess());
                }
                if (tickets.getProcessed() != null) {
                    existingTickets.setProcessed(tickets.getProcessed());
                }

                return existingTickets;
            })
            .map(ticketsRepository::save)
            .map(savedTickets -> {
                ticketsSearchRepository.index(savedTickets);
                return savedTickets;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tickets.getId().toString())
        );
    }

    /**
     * {@code GET  /tickets} : get all the tickets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tickets in body.
     */
    @GetMapping("")
    public List<Tickets> getAllTickets() {
        log.debug("REST request to get all Tickets");
        return ticketsRepository.findAll();
    }

    /**
     * {@code GET  /tickets/:id} : get the "id" tickets.
     *
     * @param id the id of the tickets to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tickets, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tickets> getTickets(@PathVariable("id") Long id) {
        log.debug("REST request to get Tickets : {}", id);
        Optional<Tickets> tickets = ticketsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tickets);
    }

    /**
     * {@code DELETE  /tickets/:id} : delete the "id" tickets.
     *
     * @param id the id of the tickets to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTickets(@PathVariable("id") Long id) {
        log.debug("REST request to delete Tickets : {}", id);
        ticketsRepository.deleteById(id);
        ticketsSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /tickets/_search?query=:query} : search for the tickets corresponding
     * to the query.
     *
     * @param query the query of the tickets search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Tickets> searchTickets(@RequestParam("query") String query) {
        log.debug("REST request to search Tickets for query {}", query);
        try {
            return StreamSupport.stream(ticketsSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
