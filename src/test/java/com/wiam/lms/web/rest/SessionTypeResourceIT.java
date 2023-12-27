package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.SessionType;
import com.wiam.lms.repository.SessionTypeRepository;
import com.wiam.lms.repository.search.SessionTypeSearchRepository;
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
 * Integration tests for the {@link SessionTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SessionTypeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/session-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/session-types/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SessionTypeRepository sessionTypeRepository;

    @Autowired
    private SessionTypeSearchRepository sessionTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionTypeMockMvc;

    private SessionType sessionType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionType createEntity(EntityManager em) {
        SessionType sessionType = new SessionType().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
        return sessionType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionType createUpdatedEntity(EntityManager em) {
        SessionType sessionType = new SessionType().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        return sessionType;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        sessionTypeSearchRepository.deleteAll();
        assertThat(sessionTypeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        sessionType = createEntity(em);
    }

    @Test
    @Transactional
    void createSessionType() throws Exception {
        int databaseSizeBeforeCreate = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        // Create the SessionType
        restSessionTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionType)))
            .andExpect(status().isCreated());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SessionType testSessionType = sessionTypeList.get(sessionTypeList.size() - 1);
        assertThat(testSessionType.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSessionType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createSessionTypeWithExistingId() throws Exception {
        // Create the SessionType with an existing ID
        sessionType.setId(1L);

        int databaseSizeBeforeCreate = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSessionTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionType)))
            .andExpect(status().isBadRequest());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        // set the field null
        sessionType.setTitle(null);

        // Create the SessionType, which fails.

        restSessionTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionType)))
            .andExpect(status().isBadRequest());

        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSessionTypes() throws Exception {
        // Initialize the database
        sessionTypeRepository.saveAndFlush(sessionType);

        // Get all the sessionTypeList
        restSessionTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionType.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getSessionType() throws Exception {
        // Initialize the database
        sessionTypeRepository.saveAndFlush(sessionType);

        // Get the sessionType
        restSessionTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, sessionType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sessionType.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingSessionType() throws Exception {
        // Get the sessionType
        restSessionTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSessionType() throws Exception {
        // Initialize the database
        sessionTypeRepository.saveAndFlush(sessionType);

        int databaseSizeBeforeUpdate = sessionTypeRepository.findAll().size();
        sessionTypeSearchRepository.save(sessionType);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());

        // Update the sessionType
        SessionType updatedSessionType = sessionTypeRepository.findById(sessionType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSessionType are not directly saved in db
        em.detach(updatedSessionType);
        updatedSessionType.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restSessionTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSessionType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSessionType))
            )
            .andExpect(status().isOk());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeUpdate);
        SessionType testSessionType = sessionTypeList.get(sessionTypeList.size() - 1);
        assertThat(testSessionType.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSessionType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SessionType> sessionTypeSearchList = IterableUtils.toList(sessionTypeSearchRepository.findAll());
                SessionType testSessionTypeSearch = sessionTypeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSessionTypeSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testSessionTypeSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingSessionType() throws Exception {
        int databaseSizeBeforeUpdate = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        sessionType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sessionType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionType))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSessionType() throws Exception {
        int databaseSizeBeforeUpdate = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        sessionType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionType))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSessionType() throws Exception {
        int databaseSizeBeforeUpdate = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        sessionType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSessionTypeWithPatch() throws Exception {
        // Initialize the database
        sessionTypeRepository.saveAndFlush(sessionType);

        int databaseSizeBeforeUpdate = sessionTypeRepository.findAll().size();

        // Update the sessionType using partial update
        SessionType partialUpdatedSessionType = new SessionType();
        partialUpdatedSessionType.setId(sessionType.getId());

        partialUpdatedSessionType.description(UPDATED_DESCRIPTION);

        restSessionTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionType))
            )
            .andExpect(status().isOk());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeUpdate);
        SessionType testSessionType = sessionTypeList.get(sessionTypeList.size() - 1);
        assertThat(testSessionType.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSessionType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateSessionTypeWithPatch() throws Exception {
        // Initialize the database
        sessionTypeRepository.saveAndFlush(sessionType);

        int databaseSizeBeforeUpdate = sessionTypeRepository.findAll().size();

        // Update the sessionType using partial update
        SessionType partialUpdatedSessionType = new SessionType();
        partialUpdatedSessionType.setId(sessionType.getId());

        partialUpdatedSessionType.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restSessionTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionType))
            )
            .andExpect(status().isOk());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeUpdate);
        SessionType testSessionType = sessionTypeList.get(sessionTypeList.size() - 1);
        assertThat(testSessionType.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSessionType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingSessionType() throws Exception {
        int databaseSizeBeforeUpdate = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        sessionType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sessionType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionType))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSessionType() throws Exception {
        int databaseSizeBeforeUpdate = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        sessionType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionType))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSessionType() throws Exception {
        int databaseSizeBeforeUpdate = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        sessionType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionTypeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sessionType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionType in the database
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSessionType() throws Exception {
        // Initialize the database
        sessionTypeRepository.saveAndFlush(sessionType);
        sessionTypeRepository.save(sessionType);
        sessionTypeSearchRepository.save(sessionType);

        int databaseSizeBeforeDelete = sessionTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the sessionType
        restSessionTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, sessionType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SessionType> sessionTypeList = sessionTypeRepository.findAll();
        assertThat(sessionTypeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSessionType() throws Exception {
        // Initialize the database
        sessionType = sessionTypeRepository.saveAndFlush(sessionType);
        sessionTypeSearchRepository.save(sessionType);

        // Search the sessionType
        restSessionTypeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sessionType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionType.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
