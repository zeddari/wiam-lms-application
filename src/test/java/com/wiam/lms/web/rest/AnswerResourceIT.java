package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Answer;
import com.wiam.lms.repository.AnswerRepository;
import com.wiam.lms.repository.search.AnswerSearchRepository;
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
 * Integration tests for the {@link AnswerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AnswerResourceIT {

    private static final Boolean DEFAULT_A_1_V = false;
    private static final Boolean UPDATED_A_1_V = true;

    private static final Boolean DEFAULT_A_2_V = false;
    private static final Boolean UPDATED_A_2_V = true;

    private static final Boolean DEFAULT_A_3_V = false;
    private static final Boolean UPDATED_A_3_V = true;

    private static final Boolean DEFAULT_A_4_V = false;
    private static final Boolean UPDATED_A_4_V = true;

    private static final Boolean DEFAULT_RESULT = false;
    private static final Boolean UPDATED_RESULT = true;

    private static final String ENTITY_API_URL = "/api/answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/answers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AnswerSearchRepository answerSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAnswerMockMvc;

    private Answer answer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Answer createEntity(EntityManager em) {
        Answer answer = new Answer().a1v(DEFAULT_A_1_V).a2v(DEFAULT_A_2_V).a3v(DEFAULT_A_3_V).a4v(DEFAULT_A_4_V).result(DEFAULT_RESULT);
        return answer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Answer createUpdatedEntity(EntityManager em) {
        Answer answer = new Answer().a1v(UPDATED_A_1_V).a2v(UPDATED_A_2_V).a3v(UPDATED_A_3_V).a4v(UPDATED_A_4_V).result(UPDATED_RESULT);
        return answer;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        answerSearchRepository.deleteAll();
        assertThat(answerSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        answer = createEntity(em);
    }

    @Test
    @Transactional
    void createAnswer() throws Exception {
        int databaseSizeBeforeCreate = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        // Create the Answer
        restAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isCreated());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.geta1v()).isEqualTo(DEFAULT_A_1_V);
        assertThat(testAnswer.geta2v()).isEqualTo(DEFAULT_A_2_V);
        assertThat(testAnswer.geta3v()).isEqualTo(DEFAULT_A_3_V);
        assertThat(testAnswer.geta4v()).isEqualTo(DEFAULT_A_4_V);
        assertThat(testAnswer.getResult()).isEqualTo(DEFAULT_RESULT);
    }

    @Test
    @Transactional
    void createAnswerWithExistingId() throws Exception {
        // Create the Answer with an existing ID
        answer.setId(1L);

        int databaseSizeBeforeCreate = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checka1vIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        // set the field null
        answer.seta1v(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isBadRequest());

        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checka2vIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        // set the field null
        answer.seta2v(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isBadRequest());

        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkResultIsRequired() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        // set the field null
        answer.setResult(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isBadRequest());

        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAnswers() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answer.getId().intValue())))
            .andExpect(jsonPath("$.[*].a1v").value(hasItem(DEFAULT_A_1_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a2v").value(hasItem(DEFAULT_A_2_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a3v").value(hasItem(DEFAULT_A_3_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a4v").value(hasItem(DEFAULT_A_4_V.booleanValue())))
            .andExpect(jsonPath("$.[*].result").value(hasItem(DEFAULT_RESULT.booleanValue())));
    }

    @Test
    @Transactional
    void getAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get the answer
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL_ID, answer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(answer.getId().intValue()))
            .andExpect(jsonPath("$.a1v").value(DEFAULT_A_1_V.booleanValue()))
            .andExpect(jsonPath("$.a2v").value(DEFAULT_A_2_V.booleanValue()))
            .andExpect(jsonPath("$.a3v").value(DEFAULT_A_3_V.booleanValue()))
            .andExpect(jsonPath("$.a4v").value(DEFAULT_A_4_V.booleanValue()))
            .andExpect(jsonPath("$.result").value(DEFAULT_RESULT.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingAnswer() throws Exception {
        // Get the answer
        restAnswerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answerSearchRepository.save(answer);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());

        // Update the answer
        Answer updatedAnswer = answerRepository.findById(answer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAnswer are not directly saved in db
        em.detach(updatedAnswer);
        updatedAnswer.a1v(UPDATED_A_1_V).a2v(UPDATED_A_2_V).a3v(UPDATED_A_3_V).a4v(UPDATED_A_4_V).result(UPDATED_RESULT);

        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAnswer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.geta1v()).isEqualTo(UPDATED_A_1_V);
        assertThat(testAnswer.geta2v()).isEqualTo(UPDATED_A_2_V);
        assertThat(testAnswer.geta3v()).isEqualTo(UPDATED_A_3_V);
        assertThat(testAnswer.geta4v()).isEqualTo(UPDATED_A_4_V);
        assertThat(testAnswer.getResult()).isEqualTo(UPDATED_RESULT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Answer> answerSearchList = IterableUtils.toList(answerSearchRepository.findAll());
                Answer testAnswerSearch = answerSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testAnswerSearch.geta1v()).isEqualTo(UPDATED_A_1_V);
                assertThat(testAnswerSearch.geta2v()).isEqualTo(UPDATED_A_2_V);
                assertThat(testAnswerSearch.geta3v()).isEqualTo(UPDATED_A_3_V);
                assertThat(testAnswerSearch.geta4v()).isEqualTo(UPDATED_A_4_V);
                assertThat(testAnswerSearch.getResult()).isEqualTo(UPDATED_RESULT);
            });
    }

    @Test
    @Transactional
    void putNonExistingAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        answer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, answer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        answer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        answer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAnswerWithPatch() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Update the answer using partial update
        Answer partialUpdatedAnswer = new Answer();
        partialUpdatedAnswer.setId(answer.getId());

        partialUpdatedAnswer.result(UPDATED_RESULT);

        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.geta1v()).isEqualTo(DEFAULT_A_1_V);
        assertThat(testAnswer.geta2v()).isEqualTo(DEFAULT_A_2_V);
        assertThat(testAnswer.geta3v()).isEqualTo(DEFAULT_A_3_V);
        assertThat(testAnswer.geta4v()).isEqualTo(DEFAULT_A_4_V);
        assertThat(testAnswer.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    @Transactional
    void fullUpdateAnswerWithPatch() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Update the answer using partial update
        Answer partialUpdatedAnswer = new Answer();
        partialUpdatedAnswer.setId(answer.getId());

        partialUpdatedAnswer.a1v(UPDATED_A_1_V).a2v(UPDATED_A_2_V).a3v(UPDATED_A_3_V).a4v(UPDATED_A_4_V).result(UPDATED_RESULT);

        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.geta1v()).isEqualTo(UPDATED_A_1_V);
        assertThat(testAnswer.geta2v()).isEqualTo(UPDATED_A_2_V);
        assertThat(testAnswer.geta3v()).isEqualTo(UPDATED_A_3_V);
        assertThat(testAnswer.geta4v()).isEqualTo(UPDATED_A_4_V);
        assertThat(testAnswer.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    @Transactional
    void patchNonExistingAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        answer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, answer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        answer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        answer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(answer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);
        answerRepository.save(answer);
        answerSearchRepository.save(answer);

        int databaseSizeBeforeDelete = answerRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the answer
        restAnswerMockMvc
            .perform(delete(ENTITY_API_URL_ID, answer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(answerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAnswer() throws Exception {
        // Initialize the database
        answer = answerRepository.saveAndFlush(answer);
        answerSearchRepository.save(answer);

        // Search the answer
        restAnswerMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + answer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answer.getId().intValue())))
            .andExpect(jsonPath("$.[*].a1v").value(hasItem(DEFAULT_A_1_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a2v").value(hasItem(DEFAULT_A_2_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a3v").value(hasItem(DEFAULT_A_3_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a4v").value(hasItem(DEFAULT_A_4_V.booleanValue())))
            .andExpect(jsonPath("$.[*].result").value(hasItem(DEFAULT_RESULT.booleanValue())));
    }
}
