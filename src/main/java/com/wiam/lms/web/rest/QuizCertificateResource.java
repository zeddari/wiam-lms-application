package com.wiam.lms.web.rest;

import com.wiam.lms.domain.QuizCertificate;
import com.wiam.lms.repository.QuizCertificateRepository;
import com.wiam.lms.repository.search.QuizCertificateSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.QuizCertificate}.
 */
@RestController
@RequestMapping("/api/quiz-certificates")
@Transactional
public class QuizCertificateResource {

    private final Logger log = LoggerFactory.getLogger(QuizCertificateResource.class);

    private static final String ENTITY_NAME = "quizCertificate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuizCertificateRepository quizCertificateRepository;

    private final QuizCertificateSearchRepository quizCertificateSearchRepository;

    public QuizCertificateResource(
        QuizCertificateRepository quizCertificateRepository,
        QuizCertificateSearchRepository quizCertificateSearchRepository
    ) {
        this.quizCertificateRepository = quizCertificateRepository;
        this.quizCertificateSearchRepository = quizCertificateSearchRepository;
    }

    /**
     * {@code POST  /quiz-certificates} : Create a new quizCertificate.
     *
     * @param quizCertificate the quizCertificate to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quizCertificate, or with status {@code 400 (Bad Request)} if the quizCertificate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<QuizCertificate> createQuizCertificate(@Valid @RequestBody QuizCertificate quizCertificate)
        throws URISyntaxException {
        log.debug("REST request to save QuizCertificate : {}", quizCertificate);
        if (quizCertificate.getId() != null) {
            throw new BadRequestAlertException("A new quizCertificate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        QuizCertificate result = quizCertificateRepository.save(quizCertificate);
        quizCertificateSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/quiz-certificates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /quiz-certificates/:id} : Updates an existing quizCertificate.
     *
     * @param id the id of the quizCertificate to save.
     * @param quizCertificate the quizCertificate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizCertificate,
     * or with status {@code 400 (Bad Request)} if the quizCertificate is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quizCertificate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuizCertificate> updateQuizCertificate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody QuizCertificate quizCertificate
    ) throws URISyntaxException {
        log.debug("REST request to update QuizCertificate : {}, {}", id, quizCertificate);
        if (quizCertificate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizCertificate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizCertificateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        QuizCertificate result = quizCertificateRepository.save(quizCertificate);
        quizCertificateSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quizCertificate.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /quiz-certificates/:id} : Partial updates given fields of an existing quizCertificate, field will ignore if it is null
     *
     * @param id the id of the quizCertificate to save.
     * @param quizCertificate the quizCertificate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizCertificate,
     * or with status {@code 400 (Bad Request)} if the quizCertificate is not valid,
     * or with status {@code 404 (Not Found)} if the quizCertificate is not found,
     * or with status {@code 500 (Internal Server Error)} if the quizCertificate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuizCertificate> partialUpdateQuizCertificate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody QuizCertificate quizCertificate
    ) throws URISyntaxException {
        log.debug("REST request to partial update QuizCertificate partially : {}, {}", id, quizCertificate);
        if (quizCertificate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizCertificate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizCertificateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuizCertificate> result = quizCertificateRepository
            .findById(quizCertificate.getId())
            .map(existingQuizCertificate -> {
                if (quizCertificate.getTitle() != null) {
                    existingQuizCertificate.setTitle(quizCertificate.getTitle());
                }
                if (quizCertificate.getDescription() != null) {
                    existingQuizCertificate.setDescription(quizCertificate.getDescription());
                }
                if (quizCertificate.getIsActive() != null) {
                    existingQuizCertificate.setIsActive(quizCertificate.getIsActive());
                }

                return existingQuizCertificate;
            })
            .map(quizCertificateRepository::save)
            .map(savedQuizCertificate -> {
                quizCertificateSearchRepository.index(savedQuizCertificate);
                return savedQuizCertificate;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quizCertificate.getId().toString())
        );
    }

    /**
     * {@code GET  /quiz-certificates} : get all the quizCertificates.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quizCertificates in body.
     */
    @GetMapping("")
    public List<QuizCertificate> getAllQuizCertificates(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all QuizCertificates");
        if (eagerload) {
            return quizCertificateRepository.findAllWithEagerRelationships();
        } else {
            return quizCertificateRepository.findAll();
        }
    }

    /**
     * {@code GET  /quiz-certificates/:id} : get the "id" quizCertificate.
     *
     * @param id the id of the quizCertificate to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quizCertificate, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizCertificate> getQuizCertificate(@PathVariable("id") Long id) {
        log.debug("REST request to get QuizCertificate : {}", id);
        Optional<QuizCertificate> quizCertificate = quizCertificateRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(quizCertificate);
    }

    /**
     * {@code DELETE  /quiz-certificates/:id} : delete the "id" quizCertificate.
     *
     * @param id the id of the quizCertificate to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuizCertificate(@PathVariable("id") Long id) {
        log.debug("REST request to delete QuizCertificate : {}", id);
        quizCertificateRepository.deleteById(id);
        quizCertificateSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /quiz-certificates/_search?query=:query} : search for the quizCertificate corresponding
     * to the query.
     *
     * @param query the query of the quizCertificate search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<QuizCertificate> searchQuizCertificates(@RequestParam("query") String query) {
        log.debug("REST request to search QuizCertificates for query {}", query);
        try {
            return StreamSupport.stream(quizCertificateSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
