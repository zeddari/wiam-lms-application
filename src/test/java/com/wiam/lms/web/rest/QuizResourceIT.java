package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Quiz;
import com.wiam.lms.domain.enumeration.QuizType;
import com.wiam.lms.repository.QuizRepository;
import com.wiam.lms.repository.search.QuizSearchRepository;
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
 * Integration tests for the {@link QuizResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuizResourceIT {

    private static final String DEFAULT_QUIZ_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_QUIZ_TITLE = "BBBBBBBBBB";

    private static final QuizType DEFAULT_QUIZ_TYPE = QuizType.QUIZ_TYPE1;
    private static final QuizType UPDATED_QUIZ_TYPE = QuizType.QUIZ_TYPE2;

    private static final String DEFAULT_QUIZ_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_QUIZ_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/quizzes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/quizzes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private QuizRepository quizRepository;

    @Mock
    private QuizRepository quizRepositoryMock;

    @Autowired
    private QuizSearchRepository quizSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuizMockMvc;

    private Quiz quiz;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quiz createEntity(EntityManager em) {
        Quiz quiz = new Quiz().quizTitle(DEFAULT_QUIZ_TITLE).quizType(DEFAULT_QUIZ_TYPE).quizDescription(DEFAULT_QUIZ_DESCRIPTION);
        return quiz;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quiz createUpdatedEntity(EntityManager em) {
        Quiz quiz = new Quiz().quizTitle(UPDATED_QUIZ_TITLE).quizType(UPDATED_QUIZ_TYPE).quizDescription(UPDATED_QUIZ_DESCRIPTION);
        return quiz;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        quizSearchRepository.deleteAll();
        assertThat(quizSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        quiz = createEntity(em);
    }

    @Test
    @Transactional
    void createQuiz() throws Exception {
        int databaseSizeBeforeCreate = quizRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());
        // Create the Quiz
        restQuizMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quiz)))
            .andExpect(status().isCreated());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Quiz testQuiz = quizList.get(quizList.size() - 1);
        assertThat(testQuiz.getQuizTitle()).isEqualTo(DEFAULT_QUIZ_TITLE);
        assertThat(testQuiz.getQuizType()).isEqualTo(DEFAULT_QUIZ_TYPE);
        assertThat(testQuiz.getQuizDescription()).isEqualTo(DEFAULT_QUIZ_DESCRIPTION);
    }

    @Test
    @Transactional
    void createQuizWithExistingId() throws Exception {
        // Create the Quiz with an existing ID
        quiz.setId(1L);

        int databaseSizeBeforeCreate = quizRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quiz)))
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllQuizzes() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizList
        restQuizMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quiz.getId().intValue())))
            .andExpect(jsonPath("$.[*].quizTitle").value(hasItem(DEFAULT_QUIZ_TITLE)))
            .andExpect(jsonPath("$.[*].quizType").value(hasItem(DEFAULT_QUIZ_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quizDescription").value(hasItem(DEFAULT_QUIZ_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizzesWithEagerRelationshipsIsEnabled() throws Exception {
        when(quizRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(quizRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizzesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(quizRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(quizRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuiz() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get the quiz
        restQuizMockMvc
            .perform(get(ENTITY_API_URL_ID, quiz.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quiz.getId().intValue()))
            .andExpect(jsonPath("$.quizTitle").value(DEFAULT_QUIZ_TITLE))
            .andExpect(jsonPath("$.quizType").value(DEFAULT_QUIZ_TYPE.toString()))
            .andExpect(jsonPath("$.quizDescription").value(DEFAULT_QUIZ_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingQuiz() throws Exception {
        // Get the quiz
        restQuizMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuiz() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        int databaseSizeBeforeUpdate = quizRepository.findAll().size();
        quizSearchRepository.save(quiz);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());

        // Update the quiz
        Quiz updatedQuiz = quizRepository.findById(quiz.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuiz are not directly saved in db
        em.detach(updatedQuiz);
        updatedQuiz.quizTitle(UPDATED_QUIZ_TITLE).quizType(UPDATED_QUIZ_TYPE).quizDescription(UPDATED_QUIZ_DESCRIPTION);

        restQuizMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuiz.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedQuiz))
            )
            .andExpect(status().isOk());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        Quiz testQuiz = quizList.get(quizList.size() - 1);
        assertThat(testQuiz.getQuizTitle()).isEqualTo(UPDATED_QUIZ_TITLE);
        assertThat(testQuiz.getQuizType()).isEqualTo(UPDATED_QUIZ_TYPE);
        assertThat(testQuiz.getQuizDescription()).isEqualTo(UPDATED_QUIZ_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Quiz> quizSearchList = IterableUtils.toList(quizSearchRepository.findAll());
                Quiz testQuizSearch = quizSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testQuizSearch.getQuizTitle()).isEqualTo(UPDATED_QUIZ_TITLE);
                assertThat(testQuizSearch.getQuizType()).isEqualTo(UPDATED_QUIZ_TYPE);
                assertThat(testQuizSearch.getQuizDescription()).isEqualTo(UPDATED_QUIZ_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingQuiz() throws Exception {
        int databaseSizeBeforeUpdate = quizRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());
        quiz.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quiz.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quiz))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuiz() throws Exception {
        int databaseSizeBeforeUpdate = quizRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());
        quiz.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quiz))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuiz() throws Exception {
        int databaseSizeBeforeUpdate = quizRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());
        quiz.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(quiz)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateQuizWithPatch() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        int databaseSizeBeforeUpdate = quizRepository.findAll().size();

        // Update the quiz using partial update
        Quiz partialUpdatedQuiz = new Quiz();
        partialUpdatedQuiz.setId(quiz.getId());

        restQuizMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuiz.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuiz))
            )
            .andExpect(status().isOk());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        Quiz testQuiz = quizList.get(quizList.size() - 1);
        assertThat(testQuiz.getQuizTitle()).isEqualTo(DEFAULT_QUIZ_TITLE);
        assertThat(testQuiz.getQuizType()).isEqualTo(DEFAULT_QUIZ_TYPE);
        assertThat(testQuiz.getQuizDescription()).isEqualTo(DEFAULT_QUIZ_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateQuizWithPatch() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        int databaseSizeBeforeUpdate = quizRepository.findAll().size();

        // Update the quiz using partial update
        Quiz partialUpdatedQuiz = new Quiz();
        partialUpdatedQuiz.setId(quiz.getId());

        partialUpdatedQuiz.quizTitle(UPDATED_QUIZ_TITLE).quizType(UPDATED_QUIZ_TYPE).quizDescription(UPDATED_QUIZ_DESCRIPTION);

        restQuizMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuiz.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuiz))
            )
            .andExpect(status().isOk());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        Quiz testQuiz = quizList.get(quizList.size() - 1);
        assertThat(testQuiz.getQuizTitle()).isEqualTo(UPDATED_QUIZ_TITLE);
        assertThat(testQuiz.getQuizType()).isEqualTo(UPDATED_QUIZ_TYPE);
        assertThat(testQuiz.getQuizDescription()).isEqualTo(UPDATED_QUIZ_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingQuiz() throws Exception {
        int databaseSizeBeforeUpdate = quizRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());
        quiz.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quiz.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quiz))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuiz() throws Exception {
        int databaseSizeBeforeUpdate = quizRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());
        quiz.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quiz))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuiz() throws Exception {
        int databaseSizeBeforeUpdate = quizRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());
        quiz.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(quiz)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quiz in the database
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteQuiz() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);
        quizRepository.save(quiz);
        quizSearchRepository.save(quiz);

        int databaseSizeBeforeDelete = quizRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(quizSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the quiz
        restQuizMockMvc
            .perform(delete(ENTITY_API_URL_ID, quiz.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Quiz> quizList = quizRepository.findAll();
        assertThat(quizList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(quizSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchQuiz() throws Exception {
        // Initialize the database
        quiz = quizRepository.saveAndFlush(quiz);
        quizSearchRepository.save(quiz);

        // Search the quiz
        restQuizMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + quiz.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quiz.getId().intValue())))
            .andExpect(jsonPath("$.[*].quizTitle").value(hasItem(DEFAULT_QUIZ_TITLE)))
            .andExpect(jsonPath("$.[*].quizType").value(hasItem(DEFAULT_QUIZ_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quizDescription").value(hasItem(DEFAULT_QUIZ_DESCRIPTION)));
    }
}
