package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Sponsoring;
import com.wiam.lms.repository.SponsoringRepository;
import com.wiam.lms.repository.search.SponsoringSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Sponsoring}.
 */
@RestController
@RequestMapping("/api/sponsorings")
@Transactional
public class SponsoringResource {

    private final Logger log = LoggerFactory.getLogger(SponsoringResource.class);

    private static final String ENTITY_NAME = "sponsoring";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SponsoringRepository sponsoringRepository;

    private final SponsoringSearchRepository sponsoringSearchRepository;

    public SponsoringResource(SponsoringRepository sponsoringRepository, SponsoringSearchRepository sponsoringSearchRepository) {
        this.sponsoringRepository = sponsoringRepository;
        this.sponsoringSearchRepository = sponsoringSearchRepository;
    }

    /**
     * {@code POST  /sponsorings} : Create a new sponsoring.
     *
     * @param sponsoring the sponsoring to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sponsoring, or with status {@code 400 (Bad Request)} if the sponsoring has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Sponsoring> createSponsoring(@Valid @RequestBody Sponsoring sponsoring) throws URISyntaxException {
        log.debug("REST request to save Sponsoring : {}", sponsoring);
        if (sponsoring.getId() != null) {
            throw new BadRequestAlertException("A new sponsoring cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Sponsoring result = sponsoringRepository.save(sponsoring);
        sponsoringSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/sponsorings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sponsorings/:id} : Updates an existing sponsoring.
     *
     * @param id the id of the sponsoring to save.
     * @param sponsoring the sponsoring to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sponsoring,
     * or with status {@code 400 (Bad Request)} if the sponsoring is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sponsoring couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Sponsoring> updateSponsoring(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Sponsoring sponsoring
    ) throws URISyntaxException {
        log.debug("REST request to update Sponsoring : {}, {}", id, sponsoring);
        if (sponsoring.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sponsoring.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sponsoringRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Sponsoring result = sponsoringRepository.save(sponsoring);
        sponsoringSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sponsoring.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sponsorings/:id} : Partial updates given fields of an existing sponsoring, field will ignore if it is null
     *
     * @param id the id of the sponsoring to save.
     * @param sponsoring the sponsoring to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sponsoring,
     * or with status {@code 400 (Bad Request)} if the sponsoring is not valid,
     * or with status {@code 404 (Not Found)} if the sponsoring is not found,
     * or with status {@code 500 (Internal Server Error)} if the sponsoring couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Sponsoring> partialUpdateSponsoring(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Sponsoring sponsoring
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sponsoring partially : {}, {}", id, sponsoring);
        if (sponsoring.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sponsoring.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sponsoringRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Sponsoring> result = sponsoringRepository
            .findById(sponsoring.getId())
            .map(existingSponsoring -> {
                if (sponsoring.getMessage() != null) {
                    existingSponsoring.setMessage(sponsoring.getMessage());
                }
                if (sponsoring.getAmount() != null) {
                    existingSponsoring.setAmount(sponsoring.getAmount());
                }
                if (sponsoring.getStartDate() != null) {
                    existingSponsoring.setStartDate(sponsoring.getStartDate());
                }
                if (sponsoring.getEndDate() != null) {
                    existingSponsoring.setEndDate(sponsoring.getEndDate());
                }
                if (sponsoring.getIsAlways() != null) {
                    existingSponsoring.setIsAlways(sponsoring.getIsAlways());
                }

                return existingSponsoring;
            })
            .map(sponsoringRepository::save)
            .map(savedSponsoring -> {
                sponsoringSearchRepository.index(savedSponsoring);
                return savedSponsoring;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sponsoring.getId().toString())
        );
    }

    /**
     * {@code GET  /sponsorings} : get all the sponsorings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sponsorings in body.
     */
    @GetMapping("")
    public List<Sponsoring> getAllSponsorings() {
        log.debug("REST request to get all Sponsorings");
        return sponsoringRepository.findAll();
    }

    /**
     * {@code GET  /sponsorings/:id} : get the "id" sponsoring.
     *
     * @param id the id of the sponsoring to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sponsoring, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sponsoring> getSponsoring(@PathVariable("id") Long id) {
        log.debug("REST request to get Sponsoring : {}", id);
        Optional<Sponsoring> sponsoring = sponsoringRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sponsoring);
    }

    /**
     * {@code DELETE  /sponsorings/:id} : delete the "id" sponsoring.
     *
     * @param id the id of the sponsoring to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSponsoring(@PathVariable("id") Long id) {
        log.debug("REST request to delete Sponsoring : {}", id);
        sponsoringRepository.deleteById(id);
        sponsoringSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /sponsorings/_search?query=:query} : search for the sponsoring corresponding
     * to the query.
     *
     * @param query the query of the sponsoring search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Sponsoring> searchSponsorings(@RequestParam("query") String query) {
        log.debug("REST request to search Sponsorings for query {}", query);
        try {
            return StreamSupport.stream(sponsoringSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
