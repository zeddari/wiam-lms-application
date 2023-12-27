package com.wiam.lms.web.rest;

import com.wiam.lms.domain.SessionJoinMode;
import com.wiam.lms.repository.SessionJoinModeRepository;
import com.wiam.lms.repository.search.SessionJoinModeSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.SessionJoinMode}.
 */
@RestController
@RequestMapping("/api/session-join-modes")
@Transactional
public class SessionJoinModeResource {

    private final Logger log = LoggerFactory.getLogger(SessionJoinModeResource.class);

    private static final String ENTITY_NAME = "sessionJoinMode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SessionJoinModeRepository sessionJoinModeRepository;

    private final SessionJoinModeSearchRepository sessionJoinModeSearchRepository;

    public SessionJoinModeResource(
        SessionJoinModeRepository sessionJoinModeRepository,
        SessionJoinModeSearchRepository sessionJoinModeSearchRepository
    ) {
        this.sessionJoinModeRepository = sessionJoinModeRepository;
        this.sessionJoinModeSearchRepository = sessionJoinModeSearchRepository;
    }

    /**
     * {@code POST  /session-join-modes} : Create a new sessionJoinMode.
     *
     * @param sessionJoinMode the sessionJoinMode to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sessionJoinMode, or with status {@code 400 (Bad Request)} if the sessionJoinMode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SessionJoinMode> createSessionJoinMode(@Valid @RequestBody SessionJoinMode sessionJoinMode)
        throws URISyntaxException {
        log.debug("REST request to save SessionJoinMode : {}", sessionJoinMode);
        if (sessionJoinMode.getId() != null) {
            throw new BadRequestAlertException("A new sessionJoinMode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SessionJoinMode result = sessionJoinModeRepository.save(sessionJoinMode);
        sessionJoinModeSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/session-join-modes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /session-join-modes/:id} : Updates an existing sessionJoinMode.
     *
     * @param id the id of the sessionJoinMode to save.
     * @param sessionJoinMode the sessionJoinMode to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionJoinMode,
     * or with status {@code 400 (Bad Request)} if the sessionJoinMode is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sessionJoinMode couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SessionJoinMode> updateSessionJoinMode(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SessionJoinMode sessionJoinMode
    ) throws URISyntaxException {
        log.debug("REST request to update SessionJoinMode : {}, {}", id, sessionJoinMode);
        if (sessionJoinMode.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionJoinMode.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionJoinModeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SessionJoinMode result = sessionJoinModeRepository.save(sessionJoinMode);
        sessionJoinModeSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionJoinMode.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /session-join-modes/:id} : Partial updates given fields of an existing sessionJoinMode, field will ignore if it is null
     *
     * @param id the id of the sessionJoinMode to save.
     * @param sessionJoinMode the sessionJoinMode to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionJoinMode,
     * or with status {@code 400 (Bad Request)} if the sessionJoinMode is not valid,
     * or with status {@code 404 (Not Found)} if the sessionJoinMode is not found,
     * or with status {@code 500 (Internal Server Error)} if the sessionJoinMode couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SessionJoinMode> partialUpdateSessionJoinMode(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SessionJoinMode sessionJoinMode
    ) throws URISyntaxException {
        log.debug("REST request to partial update SessionJoinMode partially : {}, {}", id, sessionJoinMode);
        if (sessionJoinMode.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionJoinMode.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionJoinModeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SessionJoinMode> result = sessionJoinModeRepository
            .findById(sessionJoinMode.getId())
            .map(existingSessionJoinMode -> {
                if (sessionJoinMode.getTitle() != null) {
                    existingSessionJoinMode.setTitle(sessionJoinMode.getTitle());
                }
                if (sessionJoinMode.getDescription() != null) {
                    existingSessionJoinMode.setDescription(sessionJoinMode.getDescription());
                }

                return existingSessionJoinMode;
            })
            .map(sessionJoinModeRepository::save)
            .map(savedSessionJoinMode -> {
                sessionJoinModeSearchRepository.index(savedSessionJoinMode);
                return savedSessionJoinMode;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionJoinMode.getId().toString())
        );
    }

    /**
     * {@code GET  /session-join-modes} : get all the sessionJoinModes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sessionJoinModes in body.
     */
    @GetMapping("")
    public List<SessionJoinMode> getAllSessionJoinModes() {
        log.debug("REST request to get all SessionJoinModes");
        return sessionJoinModeRepository.findAll();
    }

    /**
     * {@code GET  /session-join-modes/:id} : get the "id" sessionJoinMode.
     *
     * @param id the id of the sessionJoinMode to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sessionJoinMode, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionJoinMode> getSessionJoinMode(@PathVariable("id") Long id) {
        log.debug("REST request to get SessionJoinMode : {}", id);
        Optional<SessionJoinMode> sessionJoinMode = sessionJoinModeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sessionJoinMode);
    }

    /**
     * {@code DELETE  /session-join-modes/:id} : delete the "id" sessionJoinMode.
     *
     * @param id the id of the sessionJoinMode to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionJoinMode(@PathVariable("id") Long id) {
        log.debug("REST request to delete SessionJoinMode : {}", id);
        sessionJoinModeRepository.deleteById(id);
        sessionJoinModeSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /session-join-modes/_search?query=:query} : search for the sessionJoinMode corresponding
     * to the query.
     *
     * @param query the query of the sessionJoinMode search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<SessionJoinMode> searchSessionJoinModes(@RequestParam("query") String query) {
        log.debug("REST request to search SessionJoinModes for query {}", query);
        try {
            return StreamSupport.stream(sessionJoinModeSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
