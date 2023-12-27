package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Diploma;
import com.wiam.lms.repository.DiplomaRepository;
import com.wiam.lms.repository.search.DiplomaSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Diploma}.
 */
@RestController
@RequestMapping("/api/diplomas")
@Transactional
public class DiplomaResource {

    private final Logger log = LoggerFactory.getLogger(DiplomaResource.class);

    private static final String ENTITY_NAME = "diploma";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DiplomaRepository diplomaRepository;

    private final DiplomaSearchRepository diplomaSearchRepository;

    public DiplomaResource(DiplomaRepository diplomaRepository, DiplomaSearchRepository diplomaSearchRepository) {
        this.diplomaRepository = diplomaRepository;
        this.diplomaSearchRepository = diplomaSearchRepository;
    }

    /**
     * {@code POST  /diplomas} : Create a new diploma.
     *
     * @param diploma the diploma to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new diploma, or with status {@code 400 (Bad Request)} if the diploma has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Diploma> createDiploma(@Valid @RequestBody Diploma diploma) throws URISyntaxException {
        log.debug("REST request to save Diploma : {}", diploma);
        if (diploma.getId() != null) {
            throw new BadRequestAlertException("A new diploma cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Diploma result = diplomaRepository.save(diploma);
        diplomaSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/diplomas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /diplomas/:id} : Updates an existing diploma.
     *
     * @param id the id of the diploma to save.
     * @param diploma the diploma to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diploma,
     * or with status {@code 400 (Bad Request)} if the diploma is not valid,
     * or with status {@code 500 (Internal Server Error)} if the diploma couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Diploma> updateDiploma(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Diploma diploma
    ) throws URISyntaxException {
        log.debug("REST request to update Diploma : {}, {}", id, diploma);
        if (diploma.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, diploma.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!diplomaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Diploma result = diplomaRepository.save(diploma);
        diplomaSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, diploma.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /diplomas/:id} : Partial updates given fields of an existing diploma, field will ignore if it is null
     *
     * @param id the id of the diploma to save.
     * @param diploma the diploma to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diploma,
     * or with status {@code 400 (Bad Request)} if the diploma is not valid,
     * or with status {@code 404 (Not Found)} if the diploma is not found,
     * or with status {@code 500 (Internal Server Error)} if the diploma couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Diploma> partialUpdateDiploma(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Diploma diploma
    ) throws URISyntaxException {
        log.debug("REST request to partial update Diploma partially : {}, {}", id, diploma);
        if (diploma.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, diploma.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!diplomaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Diploma> result = diplomaRepository
            .findById(diploma.getId())
            .map(existingDiploma -> {
                if (diploma.getTitle() != null) {
                    existingDiploma.setTitle(diploma.getTitle());
                }
                if (diploma.getSubject() != null) {
                    existingDiploma.setSubject(diploma.getSubject());
                }
                if (diploma.getDetail() != null) {
                    existingDiploma.setDetail(diploma.getDetail());
                }
                if (diploma.getSupervisor() != null) {
                    existingDiploma.setSupervisor(diploma.getSupervisor());
                }
                if (diploma.getGrade() != null) {
                    existingDiploma.setGrade(diploma.getGrade());
                }
                if (diploma.getGraduationDate() != null) {
                    existingDiploma.setGraduationDate(diploma.getGraduationDate());
                }
                if (diploma.getSchool() != null) {
                    existingDiploma.setSchool(diploma.getSchool());
                }

                return existingDiploma;
            })
            .map(diplomaRepository::save)
            .map(savedDiploma -> {
                diplomaSearchRepository.index(savedDiploma);
                return savedDiploma;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, diploma.getId().toString())
        );
    }

    /**
     * {@code GET  /diplomas} : get all the diplomas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of diplomas in body.
     */
    @GetMapping("")
    public List<Diploma> getAllDiplomas() {
        log.debug("REST request to get all Diplomas");
        return diplomaRepository.findAll();
    }

    /**
     * {@code GET  /diplomas/:id} : get the "id" diploma.
     *
     * @param id the id of the diploma to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the diploma, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Diploma> getDiploma(@PathVariable("id") Long id) {
        log.debug("REST request to get Diploma : {}", id);
        Optional<Diploma> diploma = diplomaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(diploma);
    }

    /**
     * {@code DELETE  /diplomas/:id} : delete the "id" diploma.
     *
     * @param id the id of the diploma to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiploma(@PathVariable("id") Long id) {
        log.debug("REST request to delete Diploma : {}", id);
        diplomaRepository.deleteById(id);
        diplomaSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /diplomas/_search?query=:query} : search for the diploma corresponding
     * to the query.
     *
     * @param query the query of the diploma search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Diploma> searchDiplomas(@RequestParam("query") String query) {
        log.debug("REST request to search Diplomas for query {}", query);
        try {
            return StreamSupport.stream(diplomaSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
