package com.wiam.lms.web.rest;

import com.wiam.lms.domain.JobTitle;
import com.wiam.lms.repository.JobTitleRepository;
import com.wiam.lms.repository.search.JobTitleSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.JobTitle}.
 */
@RestController
@RequestMapping("/api/job-titles")
@Transactional
public class JobTitleResource {

    private final Logger log = LoggerFactory.getLogger(JobTitleResource.class);

    private static final String ENTITY_NAME = "jobTitle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JobTitleRepository jobTitleRepository;

    private final JobTitleSearchRepository jobTitleSearchRepository;

    public JobTitleResource(JobTitleRepository jobTitleRepository, JobTitleSearchRepository jobTitleSearchRepository) {
        this.jobTitleRepository = jobTitleRepository;
        this.jobTitleSearchRepository = jobTitleSearchRepository;
    }

    /**
     * {@code POST  /job-titles} : Create a new jobTitle.
     *
     * @param jobTitle the jobTitle to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jobTitle, or with status {@code 400 (Bad Request)} if the jobTitle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<JobTitle> createJobTitle(@Valid @RequestBody JobTitle jobTitle) throws URISyntaxException {
        log.debug("REST request to save JobTitle : {}", jobTitle);
        if (jobTitle.getId() != null) {
            throw new BadRequestAlertException("A new jobTitle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        JobTitle result = jobTitleRepository.save(jobTitle);
        jobTitleSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/job-titles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /job-titles/:id} : Updates an existing jobTitle.
     *
     * @param id the id of the jobTitle to save.
     * @param jobTitle the jobTitle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jobTitle,
     * or with status {@code 400 (Bad Request)} if the jobTitle is not valid,
     * or with status {@code 500 (Internal Server Error)} if the jobTitle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobTitle> updateJobTitle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody JobTitle jobTitle
    ) throws URISyntaxException {
        log.debug("REST request to update JobTitle : {}, {}", id, jobTitle);
        if (jobTitle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jobTitle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jobTitleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        JobTitle result = jobTitleRepository.save(jobTitle);
        jobTitleSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, jobTitle.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /job-titles/:id} : Partial updates given fields of an existing jobTitle, field will ignore if it is null
     *
     * @param id the id of the jobTitle to save.
     * @param jobTitle the jobTitle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jobTitle,
     * or with status {@code 400 (Bad Request)} if the jobTitle is not valid,
     * or with status {@code 404 (Not Found)} if the jobTitle is not found,
     * or with status {@code 500 (Internal Server Error)} if the jobTitle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<JobTitle> partialUpdateJobTitle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody JobTitle jobTitle
    ) throws URISyntaxException {
        log.debug("REST request to partial update JobTitle partially : {}, {}", id, jobTitle);
        if (jobTitle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jobTitle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jobTitleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<JobTitle> result = jobTitleRepository
            .findById(jobTitle.getId())
            .map(existingJobTitle -> {
                if (jobTitle.getTitleAr() != null) {
                    existingJobTitle.setTitleAr(jobTitle.getTitleAr());
                }
                if (jobTitle.getTitleLat() != null) {
                    existingJobTitle.setTitleLat(jobTitle.getTitleLat());
                }
                if (jobTitle.getDescription() != null) {
                    existingJobTitle.setDescription(jobTitle.getDescription());
                }

                return existingJobTitle;
            })
            .map(jobTitleRepository::save)
            .map(savedJobTitle -> {
                jobTitleSearchRepository.index(savedJobTitle);
                return savedJobTitle;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, jobTitle.getId().toString())
        );
    }

    /**
     * {@code GET  /job-titles} : get all the jobTitles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jobTitles in body.
     */
    @GetMapping("")
    public List<JobTitle> getAllJobTitles() {
        log.debug("REST request to get all JobTitles");
        return jobTitleRepository.findAll();
    }

    /**
     * {@code GET  /job-titles/:id} : get the "id" jobTitle.
     *
     * @param id the id of the jobTitle to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jobTitle, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobTitle> getJobTitle(@PathVariable("id") Long id) {
        log.debug("REST request to get JobTitle : {}", id);
        Optional<JobTitle> jobTitle = jobTitleRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(jobTitle);
    }

    /**
     * {@code DELETE  /job-titles/:id} : delete the "id" jobTitle.
     *
     * @param id the id of the jobTitle to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobTitle(@PathVariable("id") Long id) {
        log.debug("REST request to delete JobTitle : {}", id);
        jobTitleRepository.deleteById(id);
        jobTitleSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /job-titles/_search?query=:query} : search for the jobTitle corresponding
     * to the query.
     *
     * @param query the query of the jobTitle search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<JobTitle> searchJobTitles(@RequestParam("query") String query) {
        log.debug("REST request to search JobTitles for query {}", query);
        try {
            return StreamSupport.stream(jobTitleSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
