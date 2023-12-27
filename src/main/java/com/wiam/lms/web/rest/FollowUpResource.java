package com.wiam.lms.web.rest;

import com.wiam.lms.domain.FollowUp;
import com.wiam.lms.repository.FollowUpRepository;
import com.wiam.lms.repository.search.FollowUpSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.FollowUp}.
 */
@RestController
@RequestMapping("/api/follow-ups")
@Transactional
public class FollowUpResource {

    private final Logger log = LoggerFactory.getLogger(FollowUpResource.class);

    private static final String ENTITY_NAME = "followUp";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FollowUpRepository followUpRepository;

    private final FollowUpSearchRepository followUpSearchRepository;

    public FollowUpResource(FollowUpRepository followUpRepository, FollowUpSearchRepository followUpSearchRepository) {
        this.followUpRepository = followUpRepository;
        this.followUpSearchRepository = followUpSearchRepository;
    }

    /**
     * {@code POST  /follow-ups} : Create a new followUp.
     *
     * @param followUp the followUp to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new followUp, or with status {@code 400 (Bad Request)} if the followUp has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FollowUp> createFollowUp(@RequestBody FollowUp followUp) throws URISyntaxException {
        log.debug("REST request to save FollowUp : {}", followUp);
        if (followUp.getId() != null) {
            throw new BadRequestAlertException("A new followUp cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FollowUp result = followUpRepository.save(followUp);
        followUpSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/follow-ups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /follow-ups/:id} : Updates an existing followUp.
     *
     * @param id the id of the followUp to save.
     * @param followUp the followUp to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followUp,
     * or with status {@code 400 (Bad Request)} if the followUp is not valid,
     * or with status {@code 500 (Internal Server Error)} if the followUp couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FollowUp> updateFollowUp(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FollowUp followUp
    ) throws URISyntaxException {
        log.debug("REST request to update FollowUp : {}, {}", id, followUp);
        if (followUp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followUp.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followUpRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FollowUp result = followUpRepository.save(followUp);
        followUpSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, followUp.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /follow-ups/:id} : Partial updates given fields of an existing followUp, field will ignore if it is null
     *
     * @param id the id of the followUp to save.
     * @param followUp the followUp to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followUp,
     * or with status {@code 400 (Bad Request)} if the followUp is not valid,
     * or with status {@code 404 (Not Found)} if the followUp is not found,
     * or with status {@code 500 (Internal Server Error)} if the followUp couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FollowUp> partialUpdateFollowUp(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FollowUp followUp
    ) throws URISyntaxException {
        log.debug("REST request to partial update FollowUp partially : {}, {}", id, followUp);
        if (followUp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followUp.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followUpRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FollowUp> result = followUpRepository
            .findById(followUp.getId())
            .map(existingFollowUp -> {
                if (followUp.getFromSourate() != null) {
                    existingFollowUp.setFromSourate(followUp.getFromSourate());
                }
                if (followUp.getFromAya() != null) {
                    existingFollowUp.setFromAya(followUp.getFromAya());
                }
                if (followUp.getToSourate() != null) {
                    existingFollowUp.setToSourate(followUp.getToSourate());
                }
                if (followUp.getToAya() != null) {
                    existingFollowUp.setToAya(followUp.getToAya());
                }
                if (followUp.getTilawaType() != null) {
                    existingFollowUp.setTilawaType(followUp.getTilawaType());
                }
                if (followUp.getNotation() != null) {
                    existingFollowUp.setNotation(followUp.getNotation());
                }
                if (followUp.getRemarks() != null) {
                    existingFollowUp.setRemarks(followUp.getRemarks());
                }

                return existingFollowUp;
            })
            .map(followUpRepository::save)
            .map(savedFollowUp -> {
                followUpSearchRepository.index(savedFollowUp);
                return savedFollowUp;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, followUp.getId().toString())
        );
    }

    /**
     * {@code GET  /follow-ups} : get all the followUps.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of followUps in body.
     */
    @GetMapping("")
    public List<FollowUp> getAllFollowUps() {
        log.debug("REST request to get all FollowUps");
        return followUpRepository.findAll();
    }

    /**
     * {@code GET  /follow-ups/:id} : get the "id" followUp.
     *
     * @param id the id of the followUp to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the followUp, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FollowUp> getFollowUp(@PathVariable("id") Long id) {
        log.debug("REST request to get FollowUp : {}", id);
        Optional<FollowUp> followUp = followUpRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(followUp);
    }

    /**
     * {@code DELETE  /follow-ups/:id} : delete the "id" followUp.
     *
     * @param id the id of the followUp to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollowUp(@PathVariable("id") Long id) {
        log.debug("REST request to delete FollowUp : {}", id);
        followUpRepository.deleteById(id);
        followUpSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /follow-ups/_search?query=:query} : search for the followUp corresponding
     * to the query.
     *
     * @param query the query of the followUp search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<FollowUp> searchFollowUps(@RequestParam("query") String query) {
        log.debug("REST request to search FollowUps for query {}", query);
        try {
            return StreamSupport.stream(followUpSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
