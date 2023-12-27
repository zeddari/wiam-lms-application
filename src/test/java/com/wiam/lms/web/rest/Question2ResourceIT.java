package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Question2;
import com.wiam.lms.domain.enumeration.QuestionType;
import com.wiam.lms.repository.Question2Repository;
import com.wiam.lms.repository.search.Question2SearchRepository;
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
 * Integration tests for the {@link Question2Resource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class Question2ResourceIT {

    private static final String DEFAULT_QUESTION_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION_TITLE = "BBBBBBBBBB";

    private static final QuestionType DEFAULT_QUESTION_TYPE = QuestionType.QUES_TYPE1;
    private static final QuestionType UPDATED_QUESTION_TYPE = QuestionType.QUES_TYPE2;

    private static final String DEFAULT_QUESTION_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUESTION_POINT = 1;
    private static final Integer UPDATED_QUESTION_POINT = 2;

    private static final String DEFAULT_QUESTION_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_QUESTION_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/question-2-s";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/question-2-s/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private Question2Repository question2Repository;

    @Autowired
    private Question2SearchRepository question2SearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuestion2MockMvc;

    private Question2 question2;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Question2 createEntity(EntityManager em) {
        Question2 question2 = new Question2()
            .questionTitle(DEFAULT_QUESTION_TITLE)
            .questionType(DEFAULT_QUESTION_TYPE)
            .questionDescription(DEFAULT_QUESTION_DESCRIPTION)
            .questionPoint(DEFAULT_QUESTION_POINT)
            .questionSubject(DEFAULT_QUESTION_SUBJECT)
            .questionStatus(DEFAULT_QUESTION_STATUS);
        return question2;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Question2 createUpdatedEntity(EntityManager em) {
        Question2 question2 = new Question2()
            .questionTitle(UPDATED_QUESTION_TITLE)
            .questionType(UPDATED_QUESTION_TYPE)
            .questionDescription(UPDATED_QUESTION_DESCRIPTION)
            .questionPoint(UPDATED_QUESTION_POINT)
            .questionSubject(UPDATED_QUESTION_SUBJECT)
            .questionStatus(UPDATED_QUESTION_STATUS);
        return question2;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        question2SearchRepository.deleteAll();
        assertThat(question2SearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        question2 = createEntity(em);
    }

    @Test
    @Transactional
    void createQuestion2() throws Exception {
        int databaseSizeBeforeCreate = question2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());
        // Create the Question2
        restQuestion2MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question2)))
            .andExpect(status().isCreated());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Question2 testQuestion2 = question2List.get(question2List.size() - 1);
        assertThat(testQuestion2.getQuestionTitle()).isEqualTo(DEFAULT_QUESTION_TITLE);
        assertThat(testQuestion2.getQuestionType()).isEqualTo(DEFAULT_QUESTION_TYPE);
        assertThat(testQuestion2.getQuestionDescription()).isEqualTo(DEFAULT_QUESTION_DESCRIPTION);
        assertThat(testQuestion2.getQuestionPoint()).isEqualTo(DEFAULT_QUESTION_POINT);
        assertThat(testQuestion2.getQuestionSubject()).isEqualTo(DEFAULT_QUESTION_SUBJECT);
        assertThat(testQuestion2.getQuestionStatus()).isEqualTo(DEFAULT_QUESTION_STATUS);
    }

    @Test
    @Transactional
    void createQuestion2WithExistingId() throws Exception {
        // Create the Question2 with an existing ID
        question2.setId(1L);

        int databaseSizeBeforeCreate = question2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestion2MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question2)))
            .andExpect(status().isBadRequest());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllQuestion2s() throws Exception {
        // Initialize the database
        question2Repository.saveAndFlush(question2);

        // Get all the question2List
        restQuestion2MockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question2.getId().intValue())))
            .andExpect(jsonPath("$.[*].questionTitle").value(hasItem(DEFAULT_QUESTION_TITLE)))
            .andExpect(jsonPath("$.[*].questionType").value(hasItem(DEFAULT_QUESTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].questionDescription").value(hasItem(DEFAULT_QUESTION_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].questionPoint").value(hasItem(DEFAULT_QUESTION_POINT)))
            .andExpect(jsonPath("$.[*].questionSubject").value(hasItem(DEFAULT_QUESTION_SUBJECT)))
            .andExpect(jsonPath("$.[*].questionStatus").value(hasItem(DEFAULT_QUESTION_STATUS)));
    }

    @Test
    @Transactional
    void getQuestion2() throws Exception {
        // Initialize the database
        question2Repository.saveAndFlush(question2);

        // Get the question2
        restQuestion2MockMvc
            .perform(get(ENTITY_API_URL_ID, question2.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(question2.getId().intValue()))
            .andExpect(jsonPath("$.questionTitle").value(DEFAULT_QUESTION_TITLE))
            .andExpect(jsonPath("$.questionType").value(DEFAULT_QUESTION_TYPE.toString()))
            .andExpect(jsonPath("$.questionDescription").value(DEFAULT_QUESTION_DESCRIPTION))
            .andExpect(jsonPath("$.questionPoint").value(DEFAULT_QUESTION_POINT))
            .andExpect(jsonPath("$.questionSubject").value(DEFAULT_QUESTION_SUBJECT))
            .andExpect(jsonPath("$.questionStatus").value(DEFAULT_QUESTION_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingQuestion2() throws Exception {
        // Get the question2
        restQuestion2MockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuestion2() throws Exception {
        // Initialize the database
        question2Repository.saveAndFlush(question2);

        int databaseSizeBeforeUpdate = question2Repository.findAll().size();
        question2SearchRepository.save(question2);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());

        // Update the question2
        Question2 updatedQuestion2 = question2Repository.findById(question2.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuestion2 are not directly saved in db
        em.detach(updatedQuestion2);
        updatedQuestion2
            .questionTitle(UPDATED_QUESTION_TITLE)
            .questionType(UPDATED_QUESTION_TYPE)
            .questionDescription(UPDATED_QUESTION_DESCRIPTION)
            .questionPoint(UPDATED_QUESTION_POINT)
            .questionSubject(UPDATED_QUESTION_SUBJECT)
            .questionStatus(UPDATED_QUESTION_STATUS);

        restQuestion2MockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuestion2.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedQuestion2))
            )
            .andExpect(status().isOk());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeUpdate);
        Question2 testQuestion2 = question2List.get(question2List.size() - 1);
        assertThat(testQuestion2.getQuestionTitle()).isEqualTo(UPDATED_QUESTION_TITLE);
        assertThat(testQuestion2.getQuestionType()).isEqualTo(UPDATED_QUESTION_TYPE);
        assertThat(testQuestion2.getQuestionDescription()).isEqualTo(UPDATED_QUESTION_DESCRIPTION);
        assertThat(testQuestion2.getQuestionPoint()).isEqualTo(UPDATED_QUESTION_POINT);
        assertThat(testQuestion2.getQuestionSubject()).isEqualTo(UPDATED_QUESTION_SUBJECT);
        assertThat(testQuestion2.getQuestionStatus()).isEqualTo(UPDATED_QUESTION_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Question2> question2SearchList = IterableUtils.toList(question2SearchRepository.findAll());
                Question2 testQuestion2Search = question2SearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testQuestion2Search.getQuestionTitle()).isEqualTo(UPDATED_QUESTION_TITLE);
                assertThat(testQuestion2Search.getQuestionType()).isEqualTo(UPDATED_QUESTION_TYPE);
                assertThat(testQuestion2Search.getQuestionDescription()).isEqualTo(UPDATED_QUESTION_DESCRIPTION);
                assertThat(testQuestion2Search.getQuestionPoint()).isEqualTo(UPDATED_QUESTION_POINT);
                assertThat(testQuestion2Search.getQuestionSubject()).isEqualTo(UPDATED_QUESTION_SUBJECT);
                assertThat(testQuestion2Search.getQuestionStatus()).isEqualTo(UPDATED_QUESTION_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingQuestion2() throws Exception {
        int databaseSizeBeforeUpdate = question2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());
        question2.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestion2MockMvc
            .perform(
                put(ENTITY_API_URL_ID, question2.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(question2))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuestion2() throws Exception {
        int databaseSizeBeforeUpdate = question2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());
        question2.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestion2MockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(question2))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuestion2() throws Exception {
        int databaseSizeBeforeUpdate = question2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());
        question2.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestion2MockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question2)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateQuestion2WithPatch() throws Exception {
        // Initialize the database
        question2Repository.saveAndFlush(question2);

        int databaseSizeBeforeUpdate = question2Repository.findAll().size();

        // Update the question2 using partial update
        Question2 partialUpdatedQuestion2 = new Question2();
        partialUpdatedQuestion2.setId(question2.getId());

        partialUpdatedQuestion2.questionTitle(UPDATED_QUESTION_TITLE);

        restQuestion2MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestion2.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestion2))
            )
            .andExpect(status().isOk());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeUpdate);
        Question2 testQuestion2 = question2List.get(question2List.size() - 1);
        assertThat(testQuestion2.getQuestionTitle()).isEqualTo(UPDATED_QUESTION_TITLE);
        assertThat(testQuestion2.getQuestionType()).isEqualTo(DEFAULT_QUESTION_TYPE);
        assertThat(testQuestion2.getQuestionDescription()).isEqualTo(DEFAULT_QUESTION_DESCRIPTION);
        assertThat(testQuestion2.getQuestionPoint()).isEqualTo(DEFAULT_QUESTION_POINT);
        assertThat(testQuestion2.getQuestionSubject()).isEqualTo(DEFAULT_QUESTION_SUBJECT);
        assertThat(testQuestion2.getQuestionStatus()).isEqualTo(DEFAULT_QUESTION_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateQuestion2WithPatch() throws Exception {
        // Initialize the database
        question2Repository.saveAndFlush(question2);

        int databaseSizeBeforeUpdate = question2Repository.findAll().size();

        // Update the question2 using partial update
        Question2 partialUpdatedQuestion2 = new Question2();
        partialUpdatedQuestion2.setId(question2.getId());

        partialUpdatedQuestion2
            .questionTitle(UPDATED_QUESTION_TITLE)
            .questionType(UPDATED_QUESTION_TYPE)
            .questionDescription(UPDATED_QUESTION_DESCRIPTION)
            .questionPoint(UPDATED_QUESTION_POINT)
            .questionSubject(UPDATED_QUESTION_SUBJECT)
            .questionStatus(UPDATED_QUESTION_STATUS);

        restQuestion2MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestion2.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestion2))
            )
            .andExpect(status().isOk());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeUpdate);
        Question2 testQuestion2 = question2List.get(question2List.size() - 1);
        assertThat(testQuestion2.getQuestionTitle()).isEqualTo(UPDATED_QUESTION_TITLE);
        assertThat(testQuestion2.getQuestionType()).isEqualTo(UPDATED_QUESTION_TYPE);
        assertThat(testQuestion2.getQuestionDescription()).isEqualTo(UPDATED_QUESTION_DESCRIPTION);
        assertThat(testQuestion2.getQuestionPoint()).isEqualTo(UPDATED_QUESTION_POINT);
        assertThat(testQuestion2.getQuestionSubject()).isEqualTo(UPDATED_QUESTION_SUBJECT);
        assertThat(testQuestion2.getQuestionStatus()).isEqualTo(UPDATED_QUESTION_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingQuestion2() throws Exception {
        int databaseSizeBeforeUpdate = question2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());
        question2.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestion2MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, question2.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(question2))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuestion2() throws Exception {
        int databaseSizeBeforeUpdate = question2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());
        question2.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestion2MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(question2))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuestion2() throws Exception {
        int databaseSizeBeforeUpdate = question2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());
        question2.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestion2MockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(question2))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Question2 in the database
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteQuestion2() throws Exception {
        // Initialize the database
        question2Repository.saveAndFlush(question2);
        question2Repository.save(question2);
        question2SearchRepository.save(question2);

        int databaseSizeBeforeDelete = question2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(question2SearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the question2
        restQuestion2MockMvc
            .perform(delete(ENTITY_API_URL_ID, question2.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Question2> question2List = question2Repository.findAll();
        assertThat(question2List).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(question2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchQuestion2() throws Exception {
        // Initialize the database
        question2 = question2Repository.saveAndFlush(question2);
        question2SearchRepository.save(question2);

        // Search the question2
        restQuestion2MockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + question2.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question2.getId().intValue())))
            .andExpect(jsonPath("$.[*].questionTitle").value(hasItem(DEFAULT_QUESTION_TITLE)))
            .andExpect(jsonPath("$.[*].questionType").value(hasItem(DEFAULT_QUESTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].questionDescription").value(hasItem(DEFAULT_QUESTION_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].questionPoint").value(hasItem(DEFAULT_QUESTION_POINT)))
            .andExpect(jsonPath("$.[*].questionSubject").value(hasItem(DEFAULT_QUESTION_SUBJECT)))
            .andExpect(jsonPath("$.[*].questionStatus").value(hasItem(DEFAULT_QUESTION_STATUS)));
    }
}
