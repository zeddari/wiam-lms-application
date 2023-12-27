package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.ProgressionMode;
import com.wiam.lms.repository.ProgressionModeRepository;
import com.wiam.lms.repository.search.ProgressionModeSearchRepository;
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
 * Integration tests for the {@link ProgressionModeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProgressionModeResourceIT {

    private static final String DEFAULT_TITLE_AR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_AR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_LAT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_LAT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/progression-modes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/progression-modes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProgressionModeRepository progressionModeRepository;

    @Autowired
    private ProgressionModeSearchRepository progressionModeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProgressionModeMockMvc;

    private ProgressionMode progressionMode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProgressionMode createEntity(EntityManager em) {
        ProgressionMode progressionMode = new ProgressionMode().titleAr(DEFAULT_TITLE_AR).titleLat(DEFAULT_TITLE_LAT);
        return progressionMode;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProgressionMode createUpdatedEntity(EntityManager em) {
        ProgressionMode progressionMode = new ProgressionMode().titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);
        return progressionMode;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        progressionModeSearchRepository.deleteAll();
        assertThat(progressionModeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        progressionMode = createEntity(em);
    }

    @Test
    @Transactional
    void createProgressionMode() throws Exception {
        int databaseSizeBeforeCreate = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        // Create the ProgressionMode
        restProgressionModeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isCreated());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProgressionMode testProgressionMode = progressionModeList.get(progressionModeList.size() - 1);
        assertThat(testProgressionMode.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testProgressionMode.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
    }

    @Test
    @Transactional
    void createProgressionModeWithExistingId() throws Exception {
        // Create the ProgressionMode with an existing ID
        progressionMode.setId(1L);

        int databaseSizeBeforeCreate = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProgressionModeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleArIsRequired() throws Exception {
        int databaseSizeBeforeTest = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        // set the field null
        progressionMode.setTitleAr(null);

        // Create the ProgressionMode, which fails.

        restProgressionModeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isBadRequest());

        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleLatIsRequired() throws Exception {
        int databaseSizeBeforeTest = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        // set the field null
        progressionMode.setTitleLat(null);

        // Create the ProgressionMode, which fails.

        restProgressionModeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isBadRequest());

        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProgressionModes() throws Exception {
        // Initialize the database
        progressionModeRepository.saveAndFlush(progressionMode);

        // Get all the progressionModeList
        restProgressionModeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(progressionMode.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)));
    }

    @Test
    @Transactional
    void getProgressionMode() throws Exception {
        // Initialize the database
        progressionModeRepository.saveAndFlush(progressionMode);

        // Get the progressionMode
        restProgressionModeMockMvc
            .perform(get(ENTITY_API_URL_ID, progressionMode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(progressionMode.getId().intValue()))
            .andExpect(jsonPath("$.titleAr").value(DEFAULT_TITLE_AR))
            .andExpect(jsonPath("$.titleLat").value(DEFAULT_TITLE_LAT));
    }

    @Test
    @Transactional
    void getNonExistingProgressionMode() throws Exception {
        // Get the progressionMode
        restProgressionModeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProgressionMode() throws Exception {
        // Initialize the database
        progressionModeRepository.saveAndFlush(progressionMode);

        int databaseSizeBeforeUpdate = progressionModeRepository.findAll().size();
        progressionModeSearchRepository.save(progressionMode);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());

        // Update the progressionMode
        ProgressionMode updatedProgressionMode = progressionModeRepository.findById(progressionMode.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProgressionMode are not directly saved in db
        em.detach(updatedProgressionMode);
        updatedProgressionMode.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);

        restProgressionModeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProgressionMode.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProgressionMode))
            )
            .andExpect(status().isOk());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeUpdate);
        ProgressionMode testProgressionMode = progressionModeList.get(progressionModeList.size() - 1);
        assertThat(testProgressionMode.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testProgressionMode.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProgressionMode> progressionModeSearchList = IterableUtils.toList(progressionModeSearchRepository.findAll());
                ProgressionMode testProgressionModeSearch = progressionModeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProgressionModeSearch.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
                assertThat(testProgressionModeSearch.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
            });
    }

    @Test
    @Transactional
    void putNonExistingProgressionMode() throws Exception {
        int databaseSizeBeforeUpdate = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        progressionMode.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProgressionModeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, progressionMode.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProgressionMode() throws Exception {
        int databaseSizeBeforeUpdate = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        progressionMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressionModeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProgressionMode() throws Exception {
        int databaseSizeBeforeUpdate = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        progressionMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressionModeMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProgressionModeWithPatch() throws Exception {
        // Initialize the database
        progressionModeRepository.saveAndFlush(progressionMode);

        int databaseSizeBeforeUpdate = progressionModeRepository.findAll().size();

        // Update the progressionMode using partial update
        ProgressionMode partialUpdatedProgressionMode = new ProgressionMode();
        partialUpdatedProgressionMode.setId(progressionMode.getId());

        partialUpdatedProgressionMode.titleAr(UPDATED_TITLE_AR);

        restProgressionModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProgressionMode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProgressionMode))
            )
            .andExpect(status().isOk());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeUpdate);
        ProgressionMode testProgressionMode = progressionModeList.get(progressionModeList.size() - 1);
        assertThat(testProgressionMode.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testProgressionMode.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
    }

    @Test
    @Transactional
    void fullUpdateProgressionModeWithPatch() throws Exception {
        // Initialize the database
        progressionModeRepository.saveAndFlush(progressionMode);

        int databaseSizeBeforeUpdate = progressionModeRepository.findAll().size();

        // Update the progressionMode using partial update
        ProgressionMode partialUpdatedProgressionMode = new ProgressionMode();
        partialUpdatedProgressionMode.setId(progressionMode.getId());

        partialUpdatedProgressionMode.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);

        restProgressionModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProgressionMode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProgressionMode))
            )
            .andExpect(status().isOk());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeUpdate);
        ProgressionMode testProgressionMode = progressionModeList.get(progressionModeList.size() - 1);
        assertThat(testProgressionMode.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testProgressionMode.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
    }

    @Test
    @Transactional
    void patchNonExistingProgressionMode() throws Exception {
        int databaseSizeBeforeUpdate = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        progressionMode.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProgressionModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, progressionMode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProgressionMode() throws Exception {
        int databaseSizeBeforeUpdate = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        progressionMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressionModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProgressionMode() throws Exception {
        int databaseSizeBeforeUpdate = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        progressionMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressionModeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(progressionMode))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProgressionMode in the database
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProgressionMode() throws Exception {
        // Initialize the database
        progressionModeRepository.saveAndFlush(progressionMode);
        progressionModeRepository.save(progressionMode);
        progressionModeSearchRepository.save(progressionMode);

        int databaseSizeBeforeDelete = progressionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the progressionMode
        restProgressionModeMockMvc
            .perform(delete(ENTITY_API_URL_ID, progressionMode.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProgressionMode> progressionModeList = progressionModeRepository.findAll();
        assertThat(progressionModeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(progressionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProgressionMode() throws Exception {
        // Initialize the database
        progressionMode = progressionModeRepository.saveAndFlush(progressionMode);
        progressionModeSearchRepository.save(progressionMode);

        // Search the progressionMode
        restProgressionModeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + progressionMode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(progressionMode.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)));
    }
}
