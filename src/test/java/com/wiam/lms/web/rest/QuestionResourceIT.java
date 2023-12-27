package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Question;
import com.wiam.lms.domain.enumeration.QuestionType;
import com.wiam.lms.repository.QuestionRepository;
import com.wiam.lms.repository.search.QuestionSearchRepository;
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
 * Integration tests for the {@link QuestionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QuestionResourceIT {

    private static final String DEFAULT_QUESTION = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String DEFAULT_A_1 = "AAAAAAAAAA";
    private static final String UPDATED_A_1 = "BBBBBBBBBB";

    private static final Boolean DEFAULT_A_1_V = false;
    private static final Boolean UPDATED_A_1_V = true;

    private static final String DEFAULT_A_2 = "AAAAAAAAAA";
    private static final String UPDATED_A_2 = "BBBBBBBBBB";

    private static final Boolean DEFAULT_A_2_V = false;
    private static final Boolean UPDATED_A_2_V = true;

    private static final String DEFAULT_A_3 = "AAAAAAAAAA";
    private static final String UPDATED_A_3 = "BBBBBBBBBB";

    private static final Boolean DEFAULT_A_3_V = false;
    private static final Boolean UPDATED_A_3_V = true;

    private static final String DEFAULT_A_4 = "AAAAAAAAAA";
    private static final String UPDATED_A_4 = "BBBBBBBBBB";

    private static final Boolean DEFAULT_A_4_V = false;
    private static final Boolean UPDATED_A_4_V = true;

    private static final Boolean DEFAULT_ISACTIVE = false;
    private static final Boolean UPDATED_ISACTIVE = true;

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

    private static final String ENTITY_API_URL = "/api/questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/questions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionSearchRepository questionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuestionMockMvc;

    private Question question;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Question createEntity(EntityManager em) {
        Question question = new Question()
            .question(DEFAULT_QUESTION)
            .note(DEFAULT_NOTE)
            .a1(DEFAULT_A_1)
            .a1v(DEFAULT_A_1_V)
            .a2(DEFAULT_A_2)
            .a2v(DEFAULT_A_2_V)
            .a3(DEFAULT_A_3)
            .a3v(DEFAULT_A_3_V)
            .a4(DEFAULT_A_4)
            .a4v(DEFAULT_A_4_V)
            .isactive(DEFAULT_ISACTIVE)
            .questionTitle(DEFAULT_QUESTION_TITLE)
            .questionType(DEFAULT_QUESTION_TYPE)
            .questionDescription(DEFAULT_QUESTION_DESCRIPTION)
            .questionPoint(DEFAULT_QUESTION_POINT)
            .questionSubject(DEFAULT_QUESTION_SUBJECT)
            .questionStatus(DEFAULT_QUESTION_STATUS);
        return question;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Question createUpdatedEntity(EntityManager em) {
        Question question = new Question()
            .question(UPDATED_QUESTION)
            .note(UPDATED_NOTE)
            .a1(UPDATED_A_1)
            .a1v(UPDATED_A_1_V)
            .a2(UPDATED_A_2)
            .a2v(UPDATED_A_2_V)
            .a3(UPDATED_A_3)
            .a3v(UPDATED_A_3_V)
            .a4(UPDATED_A_4)
            .a4v(UPDATED_A_4_V)
            .isactive(UPDATED_ISACTIVE)
            .questionTitle(UPDATED_QUESTION_TITLE)
            .questionType(UPDATED_QUESTION_TYPE)
            .questionDescription(UPDATED_QUESTION_DESCRIPTION)
            .questionPoint(UPDATED_QUESTION_POINT)
            .questionSubject(UPDATED_QUESTION_SUBJECT)
            .questionStatus(UPDATED_QUESTION_STATUS);
        return question;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        questionSearchRepository.deleteAll();
        assertThat(questionSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        question = createEntity(em);
    }

    @Test
    @Transactional
    void createQuestion() throws Exception {
        int databaseSizeBeforeCreate = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        // Create the Question
        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isCreated());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Question testQuestion = questionList.get(questionList.size() - 1);
        assertThat(testQuestion.getQuestion()).isEqualTo(DEFAULT_QUESTION);
        assertThat(testQuestion.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testQuestion.geta1()).isEqualTo(DEFAULT_A_1);
        assertThat(testQuestion.geta1v()).isEqualTo(DEFAULT_A_1_V);
        assertThat(testQuestion.geta2()).isEqualTo(DEFAULT_A_2);
        assertThat(testQuestion.geta2v()).isEqualTo(DEFAULT_A_2_V);
        assertThat(testQuestion.geta3()).isEqualTo(DEFAULT_A_3);
        assertThat(testQuestion.geta3v()).isEqualTo(DEFAULT_A_3_V);
        assertThat(testQuestion.geta4()).isEqualTo(DEFAULT_A_4);
        assertThat(testQuestion.geta4v()).isEqualTo(DEFAULT_A_4_V);
        assertThat(testQuestion.getIsactive()).isEqualTo(DEFAULT_ISACTIVE);
        assertThat(testQuestion.getQuestionTitle()).isEqualTo(DEFAULT_QUESTION_TITLE);
        assertThat(testQuestion.getQuestionType()).isEqualTo(DEFAULT_QUESTION_TYPE);
        assertThat(testQuestion.getQuestionDescription()).isEqualTo(DEFAULT_QUESTION_DESCRIPTION);
        assertThat(testQuestion.getQuestionPoint()).isEqualTo(DEFAULT_QUESTION_POINT);
        assertThat(testQuestion.getQuestionSubject()).isEqualTo(DEFAULT_QUESTION_SUBJECT);
        assertThat(testQuestion.getQuestionStatus()).isEqualTo(DEFAULT_QUESTION_STATUS);
    }

    @Test
    @Transactional
    void createQuestionWithExistingId() throws Exception {
        // Create the Question with an existing ID
        question.setId(1L);

        int databaseSizeBeforeCreate = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuestionIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        // set the field null
        question.setQuestion(null);

        // Create the Question, which fails.

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isBadRequest());

        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checka1IsRequired() throws Exception {
        int databaseSizeBeforeTest = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        // set the field null
        question.seta1(null);

        // Create the Question, which fails.

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isBadRequest());

        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checka1vIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        // set the field null
        question.seta1v(null);

        // Create the Question, which fails.

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isBadRequest());

        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checka2IsRequired() throws Exception {
        int databaseSizeBeforeTest = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        // set the field null
        question.seta2(null);

        // Create the Question, which fails.

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isBadRequest());

        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checka2vIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        // set the field null
        question.seta2v(null);

        // Create the Question, which fails.

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isBadRequest());

        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsactiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        // set the field null
        question.setIsactive(null);

        // Create the Question, which fails.

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isBadRequest());

        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllQuestions() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        // Get all the questionList
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId().intValue())))
            .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].a1").value(hasItem(DEFAULT_A_1)))
            .andExpect(jsonPath("$.[*].a1v").value(hasItem(DEFAULT_A_1_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a2").value(hasItem(DEFAULT_A_2)))
            .andExpect(jsonPath("$.[*].a2v").value(hasItem(DEFAULT_A_2_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a3").value(hasItem(DEFAULT_A_3)))
            .andExpect(jsonPath("$.[*].a3v").value(hasItem(DEFAULT_A_3_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a4").value(hasItem(DEFAULT_A_4)))
            .andExpect(jsonPath("$.[*].a4v").value(hasItem(DEFAULT_A_4_V.booleanValue())))
            .andExpect(jsonPath("$.[*].isactive").value(hasItem(DEFAULT_ISACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].questionTitle").value(hasItem(DEFAULT_QUESTION_TITLE)))
            .andExpect(jsonPath("$.[*].questionType").value(hasItem(DEFAULT_QUESTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].questionDescription").value(hasItem(DEFAULT_QUESTION_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].questionPoint").value(hasItem(DEFAULT_QUESTION_POINT)))
            .andExpect(jsonPath("$.[*].questionSubject").value(hasItem(DEFAULT_QUESTION_SUBJECT)))
            .andExpect(jsonPath("$.[*].questionStatus").value(hasItem(DEFAULT_QUESTION_STATUS)));
    }

    @Test
    @Transactional
    void getQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        // Get the question
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL_ID, question.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(question.getId().intValue()))
            .andExpect(jsonPath("$.question").value(DEFAULT_QUESTION))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.a1").value(DEFAULT_A_1))
            .andExpect(jsonPath("$.a1v").value(DEFAULT_A_1_V.booleanValue()))
            .andExpect(jsonPath("$.a2").value(DEFAULT_A_2))
            .andExpect(jsonPath("$.a2v").value(DEFAULT_A_2_V.booleanValue()))
            .andExpect(jsonPath("$.a3").value(DEFAULT_A_3))
            .andExpect(jsonPath("$.a3v").value(DEFAULT_A_3_V.booleanValue()))
            .andExpect(jsonPath("$.a4").value(DEFAULT_A_4))
            .andExpect(jsonPath("$.a4v").value(DEFAULT_A_4_V.booleanValue()))
            .andExpect(jsonPath("$.isactive").value(DEFAULT_ISACTIVE.booleanValue()))
            .andExpect(jsonPath("$.questionTitle").value(DEFAULT_QUESTION_TITLE))
            .andExpect(jsonPath("$.questionType").value(DEFAULT_QUESTION_TYPE.toString()))
            .andExpect(jsonPath("$.questionDescription").value(DEFAULT_QUESTION_DESCRIPTION))
            .andExpect(jsonPath("$.questionPoint").value(DEFAULT_QUESTION_POINT))
            .andExpect(jsonPath("$.questionSubject").value(DEFAULT_QUESTION_SUBJECT))
            .andExpect(jsonPath("$.questionStatus").value(DEFAULT_QUESTION_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingQuestion() throws Exception {
        // Get the question
        restQuestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        int databaseSizeBeforeUpdate = questionRepository.findAll().size();
        questionSearchRepository.save(question);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());

        // Update the question
        Question updatedQuestion = questionRepository.findById(question.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuestion are not directly saved in db
        em.detach(updatedQuestion);
        updatedQuestion
            .question(UPDATED_QUESTION)
            .note(UPDATED_NOTE)
            .a1(UPDATED_A_1)
            .a1v(UPDATED_A_1_V)
            .a2(UPDATED_A_2)
            .a2v(UPDATED_A_2_V)
            .a3(UPDATED_A_3)
            .a3v(UPDATED_A_3_V)
            .a4(UPDATED_A_4)
            .a4v(UPDATED_A_4_V)
            .isactive(UPDATED_ISACTIVE)
            .questionTitle(UPDATED_QUESTION_TITLE)
            .questionType(UPDATED_QUESTION_TYPE)
            .questionDescription(UPDATED_QUESTION_DESCRIPTION)
            .questionPoint(UPDATED_QUESTION_POINT)
            .questionSubject(UPDATED_QUESTION_SUBJECT)
            .questionStatus(UPDATED_QUESTION_STATUS);

        restQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuestion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedQuestion))
            )
            .andExpect(status().isOk());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        Question testQuestion = questionList.get(questionList.size() - 1);
        assertThat(testQuestion.getQuestion()).isEqualTo(UPDATED_QUESTION);
        assertThat(testQuestion.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testQuestion.geta1()).isEqualTo(UPDATED_A_1);
        assertThat(testQuestion.geta1v()).isEqualTo(UPDATED_A_1_V);
        assertThat(testQuestion.geta2()).isEqualTo(UPDATED_A_2);
        assertThat(testQuestion.geta2v()).isEqualTo(UPDATED_A_2_V);
        assertThat(testQuestion.geta3()).isEqualTo(UPDATED_A_3);
        assertThat(testQuestion.geta3v()).isEqualTo(UPDATED_A_3_V);
        assertThat(testQuestion.geta4()).isEqualTo(UPDATED_A_4);
        assertThat(testQuestion.geta4v()).isEqualTo(UPDATED_A_4_V);
        assertThat(testQuestion.getIsactive()).isEqualTo(UPDATED_ISACTIVE);
        assertThat(testQuestion.getQuestionTitle()).isEqualTo(UPDATED_QUESTION_TITLE);
        assertThat(testQuestion.getQuestionType()).isEqualTo(UPDATED_QUESTION_TYPE);
        assertThat(testQuestion.getQuestionDescription()).isEqualTo(UPDATED_QUESTION_DESCRIPTION);
        assertThat(testQuestion.getQuestionPoint()).isEqualTo(UPDATED_QUESTION_POINT);
        assertThat(testQuestion.getQuestionSubject()).isEqualTo(UPDATED_QUESTION_SUBJECT);
        assertThat(testQuestion.getQuestionStatus()).isEqualTo(UPDATED_QUESTION_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Question> questionSearchList = IterableUtils.toList(questionSearchRepository.findAll());
                Question testQuestionSearch = questionSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testQuestionSearch.getQuestion()).isEqualTo(UPDATED_QUESTION);
                assertThat(testQuestionSearch.getNote()).isEqualTo(UPDATED_NOTE);
                assertThat(testQuestionSearch.geta1()).isEqualTo(UPDATED_A_1);
                assertThat(testQuestionSearch.geta1v()).isEqualTo(UPDATED_A_1_V);
                assertThat(testQuestionSearch.geta2()).isEqualTo(UPDATED_A_2);
                assertThat(testQuestionSearch.geta2v()).isEqualTo(UPDATED_A_2_V);
                assertThat(testQuestionSearch.geta3()).isEqualTo(UPDATED_A_3);
                assertThat(testQuestionSearch.geta3v()).isEqualTo(UPDATED_A_3_V);
                assertThat(testQuestionSearch.geta4()).isEqualTo(UPDATED_A_4);
                assertThat(testQuestionSearch.geta4v()).isEqualTo(UPDATED_A_4_V);
                assertThat(testQuestionSearch.getIsactive()).isEqualTo(UPDATED_ISACTIVE);
                assertThat(testQuestionSearch.getQuestionTitle()).isEqualTo(UPDATED_QUESTION_TITLE);
                assertThat(testQuestionSearch.getQuestionType()).isEqualTo(UPDATED_QUESTION_TYPE);
                assertThat(testQuestionSearch.getQuestionDescription()).isEqualTo(UPDATED_QUESTION_DESCRIPTION);
                assertThat(testQuestionSearch.getQuestionPoint()).isEqualTo(UPDATED_QUESTION_POINT);
                assertThat(testQuestionSearch.getQuestionSubject()).isEqualTo(UPDATED_QUESTION_SUBJECT);
                assertThat(testQuestionSearch.getQuestionStatus()).isEqualTo(UPDATED_QUESTION_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingQuestion() throws Exception {
        int databaseSizeBeforeUpdate = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        question.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, question.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(question))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuestion() throws Exception {
        int databaseSizeBeforeUpdate = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        question.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(question))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuestion() throws Exception {
        int databaseSizeBeforeUpdate = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        question.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateQuestionWithPatch() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        int databaseSizeBeforeUpdate = questionRepository.findAll().size();

        // Update the question using partial update
        Question partialUpdatedQuestion = new Question();
        partialUpdatedQuestion.setId(question.getId());

        partialUpdatedQuestion
            .note(UPDATED_NOTE)
            .a1v(UPDATED_A_1_V)
            .a2v(UPDATED_A_2_V)
            .a3v(UPDATED_A_3_V)
            .questionTitle(UPDATED_QUESTION_TITLE)
            .questionPoint(UPDATED_QUESTION_POINT)
            .questionSubject(UPDATED_QUESTION_SUBJECT);

        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestion))
            )
            .andExpect(status().isOk());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        Question testQuestion = questionList.get(questionList.size() - 1);
        assertThat(testQuestion.getQuestion()).isEqualTo(DEFAULT_QUESTION);
        assertThat(testQuestion.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testQuestion.geta1()).isEqualTo(DEFAULT_A_1);
        assertThat(testQuestion.geta1v()).isEqualTo(UPDATED_A_1_V);
        assertThat(testQuestion.geta2()).isEqualTo(DEFAULT_A_2);
        assertThat(testQuestion.geta2v()).isEqualTo(UPDATED_A_2_V);
        assertThat(testQuestion.geta3()).isEqualTo(DEFAULT_A_3);
        assertThat(testQuestion.geta3v()).isEqualTo(UPDATED_A_3_V);
        assertThat(testQuestion.geta4()).isEqualTo(DEFAULT_A_4);
        assertThat(testQuestion.geta4v()).isEqualTo(DEFAULT_A_4_V);
        assertThat(testQuestion.getIsactive()).isEqualTo(DEFAULT_ISACTIVE);
        assertThat(testQuestion.getQuestionTitle()).isEqualTo(UPDATED_QUESTION_TITLE);
        assertThat(testQuestion.getQuestionType()).isEqualTo(DEFAULT_QUESTION_TYPE);
        assertThat(testQuestion.getQuestionDescription()).isEqualTo(DEFAULT_QUESTION_DESCRIPTION);
        assertThat(testQuestion.getQuestionPoint()).isEqualTo(UPDATED_QUESTION_POINT);
        assertThat(testQuestion.getQuestionSubject()).isEqualTo(UPDATED_QUESTION_SUBJECT);
        assertThat(testQuestion.getQuestionStatus()).isEqualTo(DEFAULT_QUESTION_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateQuestionWithPatch() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        int databaseSizeBeforeUpdate = questionRepository.findAll().size();

        // Update the question using partial update
        Question partialUpdatedQuestion = new Question();
        partialUpdatedQuestion.setId(question.getId());

        partialUpdatedQuestion
            .question(UPDATED_QUESTION)
            .note(UPDATED_NOTE)
            .a1(UPDATED_A_1)
            .a1v(UPDATED_A_1_V)
            .a2(UPDATED_A_2)
            .a2v(UPDATED_A_2_V)
            .a3(UPDATED_A_3)
            .a3v(UPDATED_A_3_V)
            .a4(UPDATED_A_4)
            .a4v(UPDATED_A_4_V)
            .isactive(UPDATED_ISACTIVE)
            .questionTitle(UPDATED_QUESTION_TITLE)
            .questionType(UPDATED_QUESTION_TYPE)
            .questionDescription(UPDATED_QUESTION_DESCRIPTION)
            .questionPoint(UPDATED_QUESTION_POINT)
            .questionSubject(UPDATED_QUESTION_SUBJECT)
            .questionStatus(UPDATED_QUESTION_STATUS);

        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestion))
            )
            .andExpect(status().isOk());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        Question testQuestion = questionList.get(questionList.size() - 1);
        assertThat(testQuestion.getQuestion()).isEqualTo(UPDATED_QUESTION);
        assertThat(testQuestion.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testQuestion.geta1()).isEqualTo(UPDATED_A_1);
        assertThat(testQuestion.geta1v()).isEqualTo(UPDATED_A_1_V);
        assertThat(testQuestion.geta2()).isEqualTo(UPDATED_A_2);
        assertThat(testQuestion.geta2v()).isEqualTo(UPDATED_A_2_V);
        assertThat(testQuestion.geta3()).isEqualTo(UPDATED_A_3);
        assertThat(testQuestion.geta3v()).isEqualTo(UPDATED_A_3_V);
        assertThat(testQuestion.geta4()).isEqualTo(UPDATED_A_4);
        assertThat(testQuestion.geta4v()).isEqualTo(UPDATED_A_4_V);
        assertThat(testQuestion.getIsactive()).isEqualTo(UPDATED_ISACTIVE);
        assertThat(testQuestion.getQuestionTitle()).isEqualTo(UPDATED_QUESTION_TITLE);
        assertThat(testQuestion.getQuestionType()).isEqualTo(UPDATED_QUESTION_TYPE);
        assertThat(testQuestion.getQuestionDescription()).isEqualTo(UPDATED_QUESTION_DESCRIPTION);
        assertThat(testQuestion.getQuestionPoint()).isEqualTo(UPDATED_QUESTION_POINT);
        assertThat(testQuestion.getQuestionSubject()).isEqualTo(UPDATED_QUESTION_SUBJECT);
        assertThat(testQuestion.getQuestionStatus()).isEqualTo(UPDATED_QUESTION_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingQuestion() throws Exception {
        int databaseSizeBeforeUpdate = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        question.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, question.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(question))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuestion() throws Exception {
        int databaseSizeBeforeUpdate = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        question.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(question))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuestion() throws Exception {
        int databaseSizeBeforeUpdate = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        question.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(question)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);
        questionRepository.save(question);
        questionSearchRepository.save(question);

        int databaseSizeBeforeDelete = questionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the question
        restQuestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, question.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(questionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchQuestion() throws Exception {
        // Initialize the database
        question = questionRepository.saveAndFlush(question);
        questionSearchRepository.save(question);

        // Search the question
        restQuestionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + question.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId().intValue())))
            .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].a1").value(hasItem(DEFAULT_A_1)))
            .andExpect(jsonPath("$.[*].a1v").value(hasItem(DEFAULT_A_1_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a2").value(hasItem(DEFAULT_A_2)))
            .andExpect(jsonPath("$.[*].a2v").value(hasItem(DEFAULT_A_2_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a3").value(hasItem(DEFAULT_A_3)))
            .andExpect(jsonPath("$.[*].a3v").value(hasItem(DEFAULT_A_3_V.booleanValue())))
            .andExpect(jsonPath("$.[*].a4").value(hasItem(DEFAULT_A_4)))
            .andExpect(jsonPath("$.[*].a4v").value(hasItem(DEFAULT_A_4_V.booleanValue())))
            .andExpect(jsonPath("$.[*].isactive").value(hasItem(DEFAULT_ISACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].questionTitle").value(hasItem(DEFAULT_QUESTION_TITLE)))
            .andExpect(jsonPath("$.[*].questionType").value(hasItem(DEFAULT_QUESTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].questionDescription").value(hasItem(DEFAULT_QUESTION_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].questionPoint").value(hasItem(DEFAULT_QUESTION_POINT)))
            .andExpect(jsonPath("$.[*].questionSubject").value(hasItem(DEFAULT_QUESTION_SUBJECT)))
            .andExpect(jsonPath("$.[*].questionStatus").value(hasItem(DEFAULT_QUESTION_STATUS)));
    }
}
