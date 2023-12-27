package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Progression;
import com.wiam.lms.repository.ProgressionRepository;
import com.wiam.lms.repository.search.ProgressionSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ProgressionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProgressionResourceIT {

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final Boolean DEFAULT_IS_JUSTIFIED = false;
    private static final Boolean UPDATED_IS_JUSTIFIED = true;

    private static final String DEFAULT_JUSTIF_REF = "AAAAAAAAAA";
    private static final String UPDATED_JUSTIF_REF = "BBBBBBBBBB";

    private static final Integer DEFAULT_LATE_ARRIVAL = 0;
    private static final Integer UPDATED_LATE_ARRIVAL = 1;

    private static final Integer DEFAULT_EARLY_DEPARTURE = 0;
    private static final Integer UPDATED_EARLY_DEPARTURE = 1;

    private static final Boolean DEFAULT_TASK_DONE = false;
    private static final Boolean UPDATED_TASK_DONE = true;

    private static final String DEFAULT_GRADE_1 = "AAAAAAAAAA";
    private static final String UPDATED_GRADE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_TASK_START = 1;
    private static final Integer UPDATED_TASK_START = 2;

    private static final Integer DEFAULT_TASK_END = 1;
    private static final Integer UPDATED_TASK_END = 2;

    private static final Integer DEFAULT_TASK_STEP = 1;
    private static final Integer UPDATED_TASK_STEP = 2;

    private static final LocalDate DEFAULT_PROGRESSION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PROGRESSION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/progressions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/progressions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProgressionRepository progressionRepository;

    @Autowired
    private ProgressionSearchRepository progressionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProgressionMockMvc;

    private Progression progression;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Progression createEntity(EntityManager em) {
        Progression progression = new Progression()
            .status(DEFAULT_STATUS)
            .isJustified(DEFAULT_IS_JUSTIFIED)
            .justifRef(DEFAULT_JUSTIF_REF)
            .lateArrival(DEFAULT_LATE_ARRIVAL)
            .earlyDeparture(DEFAULT_EARLY_DEPARTURE)
            .taskDone(DEFAULT_TASK_DONE)
            .grade1(DEFAULT_GRADE_1)
            .description(DEFAULT_DESCRIPTION)
            .taskStart(DEFAULT_TASK_START)
            .taskEnd(DEFAULT_TASK_END)
            .taskStep(DEFAULT_TASK_STEP)
            .progressionDate(DEFAULT_PROGRESSION_DATE);
        return progression;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Progression createUpdatedEntity(EntityManager em) {
        Progression progression = new Progression()
            .status(UPDATED_STATUS)
            .isJustified(UPDATED_IS_JUSTIFIED)
            .justifRef(UPDATED_JUSTIF_REF)
            .lateArrival(UPDATED_LATE_ARRIVAL)
            .earlyDeparture(UPDATED_EARLY_DEPARTURE)
            .taskDone(UPDATED_TASK_DONE)
            .grade1(UPDATED_GRADE_1)
            .description(UPDATED_DESCRIPTION)
            .taskStart(UPDATED_TASK_START)
            .taskEnd(UPDATED_TASK_END)
            .taskStep(UPDATED_TASK_STEP)
            .progressionDate(UPDATED_PROGRESSION_DATE);
        return progression;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        progressionSearchRepository.deleteAll();
        assertThat(progressionSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        progression = createEntity(em);
    }

    @Test
    @Transactional
    void createProgression() throws Exception {
        int databaseSizeBeforeCreate = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        // Create the Progression
        restProgressionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progression)))
            .andExpect(status().isCreated());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Progression testProgression = progressionList.get(progressionList.size() - 1);
        assertThat(testProgression.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProgression.getIsJustified()).isEqualTo(DEFAULT_IS_JUSTIFIED);
        assertThat(testProgression.getJustifRef()).isEqualTo(DEFAULT_JUSTIF_REF);
        assertThat(testProgression.getLateArrival()).isEqualTo(DEFAULT_LATE_ARRIVAL);
        assertThat(testProgression.getEarlyDeparture()).isEqualTo(DEFAULT_EARLY_DEPARTURE);
        assertThat(testProgression.getTaskDone()).isEqualTo(DEFAULT_TASK_DONE);
        assertThat(testProgression.getGrade1()).isEqualTo(DEFAULT_GRADE_1);
        assertThat(testProgression.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProgression.getTaskStart()).isEqualTo(DEFAULT_TASK_START);
        assertThat(testProgression.getTaskEnd()).isEqualTo(DEFAULT_TASK_END);
        assertThat(testProgression.getTaskStep()).isEqualTo(DEFAULT_TASK_STEP);
        assertThat(testProgression.getProgressionDate()).isEqualTo(DEFAULT_PROGRESSION_DATE);
    }

    @Test
    @Transactional
    void createProgressionWithExistingId() throws Exception {
        // Create the Progression with an existing ID
        progression.setId(1L);

        int databaseSizeBeforeCreate = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProgressionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progression)))
            .andExpect(status().isBadRequest());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        // set the field null
        progression.setStatus(null);

        // Create the Progression, which fails.

        restProgressionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progression)))
            .andExpect(status().isBadRequest());

        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTaskDoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        // set the field null
        progression.setTaskDone(null);

        // Create the Progression, which fails.

        restProgressionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progression)))
            .andExpect(status().isBadRequest());

        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkProgressionDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        // set the field null
        progression.setProgressionDate(null);

        // Create the Progression, which fails.

        restProgressionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progression)))
            .andExpect(status().isBadRequest());

        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProgressions() throws Exception {
        // Initialize the database
        progressionRepository.saveAndFlush(progression);

        // Get all the progressionList
        restProgressionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(progression.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].isJustified").value(hasItem(DEFAULT_IS_JUSTIFIED.booleanValue())))
            .andExpect(jsonPath("$.[*].justifRef").value(hasItem(DEFAULT_JUSTIF_REF)))
            .andExpect(jsonPath("$.[*].lateArrival").value(hasItem(DEFAULT_LATE_ARRIVAL)))
            .andExpect(jsonPath("$.[*].earlyDeparture").value(hasItem(DEFAULT_EARLY_DEPARTURE)))
            .andExpect(jsonPath("$.[*].taskDone").value(hasItem(DEFAULT_TASK_DONE.booleanValue())))
            .andExpect(jsonPath("$.[*].grade1").value(hasItem(DEFAULT_GRADE_1)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].taskStart").value(hasItem(DEFAULT_TASK_START)))
            .andExpect(jsonPath("$.[*].taskEnd").value(hasItem(DEFAULT_TASK_END)))
            .andExpect(jsonPath("$.[*].taskStep").value(hasItem(DEFAULT_TASK_STEP)))
            .andExpect(jsonPath("$.[*].progressionDate").value(hasItem(DEFAULT_PROGRESSION_DATE.toString())));
    }

    @Test
    @Transactional
    void getProgression() throws Exception {
        // Initialize the database
        progressionRepository.saveAndFlush(progression);

        // Get the progression
        restProgressionMockMvc
            .perform(get(ENTITY_API_URL_ID, progression.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(progression.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.isJustified").value(DEFAULT_IS_JUSTIFIED.booleanValue()))
            .andExpect(jsonPath("$.justifRef").value(DEFAULT_JUSTIF_REF))
            .andExpect(jsonPath("$.lateArrival").value(DEFAULT_LATE_ARRIVAL))
            .andExpect(jsonPath("$.earlyDeparture").value(DEFAULT_EARLY_DEPARTURE))
            .andExpect(jsonPath("$.taskDone").value(DEFAULT_TASK_DONE.booleanValue()))
            .andExpect(jsonPath("$.grade1").value(DEFAULT_GRADE_1))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.taskStart").value(DEFAULT_TASK_START))
            .andExpect(jsonPath("$.taskEnd").value(DEFAULT_TASK_END))
            .andExpect(jsonPath("$.taskStep").value(DEFAULT_TASK_STEP))
            .andExpect(jsonPath("$.progressionDate").value(DEFAULT_PROGRESSION_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProgression() throws Exception {
        // Get the progression
        restProgressionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProgression() throws Exception {
        // Initialize the database
        progressionRepository.saveAndFlush(progression);

        int databaseSizeBeforeUpdate = progressionRepository.findAll().size();
        progressionSearchRepository.save(progression);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());

        // Update the progression
        Progression updatedProgression = progressionRepository.findById(progression.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProgression are not directly saved in db
        em.detach(updatedProgression);
        updatedProgression
            .status(UPDATED_STATUS)
            .isJustified(UPDATED_IS_JUSTIFIED)
            .justifRef(UPDATED_JUSTIF_REF)
            .lateArrival(UPDATED_LATE_ARRIVAL)
            .earlyDeparture(UPDATED_EARLY_DEPARTURE)
            .taskDone(UPDATED_TASK_DONE)
            .grade1(UPDATED_GRADE_1)
            .description(UPDATED_DESCRIPTION)
            .taskStart(UPDATED_TASK_START)
            .taskEnd(UPDATED_TASK_END)
            .taskStep(UPDATED_TASK_STEP)
            .progressionDate(UPDATED_PROGRESSION_DATE);

        restProgressionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProgression.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProgression))
            )
            .andExpect(status().isOk());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeUpdate);
        Progression testProgression = progressionList.get(progressionList.size() - 1);
        assertThat(testProgression.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProgression.getIsJustified()).isEqualTo(UPDATED_IS_JUSTIFIED);
        assertThat(testProgression.getJustifRef()).isEqualTo(UPDATED_JUSTIF_REF);
        assertThat(testProgression.getLateArrival()).isEqualTo(UPDATED_LATE_ARRIVAL);
        assertThat(testProgression.getEarlyDeparture()).isEqualTo(UPDATED_EARLY_DEPARTURE);
        assertThat(testProgression.getTaskDone()).isEqualTo(UPDATED_TASK_DONE);
        assertThat(testProgression.getGrade1()).isEqualTo(UPDATED_GRADE_1);
        assertThat(testProgression.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProgression.getTaskStart()).isEqualTo(UPDATED_TASK_START);
        assertThat(testProgression.getTaskEnd()).isEqualTo(UPDATED_TASK_END);
        assertThat(testProgression.getTaskStep()).isEqualTo(UPDATED_TASK_STEP);
        assertThat(testProgression.getProgressionDate()).isEqualTo(UPDATED_PROGRESSION_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Progression> progressionSearchList = IterableUtils.toList(progressionSearchRepository.findAll());
                Progression testProgressionSearch = progressionSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProgressionSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testProgressionSearch.getIsJustified()).isEqualTo(UPDATED_IS_JUSTIFIED);
                assertThat(testProgressionSearch.getJustifRef()).isEqualTo(UPDATED_JUSTIF_REF);
                assertThat(testProgressionSearch.getLateArrival()).isEqualTo(UPDATED_LATE_ARRIVAL);
                assertThat(testProgressionSearch.getEarlyDeparture()).isEqualTo(UPDATED_EARLY_DEPARTURE);
                assertThat(testProgressionSearch.getTaskDone()).isEqualTo(UPDATED_TASK_DONE);
                assertThat(testProgressionSearch.getGrade1()).isEqualTo(UPDATED_GRADE_1);
                assertThat(testProgressionSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testProgressionSearch.getTaskStart()).isEqualTo(UPDATED_TASK_START);
                assertThat(testProgressionSearch.getTaskEnd()).isEqualTo(UPDATED_TASK_END);
                assertThat(testProgressionSearch.getTaskStep()).isEqualTo(UPDATED_TASK_STEP);
                assertThat(testProgressionSearch.getProgressionDate()).isEqualTo(UPDATED_PROGRESSION_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingProgression() throws Exception {
        int databaseSizeBeforeUpdate = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        progression.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProgressionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, progression.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(progression))
            )
            .andExpect(status().isBadRequest());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProgression() throws Exception {
        int databaseSizeBeforeUpdate = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        progression.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(progression))
            )
            .andExpect(status().isBadRequest());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProgression() throws Exception {
        int databaseSizeBeforeUpdate = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        progression.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progression)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProgressionWithPatch() throws Exception {
        // Initialize the database
        progressionRepository.saveAndFlush(progression);

        int databaseSizeBeforeUpdate = progressionRepository.findAll().size();

        // Update the progression using partial update
        Progression partialUpdatedProgression = new Progression();
        partialUpdatedProgression.setId(progression.getId());

        partialUpdatedProgression
            .status(UPDATED_STATUS)
            .isJustified(UPDATED_IS_JUSTIFIED)
            .justifRef(UPDATED_JUSTIF_REF)
            .earlyDeparture(UPDATED_EARLY_DEPARTURE)
            .taskDone(UPDATED_TASK_DONE)
            .taskStart(UPDATED_TASK_START)
            .progressionDate(UPDATED_PROGRESSION_DATE);

        restProgressionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProgression.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProgression))
            )
            .andExpect(status().isOk());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeUpdate);
        Progression testProgression = progressionList.get(progressionList.size() - 1);
        assertThat(testProgression.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProgression.getIsJustified()).isEqualTo(UPDATED_IS_JUSTIFIED);
        assertThat(testProgression.getJustifRef()).isEqualTo(UPDATED_JUSTIF_REF);
        assertThat(testProgression.getLateArrival()).isEqualTo(DEFAULT_LATE_ARRIVAL);
        assertThat(testProgression.getEarlyDeparture()).isEqualTo(UPDATED_EARLY_DEPARTURE);
        assertThat(testProgression.getTaskDone()).isEqualTo(UPDATED_TASK_DONE);
        assertThat(testProgression.getGrade1()).isEqualTo(DEFAULT_GRADE_1);
        assertThat(testProgression.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProgression.getTaskStart()).isEqualTo(UPDATED_TASK_START);
        assertThat(testProgression.getTaskEnd()).isEqualTo(DEFAULT_TASK_END);
        assertThat(testProgression.getTaskStep()).isEqualTo(DEFAULT_TASK_STEP);
        assertThat(testProgression.getProgressionDate()).isEqualTo(UPDATED_PROGRESSION_DATE);
    }

    @Test
    @Transactional
    void fullUpdateProgressionWithPatch() throws Exception {
        // Initialize the database
        progressionRepository.saveAndFlush(progression);

        int databaseSizeBeforeUpdate = progressionRepository.findAll().size();

        // Update the progression using partial update
        Progression partialUpdatedProgression = new Progression();
        partialUpdatedProgression.setId(progression.getId());

        partialUpdatedProgression
            .status(UPDATED_STATUS)
            .isJustified(UPDATED_IS_JUSTIFIED)
            .justifRef(UPDATED_JUSTIF_REF)
            .lateArrival(UPDATED_LATE_ARRIVAL)
            .earlyDeparture(UPDATED_EARLY_DEPARTURE)
            .taskDone(UPDATED_TASK_DONE)
            .grade1(UPDATED_GRADE_1)
            .description(UPDATED_DESCRIPTION)
            .taskStart(UPDATED_TASK_START)
            .taskEnd(UPDATED_TASK_END)
            .taskStep(UPDATED_TASK_STEP)
            .progressionDate(UPDATED_PROGRESSION_DATE);

        restProgressionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProgression.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProgression))
            )
            .andExpect(status().isOk());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeUpdate);
        Progression testProgression = progressionList.get(progressionList.size() - 1);
        assertThat(testProgression.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProgression.getIsJustified()).isEqualTo(UPDATED_IS_JUSTIFIED);
        assertThat(testProgression.getJustifRef()).isEqualTo(UPDATED_JUSTIF_REF);
        assertThat(testProgression.getLateArrival()).isEqualTo(UPDATED_LATE_ARRIVAL);
        assertThat(testProgression.getEarlyDeparture()).isEqualTo(UPDATED_EARLY_DEPARTURE);
        assertThat(testProgression.getTaskDone()).isEqualTo(UPDATED_TASK_DONE);
        assertThat(testProgression.getGrade1()).isEqualTo(UPDATED_GRADE_1);
        assertThat(testProgression.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProgression.getTaskStart()).isEqualTo(UPDATED_TASK_START);
        assertThat(testProgression.getTaskEnd()).isEqualTo(UPDATED_TASK_END);
        assertThat(testProgression.getTaskStep()).isEqualTo(UPDATED_TASK_STEP);
        assertThat(testProgression.getProgressionDate()).isEqualTo(UPDATED_PROGRESSION_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingProgression() throws Exception {
        int databaseSizeBeforeUpdate = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        progression.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProgressionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, progression.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(progression))
            )
            .andExpect(status().isBadRequest());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProgression() throws Exception {
        int databaseSizeBeforeUpdate = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        progression.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(progression))
            )
            .andExpect(status().isBadRequest());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProgression() throws Exception {
        int databaseSizeBeforeUpdate = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        progression.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(progression))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Progression in the database
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProgression() throws Exception {
        // Initialize the database
        progressionRepository.saveAndFlush(progression);
        progressionRepository.save(progression);
        progressionSearchRepository.save(progression);

        int databaseSizeBeforeDelete = progressionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the progression
        restProgressionMockMvc
            .perform(delete(ENTITY_API_URL_ID, progression.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Progression> progressionList = progressionRepository.findAll();
        assertThat(progressionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProgression() throws Exception {
        // Initialize the database
        progression = progressionRepository.saveAndFlush(progression);
        progressionSearchRepository.save(progression);

        // Search the progression
        restProgressionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + progression.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(progression.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].isJustified").value(hasItem(DEFAULT_IS_JUSTIFIED.booleanValue())))
            .andExpect(jsonPath("$.[*].justifRef").value(hasItem(DEFAULT_JUSTIF_REF)))
            .andExpect(jsonPath("$.[*].lateArrival").value(hasItem(DEFAULT_LATE_ARRIVAL)))
            .andExpect(jsonPath("$.[*].earlyDeparture").value(hasItem(DEFAULT_EARLY_DEPARTURE)))
            .andExpect(jsonPath("$.[*].taskDone").value(hasItem(DEFAULT_TASK_DONE.booleanValue())))
            .andExpect(jsonPath("$.[*].grade1").value(hasItem(DEFAULT_GRADE_1)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].taskStart").value(hasItem(DEFAULT_TASK_START)))
            .andExpect(jsonPath("$.[*].taskEnd").value(hasItem(DEFAULT_TASK_END)))
            .andExpect(jsonPath("$.[*].taskStep").value(hasItem(DEFAULT_TASK_STEP)))
            .andExpect(jsonPath("$.[*].progressionDate").value(hasItem(DEFAULT_PROGRESSION_DATE.toString())));
    }
}
