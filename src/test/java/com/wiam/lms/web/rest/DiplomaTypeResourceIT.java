package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.DiplomaType;
import com.wiam.lms.repository.DiplomaTypeRepository;
import com.wiam.lms.repository.search.DiplomaTypeSearchRepository;
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
 * Integration tests for the {@link DiplomaTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DiplomaTypeResourceIT {

    private static final String DEFAULT_TITLE_AR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_AR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_LAT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_LAT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/diploma-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/diploma-types/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DiplomaTypeRepository diplomaTypeRepository;

    @Autowired
    private DiplomaTypeSearchRepository diplomaTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDiplomaTypeMockMvc;

    private DiplomaType diplomaType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DiplomaType createEntity(EntityManager em) {
        DiplomaType diplomaType = new DiplomaType().titleAr(DEFAULT_TITLE_AR).titleLat(DEFAULT_TITLE_LAT);
        return diplomaType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DiplomaType createUpdatedEntity(EntityManager em) {
        DiplomaType diplomaType = new DiplomaType().titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);
        return diplomaType;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        diplomaTypeSearchRepository.deleteAll();
        assertThat(diplomaTypeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        diplomaType = createEntity(em);
    }

    @Test
    @Transactional
    void createDiplomaType() throws Exception {
        int databaseSizeBeforeCreate = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        // Create the DiplomaType
        restDiplomaTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diplomaType)))
            .andExpect(status().isCreated());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        DiplomaType testDiplomaType = diplomaTypeList.get(diplomaTypeList.size() - 1);
        assertThat(testDiplomaType.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testDiplomaType.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
    }

    @Test
    @Transactional
    void createDiplomaTypeWithExistingId() throws Exception {
        // Create the DiplomaType with an existing ID
        diplomaType.setId(1L);

        int databaseSizeBeforeCreate = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiplomaTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diplomaType)))
            .andExpect(status().isBadRequest());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleArIsRequired() throws Exception {
        int databaseSizeBeforeTest = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        // set the field null
        diplomaType.setTitleAr(null);

        // Create the DiplomaType, which fails.

        restDiplomaTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diplomaType)))
            .andExpect(status().isBadRequest());

        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDiplomaTypes() throws Exception {
        // Initialize the database
        diplomaTypeRepository.saveAndFlush(diplomaType);

        // Get all the diplomaTypeList
        restDiplomaTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diplomaType.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)));
    }

    @Test
    @Transactional
    void getDiplomaType() throws Exception {
        // Initialize the database
        diplomaTypeRepository.saveAndFlush(diplomaType);

        // Get the diplomaType
        restDiplomaTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, diplomaType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(diplomaType.getId().intValue()))
            .andExpect(jsonPath("$.titleAr").value(DEFAULT_TITLE_AR))
            .andExpect(jsonPath("$.titleLat").value(DEFAULT_TITLE_LAT));
    }

    @Test
    @Transactional
    void getNonExistingDiplomaType() throws Exception {
        // Get the diplomaType
        restDiplomaTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDiplomaType() throws Exception {
        // Initialize the database
        diplomaTypeRepository.saveAndFlush(diplomaType);

        int databaseSizeBeforeUpdate = diplomaTypeRepository.findAll().size();
        diplomaTypeSearchRepository.save(diplomaType);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());

        // Update the diplomaType
        DiplomaType updatedDiplomaType = diplomaTypeRepository.findById(diplomaType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDiplomaType are not directly saved in db
        em.detach(updatedDiplomaType);
        updatedDiplomaType.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);

        restDiplomaTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDiplomaType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDiplomaType))
            )
            .andExpect(status().isOk());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeUpdate);
        DiplomaType testDiplomaType = diplomaTypeList.get(diplomaTypeList.size() - 1);
        assertThat(testDiplomaType.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testDiplomaType.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<DiplomaType> diplomaTypeSearchList = IterableUtils.toList(diplomaTypeSearchRepository.findAll());
                DiplomaType testDiplomaTypeSearch = diplomaTypeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testDiplomaTypeSearch.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
                assertThat(testDiplomaTypeSearch.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
            });
    }

    @Test
    @Transactional
    void putNonExistingDiplomaType() throws Exception {
        int databaseSizeBeforeUpdate = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        diplomaType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiplomaTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, diplomaType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(diplomaType))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDiplomaType() throws Exception {
        int databaseSizeBeforeUpdate = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        diplomaType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiplomaTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(diplomaType))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDiplomaType() throws Exception {
        int databaseSizeBeforeUpdate = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        diplomaType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiplomaTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diplomaType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDiplomaTypeWithPatch() throws Exception {
        // Initialize the database
        diplomaTypeRepository.saveAndFlush(diplomaType);

        int databaseSizeBeforeUpdate = diplomaTypeRepository.findAll().size();

        // Update the diplomaType using partial update
        DiplomaType partialUpdatedDiplomaType = new DiplomaType();
        partialUpdatedDiplomaType.setId(diplomaType.getId());

        partialUpdatedDiplomaType.titleLat(UPDATED_TITLE_LAT);

        restDiplomaTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiplomaType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiplomaType))
            )
            .andExpect(status().isOk());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeUpdate);
        DiplomaType testDiplomaType = diplomaTypeList.get(diplomaTypeList.size() - 1);
        assertThat(testDiplomaType.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testDiplomaType.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
    }

    @Test
    @Transactional
    void fullUpdateDiplomaTypeWithPatch() throws Exception {
        // Initialize the database
        diplomaTypeRepository.saveAndFlush(diplomaType);

        int databaseSizeBeforeUpdate = diplomaTypeRepository.findAll().size();

        // Update the diplomaType using partial update
        DiplomaType partialUpdatedDiplomaType = new DiplomaType();
        partialUpdatedDiplomaType.setId(diplomaType.getId());

        partialUpdatedDiplomaType.titleAr(UPDATED_TITLE_AR).titleLat(UPDATED_TITLE_LAT);

        restDiplomaTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiplomaType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiplomaType))
            )
            .andExpect(status().isOk());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeUpdate);
        DiplomaType testDiplomaType = diplomaTypeList.get(diplomaTypeList.size() - 1);
        assertThat(testDiplomaType.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testDiplomaType.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
    }

    @Test
    @Transactional
    void patchNonExistingDiplomaType() throws Exception {
        int databaseSizeBeforeUpdate = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        diplomaType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiplomaTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, diplomaType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(diplomaType))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDiplomaType() throws Exception {
        int databaseSizeBeforeUpdate = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        diplomaType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiplomaTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(diplomaType))
            )
            .andExpect(status().isBadRequest());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDiplomaType() throws Exception {
        int databaseSizeBeforeUpdate = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        diplomaType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiplomaTypeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(diplomaType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DiplomaType in the database
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDiplomaType() throws Exception {
        // Initialize the database
        diplomaTypeRepository.saveAndFlush(diplomaType);
        diplomaTypeRepository.save(diplomaType);
        diplomaTypeSearchRepository.save(diplomaType);

        int databaseSizeBeforeDelete = diplomaTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the diplomaType
        restDiplomaTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, diplomaType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DiplomaType> diplomaTypeList = diplomaTypeRepository.findAll();
        assertThat(diplomaTypeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDiplomaType() throws Exception {
        // Initialize the database
        diplomaType = diplomaTypeRepository.saveAndFlush(diplomaType);
        diplomaTypeSearchRepository.save(diplomaType);

        // Search the diplomaType
        restDiplomaTypeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + diplomaType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diplomaType.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)));
    }
}
