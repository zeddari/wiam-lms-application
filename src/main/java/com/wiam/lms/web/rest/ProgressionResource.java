package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Progression;
import com.wiam.lms.repository.ProgressionRepository;
import com.wiam.lms.repository.search.ProgressionSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Progression}.
 */
@RestController
@RequestMapping("/api/progressions")
@Transactional
public class ProgressionResource {

    private final Logger log = LoggerFactory.getLogger(ProgressionResource.class);

    private static final String ENTITY_NAME = "progression";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProgressionRepository progressionRepository;

    private final ProgressionSearchRepository progressionSearchRepository;

    public ProgressionResource(ProgressionRepository progressionRepository, ProgressionSearchRepository progressionSearchRepository) {
        this.progressionRepository = progressionRepository;
        this.progressionSearchRepository = progressionSearchRepository;
    }

    /**
     * {@code POST  /progressions} : Create a new progression.
     *
     * @param progression the progression to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new progression, or with status {@code 400 (Bad Request)} if the progression has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Progression> createProgression(@Valid @RequestBody Progression progression) throws URISyntaxException {
        log.debug("REST request to save Progression : {}", progression);
        if (progression.getId() != null) {
            throw new BadRequestAlertException("A new progression cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Progression result = progressionRepository.save(progression);
        progressionSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/progressions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /progressions/:id} : Updates an existing progression.
     *
     * @param id the id of the progression to save.
     * @param progression the progression to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated progression,
     * or with status {@code 400 (Bad Request)} if the progression is not valid,
     * or with status {@code 500 (Internal Server Error)} if the progression couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Progression> updateProgression(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Progression progression
    ) throws URISyntaxException {
        log.debug("REST request to update Progression : {}, {}", id, progression);
        if (progression.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, progression.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!progressionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Progression result = progressionRepository.save(progression);
        progressionSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, progression.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /progressions/:id} : Partial updates given fields of an existing progression, field will ignore if it is null
     *
     * @param id the id of the progression to save.
     * @param progression the progression to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated progression,
     * or with status {@code 400 (Bad Request)} if the progression is not valid,
     * or with status {@code 404 (Not Found)} if the progression is not found,
     * or with status {@code 500 (Internal Server Error)} if the progression couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Progression> partialUpdateProgression(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Progression progression
    ) throws URISyntaxException {
        log.debug("REST request to partial update Progression partially : {}, {}", id, progression);
        if (progression.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, progression.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!progressionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Progression> result = progressionRepository
            .findById(progression.getId())
            .map(existingProgression -> {
                if (progression.getStatus() != null) {
                    existingProgression.setStatus(progression.getStatus());
                }
                if (progression.getIsJustified() != null) {
                    existingProgression.setIsJustified(progression.getIsJustified());
                }
                if (progression.getJustifRef() != null) {
                    existingProgression.setJustifRef(progression.getJustifRef());
                }
                if (progression.getLateArrival() != null) {
                    existingProgression.setLateArrival(progression.getLateArrival());
                }
                if (progression.getEarlyDeparture() != null) {
                    existingProgression.setEarlyDeparture(progression.getEarlyDeparture());
                }
                if (progression.getTaskDone() != null) {
                    existingProgression.setTaskDone(progression.getTaskDone());
                }
                if (progression.getGrade1() != null) {
                    existingProgression.setGrade1(progression.getGrade1());
                }
                if (progression.getDescription() != null) {
                    existingProgression.setDescription(progression.getDescription());
                }
                if (progression.getTaskStart() != null) {
                    existingProgression.setTaskStart(progression.getTaskStart());
                }
                if (progression.getTaskEnd() != null) {
                    existingProgression.setTaskEnd(progression.getTaskEnd());
                }
                if (progression.getTaskStep() != null) {
                    existingProgression.setTaskStep(progression.getTaskStep());
                }
                if (progression.getProgressionDate() != null) {
                    existingProgression.setProgressionDate(progression.getProgressionDate());
                }

                return existingProgression;
            })
            .map(progressionRepository::save)
            .map(savedProgression -> {
                progressionSearchRepository.index(savedProgression);
                return savedProgression;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, progression.getId().toString())
        );
    }

    /**
     * {@code GET  /progressions} : get all the progressions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of progressions in body.
     */
    @GetMapping("")
    public List<Progression> getAllProgressions() {
        log.debug("REST request to get all Progressions");
        return progressionRepository.findAll();
    }

    /**
     * {@code GET  /progressions/:id} : get the "id" progression.
     *
     * @param id the id of the progression to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the progression, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Progression> getProgression(@PathVariable("id") Long id) {
        log.debug("REST request to get Progression : {}", id);
        Optional<Progression> progression = progressionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(progression);
    }

    /**
     * {@code DELETE  /progressions/:id} : delete the "id" progression.
     *
     * @param id the id of the progression to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgression(@PathVariable("id") Long id) {
        log.debug("REST request to delete Progression : {}", id);
        progressionRepository.deleteById(id);
        progressionSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /progressions/_search?query=:query} : search for the progression corresponding
     * to the query.
     *
     * @param query the query of the progression search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Progression> searchProgressions(@RequestParam("query") String query) {
        log.debug("REST request to search Progressions for query {}", query);
        try {
            return StreamSupport.stream(progressionSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
