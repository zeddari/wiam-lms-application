package com.wiam.lms.web.rest;

import com.wiam.lms.domain.SessionType;
import com.wiam.lms.repository.SessionTypeRepository;
import com.wiam.lms.repository.search.SessionTypeSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.SessionType}.
 */
@RestController
@RequestMapping("/api/session-types")
@Transactional
public class SessionTypeResource {

    private final Logger log = LoggerFactory.getLogger(SessionTypeResource.class);

    private static final String ENTITY_NAME = "sessionType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SessionTypeRepository sessionTypeRepository;

    private final SessionTypeSearchRepository sessionTypeSearchRepository;

    public SessionTypeResource(SessionTypeRepository sessionTypeRepository, SessionTypeSearchRepository sessionTypeSearchRepository) {
        this.sessionTypeRepository = sessionTypeRepository;
        this.sessionTypeSearchRepository = sessionTypeSearchRepository;
    }

    /**
     * {@code POST  /session-types} : Create a new sessionType.
     *
     * @param sessionType the sessionType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sessionType, or with status {@code 400 (Bad Request)} if the sessionType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SessionType> createSessionType(@Valid @RequestBody SessionType sessionType) throws URISyntaxException {
        log.debug("REST request to save SessionType : {}", sessionType);
        if (sessionType.getId() != null) {
            throw new BadRequestAlertException("A new sessionType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SessionType result = sessionTypeRepository.save(sessionType);
        sessionTypeSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/session-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /session-types/:id} : Updates an existing sessionType.
     *
     * @param id the id of the sessionType to save.
     * @param sessionType the sessionType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionType,
     * or with status {@code 400 (Bad Request)} if the sessionType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sessionType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SessionType> updateSessionType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SessionType sessionType
    ) throws URISyntaxException {
        log.debug("REST request to update SessionType : {}, {}", id, sessionType);
        if (sessionType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SessionType result = sessionTypeRepository.save(sessionType);
        sessionTypeSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionType.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /session-types/:id} : Partial updates given fields of an existing sessionType, field will ignore if it is null
     *
     * @param id the id of the sessionType to save.
     * @param sessionType the sessionType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionType,
     * or with status {@code 400 (Bad Request)} if the sessionType is not valid,
     * or with status {@code 404 (Not Found)} if the sessionType is not found,
     * or with status {@code 500 (Internal Server Error)} if the sessionType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SessionType> partialUpdateSessionType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SessionType sessionType
    ) throws URISyntaxException {
        log.debug("REST request to partial update SessionType partially : {}, {}", id, sessionType);
        if (sessionType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SessionType> result = sessionTypeRepository
            .findById(sessionType.getId())
            .map(existingSessionType -> {
                if (sessionType.getTitle() != null) {
                    existingSessionType.setTitle(sessionType.getTitle());
                }
                if (sessionType.getDescription() != null) {
                    existingSessionType.setDescription(sessionType.getDescription());
                }

                return existingSessionType;
            })
            .map(sessionTypeRepository::save)
            .map(savedSessionType -> {
                sessionTypeSearchRepository.index(savedSessionType);
                return savedSessionType;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionType.getId().toString())
        );
    }

    /**
     * {@code GET  /session-types} : get all the sessionTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sessionTypes in body.
     */
    @GetMapping("")
    public List<SessionType> getAllSessionTypes() {
        log.debug("REST request to get all SessionTypes");
        return sessionTypeRepository.findAll();
    }

    /**
     * {@code GET  /session-types/:id} : get the "id" sessionType.
     *
     * @param id the id of the sessionType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sessionType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionType> getSessionType(@PathVariable("id") Long id) {
        log.debug("REST request to get SessionType : {}", id);
        Optional<SessionType> sessionType = sessionTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sessionType);
    }

    /**
     * {@code DELETE  /session-types/:id} : delete the "id" sessionType.
     *
     * @param id the id of the sessionType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionType(@PathVariable("id") Long id) {
        log.debug("REST request to delete SessionType : {}", id);
        sessionTypeRepository.deleteById(id);
        sessionTypeSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /session-types/_search?query=:query} : search for the sessionType corresponding
     * to the query.
     *
     * @param query the query of the sessionType search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<SessionType> searchSessionTypes(@RequestParam("query") String query) {
        log.debug("REST request to search SessionTypes for query {}", query);
        try {
            return StreamSupport.stream(sessionTypeSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
