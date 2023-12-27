package com.wiam.lms.web.rest;

import com.wiam.lms.domain.DiplomaType;
import com.wiam.lms.repository.DiplomaTypeRepository;
import com.wiam.lms.repository.search.DiplomaTypeSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.DiplomaType}.
 */
@RestController
@RequestMapping("/api/diploma-types")
@Transactional
public class DiplomaTypeResource {

    private final Logger log = LoggerFactory.getLogger(DiplomaTypeResource.class);

    private static final String ENTITY_NAME = "diplomaType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DiplomaTypeRepository diplomaTypeRepository;

    private final DiplomaTypeSearchRepository diplomaTypeSearchRepository;

    public DiplomaTypeResource(DiplomaTypeRepository diplomaTypeRepository, DiplomaTypeSearchRepository diplomaTypeSearchRepository) {
        this.diplomaTypeRepository = diplomaTypeRepository;
        this.diplomaTypeSearchRepository = diplomaTypeSearchRepository;
    }

    /**
     * {@code POST  /diploma-types} : Create a new diplomaType.
     *
     * @param diplomaType the diplomaType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new diplomaType, or with status {@code 400 (Bad Request)} if the diplomaType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DiplomaType> createDiplomaType(@Valid @RequestBody DiplomaType diplomaType) throws URISyntaxException {
        log.debug("REST request to save DiplomaType : {}", diplomaType);
        if (diplomaType.getId() != null) {
            throw new BadRequestAlertException("A new diplomaType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DiplomaType result = diplomaTypeRepository.save(diplomaType);
        diplomaTypeSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/diploma-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /diploma-types/:id} : Updates an existing diplomaType.
     *
     * @param id the id of the diplomaType to save.
     * @param diplomaType the diplomaType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diplomaType,
     * or with status {@code 400 (Bad Request)} if the diplomaType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the diplomaType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiplomaType> updateDiplomaType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DiplomaType diplomaType
    ) throws URISyntaxException {
        log.debug("REST request to update DiplomaType : {}, {}", id, diplomaType);
        if (diplomaType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, diplomaType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!diplomaTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DiplomaType result = diplomaTypeRepository.save(diplomaType);
        diplomaTypeSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, diplomaType.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /diploma-types/:id} : Partial updates given fields of an existing diplomaType, field will ignore if it is null
     *
     * @param id the id of the diplomaType to save.
     * @param diplomaType the diplomaType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diplomaType,
     * or with status {@code 400 (Bad Request)} if the diplomaType is not valid,
     * or with status {@code 404 (Not Found)} if the diplomaType is not found,
     * or with status {@code 500 (Internal Server Error)} if the diplomaType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DiplomaType> partialUpdateDiplomaType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DiplomaType diplomaType
    ) throws URISyntaxException {
        log.debug("REST request to partial update DiplomaType partially : {}, {}", id, diplomaType);
        if (diplomaType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, diplomaType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!diplomaTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DiplomaType> result = diplomaTypeRepository
            .findById(diplomaType.getId())
            .map(existingDiplomaType -> {
                if (diplomaType.getTitleAr() != null) {
                    existingDiplomaType.setTitleAr(diplomaType.getTitleAr());
                }
                if (diplomaType.getTitleLat() != null) {
                    existingDiplomaType.setTitleLat(diplomaType.getTitleLat());
                }

                return existingDiplomaType;
            })
            .map(diplomaTypeRepository::save)
            .map(savedDiplomaType -> {
                diplomaTypeSearchRepository.index(savedDiplomaType);
                return savedDiplomaType;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, diplomaType.getId().toString())
        );
    }

    /**
     * {@code GET  /diploma-types} : get all the diplomaTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of diplomaTypes in body.
     */
    @GetMapping("")
    public List<DiplomaType> getAllDiplomaTypes() {
        log.debug("REST request to get all DiplomaTypes");
        return diplomaTypeRepository.findAll();
    }

    /**
     * {@code GET  /diploma-types/:id} : get the "id" diplomaType.
     *
     * @param id the id of the diplomaType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the diplomaType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiplomaType> getDiplomaType(@PathVariable("id") Long id) {
        log.debug("REST request to get DiplomaType : {}", id);
        Optional<DiplomaType> diplomaType = diplomaTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(diplomaType);
    }

    /**
     * {@code DELETE  /diploma-types/:id} : delete the "id" diplomaType.
     *
     * @param id the id of the diplomaType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiplomaType(@PathVariable("id") Long id) {
        log.debug("REST request to delete DiplomaType : {}", id);
        diplomaTypeRepository.deleteById(id);
        diplomaTypeSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /diploma-types/_search?query=:query} : search for the diplomaType corresponding
     * to the query.
     *
     * @param query the query of the diplomaType search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<DiplomaType> searchDiplomaTypes(@RequestParam("query") String query) {
        log.debug("REST request to search DiplomaTypes for query {}", query);
        try {
            return StreamSupport.stream(diplomaTypeSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
