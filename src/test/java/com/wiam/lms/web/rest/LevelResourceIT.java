package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Level;
import com.wiam.lms.repository.LevelRepository;
import com.wiam.lms.repository.search.LevelSearchRepository;
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
 * Integration tests for the {@link LevelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LevelResourceIT {

    private static final String DEFAULT_TITLE_AR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_AR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_LAT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_LAT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/levels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/levels/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private LevelSearchRepository levelSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLevelMockMvc;

    private Level level;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Level createEntity(EntityManager em) {
        Level level = new Level().titleAr(DEFAULT_TITLE_AR).titleLat(DEFAULT_TITLE_LAT);
        return level;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Level createUpdatedEntity(EntityManager em) {
        Level level = new Level().titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);
        return level;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        levelSearchRepository.deleteAll();
        assertThat(levelSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        level = createEntity(em);
    }

    @Test
    @Transactional
    void createLevel() throws Exception {
        int databaseSizeBeforeCreate = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        // Create the Level
        restLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(level)))
            .andExpect(status().isCreated());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testLevel.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
    }

    @Test
    @Transactional
    void createLevelWithExistingId() throws Exception {
        // Create the Level with an existing ID
        level.setId(1L);

        int databaseSizeBeforeCreate = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(level)))
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleArIsRequired() throws Exception {
        int databaseSizeBeforeTest = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        // set the field null
        level.setTitleAr(null);

        // Create the Level, which fails.

        restLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(level)))
            .andExpect(status().isBadRequest());

        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleLatIsRequired() throws Exception {
        int databaseSizeBeforeTest = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        // set the field null
        level.setTitleLat(null);

        // Create the Level, which fails.

        restLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(level)))
            .andExpect(status().isBadRequest());

        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllLevels() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList
        restLevelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(level.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)));
    }

    @Test
    @Transactional
    void getLevel() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get the level
        restLevelMockMvc
            .perform(get(ENTITY_API_URL_ID, level.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(level.getId().intValue()))
            .andExpect(jsonPath("$.titleAr").value(DEFAULT_TITLE_AR))
            .andExpect(jsonPath("$.titleLat").value(DEFAULT_TITLE_LAT));
    }

    @Test
    @Transactional
    void getNonExistingLevel() throws Exception {
        // Get the level
        restLevelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLevel() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        levelSearchRepository.save(level);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());

        // Update the level
        Level updatedLevel = levelRepository.findById(level.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLevel are not directly saved in db
        em.detach(updatedLevel);
        updatedLevel.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);

        restLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLevel.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLevel))
            )
            .andExpect(status().isOk());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testLevel.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Level> levelSearchList = IterableUtils.toList(levelSearchRepository.findAll());
                Level testLevelSearch = levelSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testLevelSearch.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
                assertThat(testLevelSearch.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
            });
    }

    @Test
    @Transactional
    void putNonExistingLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        level.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, level.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(level))
            )
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        level.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(level))
            )
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        level.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(level)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateLevelWithPatch() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        int databaseSizeBeforeUpdate = levelRepository.findAll().size();

        // Update the level using partial update
        Level partialUpdatedLevel = new Level();
        partialUpdatedLevel.setId(level.getId());

        partialUpdatedLevel.titleAr(UPDATED_TITLE_AR);

        restLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLevel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLevel))
            )
            .andExpect(status().isOk());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testLevel.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
    }

    @Test
    @Transactional
    void fullUpdateLevelWithPatch() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        int databaseSizeBeforeUpdate = levelRepository.findAll().size();

        // Update the level using partial update
        Level partialUpdatedLevel = new Level();
        partialUpdatedLevel.setId(level.getId());

        partialUpdatedLevel.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);

        restLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLevel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLevel))
            )
            .andExpect(status().isOk());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testLevel.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
    }

    @Test
    @Transactional
    void patchNonExistingLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        level.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, level.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(level))
            )
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        level.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(level))
            )
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        level.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLevelMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(level)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteLevel() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);
        levelRepository.save(level);
        levelSearchRepository.save(level);

        int databaseSizeBeforeDelete = levelRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the level
        restLevelMockMvc
            .perform(delete(ENTITY_API_URL_ID, level.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(levelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchLevel() throws Exception {
        // Initialize the database
        level = levelRepository.saveAndFlush(level);
        levelSearchRepository.save(level);

        // Search the level
        restLevelMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + level.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(level.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)));
    }
}
