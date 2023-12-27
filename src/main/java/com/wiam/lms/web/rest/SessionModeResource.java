package com.wiam.lms.web.rest;

import com.wiam.lms.domain.SessionMode;
import com.wiam.lms.repository.SessionModeRepository;
import com.wiam.lms.repository.search.SessionModeSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.SessionMode}.
 */
@RestController
@RequestMapping("/api/session-modes")
@Transactional
public class SessionModeResource {

    private final Logger log = LoggerFactory.getLogger(SessionModeResource.class);

    private static final String ENTITY_NAME = "sessionMode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SessionModeRepository sessionModeRepository;

    private final SessionModeSearchRepository sessionModeSearchRepository;

    public SessionModeResource(SessionModeRepository sessionModeRepository, SessionModeSearchRepository sessionModeSearchRepository) {
        this.sessionModeRepository = sessionModeRepository;
        this.sessionModeSearchRepository = sessionModeSearchRepository;
    }

    /**
     * {@code POST  /session-modes} : Create a new sessionMode.
     *
     * @param sessionMode the sessionMode to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sessionMode, or with status {@code 400 (Bad Request)} if the sessionMode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SessionMode> createSessionMode(@Valid @RequestBody SessionMode sessionMode) throws URISyntaxException {
        log.debug("REST request to save SessionMode : {}", sessionMode);
        if (sessionMode.getId() != null) {
            throw new BadRequestAlertException("A new sessionMode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SessionMode result = sessionModeRepository.save(sessionMode);
        sessionModeSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/session-modes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /session-modes/:id} : Updates an existing sessionMode.
     *
     * @param id the id of the sessionMode to save.
     * @param sessionMode the sessionMode to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionMode,
     * or with status {@code 400 (Bad Request)} if the sessionMode is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sessionMode couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SessionMode> updateSessionMode(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SessionMode sessionMode
    ) throws URISyntaxException {
        log.debug("REST request to update SessionMode : {}, {}", id, sessionMode);
        if (sessionMode.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionMode.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionModeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SessionMode result = sessionModeRepository.save(sessionMode);
        sessionModeSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionMode.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /session-modes/:id} : Partial updates given fields of an existing sessionMode, field will ignore if it is null
     *
     * @param id the id of the sessionMode to save.
     * @param sessionMode the sessionMode to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionMode,
     * or with status {@code 400 (Bad Request)} if the sessionMode is not valid,
     * or with status {@code 404 (Not Found)} if the sessionMode is not found,
     * or with status {@code 500 (Internal Server Error)} if the sessionMode couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SessionMode> partialUpdateSessionMode(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SessionMode sessionMode
    ) throws URISyntaxException {
        log.debug("REST request to partial update SessionMode partially : {}, {}", id, sessionMode);
        if (sessionMode.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionMode.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionModeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SessionMode> result = sessionModeRepository
            .findById(sessionMode.getId())
            .map(existingSessionMode -> {
                if (sessionMode.getTitle() != null) {
                    existingSessionMode.setTitle(sessionMode.getTitle());
                }
                if (sessionMode.getDescription() != null) {
                    existingSessionMode.setDescription(sessionMode.getDescription());
                }

                return existingSessionMode;
            })
            .map(sessionModeRepository::save)
            .map(savedSessionMode -> {
                sessionModeSearchRepository.index(savedSessionMode);
                return savedSessionMode;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionMode.getId().toString())
        );
    }

    /**
     * {@code GET  /session-modes} : get all the sessionModes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sessionModes in body.
     */
    @GetMapping("")
    public List<SessionMode> getAllSessionModes() {
        log.debug("REST request to get all SessionModes");
        return sessionModeRepository.findAll();
    }

    /**
     * {@code GET  /session-modes/:id} : get the "id" sessionMode.
     *
     * @param id the id of the sessionMode to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sessionMode, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionMode> getSessionMode(@PathVariable("id") Long id) {
        log.debug("REST request to get SessionMode : {}", id);
        Optional<SessionMode> sessionMode = sessionModeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sessionMode);
    }

    /**
     * {@code DELETE  /session-modes/:id} : delete the "id" sessionMode.
     *
     * @param id the id of the sessionMode to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionMode(@PathVariable("id") Long id) {
        log.debug("REST request to delete SessionMode : {}", id);
        sessionModeRepository.deleteById(id);
        sessionModeSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /session-modes/_search?query=:query} : search for the sessionMode corresponding
     * to the query.
     *
     * @param query the query of the sessionMode search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<SessionMode> searchSessionModes(@RequestParam("query") String query) {
        log.debug("REST request to search SessionModes for query {}", query);
        try {
            return StreamSupport.stream(sessionModeSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
