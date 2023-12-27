package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.JobTitle;
import com.wiam.lms.repository.JobTitleRepository;
import com.wiam.lms.repository.search.JobTitleSearchRepository;
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
 * Integration tests for the {@link JobTitleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JobTitleResourceIT {

    private static final String DEFAULT_TITLE_AR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_AR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_LAT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_LAT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/job-titles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/job-titles/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JobTitleRepository jobTitleRepository;

    @Autowired
    private JobTitleSearchRepository jobTitleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJobTitleMockMvc;

    private JobTitle jobTitle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobTitle createEntity(EntityManager em) {
        JobTitle jobTitle = new JobTitle().titleAr(DEFAULT_TITLE_AR).titleLat(DEFAULT_TITLE_LAT).description(DEFAULT_DESCRIPTION);
        return jobTitle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobTitle createUpdatedEntity(EntityManager em) {
        JobTitle jobTitle = new JobTitle().titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT).description(UPDATED_DESCRIPTION);
        return jobTitle;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        jobTitleSearchRepository.deleteAll();
        assertThat(jobTitleSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        jobTitle = createEntity(em);
    }

    @Test
    @Transactional
    void createJobTitle() throws Exception {
        int databaseSizeBeforeCreate = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        // Create the JobTitle
        restJobTitleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobTitle)))
            .andExpect(status().isCreated());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        JobTitle testJobTitle = jobTitleList.get(jobTitleList.size() - 1);
        assertThat(testJobTitle.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testJobTitle.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
        assertThat(testJobTitle.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createJobTitleWithExistingId() throws Exception {
        // Create the JobTitle with an existing ID
        jobTitle.setId(1L);

        int databaseSizeBeforeCreate = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobTitleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobTitle)))
            .andExpect(status().isBadRequest());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleArIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        // set the field null
        jobTitle.setTitleAr(null);

        // Create the JobTitle, which fails.

        restJobTitleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobTitle)))
            .andExpect(status().isBadRequest());

        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleLatIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        // set the field null
        jobTitle.setTitleLat(null);

        // Create the JobTitle, which fails.

        restJobTitleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobTitle)))
            .andExpect(status().isBadRequest());

        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllJobTitles() throws Exception {
        // Initialize the database
        jobTitleRepository.saveAndFlush(jobTitle);

        // Get all the jobTitleList
        restJobTitleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobTitle.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getJobTitle() throws Exception {
        // Initialize the database
        jobTitleRepository.saveAndFlush(jobTitle);

        // Get the jobTitle
        restJobTitleMockMvc
            .perform(get(ENTITY_API_URL_ID, jobTitle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(jobTitle.getId().intValue()))
            .andExpect(jsonPath("$.titleAr").value(DEFAULT_TITLE_AR))
            .andExpect(jsonPath("$.titleLat").value(DEFAULT_TITLE_LAT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingJobTitle() throws Exception {
        // Get the jobTitle
        restJobTitleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingJobTitle() throws Exception {
        // Initialize the database
        jobTitleRepository.saveAndFlush(jobTitle);

        int databaseSizeBeforeUpdate = jobTitleRepository.findAll().size();
        jobTitleSearchRepository.save(jobTitle);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());

        // Update the jobTitle
        JobTitle updatedJobTitle = jobTitleRepository.findById(jobTitle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedJobTitle are not directly saved in db
        em.detach(updatedJobTitle);
        updatedJobTitle.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT).description(UPDATED_DESCRIPTION);

        restJobTitleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJobTitle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedJobTitle))
            )
            .andExpect(status().isOk());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeUpdate);
        JobTitle testJobTitle = jobTitleList.get(jobTitleList.size() - 1);
        assertThat(testJobTitle.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testJobTitle.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testJobTitle.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<JobTitle> jobTitleSearchList = IterableUtils.toList(jobTitleSearchRepository.findAll());
                JobTitle testJobTitleSearch = jobTitleSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testJobTitleSearch.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
                assertThat(testJobTitleSearch.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
                assertThat(testJobTitleSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingJobTitle() throws Exception {
        int databaseSizeBeforeUpdate = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        jobTitle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobTitleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jobTitle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobTitle))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchJobTitle() throws Exception {
        int databaseSizeBeforeUpdate = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        jobTitle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobTitleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobTitle))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJobTitle() throws Exception {
        int databaseSizeBeforeUpdate = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        jobTitle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobTitleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobTitle)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateJobTitleWithPatch() throws Exception {
        // Initialize the database
        jobTitleRepository.saveAndFlush(jobTitle);

        int databaseSizeBeforeUpdate = jobTitleRepository.findAll().size();

        // Update the jobTitle using partial update
        JobTitle partialUpdatedJobTitle = new JobTitle();
        partialUpdatedJobTitle.setId(jobTitle.getId());

        partialUpdatedJobTitle.titleAr(UPDATED_TITLE_AR);

        restJobTitleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobTitle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobTitle))
            )
            .andExpect(status().isOk());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeUpdate);
        JobTitle testJobTitle = jobTitleList.get(jobTitleList.size() - 1);
        assertThat(testJobTitle.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testJobTitle.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
        assertThat(testJobTitle.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateJobTitleWithPatch() throws Exception {
        // Initialize the database
        jobTitleRepository.saveAndFlush(jobTitle);

        int databaseSizeBeforeUpdate = jobTitleRepository.findAll().size();

        // Update the jobTitle using partial update
        JobTitle partialUpdatedJobTitle = new JobTitle();
        partialUpdatedJobTitle.setId(jobTitle.getId());

        partialUpdatedJobTitle.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT).description(UPDATED_DESCRIPTION);

        restJobTitleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobTitle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobTitle))
            )
            .andExpect(status().isOk());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeUpdate);
        JobTitle testJobTitle = jobTitleList.get(jobTitleList.size() - 1);
        assertThat(testJobTitle.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testJobTitle.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testJobTitle.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingJobTitle() throws Exception {
        int databaseSizeBeforeUpdate = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        jobTitle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobTitleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, jobTitle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobTitle))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJobTitle() throws Exception {
        int databaseSizeBeforeUpdate = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        jobTitle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobTitleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobTitle))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJobTitle() throws Exception {
        int databaseSizeBeforeUpdate = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        jobTitle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobTitleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(jobTitle)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JobTitle in the database
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteJobTitle() throws Exception {
        // Initialize the database
        jobTitleRepository.saveAndFlush(jobTitle);
        jobTitleRepository.save(jobTitle);
        jobTitleSearchRepository.save(jobTitle);

        int databaseSizeBeforeDelete = jobTitleRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the jobTitle
        restJobTitleMockMvc
            .perform(delete(ENTITY_API_URL_ID, jobTitle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<JobTitle> jobTitleList = jobTitleRepository.findAll();
        assertThat(jobTitleList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobTitleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchJobTitle() throws Exception {
        // Initialize the database
        jobTitle = jobTitleRepository.saveAndFlush(jobTitle);
        jobTitleSearchRepository.save(jobTitle);

        // Search the jobTitle
        restJobTitleMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + jobTitle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobTitle.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
