package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.SessionJoinMode;
import com.wiam.lms.repository.SessionJoinModeRepository;
import com.wiam.lms.repository.search.SessionJoinModeSearchRepository;
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
 * Integration tests for the {@link SessionJoinModeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SessionJoinModeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/session-join-modes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/session-join-modes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SessionJoinModeRepository sessionJoinModeRepository;

    @Autowired
    private SessionJoinModeSearchRepository sessionJoinModeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionJoinModeMockMvc;

    private SessionJoinMode sessionJoinMode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionJoinMode createEntity(EntityManager em) {
        SessionJoinMode sessionJoinMode = new SessionJoinMode().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
        return sessionJoinMode;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionJoinMode createUpdatedEntity(EntityManager em) {
        SessionJoinMode sessionJoinMode = new SessionJoinMode().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        return sessionJoinMode;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        sessionJoinModeSearchRepository.deleteAll();
        assertThat(sessionJoinModeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        sessionJoinMode = createEntity(em);
    }

    @Test
    @Transactional
    void createSessionJoinMode() throws Exception {
        int databaseSizeBeforeCreate = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        // Create the SessionJoinMode
        restSessionJoinModeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionJoinMode))
            )
            .andExpect(status().isCreated());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SessionJoinMode testSessionJoinMode = sessionJoinModeList.get(sessionJoinModeList.size() - 1);
        assertThat(testSessionJoinMode.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSessionJoinMode.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createSessionJoinModeWithExistingId() throws Exception {
        // Create the SessionJoinMode with an existing ID
        sessionJoinMode.setId(1L);

        int databaseSizeBeforeCreate = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSessionJoinModeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionJoinMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        // set the field null
        sessionJoinMode.setTitle(null);

        // Create the SessionJoinMode, which fails.

        restSessionJoinModeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionJoinMode))
            )
            .andExpect(status().isBadRequest());

        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSessionJoinModes() throws Exception {
        // Initialize the database
        sessionJoinModeRepository.saveAndFlush(sessionJoinMode);

        // Get all the sessionJoinModeList
        restSessionJoinModeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionJoinMode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getSessionJoinMode() throws Exception {
        // Initialize the database
        sessionJoinModeRepository.saveAndFlush(sessionJoinMode);

        // Get the sessionJoinMode
        restSessionJoinModeMockMvc
            .perform(get(ENTITY_API_URL_ID, sessionJoinMode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sessionJoinMode.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingSessionJoinMode() throws Exception {
        // Get the sessionJoinMode
        restSessionJoinModeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSessionJoinMode() throws Exception {
        // Initialize the database
        sessionJoinModeRepository.saveAndFlush(sessionJoinMode);

        int databaseSizeBeforeUpdate = sessionJoinModeRepository.findAll().size();
        sessionJoinModeSearchRepository.save(sessionJoinMode);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());

        // Update the sessionJoinMode
        SessionJoinMode updatedSessionJoinMode = sessionJoinModeRepository.findById(sessionJoinMode.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSessionJoinMode are not directly saved in db
        em.detach(updatedSessionJoinMode);
        updatedSessionJoinMode.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restSessionJoinModeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSessionJoinMode.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSessionJoinMode))
            )
            .andExpect(status().isOk());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeUpdate);
        SessionJoinMode testSessionJoinMode = sessionJoinModeList.get(sessionJoinModeList.size() - 1);
        assertThat(testSessionJoinMode.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSessionJoinMode.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SessionJoinMode> sessionJoinModeSearchList = IterableUtils.toList(sessionJoinModeSearchRepository.findAll());
                SessionJoinMode testSessionJoinModeSearch = sessionJoinModeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSessionJoinModeSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testSessionJoinModeSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingSessionJoinMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        sessionJoinMode.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionJoinModeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sessionJoinMode.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionJoinMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSessionJoinMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        sessionJoinMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionJoinModeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionJoinMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSessionJoinMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        sessionJoinMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionJoinModeMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionJoinMode))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSessionJoinModeWithPatch() throws Exception {
        // Initialize the database
        sessionJoinModeRepository.saveAndFlush(sessionJoinMode);

        int databaseSizeBeforeUpdate = sessionJoinModeRepository.findAll().size();

        // Update the sessionJoinMode using partial update
        SessionJoinMode partialUpdatedSessionJoinMode = new SessionJoinMode();
        partialUpdatedSessionJoinMode.setId(sessionJoinMode.getId());

        restSessionJoinModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionJoinMode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionJoinMode))
            )
            .andExpect(status().isOk());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeUpdate);
        SessionJoinMode testSessionJoinMode = sessionJoinModeList.get(sessionJoinModeList.size() - 1);
        assertThat(testSessionJoinMode.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSessionJoinMode.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateSessionJoinModeWithPatch() throws Exception {
        // Initialize the database
        sessionJoinModeRepository.saveAndFlush(sessionJoinMode);

        int databaseSizeBeforeUpdate = sessionJoinModeRepository.findAll().size();

        // Update the sessionJoinMode using partial update
        SessionJoinMode partialUpdatedSessionJoinMode = new SessionJoinMode();
        partialUpdatedSessionJoinMode.setId(sessionJoinMode.getId());

        partialUpdatedSessionJoinMode.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restSessionJoinModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionJoinMode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionJoinMode))
            )
            .andExpect(status().isOk());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeUpdate);
        SessionJoinMode testSessionJoinMode = sessionJoinModeList.get(sessionJoinModeList.size() - 1);
        assertThat(testSessionJoinMode.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSessionJoinMode.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingSessionJoinMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        sessionJoinMode.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionJoinModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sessionJoinMode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionJoinMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSessionJoinMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        sessionJoinMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionJoinModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionJoinMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSessionJoinMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        sessionJoinMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionJoinModeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionJoinMode))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionJoinMode in the database
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSessionJoinMode() throws Exception {
        // Initialize the database
        sessionJoinModeRepository.saveAndFlush(sessionJoinMode);
        sessionJoinModeRepository.save(sessionJoinMode);
        sessionJoinModeSearchRepository.save(sessionJoinMode);

        int databaseSizeBeforeDelete = sessionJoinModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the sessionJoinMode
        restSessionJoinModeMockMvc
            .perform(delete(ENTITY_API_URL_ID, sessionJoinMode.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SessionJoinMode> sessionJoinModeList = sessionJoinModeRepository.findAll();
        assertThat(sessionJoinModeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionJoinModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSessionJoinMode() throws Exception {
        // Initialize the database
        sessionJoinMode = sessionJoinModeRepository.saveAndFlush(sessionJoinMode);
        sessionJoinModeSearchRepository.save(sessionJoinMode);

        // Search the sessionJoinMode
        restSessionJoinModeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sessionJoinMode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionJoinMode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
