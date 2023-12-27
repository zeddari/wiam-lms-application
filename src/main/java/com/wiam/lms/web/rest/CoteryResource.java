package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Cotery;
import com.wiam.lms.repository.CoteryRepository;
import com.wiam.lms.repository.search.CoterySearchRepository;
import com.wiam.lms.web.rest.errors.BadRequestAlertException;
import com.wiam.lms.web.rest.errors.ElasticsearchExceptionMapper;
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
 * REST controller for managing {@link com.wiam.lms.domain.Cotery}.
 */
@RestController
@RequestMapping("/api/coteries")
@Transactional
public class CoteryResource {

    private final Logger log = LoggerFactory.getLogger(CoteryResource.class);

    private static final String ENTITY_NAME = "cotery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CoteryRepository coteryRepository;

    private final CoterySearchRepository coterySearchRepository;

    public CoteryResource(CoteryRepository coteryRepository, CoterySearchRepository coterySearchRepository) {
        this.coteryRepository = coteryRepository;
        this.coterySearchRepository = coterySearchRepository;
    }

    /**
     * {@code POST  /coteries} : Create a new cotery.
     *
     * @param cotery the cotery to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cotery, or with status {@code 400 (Bad Request)} if the cotery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Cotery> createCotery(@RequestBody Cotery cotery) throws URISyntaxException {
        log.debug("REST request to save Cotery : {}", cotery);
        if (cotery.getId() != null) {
            throw new BadRequestAlertException("A new cotery cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Cotery result = coteryRepository.save(cotery);
        coterySearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/coteries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /coteries/:id} : Updates an existing cotery.
     *
     * @param id the id of the cotery to save.
     * @param cotery the cotery to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cotery,
     * or with status {@code 400 (Bad Request)} if the cotery is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cotery couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cotery> updateCotery(@PathVariable(value = "id", required = false) final Long id, @RequestBody Cotery cotery)
        throws URISyntaxException {
        log.debug("REST request to update Cotery : {}, {}", id, cotery);
        if (cotery.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cotery.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coteryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Cotery result = coteryRepository.save(cotery);
        coterySearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cotery.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /coteries/:id} : Partial updates given fields of an existing cotery, field will ignore if it is null
     *
     * @param id the id of the cotery to save.
     * @param cotery the cotery to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cotery,
     * or with status {@code 400 (Bad Request)} if the cotery is not valid,
     * or with status {@code 404 (Not Found)} if the cotery is not found,
     * or with status {@code 500 (Internal Server Error)} if the cotery couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Cotery> partialUpdateCotery(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Cotery cotery
    ) throws URISyntaxException {
        log.debug("REST request to partial update Cotery partially : {}, {}", id, cotery);
        if (cotery.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cotery.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coteryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Cotery> result = coteryRepository
            .findById(cotery.getId())
            .map(existingCotery -> {
                if (cotery.getDate() != null) {
                    existingCotery.setDate(cotery.getDate());
                }
                if (cotery.getCoteryName() != null) {
                    existingCotery.setCoteryName(cotery.getCoteryName());
                }
                if (cotery.getStudentFullName() != null) {
                    existingCotery.setStudentFullName(cotery.getStudentFullName());
                }
                if (cotery.getAttendanceStatus() != null) {
                    existingCotery.setAttendanceStatus(cotery.getAttendanceStatus());
                }

                return existingCotery;
            })
            .map(coteryRepository::save)
            .map(savedCotery -> {
                coterySearchRepository.index(savedCotery);
                return savedCotery;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cotery.getId().toString())
        );
    }

    /**
     * {@code GET  /coteries} : get all the coteries.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of coteries in body.
     */
    @GetMapping("")
    public List<Cotery> getAllCoteries() {
        log.debug("REST request to get all Coteries");
        return coteryRepository.findAll();
    }

    /**
     * {@code GET  /coteries/:id} : get the "id" cotery.
     *
     * @param id the id of the cotery to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cotery, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cotery> getCotery(@PathVariable("id") Long id) {
        log.debug("REST request to get Cotery : {}", id);
        Optional<Cotery> cotery = coteryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cotery);
    }

    /**
     * {@code DELETE  /coteries/:id} : delete the "id" cotery.
     *
     * @param id the id of the cotery to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCotery(@PathVariable("id") Long id) {
        log.debug("REST request to delete Cotery : {}", id);
        coteryRepository.deleteById(id);
        coterySearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /coteries/_search?query=:query} : search for the cotery corresponding
     * to the query.
     *
     * @param query the query of the cotery search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Cotery> searchCoteries(@RequestParam("query") String query) {
        log.debug("REST request to search Coteries for query {}", query);
        try {
            return StreamSupport.stream(coterySearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
