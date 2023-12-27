package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Exam;
import com.wiam.lms.repository.ExamRepository;
import com.wiam.lms.repository.search.ExamSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Exam}.
 */
@RestController
@RequestMapping("/api/exams")
@Transactional
public class ExamResource {

    private final Logger log = LoggerFactory.getLogger(ExamResource.class);

    private static final String ENTITY_NAME = "exam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExamRepository examRepository;

    private final ExamSearchRepository examSearchRepository;

    public ExamResource(ExamRepository examRepository, ExamSearchRepository examSearchRepository) {
        this.examRepository = examRepository;
        this.examSearchRepository = examSearchRepository;
    }

    /**
     * {@code POST  /exams} : Create a new exam.
     *
     * @param exam the exam to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new exam, or with status {@code 400 (Bad Request)} if the exam has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Exam> createExam(@Valid @RequestBody Exam exam) throws URISyntaxException {
        log.debug("REST request to save Exam : {}", exam);
        if (exam.getId() != null) {
            throw new BadRequestAlertException("A new exam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Exam result = examRepository.save(exam);
        examSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/exams/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /exams/:id} : Updates an existing exam.
     *
     * @param id the id of the exam to save.
     * @param exam the exam to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exam,
     * or with status {@code 400 (Bad Request)} if the exam is not valid,
     * or with status {@code 500 (Internal Server Error)} if the exam couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Exam exam)
        throws URISyntaxException {
        log.debug("REST request to update Exam : {}, {}", id, exam);
        if (exam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Exam result = examRepository.save(exam);
        examSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exam.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /exams/:id} : Partial updates given fields of an existing exam, field will ignore if it is null
     *
     * @param id the id of the exam to save.
     * @param exam the exam to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exam,
     * or with status {@code 400 (Bad Request)} if the exam is not valid,
     * or with status {@code 404 (Not Found)} if the exam is not found,
     * or with status {@code 500 (Internal Server Error)} if the exam couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Exam> partialUpdateExam(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Exam exam
    ) throws URISyntaxException {
        log.debug("REST request to partial update Exam partially : {}, {}", id, exam);
        if (exam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Exam> result = examRepository
            .findById(exam.getId())
            .map(existingExam -> {
                if (exam.getComite() != null) {
                    existingExam.setComite(exam.getComite());
                }
                if (exam.getStudentName() != null) {
                    existingExam.setStudentName(exam.getStudentName());
                }
                if (exam.getExamName() != null) {
                    existingExam.setExamName(exam.getExamName());
                }
                if (exam.getExamCategory() != null) {
                    existingExam.setExamCategory(exam.getExamCategory());
                }
                if (exam.getExamType() != null) {
                    existingExam.setExamType(exam.getExamType());
                }
                if (exam.getTajweedScore() != null) {
                    existingExam.setTajweedScore(exam.getTajweedScore());
                }
                if (exam.getHifdScore() != null) {
                    existingExam.setHifdScore(exam.getHifdScore());
                }
                if (exam.getAdaeScore() != null) {
                    existingExam.setAdaeScore(exam.getAdaeScore());
                }
                if (exam.getObservation() != null) {
                    existingExam.setObservation(exam.getObservation());
                }
                if (exam.getDecision() != null) {
                    existingExam.setDecision(exam.getDecision());
                }

                return existingExam;
            })
            .map(examRepository::save)
            .map(savedExam -> {
                examSearchRepository.index(savedExam);
                return savedExam;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exam.getId().toString())
        );
    }

    /**
     * {@code GET  /exams} : get all the exams.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of exams in body.
     */
    @GetMapping("")
    public List<Exam> getAllExams() {
        log.debug("REST request to get all Exams");
        return examRepository.findAll();
    }

    /**
     * {@code GET  /exams/:id} : get the "id" exam.
     *
     * @param id the id of the exam to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the exam, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExam(@PathVariable("id") Long id) {
        log.debug("REST request to get Exam : {}", id);
        Optional<Exam> exam = examRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(exam);
    }

    /**
     * {@code DELETE  /exams/:id} : delete the "id" exam.
     *
     * @param id the id of the exam to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable("id") Long id) {
        log.debug("REST request to delete Exam : {}", id);
        examRepository.deleteById(id);
        examSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /exams/_search?query=:query} : search for the exam corresponding
     * to the query.
     *
     * @param query the query of the exam search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Exam> searchExams(@RequestParam("query") String query) {
        log.debug("REST request to search Exams for query {}", query);
        try {
            return StreamSupport.stream(examSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
