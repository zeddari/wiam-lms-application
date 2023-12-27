package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.SessionLink;
import com.wiam.lms.repository.SessionLinkRepository;
import com.wiam.lms.repository.search.SessionLinkSearchRepository;
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
 * Integration tests for the {@link SessionLinkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SessionLinkResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/session-links";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/session-links/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SessionLinkRepository sessionLinkRepository;

    @Autowired
    private SessionLinkSearchRepository sessionLinkSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionLinkMockMvc;

    private SessionLink sessionLink;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionLink createEntity(EntityManager em) {
        SessionLink sessionLink = new SessionLink().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION).link(DEFAULT_LINK);
        return sessionLink;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionLink createUpdatedEntity(EntityManager em) {
        SessionLink sessionLink = new SessionLink().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).link(UPDATED_LINK);
        return sessionLink;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        sessionLinkSearchRepository.deleteAll();
        assertThat(sessionLinkSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        sessionLink = createEntity(em);
    }

    @Test
    @Transactional
    void createSessionLink() throws Exception {
        int databaseSizeBeforeCreate = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        // Create the SessionLink
        restSessionLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionLink)))
            .andExpect(status().isCreated());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SessionLink testSessionLink = sessionLinkList.get(sessionLinkList.size() - 1);
        assertThat(testSessionLink.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSessionLink.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSessionLink.getLink()).isEqualTo(DEFAULT_LINK);
    }

    @Test
    @Transactional
    void createSessionLinkWithExistingId() throws Exception {
        // Create the SessionLink with an existing ID
        sessionLink.setId(1L);

        int databaseSizeBeforeCreate = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSessionLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionLink)))
            .andExpect(status().isBadRequest());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        // set the field null
        sessionLink.setTitle(null);

        // Create the SessionLink, which fails.

        restSessionLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionLink)))
            .andExpect(status().isBadRequest());

        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSessionLinks() throws Exception {
        // Initialize the database
        sessionLinkRepository.saveAndFlush(sessionLink);

        // Get all the sessionLinkList
        restSessionLinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)));
    }

    @Test
    @Transactional
    void getSessionLink() throws Exception {
        // Initialize the database
        sessionLinkRepository.saveAndFlush(sessionLink);

        // Get the sessionLink
        restSessionLinkMockMvc
            .perform(get(ENTITY_API_URL_ID, sessionLink.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sessionLink.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK));
    }

    @Test
    @Transactional
    void getNonExistingSessionLink() throws Exception {
        // Get the sessionLink
        restSessionLinkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSessionLink() throws Exception {
        // Initialize the database
        sessionLinkRepository.saveAndFlush(sessionLink);

        int databaseSizeBeforeUpdate = sessionLinkRepository.findAll().size();
        sessionLinkSearchRepository.save(sessionLink);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());

        // Update the sessionLink
        SessionLink updatedSessionLink = sessionLinkRepository.findById(sessionLink.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSessionLink are not directly saved in db
        em.detach(updatedSessionLink);
        updatedSessionLink.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).link(UPDATED_LINK);

        restSessionLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSessionLink.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSessionLink))
            )
            .andExpect(status().isOk());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeUpdate);
        SessionLink testSessionLink = sessionLinkList.get(sessionLinkList.size() - 1);
        assertThat(testSessionLink.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSessionLink.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSessionLink.getLink()).isEqualTo(UPDATED_LINK);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SessionLink> sessionLinkSearchList = IterableUtils.toList(sessionLinkSearchRepository.findAll());
                SessionLink testSessionLinkSearch = sessionLinkSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSessionLinkSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testSessionLinkSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testSessionLinkSearch.getLink()).isEqualTo(UPDATED_LINK);
            });
    }

    @Test
    @Transactional
    void putNonExistingSessionLink() throws Exception {
        int databaseSizeBeforeUpdate = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        sessionLink.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sessionLink.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionLink))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSessionLink() throws Exception {
        int databaseSizeBeforeUpdate = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        sessionLink.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionLink))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSessionLink() throws Exception {
        int databaseSizeBeforeUpdate = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        sessionLink.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionLinkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionLink)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSessionLinkWithPatch() throws Exception {
        // Initialize the database
        sessionLinkRepository.saveAndFlush(sessionLink);

        int databaseSizeBeforeUpdate = sessionLinkRepository.findAll().size();

        // Update the sessionLink using partial update
        SessionLink partialUpdatedSessionLink = new SessionLink();
        partialUpdatedSessionLink.setId(sessionLink.getId());

        restSessionLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionLink.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionLink))
            )
            .andExpect(status().isOk());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeUpdate);
        SessionLink testSessionLink = sessionLinkList.get(sessionLinkList.size() - 1);
        assertThat(testSessionLink.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSessionLink.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSessionLink.getLink()).isEqualTo(DEFAULT_LINK);
    }

    @Test
    @Transactional
    void fullUpdateSessionLinkWithPatch() throws Exception {
        // Initialize the database
        sessionLinkRepository.saveAndFlush(sessionLink);

        int databaseSizeBeforeUpdate = sessionLinkRepository.findAll().size();

        // Update the sessionLink using partial update
        SessionLink partialUpdatedSessionLink = new SessionLink();
        partialUpdatedSessionLink.setId(sessionLink.getId());

        partialUpdatedSessionLink.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).link(UPDATED_LINK);

        restSessionLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionLink.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionLink))
            )
            .andExpect(status().isOk());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeUpdate);
        SessionLink testSessionLink = sessionLinkList.get(sessionLinkList.size() - 1);
        assertThat(testSessionLink.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSessionLink.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSessionLink.getLink()).isEqualTo(UPDATED_LINK);
    }

    @Test
    @Transactional
    void patchNonExistingSessionLink() throws Exception {
        int databaseSizeBeforeUpdate = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        sessionLink.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sessionLink.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionLink))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSessionLink() throws Exception {
        int databaseSizeBeforeUpdate = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        sessionLink.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionLink))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSessionLink() throws Exception {
        int databaseSizeBeforeUpdate = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        sessionLink.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionLinkMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sessionLink))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionLink in the database
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSessionLink() throws Exception {
        // Initialize the database
        sessionLinkRepository.saveAndFlush(sessionLink);
        sessionLinkRepository.save(sessionLink);
        sessionLinkSearchRepository.save(sessionLink);

        int databaseSizeBeforeDelete = sessionLinkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the sessionLink
        restSessionLinkMockMvc
            .perform(delete(ENTITY_API_URL_ID, sessionLink.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SessionLink> sessionLinkList = sessionLinkRepository.findAll();
        assertThat(sessionLinkList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionLinkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSessionLink() throws Exception {
        // Initialize the database
        sessionLink = sessionLinkRepository.saveAndFlush(sessionLink);
        sessionLinkSearchRepository.save(sessionLink);

        // Search the sessionLink
        restSessionLinkMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sessionLink.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)));
    }
}
