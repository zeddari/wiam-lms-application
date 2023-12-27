package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.TicketSubjects;
import com.wiam.lms.repository.TicketSubjectsRepository;
import com.wiam.lms.repository.search.TicketSubjectsSearchRepository;
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
 * Integration tests for the {@link TicketSubjectsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketSubjectsResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ticket-subjects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/ticket-subjects/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TicketSubjectsRepository ticketSubjectsRepository;

    @Autowired
    private TicketSubjectsSearchRepository ticketSubjectsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketSubjectsMockMvc;

    private TicketSubjects ticketSubjects;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketSubjects createEntity(EntityManager em) {
        TicketSubjects ticketSubjects = new TicketSubjects().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
        return ticketSubjects;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketSubjects createUpdatedEntity(EntityManager em) {
        TicketSubjects ticketSubjects = new TicketSubjects().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        return ticketSubjects;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        ticketSubjectsSearchRepository.deleteAll();
        assertThat(ticketSubjectsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        ticketSubjects = createEntity(em);
    }

    @Test
    @Transactional
    void createTicketSubjects() throws Exception {
        int databaseSizeBeforeCreate = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        // Create the TicketSubjects
        restTicketSubjectsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketSubjects))
            )
            .andExpect(status().isCreated());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TicketSubjects testTicketSubjects = ticketSubjectsList.get(ticketSubjectsList.size() - 1);
        assertThat(testTicketSubjects.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTicketSubjects.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTicketSubjectsWithExistingId() throws Exception {
        // Create the TicketSubjects with an existing ID
        ticketSubjects.setId(1L);

        int databaseSizeBeforeCreate = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketSubjectsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketSubjects))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        // set the field null
        ticketSubjects.setTitle(null);

        // Create the TicketSubjects, which fails.

        restTicketSubjectsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketSubjects))
            )
            .andExpect(status().isBadRequest());

        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTicketSubjects() throws Exception {
        // Initialize the database
        ticketSubjectsRepository.saveAndFlush(ticketSubjects);

        // Get all the ticketSubjectsList
        restTicketSubjectsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketSubjects.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getTicketSubjects() throws Exception {
        // Initialize the database
        ticketSubjectsRepository.saveAndFlush(ticketSubjects);

        // Get the ticketSubjects
        restTicketSubjectsMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketSubjects.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketSubjects.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingTicketSubjects() throws Exception {
        // Get the ticketSubjects
        restTicketSubjectsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketSubjects() throws Exception {
        // Initialize the database
        ticketSubjectsRepository.saveAndFlush(ticketSubjects);

        int databaseSizeBeforeUpdate = ticketSubjectsRepository.findAll().size();
        ticketSubjectsSearchRepository.save(ticketSubjects);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());

        // Update the ticketSubjects
        TicketSubjects updatedTicketSubjects = ticketSubjectsRepository.findById(ticketSubjects.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicketSubjects are not directly saved in db
        em.detach(updatedTicketSubjects);
        updatedTicketSubjects.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restTicketSubjectsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTicketSubjects.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTicketSubjects))
            )
            .andExpect(status().isOk());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeUpdate);
        TicketSubjects testTicketSubjects = ticketSubjectsList.get(ticketSubjectsList.size() - 1);
        assertThat(testTicketSubjects.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTicketSubjects.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TicketSubjects> ticketSubjectsSearchList = IterableUtils.toList(ticketSubjectsSearchRepository.findAll());
                TicketSubjects testTicketSubjectsSearch = ticketSubjectsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTicketSubjectsSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testTicketSubjectsSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingTicketSubjects() throws Exception {
        int databaseSizeBeforeUpdate = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        ticketSubjects.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketSubjectsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketSubjects.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketSubjects))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketSubjects() throws Exception {
        int databaseSizeBeforeUpdate = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        ticketSubjects.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketSubjectsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketSubjects))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketSubjects() throws Exception {
        int databaseSizeBeforeUpdate = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        ticketSubjects.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketSubjectsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketSubjects)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTicketSubjectsWithPatch() throws Exception {
        // Initialize the database
        ticketSubjectsRepository.saveAndFlush(ticketSubjects);

        int databaseSizeBeforeUpdate = ticketSubjectsRepository.findAll().size();

        // Update the ticketSubjects using partial update
        TicketSubjects partialUpdatedTicketSubjects = new TicketSubjects();
        partialUpdatedTicketSubjects.setId(ticketSubjects.getId());

        partialUpdatedTicketSubjects.description(UPDATED_DESCRIPTION);

        restTicketSubjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketSubjects.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTicketSubjects))
            )
            .andExpect(status().isOk());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeUpdate);
        TicketSubjects testTicketSubjects = ticketSubjectsList.get(ticketSubjectsList.size() - 1);
        assertThat(testTicketSubjects.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTicketSubjects.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTicketSubjectsWithPatch() throws Exception {
        // Initialize the database
        ticketSubjectsRepository.saveAndFlush(ticketSubjects);

        int databaseSizeBeforeUpdate = ticketSubjectsRepository.findAll().size();

        // Update the ticketSubjects using partial update
        TicketSubjects partialUpdatedTicketSubjects = new TicketSubjects();
        partialUpdatedTicketSubjects.setId(ticketSubjects.getId());

        partialUpdatedTicketSubjects.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restTicketSubjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketSubjects.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTicketSubjects))
            )
            .andExpect(status().isOk());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeUpdate);
        TicketSubjects testTicketSubjects = ticketSubjectsList.get(ticketSubjectsList.size() - 1);
        assertThat(testTicketSubjects.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTicketSubjects.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTicketSubjects() throws Exception {
        int databaseSizeBeforeUpdate = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        ticketSubjects.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketSubjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketSubjects.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketSubjects))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketSubjects() throws Exception {
        int databaseSizeBeforeUpdate = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        ticketSubjects.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketSubjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketSubjects))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketSubjects() throws Exception {
        int databaseSizeBeforeUpdate = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        ticketSubjects.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketSubjectsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ticketSubjects))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketSubjects in the database
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTicketSubjects() throws Exception {
        // Initialize the database
        ticketSubjectsRepository.saveAndFlush(ticketSubjects);
        ticketSubjectsRepository.save(ticketSubjects);
        ticketSubjectsSearchRepository.save(ticketSubjects);

        int databaseSizeBeforeDelete = ticketSubjectsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ticketSubjects
        restTicketSubjectsMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketSubjects.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TicketSubjects> ticketSubjectsList = ticketSubjectsRepository.findAll();
        assertThat(ticketSubjectsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSubjectsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTicketSubjects() throws Exception {
        // Initialize the database
        ticketSubjects = ticketSubjectsRepository.saveAndFlush(ticketSubjects);
        ticketSubjectsSearchRepository.save(ticketSubjects);

        // Search the ticketSubjects
        restTicketSubjectsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ticketSubjects.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketSubjects.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
