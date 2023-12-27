package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.SessionProvider;
import com.wiam.lms.repository.SessionProviderRepository;
import com.wiam.lms.repository.search.SessionProviderSearchRepository;
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
 * Integration tests for the {@link SessionProviderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SessionProviderResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/session-providers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/session-providers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SessionProviderRepository sessionProviderRepository;

    @Autowired
    private SessionProviderSearchRepository sessionProviderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionProviderMockMvc;

    private SessionProvider sessionProvider;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionProvider createEntity(EntityManager em) {
        SessionProvider sessionProvider = new SessionProvider()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .website(DEFAULT_WEBSITE);
        return sessionProvider;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionProvider createUpdatedEntity(EntityManager em) {
        SessionProvider sessionProvider = new SessionProvider()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .website(UPDATED_WEBSITE);
        return sessionProvider;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        sessionProviderSearchRepository.deleteAll();
        assertThat(sessionProviderSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        sessionProvider = createEntity(em);
    }

    @Test
    @Transactional
    void createSessionProvider() throws Exception {
        int databaseSizeBeforeCreate = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        // Create the SessionProvider
        restSessionProviderMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionProvider))
            )
            .andExpect(status().isCreated());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SessionProvider testSessionProvider = sessionProviderList.get(sessionProviderList.size() - 1);
        assertThat(testSessionProvider.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSessionProvider.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSessionProvider.getWebsite()).isEqualTo(DEFAULT_WEBSITE);
    }

    @Test
    @Transactional
    void createSessionProviderWithExistingId() throws Exception {
        // Create the SessionProvider with an existing ID
        sessionProvider.setId(1L);

        int databaseSizeBeforeCreate = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSessionProviderMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        // set the field null
        sessionProvider.setName(null);

        // Create the SessionProvider, which fails.

        restSessionProviderMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionProvider))
            )
            .andExpect(status().isBadRequest());

        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSessionProviders() throws Exception {
        // Initialize the database
        sessionProviderRepository.saveAndFlush(sessionProvider);

        // Get all the sessionProviderList
        restSessionProviderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionProvider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)));
    }

    @Test
    @Transactional
    void getSessionProvider() throws Exception {
        // Initialize the database
        sessionProviderRepository.saveAndFlush(sessionProvider);

        // Get the sessionProvider
        restSessionProviderMockMvc
            .perform(get(ENTITY_API_URL_ID, sessionProvider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sessionProvider.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.website").value(DEFAULT_WEBSITE));
    }

    @Test
    @Transactional
    void getNonExistingSessionProvider() throws Exception {
        // Get the sessionProvider
        restSessionProviderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSessionProvider() throws Exception {
        // Initialize the database
        sessionProviderRepository.saveAndFlush(sessionProvider);

        int databaseSizeBeforeUpdate = sessionProviderRepository.findAll().size();
        sessionProviderSearchRepository.save(sessionProvider);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());

        // Update the sessionProvider
        SessionProvider updatedSessionProvider = sessionProviderRepository.findById(sessionProvider.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSessionProvider are not directly saved in db
        em.detach(updatedSessionProvider);
        updatedSessionProvider.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).website(UPDATED_WEBSITE);

        restSessionProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSessionProvider.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSessionProvider))
            )
            .andExpect(status().isOk());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeUpdate);
        SessionProvider testSessionProvider = sessionProviderList.get(sessionProviderList.size() - 1);
        assertThat(testSessionProvider.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSessionProvider.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSessionProvider.getWebsite()).isEqualTo(UPDATED_WEBSITE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SessionProvider> sessionProviderSearchList = IterableUtils.toList(sessionProviderSearchRepository.findAll());
                SessionProvider testSessionProviderSearch = sessionProviderSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSessionProviderSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testSessionProviderSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testSessionProviderSearch.getWebsite()).isEqualTo(UPDATED_WEBSITE);
            });
    }

    @Test
    @Transactional
    void putNonExistingSessionProvider() throws Exception {
        int databaseSizeBeforeUpdate = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        sessionProvider.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sessionProvider.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSessionProvider() throws Exception {
        int databaseSizeBeforeUpdate = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        sessionProvider.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSessionProvider() throws Exception {
        int databaseSizeBeforeUpdate = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        sessionProvider.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionProviderMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionProvider))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSessionProviderWithPatch() throws Exception {
        // Initialize the database
        sessionProviderRepository.saveAndFlush(sessionProvider);

        int databaseSizeBeforeUpdate = sessionProviderRepository.findAll().size();

        // Update the sessionProvider using partial update
        SessionProvider partialUpdatedSessionProvider = new SessionProvider();
        partialUpdatedSessionProvider.setId(sessionProvider.getId());

        partialUpdatedSessionProvider.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restSessionProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionProvider.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionProvider))
            )
            .andExpect(status().isOk());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeUpdate);
        SessionProvider testSessionProvider = sessionProviderList.get(sessionProviderList.size() - 1);
        assertThat(testSessionProvider.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSessionProvider.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSessionProvider.getWebsite()).isEqualTo(DEFAULT_WEBSITE);
    }

    @Test
    @Transactional
    void fullUpdateSessionProviderWithPatch() throws Exception {
        // Initialize the database
        sessionProviderRepository.saveAndFlush(sessionProvider);

        int databaseSizeBeforeUpdate = sessionProviderRepository.findAll().size();

        // Update the sessionProvider using partial update
        SessionProvider partialUpdatedSessionProvider = new SessionProvider();
        partialUpdatedSessionProvider.setId(sessionProvider.getId());

        partialUpdatedSessionProvider.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).website(UPDATED_WEBSITE);

        restSessionProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionProvider.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionProvider))
            )
            .andExpect(status().isOk());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeUpdate);
        SessionProvider testSessionProvider = sessionProviderList.get(sessionProviderList.size() - 1);
        assertThat(testSessionProvider.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSessionProvider.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSessionProvider.getWebsite()).isEqualTo(UPDATED_WEBSITE);
    }

    @Test
    @Transactional
    void patchNonExistingSessionProvider() throws Exception {
        int databaseSizeBeforeUpdate = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        sessionProvider.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sessionProvider.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSessionProvider() throws Exception {
        int databaseSizeBeforeUpdate = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        sessionProvider.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSessionProvider() throws Exception {
        int databaseSizeBeforeUpdate = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        sessionProvider.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionProviderMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionProvider))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionProvider in the database
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSessionProvider() throws Exception {
        // Initialize the database
        sessionProviderRepository.saveAndFlush(sessionProvider);
        sessionProviderRepository.save(sessionProvider);
        sessionProviderSearchRepository.save(sessionProvider);

        int databaseSizeBeforeDelete = sessionProviderRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the sessionProvider
        restSessionProviderMockMvc
            .perform(delete(ENTITY_API_URL_ID, sessionProvider.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SessionProvider> sessionProviderList = sessionProviderRepository.findAll();
        assertThat(sessionProviderList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionProviderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSessionProvider() throws Exception {
        // Initialize the database
        sessionProvider = sessionProviderRepository.saveAndFlush(sessionProvider);
        sessionProviderSearchRepository.save(sessionProvider);

        // Search the sessionProvider
        restSessionProviderMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sessionProvider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionProvider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)));
    }
}
