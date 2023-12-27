package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Quiz;
import com.wiam.lms.repository.QuizRepository;
import com.wiam.lms.repository.search.QuizSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Quiz}.
 */
@RestController
@RequestMapping("/api/quizzes")
@Transactional
public class QuizResource {

    private final Logger log = LoggerFactory.getLogger(QuizResource.class);

    private static final String ENTITY_NAME = "quiz";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuizRepository quizRepository;

    private final QuizSearchRepository quizSearchRepository;

    public QuizResource(QuizRepository quizRepository, QuizSearchRepository quizSearchRepository) {
        this.quizRepository = quizRepository;
        this.quizSearchRepository = quizSearchRepository;
    }

    /**
     * {@code POST  /quizzes} : Create a new quiz.
     *
     * @param quiz the quiz to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quiz, or with status {@code 400 (Bad Request)} if the quiz has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) throws URISyntaxException {
        log.debug("REST request to save Quiz : {}", quiz);
        if (quiz.getId() != null) {
            throw new BadRequestAlertException("A new quiz cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Quiz result = quizRepository.save(quiz);
        quizSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/quizzes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /quizzes/:id} : Updates an existing quiz.
     *
     * @param id the id of the quiz to save.
     * @param quiz the quiz to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quiz,
     * or with status {@code 400 (Bad Request)} if the quiz is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quiz couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable(value = "id", required = false) final Long id, @RequestBody Quiz quiz)
        throws URISyntaxException {
        log.debug("REST request to update Quiz : {}, {}", id, quiz);
        if (quiz.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quiz.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Quiz result = quizRepository.save(quiz);
        quizSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quiz.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /quizzes/:id} : Partial updates given fields of an existing quiz, field will ignore if it is null
     *
     * @param id the id of the quiz to save.
     * @param quiz the quiz to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quiz,
     * or with status {@code 400 (Bad Request)} if the quiz is not valid,
     * or with status {@code 404 (Not Found)} if the quiz is not found,
     * or with status {@code 500 (Internal Server Error)} if the quiz couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Quiz> partialUpdateQuiz(@PathVariable(value = "id", required = false) final Long id, @RequestBody Quiz quiz)
        throws URISyntaxException {
        log.debug("REST request to partial update Quiz partially : {}, {}", id, quiz);
        if (quiz.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quiz.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Quiz> result = quizRepository
            .findById(quiz.getId())
            .map(existingQuiz -> {
                if (quiz.getQuizTitle() != null) {
                    existingQuiz.setQuizTitle(quiz.getQuizTitle());
                }
                if (quiz.getQuizType() != null) {
                    existingQuiz.setQuizType(quiz.getQuizType());
                }
                if (quiz.getQuizDescription() != null) {
                    existingQuiz.setQuizDescription(quiz.getQuizDescription());
                }

                return existingQuiz;
            })
            .map(quizRepository::save)
            .map(savedQuiz -> {
                quizSearchRepository.index(savedQuiz);
                return savedQuiz;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quiz.getId().toString())
        );
    }

    /**
     * {@code GET  /quizzes} : get all the quizzes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quizzes in body.
     */
    @GetMapping("")
    public List<Quiz> getAllQuizzes(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        log.debug("REST request to get all Quizzes");
        if (eagerload) {
            return quizRepository.findAllWithEagerRelationships();
        } else {
            return quizRepository.findAll();
        }
    }

    /**
     * {@code GET  /quizzes/:id} : get the "id" quiz.
     *
     * @param id the id of the quiz to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quiz, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable("id") Long id) {
        log.debug("REST request to get Quiz : {}", id);
        Optional<Quiz> quiz = quizRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(quiz);
    }

    /**
     * {@code DELETE  /quizzes/:id} : delete the "id" quiz.
     *
     * @param id the id of the quiz to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable("id") Long id) {
        log.debug("REST request to delete Quiz : {}", id);
        quizRepository.deleteById(id);
        quizSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /quizzes/_search?query=:query} : search for the quiz corresponding
     * to the query.
     *
     * @param query the query of the quiz search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Quiz> searchQuizzes(@RequestParam("query") String query) {
        log.debug("REST request to search Quizzes for query {}", query);
        try {
            return StreamSupport.stream(quizSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
