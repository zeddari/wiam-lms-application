package com.wiam.lms.web.rest;

import com.wiam.lms.domain.CoteryHistory;
import com.wiam.lms.repository.CoteryHistoryRepository;
import com.wiam.lms.repository.search.CoteryHistorySearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.CoteryHistory}.
 */
@RestController
@RequestMapping("/api/cotery-histories")
@Transactional
public class CoteryHistoryResource {

    private final Logger log = LoggerFactory.getLogger(CoteryHistoryResource.class);

    private static final String ENTITY_NAME = "coteryHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CoteryHistoryRepository coteryHistoryRepository;

    private final CoteryHistorySearchRepository coteryHistorySearchRepository;

    public CoteryHistoryResource(
        CoteryHistoryRepository coteryHistoryRepository,
        CoteryHistorySearchRepository coteryHistorySearchRepository
    ) {
        this.coteryHistoryRepository = coteryHistoryRepository;
        this.coteryHistorySearchRepository = coteryHistorySearchRepository;
    }

    /**
     * {@code POST  /cotery-histories} : Create a new coteryHistory.
     *
     * @param coteryHistory the coteryHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new coteryHistory, or with status {@code 400 (Bad Request)} if the coteryHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CoteryHistory> createCoteryHistory(@RequestBody CoteryHistory coteryHistory) throws URISyntaxException {
        log.debug("REST request to save CoteryHistory : {}", coteryHistory);
        if (coteryHistory.getId() != null) {
            throw new BadRequestAlertException("A new coteryHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CoteryHistory result = coteryHistoryRepository.save(coteryHistory);
        coteryHistorySearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/cotery-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cotery-histories/:id} : Updates an existing coteryHistory.
     *
     * @param id the id of the coteryHistory to save.
     * @param coteryHistory the coteryHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coteryHistory,
     * or with status {@code 400 (Bad Request)} if the coteryHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the coteryHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CoteryHistory> updateCoteryHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CoteryHistory coteryHistory
    ) throws URISyntaxException {
        log.debug("REST request to update CoteryHistory : {}, {}", id, coteryHistory);
        if (coteryHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coteryHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coteryHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CoteryHistory result = coteryHistoryRepository.save(coteryHistory);
        coteryHistorySearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, coteryHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cotery-histories/:id} : Partial updates given fields of an existing coteryHistory, field will ignore if it is null
     *
     * @param id the id of the coteryHistory to save.
     * @param coteryHistory the coteryHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coteryHistory,
     * or with status {@code 400 (Bad Request)} if the coteryHistory is not valid,
     * or with status {@code 404 (Not Found)} if the coteryHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the coteryHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CoteryHistory> partialUpdateCoteryHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CoteryHistory coteryHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update CoteryHistory partially : {}, {}", id, coteryHistory);
        if (coteryHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coteryHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coteryHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CoteryHistory> result = coteryHistoryRepository
            .findById(coteryHistory.getId())
            .map(existingCoteryHistory -> {
                if (coteryHistory.getDate() != null) {
                    existingCoteryHistory.setDate(coteryHistory.getDate());
                }
                if (coteryHistory.getCoteryName() != null) {
                    existingCoteryHistory.setCoteryName(coteryHistory.getCoteryName());
                }
                if (coteryHistory.getStudentFullName() != null) {
                    existingCoteryHistory.setStudentFullName(coteryHistory.getStudentFullName());
                }
                if (coteryHistory.getAttendanceStatus() != null) {
                    existingCoteryHistory.setAttendanceStatus(coteryHistory.getAttendanceStatus());
                }

                return existingCoteryHistory;
            })
            .map(coteryHistoryRepository::save)
            .map(savedCoteryHistory -> {
                coteryHistorySearchRepository.index(savedCoteryHistory);
                return savedCoteryHistory;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, coteryHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /cotery-histories} : get all the coteryHistories.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of coteryHistories in body.
     */
    @GetMapping("")
    public List<CoteryHistory> getAllCoteryHistories(@RequestParam(name = "filter", required = false) String filter) {
        if ("followup-is-null".equals(filter)) {
            log.debug("REST request to get all CoteryHistorys where followUp is null");
            return StreamSupport
                .stream(coteryHistoryRepository.findAll().spliterator(), false)
                .filter(coteryHistory -> coteryHistory.getFollowUp() == null)
                .toList();
        }
        log.debug("REST request to get all CoteryHistories");
        return coteryHistoryRepository.findAll();
    }

    /**
     * {@code GET  /cotery-histories/:id} : get the "id" coteryHistory.
     *
     * @param id the id of the coteryHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the coteryHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CoteryHistory> getCoteryHistory(@PathVariable("id") Long id) {
        log.debug("REST request to get CoteryHistory : {}", id);
        Optional<CoteryHistory> coteryHistory = coteryHistoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(coteryHistory);
    }

    /**
     * {@code DELETE  /cotery-histories/:id} : delete the "id" coteryHistory.
     *
     * @param id the id of the coteryHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoteryHistory(@PathVariable("id") Long id) {
        log.debug("REST request to delete CoteryHistory : {}", id);
        coteryHistoryRepository.deleteById(id);
        coteryHistorySearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /cotery-histories/_search?query=:query} : search for the coteryHistory corresponding
     * to the query.
     *
     * @param query the query of the coteryHistory search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<CoteryHistory> searchCoteryHistories(@RequestParam("query") String query) {
        log.debug("REST request to search CoteryHistories for query {}", query);
        try {
            return StreamSupport.stream(coteryHistorySearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
