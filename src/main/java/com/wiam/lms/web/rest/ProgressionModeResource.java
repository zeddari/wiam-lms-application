package com.wiam.lms.web.rest;

import com.wiam.lms.domain.ProgressionMode;
import com.wiam.lms.repository.ProgressionModeRepository;
import com.wiam.lms.repository.search.ProgressionModeSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.ProgressionMode}.
 */
@RestController
@RequestMapping("/api/progression-modes")
@Transactional
public class ProgressionModeResource {

    private final Logger log = LoggerFactory.getLogger(ProgressionModeResource.class);

    private static final String ENTITY_NAME = "progressionMode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProgressionModeRepository progressionModeRepository;

    private final ProgressionModeSearchRepository progressionModeSearchRepository;

    public ProgressionModeResource(
        ProgressionModeRepository progressionModeRepository,
        ProgressionModeSearchRepository progressionModeSearchRepository
    ) {
        this.progressionModeRepository = progressionModeRepository;
        this.progressionModeSearchRepository = progressionModeSearchRepository;
    }

    /**
     * {@code POST  /progression-modes} : Create a new progressionMode.
     *
     * @param progressionMode the progressionMode to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new progressionMode, or with status {@code 400 (Bad Request)} if the progressionMode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProgressionMode> createProgressionMode(@Valid @RequestBody ProgressionMode progressionMode)
        throws URISyntaxException {
        log.debug("REST request to save ProgressionMode : {}", progressionMode);
        if (progressionMode.getId() != null) {
            throw new BadRequestAlertException("A new progressionMode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProgressionMode result = progressionModeRepository.save(progressionMode);
        progressionModeSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/progression-modes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /progression-modes/:id} : Updates an existing progressionMode.
     *
     * @param id the id of the progressionMode to save.
     * @param progressionMode the progressionMode to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated progressionMode,
     * or with status {@code 400 (Bad Request)} if the progressionMode is not valid,
     * or with status {@code 500 (Internal Server Error)} if the progressionMode couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProgressionMode> updateProgressionMode(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProgressionMode progressionMode
    ) throws URISyntaxException {
        log.debug("REST request to update ProgressionMode : {}, {}", id, progressionMode);
        if (progressionMode.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, progressionMode.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!progressionModeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProgressionMode result = progressionModeRepository.save(progressionMode);
        progressionModeSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, progressionMode.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /progression-modes/:id} : Partial updates given fields of an existing progressionMode, field will ignore if it is null
     *
     * @param id the id of the progressionMode to save.
     * @param progressionMode the progressionMode to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated progressionMode,
     * or with status {@code 400 (Bad Request)} if the progressionMode is not valid,
     * or with status {@code 404 (Not Found)} if the progressionMode is not found,
     * or with status {@code 500 (Internal Server Error)} if the progressionMode couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProgressionMode> partialUpdateProgressionMode(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProgressionMode progressionMode
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProgressionMode partially : {}, {}", id, progressionMode);
        if (progressionMode.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, progressionMode.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!progressionModeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProgressionMode> result = progressionModeRepository
            .findById(progressionMode.getId())
            .map(existingProgressionMode -> {
                if (progressionMode.getTitleAr() != null) {
                    existingProgressionMode.setTitleAr(progressionMode.getTitleAr());
                }
                if (progressionMode.getTitleLat() != null) {
                    existingProgressionMode.setTitleLat(progressionMode.getTitleLat());
                }

                return existingProgressionMode;
            })
            .map(progressionModeRepository::save)
            .map(savedProgressionMode -> {
                progressionModeSearchRepository.index(savedProgressionMode);
                return savedProgressionMode;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, progressionMode.getId().toString())
        );
    }

    /**
     * {@code GET  /progression-modes} : get all the progressionModes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of progressionModes in body.
     */
    @GetMapping("")
    public List<ProgressionMode> getAllProgressionModes() {
        log.debug("REST request to get all ProgressionModes");
        return progressionModeRepository.findAll();
    }

    /**
     * {@code GET  /progression-modes/:id} : get the "id" progressionMode.
     *
     * @param id the id of the progressionMode to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the progressionMode, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProgressionMode> getProgressionMode(@PathVariable("id") Long id) {
        log.debug("REST request to get ProgressionMode : {}", id);
        Optional<ProgressionMode> progressionMode = progressionModeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(progressionMode);
    }

    /**
     * {@code DELETE  /progression-modes/:id} : delete the "id" progressionMode.
     *
     * @param id the id of the progressionMode to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgressionMode(@PathVariable("id") Long id) {
        log.debug("REST request to delete ProgressionMode : {}", id);
        progressionModeRepository.deleteById(id);
        progressionModeSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /progression-modes/_search?query=:query} : search for the progressionMode corresponding
     * to the query.
     *
     * @param query the query of the progressionMode search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ProgressionMode> searchProgressionModes(@RequestParam("query") String query) {
        log.debug("REST request to search ProgressionModes for query {}", query);
        try {
            return StreamSupport.stream(progressionModeSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
