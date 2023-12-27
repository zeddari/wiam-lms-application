package com.wiam.lms.web.rest;

import com.wiam.lms.domain.SessionLink;
import com.wiam.lms.repository.SessionLinkRepository;
import com.wiam.lms.repository.search.SessionLinkSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.SessionLink}.
 */
@RestController
@RequestMapping("/api/session-links")
@Transactional
public class SessionLinkResource {

    private final Logger log = LoggerFactory.getLogger(SessionLinkResource.class);

    private static final String ENTITY_NAME = "sessionLink";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SessionLinkRepository sessionLinkRepository;

    private final SessionLinkSearchRepository sessionLinkSearchRepository;

    public SessionLinkResource(SessionLinkRepository sessionLinkRepository, SessionLinkSearchRepository sessionLinkSearchRepository) {
        this.sessionLinkRepository = sessionLinkRepository;
        this.sessionLinkSearchRepository = sessionLinkSearchRepository;
    }

    /**
     * {@code POST  /session-links} : Create a new sessionLink.
     *
     * @param sessionLink the sessionLink to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sessionLink, or with status {@code 400 (Bad Request)} if the sessionLink has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SessionLink> createSessionLink(@Valid @RequestBody SessionLink sessionLink) throws URISyntaxException {
        log.debug("REST request to save SessionLink : {}", sessionLink);
        if (sessionLink.getId() != null) {
            throw new BadRequestAlertException("A new sessionLink cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SessionLink result = sessionLinkRepository.save(sessionLink);
        sessionLinkSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/session-links/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /session-links/:id} : Updates an existing sessionLink.
     *
     * @param id the id of the sessionLink to save.
     * @param sessionLink the sessionLink to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionLink,
     * or with status {@code 400 (Bad Request)} if the sessionLink is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sessionLink couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SessionLink> updateSessionLink(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SessionLink sessionLink
    ) throws URISyntaxException {
        log.debug("REST request to update SessionLink : {}, {}", id, sessionLink);
        if (sessionLink.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionLink.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionLinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SessionLink result = sessionLinkRepository.save(sessionLink);
        sessionLinkSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionLink.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /session-links/:id} : Partial updates given fields of an existing sessionLink, field will ignore if it is null
     *
     * @param id the id of the sessionLink to save.
     * @param sessionLink the sessionLink to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionLink,
     * or with status {@code 400 (Bad Request)} if the sessionLink is not valid,
     * or with status {@code 404 (Not Found)} if the sessionLink is not found,
     * or with status {@code 500 (Internal Server Error)} if the sessionLink couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SessionLink> partialUpdateSessionLink(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SessionLink sessionLink
    ) throws URISyntaxException {
        log.debug("REST request to partial update SessionLink partially : {}, {}", id, sessionLink);
        if (sessionLink.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionLink.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionLinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SessionLink> result = sessionLinkRepository
            .findById(sessionLink.getId())
            .map(existingSessionLink -> {
                if (sessionLink.getTitle() != null) {
                    existingSessionLink.setTitle(sessionLink.getTitle());
                }
                if (sessionLink.getDescription() != null) {
                    existingSessionLink.setDescription(sessionLink.getDescription());
                }
                if (sessionLink.getLink() != null) {
                    existingSessionLink.setLink(sessionLink.getLink());
                }

                return existingSessionLink;
            })
            .map(sessionLinkRepository::save)
            .map(savedSessionLink -> {
                sessionLinkSearchRepository.index(savedSessionLink);
                return savedSessionLink;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionLink.getId().toString())
        );
    }

    /**
     * {@code GET  /session-links} : get all the sessionLinks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sessionLinks in body.
     */
    @GetMapping("")
    public List<SessionLink> getAllSessionLinks() {
        log.debug("REST request to get all SessionLinks");
        return sessionLinkRepository.findAll();
    }

    /**
     * {@code GET  /session-links/:id} : get the "id" sessionLink.
     *
     * @param id the id of the sessionLink to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sessionLink, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionLink> getSessionLink(@PathVariable("id") Long id) {
        log.debug("REST request to get SessionLink : {}", id);
        Optional<SessionLink> sessionLink = sessionLinkRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sessionLink);
    }

    /**
     * {@code DELETE  /session-links/:id} : delete the "id" sessionLink.
     *
     * @param id the id of the sessionLink to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionLink(@PathVariable("id") Long id) {
        log.debug("REST request to delete SessionLink : {}", id);
        sessionLinkRepository.deleteById(id);
        sessionLinkSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /session-links/_search?query=:query} : search for the sessionLink corresponding
     * to the query.
     *
     * @param query the query of the sessionLink search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<SessionLink> searchSessionLinks(@RequestParam("query") String query) {
        log.debug("REST request to search SessionLinks for query {}", query);
        try {
            return StreamSupport.stream(sessionLinkSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
