package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.QuizCertificate;
import com.wiam.lms.repository.QuizCertificateRepository;
import com.wiam.lms.repository.search.QuizCertificateSearchRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link QuizCertificateResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuizCertificateResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/quiz-certificates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/quiz-certificates/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private QuizCertificateRepository quizCertificateRepository;

    @Mock
    private QuizCertificateRepository quizCertificateRepositoryMock;

    @Autowired
    private QuizCertificateSearchRepository quizCertificateSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuizCertificateMockMvc;

    private QuizCertificate quizCertificate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuizCertificate createEntity(EntityManager em) {
        QuizCertificate quizCertificate = new QuizCertificate()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .isActive(DEFAULT_IS_ACTIVE);
        return quizCertificate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuizCertificate createUpdatedEntity(EntityManager em) {
        QuizCertificate quizCertificate = new QuizCertificate()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .isActive(UPDATED_IS_ACTIVE);
        return quizCertificate;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        quizCertificateSearchRepository.deleteAll();
        assertThat(quizCertificateSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        quizCertificate = createEntity(em);
    }

    @Test
    @Transactional
    void createQuizCertificate() throws Exception {
        int databaseSizeBeforeCreate = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        // Create the QuizCertificate
        restQuizCertificateMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isCreated());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        QuizCertificate testQuizCertificate = quizCertificateList.get(quizCertificateList.size() - 1);
        assertThat(testQuizCertificate.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testQuizCertificate.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testQuizCertificate.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    void createQuizCertificateWithExistingId() throws Exception {
        // Create the QuizCertificate with an existing ID
        quizCertificate.setId(1L);

        int databaseSizeBeforeCreate = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizCertificateMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        // set the field null
        quizCertificate.setTitle(null);

        // Create the QuizCertificate, which fails.

        restQuizCertificateMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isBadRequest());

        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        // set the field null
        quizCertificate.setDescription(null);

        // Create the QuizCertificate, which fails.

        restQuizCertificateMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isBadRequest());

        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        // set the field null
        quizCertificate.setIsActive(null);

        // Create the QuizCertificate, which fails.

        restQuizCertificateMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isBadRequest());

        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllQuizCertificates() throws Exception {
        // Initialize the database
        quizCertificateRepository.saveAndFlush(quizCertificate);

        // Get all the quizCertificateList
        restQuizCertificateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizCertificate.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizCertificatesWithEagerRelationshipsIsEnabled() throws Exception {
        when(quizCertificateRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizCertificateMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(quizCertificateRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizCertificatesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(quizCertificateRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizCertificateMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(quizCertificateRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuizCertificate() throws Exception {
        // Initialize the database
        quizCertificateRepository.saveAndFlush(quizCertificate);

        // Get the quizCertificate
        restQuizCertificateMockMvc
            .perform(get(ENTITY_API_URL_ID, quizCertificate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quizCertificate.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingQuizCertificate() throws Exception {
        // Get the quizCertificate
        restQuizCertificateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuizCertificate() throws Exception {
        // Initialize the database
        quizCertificateRepository.saveAndFlush(quizCertificate);

        int databaseSizeBeforeUpdate = quizCertificateRepository.findAll().size();
        quizCertificateSearchRepository.save(quizCertificate);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());

        // Update the quizCertificate
        QuizCertificate updatedQuizCertificate = quizCertificateRepository.findById(quizCertificate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuizCertificate are not directly saved in db
        em.detach(updatedQuizCertificate);
        updatedQuizCertificate.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).isActive(UPDATED_IS_ACTIVE);

        restQuizCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuizCertificate.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedQuizCertificate))
            )
            .andExpect(status().isOk());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeUpdate);
        QuizCertificate testQuizCertificate = quizCertificateList.get(quizCertificateList.size() - 1);
        assertThat(testQuizCertificate.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testQuizCertificate.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testQuizCertificate.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<QuizCertificate> quizCertificateSearchList = IterableUtils.toList(quizCertificateSearchRepository.findAll());
                QuizCertificate testQuizCertificateSearch = quizCertificateSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testQuizCertificateSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testQuizCertificateSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testQuizCertificateSearch.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
            });
    }

    @Test
    @Transactional
    void putNonExistingQuizCertificate() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        quizCertificate.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quizCertificate.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuizCertificate() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        quizCertificate.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuizCertificate() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        quizCertificate.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizCertificateMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateQuizCertificateWithPatch() throws Exception {
        // Initialize the database
        quizCertificateRepository.saveAndFlush(quizCertificate);

        int databaseSizeBeforeUpdate = quizCertificateRepository.findAll().size();

        // Update the quizCertificate using partial update
        QuizCertificate partialUpdatedQuizCertificate = new QuizCertificate();
        partialUpdatedQuizCertificate.setId(quizCertificate.getId());

        partialUpdatedQuizCertificate.isActive(UPDATED_IS_ACTIVE);

        restQuizCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizCertificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuizCertificate))
            )
            .andExpect(status().isOk());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeUpdate);
        QuizCertificate testQuizCertificate = quizCertificateList.get(quizCertificateList.size() - 1);
        assertThat(testQuizCertificate.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testQuizCertificate.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testQuizCertificate.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateQuizCertificateWithPatch() throws Exception {
        // Initialize the database
        quizCertificateRepository.saveAndFlush(quizCertificate);

        int databaseSizeBeforeUpdate = quizCertificateRepository.findAll().size();

        // Update the quizCertificate using partial update
        QuizCertificate partialUpdatedQuizCertificate = new QuizCertificate();
        partialUpdatedQuizCertificate.setId(quizCertificate.getId());

        partialUpdatedQuizCertificate.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).isActive(UPDATED_IS_ACTIVE);

        restQuizCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizCertificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuizCertificate))
            )
            .andExpect(status().isOk());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeUpdate);
        QuizCertificate testQuizCertificate = quizCertificateList.get(quizCertificateList.size() - 1);
        assertThat(testQuizCertificate.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testQuizCertificate.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testQuizCertificate.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingQuizCertificate() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        quizCertificate.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quizCertificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuizCertificate() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        quizCertificate.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuizCertificate() throws Exception {
        int databaseSizeBeforeUpdate = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        quizCertificate.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quizCertificate))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizCertificate in the database
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteQuizCertificate() throws Exception {
        // Initialize the database
        quizCertificateRepository.saveAndFlush(quizCertificate);
        quizCertificateRepository.save(quizCertificate);
        quizCertificateSearchRepository.save(quizCertificate);

        int databaseSizeBeforeDelete = quizCertificateRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the quizCertificate
        restQuizCertificateMockMvc
            .perform(delete(ENTITY_API_URL_ID, quizCertificate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<QuizCertificate> quizCertificateList = quizCertificateRepository.findAll();
        assertThat(quizCertificateList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizCertificateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchQuizCertificate() throws Exception {
        // Initialize the database
        quizCertificate = quizCertificateRepository.saveAndFlush(quizCertificate);
        quizCertificateSearchRepository.save(quizCertificate);

        // Search the quizCertificate
        restQuizCertificateMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + quizCertificate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizCertificate.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }
}
