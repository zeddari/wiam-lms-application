package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.SessionMode;
import com.wiam.lms.repository.SessionModeRepository;
import com.wiam.lms.repository.search.SessionModeSearchRepository;
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
 * Integration tests for the {@link SessionModeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SessionModeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/session-modes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/session-modes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SessionModeRepository sessionModeRepository;

    @Autowired
    private SessionModeSearchRepository sessionModeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionModeMockMvc;

    private SessionMode sessionMode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionMode createEntity(EntityManager em) {
        SessionMode sessionMode = new SessionMode().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
        return sessionMode;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionMode createUpdatedEntity(EntityManager em) {
        SessionMode sessionMode = new SessionMode().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        return sessionMode;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        sessionModeSearchRepository.deleteAll();
        assertThat(sessionModeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        sessionMode = createEntity(em);
    }

    @Test
    @Transactional
    void createSessionMode() throws Exception {
        int databaseSizeBeforeCreate = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        // Create the SessionMode
        restSessionModeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionMode)))
            .andExpect(status().isCreated());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SessionMode testSessionMode = sessionModeList.get(sessionModeList.size() - 1);
        assertThat(testSessionMode.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSessionMode.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createSessionModeWithExistingId() throws Exception {
        // Create the SessionMode with an existing ID
        sessionMode.setId(1L);

        int databaseSizeBeforeCreate = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSessionModeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionMode)))
            .andExpect(status().isBadRequest());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        // set the field null
        sessionMode.setTitle(null);

        // Create the SessionMode, which fails.

        restSessionModeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionMode)))
            .andExpect(status().isBadRequest());

        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSessionModes() throws Exception {
        // Initialize the database
        sessionModeRepository.saveAndFlush(sessionMode);

        // Get all the sessionModeList
        restSessionModeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionMode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getSessionMode() throws Exception {
        // Initialize the database
        sessionModeRepository.saveAndFlush(sessionMode);

        // Get the sessionMode
        restSessionModeMockMvc
            .perform(get(ENTITY_API_URL_ID, sessionMode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sessionMode.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingSessionMode() throws Exception {
        // Get the sessionMode
        restSessionModeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSessionMode() throws Exception {
        // Initialize the database
        sessionModeRepository.saveAndFlush(sessionMode);

        int databaseSizeBeforeUpdate = sessionModeRepository.findAll().size();
        sessionModeSearchRepository.save(sessionMode);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());

        // Update the sessionMode
        SessionMode updatedSessionMode = sessionModeRepository.findById(sessionMode.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSessionMode are not directly saved in db
        em.detach(updatedSessionMode);
        updatedSessionMode.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restSessionModeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSessionMode.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSessionMode))
            )
            .andExpect(status().isOk());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeUpdate);
        SessionMode testSessionMode = sessionModeList.get(sessionModeList.size() - 1);
        assertThat(testSessionMode.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSessionMode.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SessionMode> sessionModeSearchList = IterableUtils.toList(sessionModeSearchRepository.findAll());
                SessionMode testSessionModeSearch = sessionModeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSessionModeSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testSessionModeSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingSessionMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        sessionMode.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionModeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sessionMode.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSessionMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        sessionMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionModeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSessionMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        sessionMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionModeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionMode)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSessionModeWithPatch() throws Exception {
        // Initialize the database
        sessionModeRepository.saveAndFlush(sessionMode);

        int databaseSizeBeforeUpdate = sessionModeRepository.findAll().size();

        // Update the sessionMode using partial update
        SessionMode partialUpdatedSessionMode = new SessionMode();
        partialUpdatedSessionMode.setId(sessionMode.getId());

        restSessionModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionMode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionMode))
            )
            .andExpect(status().isOk());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeUpdate);
        SessionMode testSessionMode = sessionModeList.get(sessionModeList.size() - 1);
        assertThat(testSessionMode.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSessionMode.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateSessionModeWithPatch() throws Exception {
        // Initialize the database
        sessionModeRepository.saveAndFlush(sessionMode);

        int databaseSizeBeforeUpdate = sessionModeRepository.findAll().size();

        // Update the sessionMode using partial update
        SessionMode partialUpdatedSessionMode = new SessionMode();
        partialUpdatedSessionMode.setId(sessionMode.getId());

        partialUpdatedSessionMode.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restSessionModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionMode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionMode))
            )
            .andExpect(status().isOk());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeUpdate);
        SessionMode testSessionMode = sessionModeList.get(sessionModeList.size() - 1);
        assertThat(testSessionMode.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSessionMode.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingSessionMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        sessionMode.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sessionMode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSessionMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        sessionMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionModeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionMode))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSessionMode() throws Exception {
        int databaseSizeBeforeUpdate = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        sessionMode.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionModeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sessionMode))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionMode in the database
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSessionMode() throws Exception {
        // Initialize the database
        sessionModeRepository.saveAndFlush(sessionMode);
        sessionModeRepository.save(sessionMode);
        sessionModeSearchRepository.save(sessionMode);

        int databaseSizeBeforeDelete = sessionModeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the sessionMode
        restSessionModeMockMvc
            .perform(delete(ENTITY_API_URL_ID, sessionMode.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SessionMode> sessionModeList = sessionModeRepository.findAll();
        assertThat(sessionModeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionModeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSessionMode() throws Exception {
        // Initialize the database
        sessionMode = sessionModeRepository.saveAndFlush(sessionMode);
        sessionModeSearchRepository.save(sessionMode);

        // Search the sessionMode
        restSessionModeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sessionMode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionMode.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
