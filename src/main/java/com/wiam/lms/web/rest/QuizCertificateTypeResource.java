package com.wiam.lms.web.rest;

import com.wiam.lms.domain.QuizCertificateType;
import com.wiam.lms.repository.QuizCertificateTypeRepository;
import com.wiam.lms.repository.search.QuizCertificateTypeSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.QuizCertificateType}.
 */
@RestController
@RequestMapping("/api/quiz-certificate-types")
@Transactional
public class QuizCertificateTypeResource {

    private final Logger log = LoggerFactory.getLogger(QuizCertificateTypeResource.class);

    private static final String ENTITY_NAME = "quizCertificateType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuizCertificateTypeRepository quizCertificateTypeRepository;

    private final QuizCertificateTypeSearchRepository quizCertificateTypeSearchRepository;

    public QuizCertificateTypeResource(
        QuizCertificateTypeRepository quizCertificateTypeRepository,
        QuizCertificateTypeSearchRepository quizCertificateTypeSearchRepository
    ) {
        this.quizCertificateTypeRepository = quizCertificateTypeRepository;
        this.quizCertificateTypeSearchRepository = quizCertificateTypeSearchRepository;
    }

    /**
     * {@code POST  /quiz-certificate-types} : Create a new quizCertificateType.
     *
     * @param quizCertificateType the quizCertificateType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quizCertificateType, or with status {@code 400 (Bad Request)} if the quizCertificateType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<QuizCertificateType> createQuizCertificateType(@Valid @RequestBody QuizCertificateType quizCertificateType)
        throws URISyntaxException {
        log.debug("REST request to save QuizCertificateType : {}", quizCertificateType);
        if (quizCertificateType.getId() != null) {
            throw new BadRequestAlertException("A new quizCertificateType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        QuizCertificateType result = quizCertificateTypeRepository.save(quizCertificateType);
        quizCertificateTypeSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/quiz-certificate-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /quiz-certificate-types/:id} : Updates an existing quizCertificateType.
     *
     * @param id the id of the quizCertificateType to save.
     * @param quizCertificateType the quizCertificateType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizCertificateType,
     * or with status {@code 400 (Bad Request)} if the quizCertificateType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quizCertificateType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuizCertificateType> updateQuizCertificateType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody QuizCertificateType quizCertificateType
    ) throws URISyntaxException {
        log.debug("REST request to update QuizCertificateType : {}, {}", id, quizCertificateType);
        if (quizCertificateType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizCertificateType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizCertificateTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        QuizCertificateType result = quizCertificateTypeRepository.save(quizCertificateType);
        quizCertificateTypeSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quizCertificateType.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /quiz-certificate-types/:id} : Partial updates given fields of an existing quizCertificateType, field will ignore if it is null
     *
     * @param id the id of the quizCertificateType to save.
     * @param quizCertificateType the quizCertificateType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizCertificateType,
     * or with status {@code 400 (Bad Request)} if the quizCertificateType is not valid,
     * or with status {@code 404 (Not Found)} if the quizCertificateType is not found,
     * or with status {@code 500 (Internal Server Error)} if the quizCertificateType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuizCertificateType> partialUpdateQuizCertificateType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody QuizCertificateType quizCertificateType
    ) throws URISyntaxException {
        log.debug("REST request to partial update QuizCertificateType partially : {}, {}", id, quizCertificateType);
        if (quizCertificateType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizCertificateType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizCertificateTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuizCertificateType> result = quizCertificateTypeRepository
            .findById(quizCertificateType.getId())
            .map(existingQuizCertificateType -> {
                if (quizCertificateType.getTitleAr() != null) {
                    existingQuizCertificateType.setTitleAr(quizCertificateType.getTitleAr());
                }
                if (quizCertificateType.getTitleLat() != null) {
                    existingQuizCertificateType.setTitleLat(quizCertificateType.getTitleLat());
                }

                return existingQuizCertificateType;
            })
            .map(quizCertificateTypeRepository::save)
            .map(savedQuizCertificateType -> {
                quizCertificateTypeSearchRepository.index(savedQuizCertificateType);
                return savedQuizCertificateType;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quizCertificateType.getId().toString())
        );
    }

    /**
     * {@code GET  /quiz-certificate-types} : get all the quizCertificateTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quizCertificateTypes in body.
     */
    @GetMapping("")
    public List<QuizCertificateType> getAllQuizCertificateTypes() {
        log.debug("REST request to get all QuizCertificateTypes");
        return quizCertificateTypeRepository.findAll();
    }

    /**
     * {@code GET  /quiz-certificate-types/:id} : get the "id" quizCertificateType.
     *
     * @param id the id of the quizCertificateType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quizCertificateType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizCertificateType> getQuizCertificateType(@PathVariable("id") Long id) {
        log.debug("REST request to get QuizCertificateType : {}", id);
        Optional<QuizCertificateType> quizCertificateType = quizCertificateTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(quizCertificateType);
    }

    /**
     * {@code DELETE  /quiz-certificate-types/:id} : delete the "id" quizCertificateType.
     *
     * @param id the id of the quizCertificateType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuizCertificateType(@PathVariable("id") Long id) {
        log.debug("REST request to delete QuizCertificateType : {}", id);
        quizCertificateTypeRepository.deleteById(id);
        quizCertificateTypeSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /quiz-certificate-types/_search?query=:query} : search for the quizCertificateType corresponding
     * to the query.
     *
     * @param query the query of the quizCertificateType search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<QuizCertificateType> searchQuizCertificateTypes(@RequestParam("query") String query) {
        log.debug("REST request to search QuizCertificateTypes for query {}", query);
        try {
            return StreamSupport.stream(quizCertificateTypeSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
