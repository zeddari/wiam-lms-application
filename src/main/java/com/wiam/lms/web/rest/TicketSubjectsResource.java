package com.wiam.lms.web.rest;

import com.wiam.lms.domain.TicketSubjects;
import com.wiam.lms.repository.TicketSubjectsRepository;
import com.wiam.lms.repository.search.TicketSubjectsSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.TicketSubjects}.
 */
@RestController
@RequestMapping("/api/ticket-subjects")
@Transactional
public class TicketSubjectsResource {

    private final Logger log = LoggerFactory.getLogger(TicketSubjectsResource.class);

    private static final String ENTITY_NAME = "ticketSubjects";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketSubjectsRepository ticketSubjectsRepository;

    private final TicketSubjectsSearchRepository ticketSubjectsSearchRepository;

    public TicketSubjectsResource(
        TicketSubjectsRepository ticketSubjectsRepository,
        TicketSubjectsSearchRepository ticketSubjectsSearchRepository
    ) {
        this.ticketSubjectsRepository = ticketSubjectsRepository;
        this.ticketSubjectsSearchRepository = ticketSubjectsSearchRepository;
    }

    /**
     * {@code POST  /ticket-subjects} : Create a new ticketSubjects.
     *
     * @param ticketSubjects the ticketSubjects to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketSubjects, or with status {@code 400 (Bad Request)} if the ticketSubjects has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TicketSubjects> createTicketSubjects(@Valid @RequestBody TicketSubjects ticketSubjects)
        throws URISyntaxException {
        log.debug("REST request to save TicketSubjects : {}", ticketSubjects);
        if (ticketSubjects.getId() != null) {
            throw new BadRequestAlertException("A new ticketSubjects cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TicketSubjects result = ticketSubjectsRepository.save(ticketSubjects);
        ticketSubjectsSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/ticket-subjects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ticket-subjects/:id} : Updates an existing ticketSubjects.
     *
     * @param id the id of the ticketSubjects to save.
     * @param ticketSubjects the ticketSubjects to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketSubjects,
     * or with status {@code 400 (Bad Request)} if the ticketSubjects is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketSubjects couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketSubjects> updateTicketSubjects(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketSubjects ticketSubjects
    ) throws URISyntaxException {
        log.debug("REST request to update TicketSubjects : {}, {}", id, ticketSubjects);
        if (ticketSubjects.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketSubjects.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketSubjectsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TicketSubjects result = ticketSubjectsRepository.save(ticketSubjects);
        ticketSubjectsSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketSubjects.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ticket-subjects/:id} : Partial updates given fields of an existing ticketSubjects, field will ignore if it is null
     *
     * @param id the id of the ticketSubjects to save.
     * @param ticketSubjects the ticketSubjects to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketSubjects,
     * or with status {@code 400 (Bad Request)} if the ticketSubjects is not valid,
     * or with status {@code 404 (Not Found)} if the ticketSubjects is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketSubjects couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketSubjects> partialUpdateTicketSubjects(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketSubjects ticketSubjects
    ) throws URISyntaxException {
        log.debug("REST request to partial update TicketSubjects partially : {}, {}", id, ticketSubjects);
        if (ticketSubjects.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketSubjects.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketSubjectsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketSubjects> result = ticketSubjectsRepository
            .findById(ticketSubjects.getId())
            .map(existingTicketSubjects -> {
                if (ticketSubjects.getTitle() != null) {
                    existingTicketSubjects.setTitle(ticketSubjects.getTitle());
                }
                if (ticketSubjects.getDescription() != null) {
                    existingTicketSubjects.setDescription(ticketSubjects.getDescription());
                }

                return existingTicketSubjects;
            })
            .map(ticketSubjectsRepository::save)
            .map(savedTicketSubjects -> {
                ticketSubjectsSearchRepository.index(savedTicketSubjects);
                return savedTicketSubjects;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketSubjects.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-subjects} : get all the ticketSubjects.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ticketSubjects in body.
     */
    @GetMapping("")
    public List<TicketSubjects> getAllTicketSubjects() {
        log.debug("REST request to get all TicketSubjects");
        return ticketSubjectsRepository.findAll();
    }

    /**
     * {@code GET  /ticket-subjects/:id} : get the "id" ticketSubjects.
     *
     * @param id the id of the ticketSubjects to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketSubjects, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketSubjects> getTicketSubjects(@PathVariable("id") Long id) {
        log.debug("REST request to get TicketSubjects : {}", id);
        Optional<TicketSubjects> ticketSubjects = ticketSubjectsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ticketSubjects);
    }

    /**
     * {@code DELETE  /ticket-subjects/:id} : delete the "id" ticketSubjects.
     *
     * @param id the id of the ticketSubjects to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketSubjects(@PathVariable("id") Long id) {
        log.debug("REST request to delete TicketSubjects : {}", id);
        ticketSubjectsRepository.deleteById(id);
        ticketSubjectsSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /ticket-subjects/_search?query=:query} : search for the ticketSubjects corresponding
     * to the query.
     *
     * @param query the query of the ticketSubjects search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<TicketSubjects> searchTicketSubjects(@RequestParam("query") String query) {
        log.debug("REST request to search TicketSubjects for query {}", query);
        try {
            return StreamSupport.stream(ticketSubjectsSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
