package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Enrolement;
import com.wiam.lms.repository.EnrolementRepository;
import com.wiam.lms.repository.search.EnrolementSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Enrolement}.
 */
@RestController
@RequestMapping("/api/enrolements")
@Transactional
public class EnrolementResource {

    private final Logger log = LoggerFactory.getLogger(EnrolementResource.class);

    private static final String ENTITY_NAME = "enrolement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnrolementRepository enrolementRepository;

    private final EnrolementSearchRepository enrolementSearchRepository;

    public EnrolementResource(EnrolementRepository enrolementRepository, EnrolementSearchRepository enrolementSearchRepository) {
        this.enrolementRepository = enrolementRepository;
        this.enrolementSearchRepository = enrolementSearchRepository;
    }

    /**
     * {@code POST  /enrolements} : Create a new enrolement.
     *
     * @param enrolement the enrolement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new enrolement, or with status {@code 400 (Bad Request)} if the enrolement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Enrolement> createEnrolement(@Valid @RequestBody Enrolement enrolement) throws URISyntaxException {
        log.debug("REST request to save Enrolement : {}", enrolement);
        if (enrolement.getId() != null) {
            throw new BadRequestAlertException("A new enrolement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Enrolement result = enrolementRepository.save(enrolement);
        enrolementSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/enrolements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /enrolements/:id} : Updates an existing enrolement.
     *
     * @param id the id of the enrolement to save.
     * @param enrolement the enrolement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enrolement,
     * or with status {@code 400 (Bad Request)} if the enrolement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the enrolement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Enrolement> updateEnrolement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Enrolement enrolement
    ) throws URISyntaxException {
        log.debug("REST request to update Enrolement : {}, {}", id, enrolement);
        if (enrolement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enrolement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enrolementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Enrolement result = enrolementRepository.save(enrolement);
        enrolementSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, enrolement.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /enrolements/:id} : Partial updates given fields of an existing enrolement, field will ignore if it is null
     *
     * @param id the id of the enrolement to save.
     * @param enrolement the enrolement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enrolement,
     * or with status {@code 400 (Bad Request)} if the enrolement is not valid,
     * or with status {@code 404 (Not Found)} if the enrolement is not found,
     * or with status {@code 500 (Internal Server Error)} if the enrolement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Enrolement> partialUpdateEnrolement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Enrolement enrolement
    ) throws URISyntaxException {
        log.debug("REST request to partial update Enrolement partially : {}, {}", id, enrolement);
        if (enrolement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enrolement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enrolementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Enrolement> result = enrolementRepository
            .findById(enrolement.getId())
            .map(existingEnrolement -> {
                if (enrolement.getIsActive() != null) {
                    existingEnrolement.setIsActive(enrolement.getIsActive());
                }
                if (enrolement.getActivatedAt() != null) {
                    existingEnrolement.setActivatedAt(enrolement.getActivatedAt());
                }
                if (enrolement.getActivatedBy() != null) {
                    existingEnrolement.setActivatedBy(enrolement.getActivatedBy());
                }
                if (enrolement.getEnrolmentStartTime() != null) {
                    existingEnrolement.setEnrolmentStartTime(enrolement.getEnrolmentStartTime());
                }
                if (enrolement.getEnrolemntEndTime() != null) {
                    existingEnrolement.setEnrolemntEndTime(enrolement.getEnrolemntEndTime());
                }

                return existingEnrolement;
            })
            .map(enrolementRepository::save)
            .map(savedEnrolement -> {
                enrolementSearchRepository.index(savedEnrolement);
                return savedEnrolement;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, enrolement.getId().toString())
        );
    }

    /**
     * {@code GET  /enrolements} : get all the enrolements.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of enrolements in body.
     */
    @GetMapping("")
    public List<Enrolement> getAllEnrolements() {
        log.debug("REST request to get all Enrolements");
        return enrolementRepository.findAll();
    }

    /**
     * {@code GET  /enrolements/:id} : get the "id" enrolement.
     *
     * @param id the id of the enrolement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the enrolement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Enrolement> getEnrolement(@PathVariable("id") Long id) {
        log.debug("REST request to get Enrolement : {}", id);
        Optional<Enrolement> enrolement = enrolementRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(enrolement);
    }

    /**
     * {@code DELETE  /enrolements/:id} : delete the "id" enrolement.
     *
     * @param id the id of the enrolement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrolement(@PathVariable("id") Long id) {
        log.debug("REST request to delete Enrolement : {}", id);
        enrolementRepository.deleteById(id);
        enrolementSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /enrolements/_search?query=:query} : search for the enrolement corresponding
     * to the query.
     *
     * @param query the query of the enrolement search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Enrolement> searchEnrolements(@RequestParam("query") String query) {
        log.debug("REST request to search Enrolements for query {}", query);
        try {
            return StreamSupport.stream(enrolementSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
