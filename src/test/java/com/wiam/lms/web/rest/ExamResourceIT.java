package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Exam;
import com.wiam.lms.domain.enumeration.ExamType;
import com.wiam.lms.domain.enumeration.Riwayats;
import com.wiam.lms.repository.ExamRepository;
import com.wiam.lms.repository.search.ExamSearchRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ExamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExamResourceIT {

    private static final String DEFAULT_COMITE = "AAAAAAAAAA";
    private static final String UPDATED_COMITE = "BBBBBBBBBB";

    private static final String DEFAULT_STUDENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_STUDENT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EXAM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_EXAM_NAME = "BBBBBBBBBB";

    private static final Riwayats DEFAULT_EXAM_CATEGORY = Riwayats.WARSHS_NARRATION_ON_THE_AUTHORITY_OF_NAFI_VIA_AL_SHATIBIYYAH;
    private static final Riwayats UPDATED_EXAM_CATEGORY =
        Riwayats.QALOUNS_NARRATION_ON_THE_AUTHORITY_OF_NAFI_ON_THE_AUTHORITY_OF_AL_SHATIBIYYAH;

    private static final ExamType DEFAULT_EXAM_TYPE = ExamType.OLD_HIFD;
    private static final ExamType UPDATED_EXAM_TYPE = ExamType.NEW_HIFD;

    private static final Integer DEFAULT_TAJWEED_SCORE = 1;
    private static final Integer UPDATED_TAJWEED_SCORE = 2;

    private static final Integer DEFAULT_HIFD_SCORE = 1;
    private static final Integer UPDATED_HIFD_SCORE = 2;

    private static final Integer DEFAULT_ADAE_SCORE = 1;
    private static final Integer UPDATED_ADAE_SCORE = 2;

    private static final String DEFAULT_OBSERVATION = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVATION = "BBBBBBBBBB";

    private static final Integer DEFAULT_DECISION = 1;
    private static final Integer UPDATED_DECISION = 2;

    private static final String ENTITY_API_URL = "/api/exams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/exams/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamSearchRepository examSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamMockMvc;

    private Exam exam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exam createEntity(EntityManager em) {
        Exam exam = new Exam()
            .comite(DEFAULT_COMITE)
            .studentName(DEFAULT_STUDENT_NAME)
            .examName(DEFAULT_EXAM_NAME)
            .examCategory(DEFAULT_EXAM_CATEGORY)
            .examType(DEFAULT_EXAM_TYPE)
            .tajweedScore(DEFAULT_TAJWEED_SCORE)
            .hifdScore(DEFAULT_HIFD_SCORE)
            .adaeScore(DEFAULT_ADAE_SCORE)
            .observation(DEFAULT_OBSERVATION)
            .decision(DEFAULT_DECISION);
        return exam;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exam createUpdatedEntity(EntityManager em) {
        Exam exam = new Exam()
            .comite(UPDATED_COMITE)
            .studentName(UPDATED_STUDENT_NAME)
            .examName(UPDATED_EXAM_NAME)
            .examCategory(UPDATED_EXAM_CATEGORY)
            .examType(UPDATED_EXAM_TYPE)
            .tajweedScore(UPDATED_TAJWEED_SCORE)
            .hifdScore(UPDATED_HIFD_SCORE)
            .adaeScore(UPDATED_ADAE_SCORE)
            .observation(UPDATED_OBSERVATION)
            .decision(UPDATED_DECISION);
        return exam;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        examSearchRepository.deleteAll();
        assertThat(examSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        exam = createEntity(em);
    }

    @Test
    @Transactional
    void createExam() throws Exception {
        int databaseSizeBeforeCreate = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        // Create the Exam
        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isCreated());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Exam testExam = examList.get(examList.size() - 1);
        assertThat(testExam.getComite()).isEqualTo(DEFAULT_COMITE);
        assertThat(testExam.getStudentName()).isEqualTo(DEFAULT_STUDENT_NAME);
        assertThat(testExam.getExamName()).isEqualTo(DEFAULT_EXAM_NAME);
        assertThat(testExam.getExamCategory()).isEqualTo(DEFAULT_EXAM_CATEGORY);
        assertThat(testExam.getExamType()).isEqualTo(DEFAULT_EXAM_TYPE);
        assertThat(testExam.getTajweedScore()).isEqualTo(DEFAULT_TAJWEED_SCORE);
        assertThat(testExam.getHifdScore()).isEqualTo(DEFAULT_HIFD_SCORE);
        assertThat(testExam.getAdaeScore()).isEqualTo(DEFAULT_ADAE_SCORE);
        assertThat(testExam.getObservation()).isEqualTo(DEFAULT_OBSERVATION);
        assertThat(testExam.getDecision()).isEqualTo(DEFAULT_DECISION);
    }

    @Test
    @Transactional
    void createExamWithExistingId() throws Exception {
        // Create the Exam with an existing ID
        exam.setId(1L);

        int databaseSizeBeforeCreate = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTajweedScoreIsRequired() throws Exception {
        int databaseSizeBeforeTest = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        // set the field null
        exam.setTajweedScore(null);

        // Create the Exam, which fails.

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isBadRequest());

        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkHifdScoreIsRequired() throws Exception {
        int databaseSizeBeforeTest = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        // set the field null
        exam.setHifdScore(null);

        // Create the Exam, which fails.

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isBadRequest());

        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAdaeScoreIsRequired() throws Exception {
        int databaseSizeBeforeTest = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        // set the field null
        exam.setAdaeScore(null);

        // Create the Exam, which fails.

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isBadRequest());

        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDecisionIsRequired() throws Exception {
        int databaseSizeBeforeTest = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        // set the field null
        exam.setDecision(null);

        // Create the Exam, which fails.

        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isBadRequest());

        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllExams() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        // Get all the examList
        restExamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exam.getId().intValue())))
            .andExpect(jsonPath("$.[*].comite").value(hasItem(DEFAULT_COMITE)))
            .andExpect(jsonPath("$.[*].studentName").value(hasItem(DEFAULT_STUDENT_NAME)))
            .andExpect(jsonPath("$.[*].examName").value(hasItem(DEFAULT_EXAM_NAME)))
            .andExpect(jsonPath("$.[*].examCategory").value(hasItem(DEFAULT_EXAM_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].examType").value(hasItem(DEFAULT_EXAM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].tajweedScore").value(hasItem(DEFAULT_TAJWEED_SCORE)))
            .andExpect(jsonPath("$.[*].hifdScore").value(hasItem(DEFAULT_HIFD_SCORE)))
            .andExpect(jsonPath("$.[*].adaeScore").value(hasItem(DEFAULT_ADAE_SCORE)))
            .andExpect(jsonPath("$.[*].observation").value(hasItem(DEFAULT_OBSERVATION.toString())))
            .andExpect(jsonPath("$.[*].decision").value(hasItem(DEFAULT_DECISION)));
    }

    @Test
    @Transactional
    void getExam() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        // Get the exam
        restExamMockMvc
            .perform(get(ENTITY_API_URL_ID, exam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exam.getId().intValue()))
            .andExpect(jsonPath("$.comite").value(DEFAULT_COMITE))
            .andExpect(jsonPath("$.studentName").value(DEFAULT_STUDENT_NAME))
            .andExpect(jsonPath("$.examName").value(DEFAULT_EXAM_NAME))
            .andExpect(jsonPath("$.examCategory").value(DEFAULT_EXAM_CATEGORY.toString()))
            .andExpect(jsonPath("$.examType").value(DEFAULT_EXAM_TYPE.toString()))
            .andExpect(jsonPath("$.tajweedScore").value(DEFAULT_TAJWEED_SCORE))
            .andExpect(jsonPath("$.hifdScore").value(DEFAULT_HIFD_SCORE))
            .andExpect(jsonPath("$.adaeScore").value(DEFAULT_ADAE_SCORE))
            .andExpect(jsonPath("$.observation").value(DEFAULT_OBSERVATION.toString()))
            .andExpect(jsonPath("$.decision").value(DEFAULT_DECISION));
    }

    @Test
    @Transactional
    void getNonExistingExam() throws Exception {
        // Get the exam
        restExamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExam() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        examSearchRepository.save(exam);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());

        // Update the exam
        Exam updatedExam = examRepository.findById(exam.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExam are not directly saved in db
        em.detach(updatedExam);
        updatedExam
            .comite(UPDATED_COMITE)
            .studentName(UPDATED_STUDENT_NAME)
            .examName(UPDATED_EXAM_NAME)
            .examCategory(UPDATED_EXAM_CATEGORY)
            .examType(UPDATED_EXAM_TYPE)
            .tajweedScore(UPDATED_TAJWEED_SCORE)
            .hifdScore(UPDATED_HIFD_SCORE)
            .adaeScore(UPDATED_ADAE_SCORE)
            .observation(UPDATED_OBSERVATION)
            .decision(UPDATED_DECISION);

        restExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExam.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        Exam testExam = examList.get(examList.size() - 1);
        assertThat(testExam.getComite()).isEqualTo(UPDATED_COMITE);
        assertThat(testExam.getStudentName()).isEqualTo(UPDATED_STUDENT_NAME);
        assertThat(testExam.getExamName()).isEqualTo(UPDATED_EXAM_NAME);
        assertThat(testExam.getExamCategory()).isEqualTo(UPDATED_EXAM_CATEGORY);
        assertThat(testExam.getExamType()).isEqualTo(UPDATED_EXAM_TYPE);
        assertThat(testExam.getTajweedScore()).isEqualTo(UPDATED_TAJWEED_SCORE);
        assertThat(testExam.getHifdScore()).isEqualTo(UPDATED_HIFD_SCORE);
        assertThat(testExam.getAdaeScore()).isEqualTo(UPDATED_ADAE_SCORE);
        assertThat(testExam.getObservation()).isEqualTo(UPDATED_OBSERVATION);
        assertThat(testExam.getDecision()).isEqualTo(UPDATED_DECISION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Exam> examSearchList = IterableUtils.toList(examSearchRepository.findAll());
                Exam testExamSearch = examSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testExamSearch.getComite()).isEqualTo(UPDATED_COMITE);
                assertThat(testExamSearch.getStudentName()).isEqualTo(UPDATED_STUDENT_NAME);
                assertThat(testExamSearch.getExamName()).isEqualTo(UPDATED_EXAM_NAME);
                assertThat(testExamSearch.getExamCategory()).isEqualTo(UPDATED_EXAM_CATEGORY);
                assertThat(testExamSearch.getExamType()).isEqualTo(UPDATED_EXAM_TYPE);
                assertThat(testExamSearch.getTajweedScore()).isEqualTo(UPDATED_TAJWEED_SCORE);
                assertThat(testExamSearch.getHifdScore()).isEqualTo(UPDATED_HIFD_SCORE);
                assertThat(testExamSearch.getAdaeScore()).isEqualTo(UPDATED_ADAE_SCORE);
                assertThat(testExamSearch.getObservation()).isEqualTo(UPDATED_OBSERVATION);
                assertThat(testExamSearch.getDecision()).isEqualTo(UPDATED_DECISION);
            });
    }

    @Test
    @Transactional
    void putNonExistingExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        exam.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exam.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exam))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        exam.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exam))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        exam.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateExamWithPatch() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        int databaseSizeBeforeUpdate = examRepository.findAll().size();

        // Update the exam using partial update
        Exam partialUpdatedExam = new Exam();
        partialUpdatedExam.setId(exam.getId());

        partialUpdatedExam
            .studentName(UPDATED_STUDENT_NAME)
            .examName(UPDATED_EXAM_NAME)
            .examCategory(UPDATED_EXAM_CATEGORY)
            .examType(UPDATED_EXAM_TYPE)
            .tajweedScore(UPDATED_TAJWEED_SCORE)
            .hifdScore(UPDATED_HIFD_SCORE)
            .observation(UPDATED_OBSERVATION);

        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        Exam testExam = examList.get(examList.size() - 1);
        assertThat(testExam.getComite()).isEqualTo(DEFAULT_COMITE);
        assertThat(testExam.getStudentName()).isEqualTo(UPDATED_STUDENT_NAME);
        assertThat(testExam.getExamName()).isEqualTo(UPDATED_EXAM_NAME);
        assertThat(testExam.getExamCategory()).isEqualTo(UPDATED_EXAM_CATEGORY);
        assertThat(testExam.getExamType()).isEqualTo(UPDATED_EXAM_TYPE);
        assertThat(testExam.getTajweedScore()).isEqualTo(UPDATED_TAJWEED_SCORE);
        assertThat(testExam.getHifdScore()).isEqualTo(UPDATED_HIFD_SCORE);
        assertThat(testExam.getAdaeScore()).isEqualTo(DEFAULT_ADAE_SCORE);
        assertThat(testExam.getObservation()).isEqualTo(UPDATED_OBSERVATION);
        assertThat(testExam.getDecision()).isEqualTo(DEFAULT_DECISION);
    }

    @Test
    @Transactional
    void fullUpdateExamWithPatch() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        int databaseSizeBeforeUpdate = examRepository.findAll().size();

        // Update the exam using partial update
        Exam partialUpdatedExam = new Exam();
        partialUpdatedExam.setId(exam.getId());

        partialUpdatedExam
            .comite(UPDATED_COMITE)
            .studentName(UPDATED_STUDENT_NAME)
            .examName(UPDATED_EXAM_NAME)
            .examCategory(UPDATED_EXAM_CATEGORY)
            .examType(UPDATED_EXAM_TYPE)
            .tajweedScore(UPDATED_TAJWEED_SCORE)
            .hifdScore(UPDATED_HIFD_SCORE)
            .adaeScore(UPDATED_ADAE_SCORE)
            .observation(UPDATED_OBSERVATION)
            .decision(UPDATED_DECISION);

        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        Exam testExam = examList.get(examList.size() - 1);
        assertThat(testExam.getComite()).isEqualTo(UPDATED_COMITE);
        assertThat(testExam.getStudentName()).isEqualTo(UPDATED_STUDENT_NAME);
        assertThat(testExam.getExamName()).isEqualTo(UPDATED_EXAM_NAME);
        assertThat(testExam.getExamCategory()).isEqualTo(UPDATED_EXAM_CATEGORY);
        assertThat(testExam.getExamType()).isEqualTo(UPDATED_EXAM_TYPE);
        assertThat(testExam.getTajweedScore()).isEqualTo(UPDATED_TAJWEED_SCORE);
        assertThat(testExam.getHifdScore()).isEqualTo(UPDATED_HIFD_SCORE);
        assertThat(testExam.getAdaeScore()).isEqualTo(UPDATED_ADAE_SCORE);
        assertThat(testExam.getObservation()).isEqualTo(UPDATED_OBSERVATION);
        assertThat(testExam.getDecision()).isEqualTo(UPDATED_DECISION);
    }

    @Test
    @Transactional
    void patchNonExistingExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        exam.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, exam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exam))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        exam.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exam))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        exam.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteExam() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);
        examRepository.save(exam);
        examSearchRepository.save(exam);

        int databaseSizeBeforeDelete = examRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the exam
        restExamMockMvc
            .perform(delete(ENTITY_API_URL_ID, exam.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(examSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchExam() throws Exception {
        // Initialize the database
        exam = examRepository.saveAndFlush(exam);
        examSearchRepository.save(exam);

        // Search the exam
        restExamMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + exam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exam.getId().intValue())))
            .andExpect(jsonPath("$.[*].comite").value(hasItem(DEFAULT_COMITE)))
            .andExpect(jsonPath("$.[*].studentName").value(hasItem(DEFAULT_STUDENT_NAME)))
            .andExpect(jsonPath("$.[*].examName").value(hasItem(DEFAULT_EXAM_NAME)))
            .andExpect(jsonPath("$.[*].examCategory").value(hasItem(DEFAULT_EXAM_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].examType").value(hasItem(DEFAULT_EXAM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].tajweedScore").value(hasItem(DEFAULT_TAJWEED_SCORE)))
            .andExpect(jsonPath("$.[*].hifdScore").value(hasItem(DEFAULT_HIFD_SCORE)))
            .andExpect(jsonPath("$.[*].adaeScore").value(hasItem(DEFAULT_ADAE_SCORE)))
            .andExpect(jsonPath("$.[*].observation").value(hasItem(DEFAULT_OBSERVATION.toString())))
            .andExpect(jsonPath("$.[*].decision").value(hasItem(DEFAULT_DECISION)));
    }
}
