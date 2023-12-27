package com.wiam.lms.web.rest;

import static com.wiam.lms.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Enrolement;
import com.wiam.lms.repository.EnrolementRepository;
import com.wiam.lms.repository.search.EnrolementSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link EnrolementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnrolementResourceIT {

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final ZonedDateTime DEFAULT_ACTIVATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ACTIVATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_ACTIVATED_BY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ACTIVATED_BY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_ENROLMENT_START_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ENROLMENT_START_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_ENROLEMNT_END_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ENROLEMNT_END_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/enrolements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/enrolements/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnrolementRepository enrolementRepository;

    @Autowired
    private EnrolementSearchRepository enrolementSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnrolementMockMvc;

    private Enrolement enrolement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enrolement createEntity(EntityManager em) {
        Enrolement enrolement = new Enrolement()
            .isActive(DEFAULT_IS_ACTIVE)
            .activatedAt(DEFAULT_ACTIVATED_AT)
            .activatedBy(DEFAULT_ACTIVATED_BY)
            .enrolmentStartTime(DEFAULT_ENROLMENT_START_TIME)
            .enrolemntEndTime(DEFAULT_ENROLEMNT_END_TIME);
        return enrolement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enrolement createUpdatedEntity(EntityManager em) {
        Enrolement enrolement = new Enrolement()
            .isActive(UPDATED_IS_ACTIVE)
            .activatedAt(UPDATED_ACTIVATED_AT)
            .activatedBy(UPDATED_ACTIVATED_BY)
            .enrolmentStartTime(UPDATED_ENROLMENT_START_TIME)
            .enrolemntEndTime(UPDATED_ENROLEMNT_END_TIME);
        return enrolement;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        enrolementSearchRepository.deleteAll();
        assertThat(enrolementSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        enrolement = createEntity(em);
    }

    @Test
    @Transactional
    void createEnrolement() throws Exception {
        int databaseSizeBeforeCreate = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        // Create the Enrolement
        restEnrolementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enrolement)))
            .andExpect(status().isCreated());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Enrolement testEnrolement = enrolementList.get(enrolementList.size() - 1);
        assertThat(testEnrolement.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testEnrolement.getActivatedAt()).isEqualTo(DEFAULT_ACTIVATED_AT);
        assertThat(testEnrolement.getActivatedBy()).isEqualTo(DEFAULT_ACTIVATED_BY);
        assertThat(testEnrolement.getEnrolmentStartTime()).isEqualTo(DEFAULT_ENROLMENT_START_TIME);
        assertThat(testEnrolement.getEnrolemntEndTime()).isEqualTo(DEFAULT_ENROLEMNT_END_TIME);
    }

    @Test
    @Transactional
    void createEnrolementWithExistingId() throws Exception {
        // Create the Enrolement with an existing ID
        enrolement.setId(1L);

        int databaseSizeBeforeCreate = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnrolementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enrolement)))
            .andExpect(status().isBadRequest());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        // set the field null
        enrolement.setIsActive(null);

        // Create the Enrolement, which fails.

        restEnrolementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enrolement)))
            .andExpect(status().isBadRequest());

        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEnrolmentStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        // set the field null
        enrolement.setEnrolmentStartTime(null);

        // Create the Enrolement, which fails.

        restEnrolementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enrolement)))
            .andExpect(status().isBadRequest());

        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEnrolemntEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        // set the field null
        enrolement.setEnrolemntEndTime(null);

        // Create the Enrolement, which fails.

        restEnrolementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enrolement)))
            .andExpect(status().isBadRequest());

        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEnrolements() throws Exception {
        // Initialize the database
        enrolementRepository.saveAndFlush(enrolement);

        // Get all the enrolementList
        restEnrolementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enrolement.getId().intValue())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].activatedAt").value(hasItem(sameInstant(DEFAULT_ACTIVATED_AT))))
            .andExpect(jsonPath("$.[*].activatedBy").value(hasItem(sameInstant(DEFAULT_ACTIVATED_BY))))
            .andExpect(jsonPath("$.[*].enrolmentStartTime").value(hasItem(sameInstant(DEFAULT_ENROLMENT_START_TIME))))
            .andExpect(jsonPath("$.[*].enrolemntEndTime").value(hasItem(sameInstant(DEFAULT_ENROLEMNT_END_TIME))));
    }

    @Test
    @Transactional
    void getEnrolement() throws Exception {
        // Initialize the database
        enrolementRepository.saveAndFlush(enrolement);

        // Get the enrolement
        restEnrolementMockMvc
            .perform(get(ENTITY_API_URL_ID, enrolement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(enrolement.getId().intValue()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.activatedAt").value(sameInstant(DEFAULT_ACTIVATED_AT)))
            .andExpect(jsonPath("$.activatedBy").value(sameInstant(DEFAULT_ACTIVATED_BY)))
            .andExpect(jsonPath("$.enrolmentStartTime").value(sameInstant(DEFAULT_ENROLMENT_START_TIME)))
            .andExpect(jsonPath("$.enrolemntEndTime").value(sameInstant(DEFAULT_ENROLEMNT_END_TIME)));
    }

    @Test
    @Transactional
    void getNonExistingEnrolement() throws Exception {
        // Get the enrolement
        restEnrolementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEnrolement() throws Exception {
        // Initialize the database
        enrolementRepository.saveAndFlush(enrolement);

        int databaseSizeBeforeUpdate = enrolementRepository.findAll().size();
        enrolementSearchRepository.save(enrolement);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());

        // Update the enrolement
        Enrolement updatedEnrolement = enrolementRepository.findById(enrolement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEnrolement are not directly saved in db
        em.detach(updatedEnrolement);
        updatedEnrolement
            .isActive(UPDATED_IS_ACTIVE)
            .activatedAt(UPDATED_ACTIVATED_AT)
            .activatedBy(UPDATED_ACTIVATED_BY)
            .enrolmentStartTime(UPDATED_ENROLMENT_START_TIME)
            .enrolemntEndTime(UPDATED_ENROLEMNT_END_TIME);

        restEnrolementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEnrolement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEnrolement))
            )
            .andExpect(status().isOk());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeUpdate);
        Enrolement testEnrolement = enrolementList.get(enrolementList.size() - 1);
        assertThat(testEnrolement.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testEnrolement.getActivatedAt()).isEqualTo(UPDATED_ACTIVATED_AT);
        assertThat(testEnrolement.getActivatedBy()).isEqualTo(UPDATED_ACTIVATED_BY);
        assertThat(testEnrolement.getEnrolmentStartTime()).isEqualTo(UPDATED_ENROLMENT_START_TIME);
        assertThat(testEnrolement.getEnrolemntEndTime()).isEqualTo(UPDATED_ENROLEMNT_END_TIME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Enrolement> enrolementSearchList = IterableUtils.toList(enrolementSearchRepository.findAll());
                Enrolement testEnrolementSearch = enrolementSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEnrolementSearch.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
                assertThat(testEnrolementSearch.getActivatedAt()).isEqualTo(UPDATED_ACTIVATED_AT);
                assertThat(testEnrolementSearch.getActivatedBy()).isEqualTo(UPDATED_ACTIVATED_BY);
                assertThat(testEnrolementSearch.getEnrolmentStartTime()).isEqualTo(UPDATED_ENROLMENT_START_TIME);
                assertThat(testEnrolementSearch.getEnrolemntEndTime()).isEqualTo(UPDATED_ENROLEMNT_END_TIME);
            });
    }

    @Test
    @Transactional
    void putNonExistingEnrolement() throws Exception {
        int databaseSizeBeforeUpdate = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        enrolement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnrolementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enrolement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enrolement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnrolement() throws Exception {
        int databaseSizeBeforeUpdate = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        enrolement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrolementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enrolement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnrolement() throws Exception {
        int databaseSizeBeforeUpdate = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        enrolement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrolementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enrolement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEnrolementWithPatch() throws Exception {
        // Initialize the database
        enrolementRepository.saveAndFlush(enrolement);

        int databaseSizeBeforeUpdate = enrolementRepository.findAll().size();

        // Update the enrolement using partial update
        Enrolement partialUpdatedEnrolement = new Enrolement();
        partialUpdatedEnrolement.setId(enrolement.getId());

        partialUpdatedEnrolement.isActive(UPDATED_IS_ACTIVE).enrolmentStartTime(UPDATED_ENROLMENT_START_TIME);

        restEnrolementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnrolement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnrolement))
            )
            .andExpect(status().isOk());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeUpdate);
        Enrolement testEnrolement = enrolementList.get(enrolementList.size() - 1);
        assertThat(testEnrolement.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testEnrolement.getActivatedAt()).isEqualTo(DEFAULT_ACTIVATED_AT);
        assertThat(testEnrolement.getActivatedBy()).isEqualTo(DEFAULT_ACTIVATED_BY);
        assertThat(testEnrolement.getEnrolmentStartTime()).isEqualTo(UPDATED_ENROLMENT_START_TIME);
        assertThat(testEnrolement.getEnrolemntEndTime()).isEqualTo(DEFAULT_ENROLEMNT_END_TIME);
    }

    @Test
    @Transactional
    void fullUpdateEnrolementWithPatch() throws Exception {
        // Initialize the database
        enrolementRepository.saveAndFlush(enrolement);

        int databaseSizeBeforeUpdate = enrolementRepository.findAll().size();

        // Update the enrolement using partial update
        Enrolement partialUpdatedEnrolement = new Enrolement();
        partialUpdatedEnrolement.setId(enrolement.getId());

        partialUpdatedEnrolement
            .isActive(UPDATED_IS_ACTIVE)
            .activatedAt(UPDATED_ACTIVATED_AT)
            .activatedBy(UPDATED_ACTIVATED_BY)
            .enrolmentStartTime(UPDATED_ENROLMENT_START_TIME)
            .enrolemntEndTime(UPDATED_ENROLEMNT_END_TIME);

        restEnrolementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnrolement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnrolement))
            )
            .andExpect(status().isOk());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeUpdate);
        Enrolement testEnrolement = enrolementList.get(enrolementList.size() - 1);
        assertThat(testEnrolement.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testEnrolement.getActivatedAt()).isEqualTo(UPDATED_ACTIVATED_AT);
        assertThat(testEnrolement.getActivatedBy()).isEqualTo(UPDATED_ACTIVATED_BY);
        assertThat(testEnrolement.getEnrolmentStartTime()).isEqualTo(UPDATED_ENROLMENT_START_TIME);
        assertThat(testEnrolement.getEnrolemntEndTime()).isEqualTo(UPDATED_ENROLEMNT_END_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingEnrolement() throws Exception {
        int databaseSizeBeforeUpdate = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        enrolement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnrolementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, enrolement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enrolement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnrolement() throws Exception {
        int databaseSizeBeforeUpdate = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        enrolement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrolementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enrolement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnrolement() throws Exception {
        int databaseSizeBeforeUpdate = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        enrolement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrolementMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(enrolement))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enrolement in the database
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEnrolement() throws Exception {
        // Initialize the database
        enrolementRepository.saveAndFlush(enrolement);
        enrolementRepository.save(enrolement);
        enrolementSearchRepository.save(enrolement);

        int databaseSizeBeforeDelete = enrolementRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the enrolement
        restEnrolementMockMvc
            .perform(delete(ENTITY_API_URL_ID, enrolement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Enrolement> enrolementList = enrolementRepository.findAll();
        assertThat(enrolementList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enrolementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEnrolement() throws Exception {
        // Initialize the database
        enrolement = enrolementRepository.saveAndFlush(enrolement);
        enrolementSearchRepository.save(enrolement);

        // Search the enrolement
        restEnrolementMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + enrolement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enrolement.getId().intValue())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].activatedAt").value(hasItem(sameInstant(DEFAULT_ACTIVATED_AT))))
            .andExpect(jsonPath("$.[*].activatedBy").value(hasItem(sameInstant(DEFAULT_ACTIVATED_BY))))
            .andExpect(jsonPath("$.[*].enrolmentStartTime").value(hasItem(sameInstant(DEFAULT_ENROLMENT_START_TIME))))
            .andExpect(jsonPath("$.[*].enrolemntEndTime").value(hasItem(sameInstant(DEFAULT_ENROLEMNT_END_TIME))));
    }
}
