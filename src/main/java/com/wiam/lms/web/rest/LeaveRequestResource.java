package com.wiam.lms.web.rest;

import com.wiam.lms.domain.LeaveRequest;
import com.wiam.lms.repository.LeaveRequestRepository;
import com.wiam.lms.repository.search.LeaveRequestSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.LeaveRequest}.
 */
@RestController
@RequestMapping("/api/leave-requests")
@Transactional
public class LeaveRequestResource {

    private final Logger log = LoggerFactory.getLogger(LeaveRequestResource.class);

    private static final String ENTITY_NAME = "leaveRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LeaveRequestRepository leaveRequestRepository;

    private final LeaveRequestSearchRepository leaveRequestSearchRepository;

    public LeaveRequestResource(LeaveRequestRepository leaveRequestRepository, LeaveRequestSearchRepository leaveRequestSearchRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveRequestSearchRepository = leaveRequestSearchRepository;
    }

    /**
     * {@code POST  /leave-requests} : Create a new leaveRequest.
     *
     * @param leaveRequest the leaveRequest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new leaveRequest, or with status {@code 400 (Bad Request)} if the leaveRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LeaveRequest> createLeaveRequest(@Valid @RequestBody LeaveRequest leaveRequest) throws URISyntaxException {
        log.debug("REST request to save LeaveRequest : {}", leaveRequest);
        if (leaveRequest.getId() != null) {
            throw new BadRequestAlertException("A new leaveRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LeaveRequest result = leaveRequestRepository.save(leaveRequest);
        leaveRequestSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/leave-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /leave-requests/:id} : Updates an existing leaveRequest.
     *
     * @param id the id of the leaveRequest to save.
     * @param leaveRequest the leaveRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated leaveRequest,
     * or with status {@code 400 (Bad Request)} if the leaveRequest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the leaveRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequest> updateLeaveRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LeaveRequest leaveRequest
    ) throws URISyntaxException {
        log.debug("REST request to update LeaveRequest : {}, {}", id, leaveRequest);
        if (leaveRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, leaveRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leaveRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LeaveRequest result = leaveRequestRepository.save(leaveRequest);
        leaveRequestSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, leaveRequest.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /leave-requests/:id} : Partial updates given fields of an existing leaveRequest, field will ignore if it is null
     *
     * @param id the id of the leaveRequest to save.
     * @param leaveRequest the leaveRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated leaveRequest,
     * or with status {@code 400 (Bad Request)} if the leaveRequest is not valid,
     * or with status {@code 404 (Not Found)} if the leaveRequest is not found,
     * or with status {@code 500 (Internal Server Error)} if the leaveRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LeaveRequest> partialUpdateLeaveRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LeaveRequest leaveRequest
    ) throws URISyntaxException {
        log.debug("REST request to partial update LeaveRequest partially : {}, {}", id, leaveRequest);
        if (leaveRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, leaveRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leaveRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LeaveRequest> result = leaveRequestRepository
            .findById(leaveRequest.getId())
            .map(existingLeaveRequest -> {
                if (leaveRequest.getTitle() != null) {
                    existingLeaveRequest.setTitle(leaveRequest.getTitle());
                }
                if (leaveRequest.getFrom() != null) {
                    existingLeaveRequest.setFrom(leaveRequest.getFrom());
                }
                if (leaveRequest.getToDate() != null) {
                    existingLeaveRequest.setToDate(leaveRequest.getToDate());
                }
                if (leaveRequest.getDetails() != null) {
                    existingLeaveRequest.setDetails(leaveRequest.getDetails());
                }

                return existingLeaveRequest;
            })
            .map(leaveRequestRepository::save)
            .map(savedLeaveRequest -> {
                leaveRequestSearchRepository.index(savedLeaveRequest);
                return savedLeaveRequest;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, leaveRequest.getId().toString())
        );
    }

    /**
     * {@code GET  /leave-requests} : get all the leaveRequests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of leaveRequests in body.
     */
    @GetMapping("")
    public List<LeaveRequest> getAllLeaveRequests() {
        log.debug("REST request to get all LeaveRequests");
        return leaveRequestRepository.findAll();
    }

    /**
     * {@code GET  /leave-requests/:id} : get the "id" leaveRequest.
     *
     * @param id the id of the leaveRequest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the leaveRequest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getLeaveRequest(@PathVariable("id") Long id) {
        log.debug("REST request to get LeaveRequest : {}", id);
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(leaveRequest);
    }

    /**
     * {@code DELETE  /leave-requests/:id} : delete the "id" leaveRequest.
     *
     * @param id the id of the leaveRequest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable("id") Long id) {
        log.debug("REST request to delete LeaveRequest : {}", id);
        leaveRequestRepository.deleteById(id);
        leaveRequestSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /leave-requests/_search?query=:query} : search for the leaveRequest corresponding
     * to the query.
     *
     * @param query the query of the leaveRequest search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<LeaveRequest> searchLeaveRequests(@RequestParam("query") String query) {
        log.debug("REST request to search LeaveRequests for query {}", query);
        try {
            return StreamSupport.stream(leaveRequestSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
