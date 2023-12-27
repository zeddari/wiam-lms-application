package com.wiam.lms.web.rest;

import com.wiam.lms.domain.SessionProvider;
import com.wiam.lms.repository.SessionProviderRepository;
import com.wiam.lms.repository.search.SessionProviderSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.SessionProvider}.
 */
@RestController
@RequestMapping("/api/session-providers")
@Transactional
public class SessionProviderResource {

    private final Logger log = LoggerFactory.getLogger(SessionProviderResource.class);

    private static final String ENTITY_NAME = "sessionProvider";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SessionProviderRepository sessionProviderRepository;

    private final SessionProviderSearchRepository sessionProviderSearchRepository;

    public SessionProviderResource(
        SessionProviderRepository sessionProviderRepository,
        SessionProviderSearchRepository sessionProviderSearchRepository
    ) {
        this.sessionProviderRepository = sessionProviderRepository;
        this.sessionProviderSearchRepository = sessionProviderSearchRepository;
    }

    /**
     * {@code POST  /session-providers} : Create a new sessionProvider.
     *
     * @param sessionProvider the sessionProvider to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sessionProvider, or with status {@code 400 (Bad Request)} if the sessionProvider has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SessionProvider> createSessionProvider(@Valid @RequestBody SessionProvider sessionProvider)
        throws URISyntaxException {
        log.debug("REST request to save SessionProvider : {}", sessionProvider);
        if (sessionProvider.getId() != null) {
            throw new BadRequestAlertException("A new sessionProvider cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SessionProvider result = sessionProviderRepository.save(sessionProvider);
        sessionProviderSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/session-providers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /session-providers/:id} : Updates an existing sessionProvider.
     *
     * @param id the id of the sessionProvider to save.
     * @param sessionProvider the sessionProvider to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionProvider,
     * or with status {@code 400 (Bad Request)} if the sessionProvider is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sessionProvider couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SessionProvider> updateSessionProvider(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SessionProvider sessionProvider
    ) throws URISyntaxException {
        log.debug("REST request to update SessionProvider : {}, {}", id, sessionProvider);
        if (sessionProvider.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionProvider.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionProviderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SessionProvider result = sessionProviderRepository.save(sessionProvider);
        sessionProviderSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionProvider.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /session-providers/:id} : Partial updates given fields of an existing sessionProvider, field will ignore if it is null
     *
     * @param id the id of the sessionProvider to save.
     * @param sessionProvider the sessionProvider to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionProvider,
     * or with status {@code 400 (Bad Request)} if the sessionProvider is not valid,
     * or with status {@code 404 (Not Found)} if the sessionProvider is not found,
     * or with status {@code 500 (Internal Server Error)} if the sessionProvider couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SessionProvider> partialUpdateSessionProvider(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SessionProvider sessionProvider
    ) throws URISyntaxException {
        log.debug("REST request to partial update SessionProvider partially : {}, {}", id, sessionProvider);
        if (sessionProvider.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionProvider.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionProviderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SessionProvider> result = sessionProviderRepository
            .findById(sessionProvider.getId())
            .map(existingSessionProvider -> {
                if (sessionProvider.getName() != null) {
                    existingSessionProvider.setName(sessionProvider.getName());
                }
                if (sessionProvider.getDescription() != null) {
                    existingSessionProvider.setDescription(sessionProvider.getDescription());
                }
                if (sessionProvider.getWebsite() != null) {
                    existingSessionProvider.setWebsite(sessionProvider.getWebsite());
                }

                return existingSessionProvider;
            })
            .map(sessionProviderRepository::save)
            .map(savedSessionProvider -> {
                sessionProviderSearchRepository.index(savedSessionProvider);
                return savedSessionProvider;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sessionProvider.getId().toString())
        );
    }

    /**
     * {@code GET  /session-providers} : get all the sessionProviders.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sessionProviders in body.
     */
    @GetMapping("")
    public List<SessionProvider> getAllSessionProviders() {
        log.debug("REST request to get all SessionProviders");
        return sessionProviderRepository.findAll();
    }

    /**
     * {@code GET  /session-providers/:id} : get the "id" sessionProvider.
     *
     * @param id the id of the sessionProvider to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sessionProvider, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionProvider> getSessionProvider(@PathVariable("id") Long id) {
        log.debug("REST request to get SessionProvider : {}", id);
        Optional<SessionProvider> sessionProvider = sessionProviderRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sessionProvider);
    }

    /**
     * {@code DELETE  /session-providers/:id} : delete the "id" sessionProvider.
     *
     * @param id the id of the sessionProvider to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionProvider(@PathVariable("id") Long id) {
        log.debug("REST request to delete SessionProvider : {}", id);
        sessionProviderRepository.deleteById(id);
        sessionProviderSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /session-providers/_search?query=:query} : search for the sessionProvider corresponding
     * to the query.
     *
     * @param query the query of the sessionProvider search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<SessionProvider> searchSessionProviders(@RequestParam("query") String query) {
        log.debug("REST request to search SessionProviders for query {}", query);
        try {
            return StreamSupport.stream(sessionProviderSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
