package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Session;
import com.wiam.lms.repository.SessionRepository;
import com.wiam.lms.repository.search.SessionSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Session}.
 */
@RestController
@RequestMapping("/api/sessions")
@Transactional
public class SessionResource {

    private final Logger log = LoggerFactory.getLogger(SessionResource.class);

    private static final String ENTITY_NAME = "session";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SessionRepository sessionRepository;

    private final SessionSearchRepository sessionSearchRepository;

    public SessionResource(SessionRepository sessionRepository, SessionSearchRepository sessionSearchRepository) {
        this.sessionRepository = sessionRepository;
        this.sessionSearchRepository = sessionSearchRepository;
    }

    /**
     * {@code POST  /sessions} : Create a new session.
     *
     * @param session the session to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new session, or with status {@code 400 (Bad Request)} if the session has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Session> createSession(@Valid @RequestBody Session session) throws URISyntaxException {
        log.debug("REST request to save Session : {}", session);
        if (session.getId() != null) {
            throw new BadRequestAlertException("A new session cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Session result = sessionRepository.save(session);
        sessionSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/sessions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sessions/:id} : Updates an existing session.
     *
     * @param id the id of the session to save.
     * @param session the session to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated session,
     * or with status {@code 400 (Bad Request)} if the session is not valid,
     * or with status {@code 500 (Internal Server Error)} if the session couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Session session
    ) throws URISyntaxException {
        log.debug("REST request to update Session : {}, {}", id, session);
        if (session.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, session.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Session result = sessionRepository.save(session);
        sessionSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, session.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sessions/:id} : Partial updates given fields of an existing session, field will ignore if it is null
     *
     * @param id the id of the session to save.
     * @param session the session to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated session,
     * or with status {@code 400 (Bad Request)} if the session is not valid,
     * or with status {@code 404 (Not Found)} if the session is not found,
     * or with status {@code 500 (Internal Server Error)} if the session couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Session> partialUpdateSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Session session
    ) throws URISyntaxException {
        log.debug("REST request to partial update Session partially : {}, {}", id, session);
        if (session.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, session.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Session> result = sessionRepository
            .findById(session.getId())
            .map(existingSession -> {
                if (session.getTitle() != null) {
                    existingSession.setTitle(session.getTitle());
                }
                if (session.getDescription() != null) {
                    existingSession.setDescription(session.getDescription());
                }
                if (session.getSessionStartTime() != null) {
                    existingSession.setSessionStartTime(session.getSessionStartTime());
                }
                if (session.getSessionEndTime() != null) {
                    existingSession.setSessionEndTime(session.getSessionEndTime());
                }
                if (session.getIsActive() != null) {
                    existingSession.setIsActive(session.getIsActive());
                }
                if (session.getSessionSize() != null) {
                    existingSession.setSessionSize(session.getSessionSize());
                }
                if (session.getPrice() != null) {
                    existingSession.setPrice(session.getPrice());
                }
                if (session.getCurrency() != null) {
                    existingSession.setCurrency(session.getCurrency());
                }
                if (session.getTargetedAge() != null) {
                    existingSession.setTargetedAge(session.getTargetedAge());
                }
                if (session.getTargetedGender() != null) {
                    existingSession.setTargetedGender(session.getTargetedGender());
                }
                if (session.getThumbnail() != null) {
                    existingSession.setThumbnail(session.getThumbnail());
                }
                if (session.getThumbnailContentType() != null) {
                    existingSession.setThumbnailContentType(session.getThumbnailContentType());
                }
                if (session.getPlanningType() != null) {
                    existingSession.setPlanningType(session.getPlanningType());
                }
                if (session.getOnceDate() != null) {
                    existingSession.setOnceDate(session.getOnceDate());
                }
                if (session.getMonday() != null) {
                    existingSession.setMonday(session.getMonday());
                }
                if (session.getTuesday() != null) {
                    existingSession.setTuesday(session.getTuesday());
                }
                if (session.getWednesday() != null) {
                    existingSession.setWednesday(session.getWednesday());
                }
                if (session.getThursday() != null) {
                    existingSession.setThursday(session.getThursday());
                }
                if (session.getFriday() != null) {
                    existingSession.setFriday(session.getFriday());
                }
                if (session.getSaturday() != null) {
                    existingSession.setSaturday(session.getSaturday());
                }
                if (session.getSanday() != null) {
                    existingSession.setSanday(session.getSanday());
                }
                if (session.getPeriodStartDate() != null) {
                    existingSession.setPeriodStartDate(session.getPeriodStartDate());
                }
                if (session.getPeriodeEndDate() != null) {
                    existingSession.setPeriodeEndDate(session.getPeriodeEndDate());
                }
                if (session.getNoPeriodeEndDate() != null) {
                    existingSession.setNoPeriodeEndDate(session.getNoPeriodeEndDate());
                }

                return existingSession;
            })
            .map(sessionRepository::save)
            .map(savedSession -> {
                sessionSearchRepository.index(savedSession);
                return savedSession;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, session.getId().toString())
        );
    }

    /**
     * {@code GET  /sessions} : get all the sessions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sessions in body.
     */
    @GetMapping("")
    public List<Session> getAllSessions(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        log.debug("REST request to get all Sessions");
        if (eagerload) {
            return sessionRepository.findAllWithEagerRelationships();
        } else {
            return sessionRepository.findAll();
        }
    }

    /**
     * {@code GET  /sessions/:id} : get the "id" session.
     *
     * @param id the id of the session to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the session, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSession(@PathVariable("id") Long id) {
        log.debug("REST request to get Session : {}", id);
        Optional<Session> session = sessionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(session);
    }

    /**
     * {@code DELETE  /sessions/:id} : delete the "id" session.
     *
     * @param id the id of the session to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable("id") Long id) {
        log.debug("REST request to delete Session : {}", id);
        sessionRepository.deleteById(id);
        sessionSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /sessions/_search?query=:query} : search for the session corresponding
     * to the query.
     *
     * @param query the query of the session search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Session> searchSessions(@RequestParam("query") String query) {
        log.debug("REST request to search Sessions for query {}", query);
        try {
            return StreamSupport.stream(sessionSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
