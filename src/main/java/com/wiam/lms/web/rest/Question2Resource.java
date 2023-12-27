package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Question2;
import com.wiam.lms.repository.Question2Repository;
import com.wiam.lms.repository.search.Question2SearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Question2}.
 */
@RestController
@RequestMapping("/api/question-2-s")
@Transactional
public class Question2Resource {

    private final Logger log = LoggerFactory.getLogger(Question2Resource.class);

    private static final String ENTITY_NAME = "question2";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Question2Repository question2Repository;

    private final Question2SearchRepository question2SearchRepository;

    public Question2Resource(Question2Repository question2Repository, Question2SearchRepository question2SearchRepository) {
        this.question2Repository = question2Repository;
        this.question2SearchRepository = question2SearchRepository;
    }

    /**
     * {@code POST  /question-2-s} : Create a new question2.
     *
     * @param question2 the question2 to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new question2, or with status {@code 400 (Bad Request)} if the question2 has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Question2> createQuestion2(@RequestBody Question2 question2) throws URISyntaxException {
        log.debug("REST request to save Question2 : {}", question2);
        if (question2.getId() != null) {
            throw new BadRequestAlertException("A new question2 cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Question2 result = question2Repository.save(question2);
        question2SearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/question-2-s/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /question-2-s/:id} : Updates an existing question2.
     *
     * @param id the id of the question2 to save.
     * @param question2 the question2 to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated question2,
     * or with status {@code 400 (Bad Request)} if the question2 is not valid,
     * or with status {@code 500 (Internal Server Error)} if the question2 couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Question2> updateQuestion2(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Question2 question2
    ) throws URISyntaxException {
        log.debug("REST request to update Question2 : {}, {}", id, question2);
        if (question2.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, question2.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!question2Repository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Question2 result = question2Repository.save(question2);
        question2SearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, question2.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /question-2-s/:id} : Partial updates given fields of an existing question2, field will ignore if it is null
     *
     * @param id the id of the question2 to save.
     * @param question2 the question2 to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated question2,
     * or with status {@code 400 (Bad Request)} if the question2 is not valid,
     * or with status {@code 404 (Not Found)} if the question2 is not found,
     * or with status {@code 500 (Internal Server Error)} if the question2 couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Question2> partialUpdateQuestion2(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Question2 question2
    ) throws URISyntaxException {
        log.debug("REST request to partial update Question2 partially : {}, {}", id, question2);
        if (question2.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, question2.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!question2Repository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Question2> result = question2Repository
            .findById(question2.getId())
            .map(existingQuestion2 -> {
                if (question2.getQuestionTitle() != null) {
                    existingQuestion2.setQuestionTitle(question2.getQuestionTitle());
                }
                if (question2.getQuestionType() != null) {
                    existingQuestion2.setQuestionType(question2.getQuestionType());
                }
                if (question2.getQuestionDescription() != null) {
                    existingQuestion2.setQuestionDescription(question2.getQuestionDescription());
                }
                if (question2.getQuestionPoint() != null) {
                    existingQuestion2.setQuestionPoint(question2.getQuestionPoint());
                }
                if (question2.getQuestionSubject() != null) {
                    existingQuestion2.setQuestionSubject(question2.getQuestionSubject());
                }
                if (question2.getQuestionStatus() != null) {
                    existingQuestion2.setQuestionStatus(question2.getQuestionStatus());
                }

                return existingQuestion2;
            })
            .map(question2Repository::save)
            .map(savedQuestion2 -> {
                question2SearchRepository.index(savedQuestion2);
                return savedQuestion2;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, question2.getId().toString())
        );
    }

    /**
     * {@code GET  /question-2-s} : get all the question2s.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of question2s in body.
     */
    @GetMapping("")
    public List<Question2> getAllQuestion2s() {
        log.debug("REST request to get all Question2s");
        return question2Repository.findAll();
    }

    /**
     * {@code GET  /question-2-s/:id} : get the "id" question2.
     *
     * @param id the id of the question2 to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the question2, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Question2> getQuestion2(@PathVariable("id") Long id) {
        log.debug("REST request to get Question2 : {}", id);
        Optional<Question2> question2 = question2Repository.findById(id);
        return ResponseUtil.wrapOrNotFound(question2);
    }

    /**
     * {@code DELETE  /question-2-s/:id} : delete the "id" question2.
     *
     * @param id the id of the question2 to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion2(@PathVariable("id") Long id) {
        log.debug("REST request to delete Question2 : {}", id);
        question2Repository.deleteById(id);
        question2SearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /question-2-s/_search?query=:query} : search for the question2 corresponding
     * to the query.
     *
     * @param query the query of the question2 search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Question2> searchQuestion2s(@RequestParam("query") String query) {
        log.debug("REST request to search Question2s for query {}", query);
        try {
            return StreamSupport.stream(question2SearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
