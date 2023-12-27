package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.QuizCertificateType;
import com.wiam.lms.repository.QuizCertificateTypeRepository;
import com.wiam.lms.repository.search.QuizCertificateTypeSearchRepository;
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
 * Integration tests for the {@link QuizCertificateTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QuizCertificateTypeResourceIT {

    private static final String DEFAULT_TITLE_AR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_AR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_LAT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_LAT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/quiz-certificate-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/quiz-certificate-types/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private QuizCertificateTypeRepository quizCertificateTypeRepository;

    @Autowired
    private QuizCertificateTypeSearchRepository quizCertificateTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuizCertificateTypeMockMvc;

    private QuizCertificateType quizCertificateType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuizCertificateType createEntity(EntityManager em) {
        QuizCertificateType quizCertificateType = new QuizCertificateType().titleAr(DEFAULT_TITLE_AR).titleLat(DEFAULT_TITLE_LAT);
        return quizCertificateType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuizCertificateType createUpdatedEntity(EntityManager em) {
        QuizCertificateType quizCertificateType = new QuizCertificateType().titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);
        return quizCertificateType;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        quizCertificateTypeSearchRepository.deleteAll();
        assertThat(quizCertificateTypeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        quizCertificateType = createEntity(em);
    }

    @Test
    @Transactional
    void createQuizCertificateType() throws Exception {
        int databaseSizeBeforeCreate = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        // Create the QuizCertificateType
        restQuizCertificateTypeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificateType))
            )
            .andExpect(status().isCreated());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        QuizCertificateType testQuizCertificateType = quizCertificateTypeList.get(quizCertificateTypeList.size() - 1);
        assertThat(testQuizCertificateType.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testQuizCertificateType.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
    }

    @Test
    @Transactional
    void createQuizCertificateTypeWithExistingId() throws Exception {
        // Create the QuizCertificateType with an existing ID
        quizCertificateType.setId(1L);

        int databaseSizeBeforeCreate = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizCertificateTypeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificateType))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleArIsRequired() throws Exception {
        int databaseSizeBeforeTest = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        // set the field null
        quizCertificateType.setTitleAr(null);

        // Create the QuizCertificateType, which fails.

        restQuizCertificateTypeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificateType))
            )
            .andExpect(status().isBadRequest());

        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllQuizCertificateTypes() throws Exception {
        // Initialize the database
        quizCertificateTypeRepository.saveAndFlush(quizCertificateType);

        // Get all the quizCertificateTypeList
        restQuizCertificateTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizCertificateType.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)));
    }

    @Test
    @Transactional
    void getQuizCertificateType() throws Exception {
        // Initialize the database
        quizCertificateTypeRepository.saveAndFlush(quizCertificateType);

        // Get the quizCertificateType
        restQuizCertificateTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, quizCertificateType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quizCertificateType.getId().intValue()))
            .andExpect(jsonPath("$.titleAr").value(DEFAULT_TITLE_AR))
            .andExpect(jsonPath("$.titleLat").value(DEFAULT_TITLE_LAT));
    }

    @Test
    @Transactional
    void getNonExistingQuizCertificateType() throws Exception {
        // Get the quizCertificateType
        restQuizCertificateTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuizCertificateType() throws Exception {
        // Initialize the database
        quizCertificateTypeRepository.saveAndFlush(quizCertificateType);

        int databaseSizeBeforeUpdate = quizCertificateTypeRepository.findAll().size();
        quizCertificateTypeSearchRepository.save(quizCertificateType);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());

        // Update the quizCertificateType
        QuizCertificateType updatedQuizCertificateType = quizCertificateTypeRepository.findById(quizCertificateType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuizCertificateType are not directly saved in db
        em.detach(updatedQuizCertificateType);
        updatedQuizCertificateType.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);

        restQuizCertificateTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuizCertificateType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedQuizCertificateType))
            )
            .andExpect(status().isOk());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeUpdate);
        QuizCertificateType testQuizCertificateType = quizCertificateTypeList.get(quizCertificateTypeList.size() - 1);
        assertThat(testQuizCertificateType.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testQuizCertificateType.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<QuizCertificateType> quizCertificateTypeSearchList = IterableUtils.toList(
                    quizCertificateTypeSearchRepository.findAll()
                );
                QuizCertificateType testQuizCertificateTypeSearch = quizCertificateTypeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testQuizCertificateTypeSearch.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
                assertThat(testQuizCertificateTypeSearch.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
            });
    }

    @Test
    @Transactional
    void putNonExistingQuizCertificateType() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        quizCertificateType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizCertificateTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quizCertificateType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificateType))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuizCertificateType() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        quizCertificateType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizCertificateTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificateType))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuizCertificateType() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        quizCertificateType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizCertificateTypeMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificateType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateQuizCertificateTypeWithPatch() throws Exception {
        // Initialize the database
        quizCertificateTypeRepository.saveAndFlush(quizCertificateType);

        int databaseSizeBeforeUpdate = quizCertificateTypeRepository.findAll().size();

        // Update the quizCertificateType using partial update
        QuizCertificateType partialUpdatedQuizCertificateType = new QuizCertificateType();
        partialUpdatedQuizCertificateType.setId(quizCertificateType.getId());

        partialUpdatedQuizCertificateType.titleAr(UPDATED_TITLE_AR);

        restQuizCertificateTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizCertificateType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuizCertificateType))
            )
            .andExpect(status().isOk());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeUpdate);
        QuizCertificateType testQuizCertificateType = quizCertificateTypeList.get(quizCertificateTypeList.size() - 1);
        assertThat(testQuizCertificateType.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testQuizCertificateType.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
    }

    @Test
    @Transactional
    void fullUpdateQuizCertificateTypeWithPatch() throws Exception {
        // Initialize the database
        quizCertificateTypeRepository.saveAndFlush(quizCertificateType);

        int databaseSizeBeforeUpdate = quizCertificateTypeRepository.findAll().size();

        // Update the quizCertificateType using partial update
        QuizCertificateType partialUpdatedQuizCertificateType = new QuizCertificateType();
        partialUpdatedQuizCertificateType.setId(quizCertificateType.getId());

        partialUpdatedQuizCertificateType.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);

        restQuizCertificateTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizCertificateType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuizCertificateType))
            )
            .andExpect(status().isOk());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeUpdate);
        QuizCertificateType testQuizCertificateType = quizCertificateTypeList.get(quizCertificateTypeList.size() - 1);
        assertThat(testQuizCertificateType.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testQuizCertificateType.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
    }

    @Test
    @Transactional
    void patchNonExistingQuizCertificateType() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        quizCertificateType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizCertificateTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quizCertificateType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificateType))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuizCertificateType() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        quizCertificateType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizCertificateTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificateType))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuizCertificateType() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        quizCertificateType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizCertificateTypeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificateType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizCertificateType in the database
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteQuizCertificateType() throws Exception {
        // Initialize the database
        quizCertificateTypeRepository.saveAndFlush(quizCertificateType);
        quizCertificateTypeRepository.save(quizCertificateType);
        quizCertificateTypeSearchRepository.save(quizCertificateType);

        int databaseSizeBeforeDelete = quizCertificateTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the quizCertificateType
        restQuizCertificateTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, quizCertificateType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<QuizCertificateType> quizCertificateTypeList = quizCertificateTypeRepository.findAll();
        assertThat(quizCertificateTypeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchQuizCertificateType() throws Exception {
        // Initialize the database
        quizCertificateType = quizCertificateTypeRepository.saveAndFlush(quizCertificateType);
        quizCertificateTypeSearchRepository.save(quizCertificateType);

        // Search the quizCertificateType
        restQuizCertificateTypeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + quizCertificateType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizCertificateType.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)));
    }
}
