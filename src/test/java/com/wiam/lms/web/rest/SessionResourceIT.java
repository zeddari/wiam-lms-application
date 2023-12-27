package com.wiam.lms.web.rest;

import static com.wiam.lms.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Session;
import com.wiam.lms.repository.SessionRepository;
import com.wiam.lms.repository.search.SessionSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
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
 * Integration tests for the {@link SessionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SessionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_SESSION_START_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SESSION_START_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_SESSION_END_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SESSION_END_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Integer DEFAULT_SESSION_SIZE = 0;
    private static final Integer UPDATED_SESSION_SIZE = 1;

    private static final Double DEFAULT_PRICE = 0D;
    private static final Double UPDATED_PRICE = 1D;

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_TARGETED_AGE = "AAAAAAAAAA";
    private static final String UPDATED_TARGETED_AGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_TARGETED_GENDER = false;
    private static final Boolean UPDATED_TARGETED_GENDER = true;

    private static final byte[] DEFAULT_THUMBNAIL = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_THUMBNAIL = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_THUMBNAIL_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_THUMBNAIL_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_PLANNING_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_PLANNING_TYPE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_ONCE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ONCE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_MONDAY = false;
    private static final Boolean UPDATED_MONDAY = true;

    private static final Boolean DEFAULT_TUESDAY = false;
    private static final Boolean UPDATED_TUESDAY = true;

    private static final Boolean DEFAULT_WEDNESDAY = false;
    private static final Boolean UPDATED_WEDNESDAY = true;

    private static final Boolean DEFAULT_THURSDAY = false;
    private static final Boolean UPDATED_THURSDAY = true;

    private static final Boolean DEFAULT_FRIDAY = false;
    private static final Boolean UPDATED_FRIDAY = true;

    private static final Boolean DEFAULT_SATURDAY = false;
    private static final Boolean UPDATED_SATURDAY = true;

    private static final Boolean DEFAULT_SANDAY = false;
    private static final Boolean UPDATED_SANDAY = true;

    private static final LocalDate DEFAULT_PERIOD_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIOD_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_PERIODE_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIODE_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_NO_PERIODE_END_DATE = false;
    private static final Boolean UPDATED_NO_PERIODE_END_DATE = true;

    private static final String ENTITY_API_URL = "/api/sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sessions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SessionRepository sessionRepository;

    @Mock
    private SessionRepository sessionRepositoryMock;

    @Autowired
    private SessionSearchRepository sessionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionMockMvc;

    private Session session;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Session createEntity(EntityManager em) {
        Session session = new Session()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .sessionStartTime(DEFAULT_SESSION_START_TIME)
            .sessionEndTime(DEFAULT_SESSION_END_TIME)
            .isActive(DEFAULT_IS_ACTIVE)
            .sessionSize(DEFAULT_SESSION_SIZE)
            .price(DEFAULT_PRICE)
            .currency(DEFAULT_CURRENCY)
            .targetedAge(DEFAULT_TARGETED_AGE)
            .targetedGender(DEFAULT_TARGETED_GENDER)
            .thumbnail(DEFAULT_THUMBNAIL)
            .thumbnailContentType(DEFAULT_THUMBNAIL_CONTENT_TYPE)
            .planningType(DEFAULT_PLANNING_TYPE)
            .onceDate(DEFAULT_ONCE_DATE)
            .monday(DEFAULT_MONDAY)
            .tuesday(DEFAULT_TUESDAY)
            .wednesday(DEFAULT_WEDNESDAY)
            .thursday(DEFAULT_THURSDAY)
            .friday(DEFAULT_FRIDAY)
            .saturday(DEFAULT_SATURDAY)
            .sanday(DEFAULT_SANDAY)
            .periodStartDate(DEFAULT_PERIOD_START_DATE)
            .periodeEndDate(DEFAULT_PERIODE_END_DATE)
            .noPeriodeEndDate(DEFAULT_NO_PERIODE_END_DATE);
        return session;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Session createUpdatedEntity(EntityManager em) {
        Session session = new Session()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .sessionStartTime(UPDATED_SESSION_START_TIME)
            .sessionEndTime(UPDATED_SESSION_END_TIME)
            .isActive(UPDATED_IS_ACTIVE)
            .sessionSize(UPDATED_SESSION_SIZE)
            .price(UPDATED_PRICE)
            .currency(UPDATED_CURRENCY)
            .targetedAge(UPDATED_TARGETED_AGE)
            .targetedGender(UPDATED_TARGETED_GENDER)
            .thumbnail(UPDATED_THUMBNAIL)
            .thumbnailContentType(UPDATED_THUMBNAIL_CONTENT_TYPE)
            .planningType(UPDATED_PLANNING_TYPE)
            .onceDate(UPDATED_ONCE_DATE)
            .monday(UPDATED_MONDAY)
            .tuesday(UPDATED_TUESDAY)
            .wednesday(UPDATED_WEDNESDAY)
            .thursday(UPDATED_THURSDAY)
            .friday(UPDATED_FRIDAY)
            .saturday(UPDATED_SATURDAY)
            .sanday(UPDATED_SANDAY)
            .periodStartDate(UPDATED_PERIOD_START_DATE)
            .periodeEndDate(UPDATED_PERIODE_END_DATE)
            .noPeriodeEndDate(UPDATED_NO_PERIODE_END_DATE);
        return session;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        sessionSearchRepository.deleteAll();
        assertThat(sessionSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        session = createEntity(em);
    }

    @Test
    @Transactional
    void createSession() throws Exception {
        int databaseSizeBeforeCreate = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // Create the Session
        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isCreated());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Session testSession = sessionList.get(sessionList.size() - 1);
        assertThat(testSession.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSession.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSession.getSessionStartTime()).isEqualTo(DEFAULT_SESSION_START_TIME);
        assertThat(testSession.getSessionEndTime()).isEqualTo(DEFAULT_SESSION_END_TIME);
        assertThat(testSession.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testSession.getSessionSize()).isEqualTo(DEFAULT_SESSION_SIZE);
        assertThat(testSession.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testSession.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testSession.getTargetedAge()).isEqualTo(DEFAULT_TARGETED_AGE);
        assertThat(testSession.getTargetedGender()).isEqualTo(DEFAULT_TARGETED_GENDER);
        assertThat(testSession.getThumbnail()).isEqualTo(DEFAULT_THUMBNAIL);
        assertThat(testSession.getThumbnailContentType()).isEqualTo(DEFAULT_THUMBNAIL_CONTENT_TYPE);
        assertThat(testSession.getPlanningType()).isEqualTo(DEFAULT_PLANNING_TYPE);
        assertThat(testSession.getOnceDate()).isEqualTo(DEFAULT_ONCE_DATE);
        assertThat(testSession.getMonday()).isEqualTo(DEFAULT_MONDAY);
        assertThat(testSession.getTuesday()).isEqualTo(DEFAULT_TUESDAY);
        assertThat(testSession.getWednesday()).isEqualTo(DEFAULT_WEDNESDAY);
        assertThat(testSession.getThursday()).isEqualTo(DEFAULT_THURSDAY);
        assertThat(testSession.getFriday()).isEqualTo(DEFAULT_FRIDAY);
        assertThat(testSession.getSaturday()).isEqualTo(DEFAULT_SATURDAY);
        assertThat(testSession.getSanday()).isEqualTo(DEFAULT_SANDAY);
        assertThat(testSession.getPeriodStartDate()).isEqualTo(DEFAULT_PERIOD_START_DATE);
        assertThat(testSession.getPeriodeEndDate()).isEqualTo(DEFAULT_PERIODE_END_DATE);
        assertThat(testSession.getNoPeriodeEndDate()).isEqualTo(DEFAULT_NO_PERIODE_END_DATE);
    }

    @Test
    @Transactional
    void createSessionWithExistingId() throws Exception {
        // Create the Session with an existing ID
        session.setId(1L);

        int databaseSizeBeforeCreate = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // set the field null
        session.setTitle(null);

        // Create the Session, which fails.

        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSessionStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // set the field null
        session.setSessionStartTime(null);

        // Create the Session, which fails.

        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSessionEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // set the field null
        session.setSessionEndTime(null);

        // Create the Session, which fails.

        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // set the field null
        session.setIsActive(null);

        // Create the Session, which fails.

        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSessionSizeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // set the field null
        session.setSessionSize(null);

        // Create the Session, which fails.

        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // set the field null
        session.setCurrency(null);

        // Create the Session, which fails.

        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTargetedAgeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // set the field null
        session.setTargetedAge(null);

        // Create the Session, which fails.

        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTargetedGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // set the field null
        session.setTargetedGender(null);

        // Create the Session, which fails.

        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPlanningTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        // set the field null
        session.setPlanningType(null);

        // Create the Session, which fails.

        restSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isBadRequest());

        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSessions() throws Exception {
        // Initialize the database
        sessionRepository.saveAndFlush(session);

        // Get all the sessionList
        restSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(session.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].sessionStartTime").value(hasItem(sameInstant(DEFAULT_SESSION_START_TIME))))
            .andExpect(jsonPath("$.[*].sessionEndTime").value(hasItem(sameInstant(DEFAULT_SESSION_END_TIME))))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].sessionSize").value(hasItem(DEFAULT_SESSION_SIZE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].targetedAge").value(hasItem(DEFAULT_TARGETED_AGE)))
            .andExpect(jsonPath("$.[*].targetedGender").value(hasItem(DEFAULT_TARGETED_GENDER.booleanValue())))
            .andExpect(jsonPath("$.[*].thumbnailContentType").value(hasItem(DEFAULT_THUMBNAIL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].thumbnail").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_THUMBNAIL))))
            .andExpect(jsonPath("$.[*].planningType").value(hasItem(DEFAULT_PLANNING_TYPE)))
            .andExpect(jsonPath("$.[*].onceDate").value(hasItem(sameInstant(DEFAULT_ONCE_DATE))))
            .andExpect(jsonPath("$.[*].monday").value(hasItem(DEFAULT_MONDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].tuesday").value(hasItem(DEFAULT_TUESDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].wednesday").value(hasItem(DEFAULT_WEDNESDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].thursday").value(hasItem(DEFAULT_THURSDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].friday").value(hasItem(DEFAULT_FRIDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].saturday").value(hasItem(DEFAULT_SATURDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].sanday").value(hasItem(DEFAULT_SANDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].periodStartDate").value(hasItem(DEFAULT_PERIOD_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].periodeEndDate").value(hasItem(DEFAULT_PERIODE_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].noPeriodeEndDate").value(hasItem(DEFAULT_NO_PERIODE_END_DATE.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSessionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(sessionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sessionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSessionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sessionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sessionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSession() throws Exception {
        // Initialize the database
        sessionRepository.saveAndFlush(session);

        // Get the session
        restSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, session.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(session.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.sessionStartTime").value(sameInstant(DEFAULT_SESSION_START_TIME)))
            .andExpect(jsonPath("$.sessionEndTime").value(sameInstant(DEFAULT_SESSION_END_TIME)))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.sessionSize").value(DEFAULT_SESSION_SIZE))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.targetedAge").value(DEFAULT_TARGETED_AGE))
            .andExpect(jsonPath("$.targetedGender").value(DEFAULT_TARGETED_GENDER.booleanValue()))
            .andExpect(jsonPath("$.thumbnailContentType").value(DEFAULT_THUMBNAIL_CONTENT_TYPE))
            .andExpect(jsonPath("$.thumbnail").value(Base64.getEncoder().encodeToString(DEFAULT_THUMBNAIL)))
            .andExpect(jsonPath("$.planningType").value(DEFAULT_PLANNING_TYPE))
            .andExpect(jsonPath("$.onceDate").value(sameInstant(DEFAULT_ONCE_DATE)))
            .andExpect(jsonPath("$.monday").value(DEFAULT_MONDAY.booleanValue()))
            .andExpect(jsonPath("$.tuesday").value(DEFAULT_TUESDAY.booleanValue()))
            .andExpect(jsonPath("$.wednesday").value(DEFAULT_WEDNESDAY.booleanValue()))
            .andExpect(jsonPath("$.thursday").value(DEFAULT_THURSDAY.booleanValue()))
            .andExpect(jsonPath("$.friday").value(DEFAULT_FRIDAY.booleanValue()))
            .andExpect(jsonPath("$.saturday").value(DEFAULT_SATURDAY.booleanValue()))
            .andExpect(jsonPath("$.sanday").value(DEFAULT_SANDAY.booleanValue()))
            .andExpect(jsonPath("$.periodStartDate").value(DEFAULT_PERIOD_START_DATE.toString()))
            .andExpect(jsonPath("$.periodeEndDate").value(DEFAULT_PERIODE_END_DATE.toString()))
            .andExpect(jsonPath("$.noPeriodeEndDate").value(DEFAULT_NO_PERIODE_END_DATE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingSession() throws Exception {
        // Get the session
        restSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSession() throws Exception {
        // Initialize the database
        sessionRepository.saveAndFlush(session);

        int databaseSizeBeforeUpdate = sessionRepository.findAll().size();
        sessionSearchRepository.save(session);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());

        // Update the session
        Session updatedSession = sessionRepository.findById(session.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSession are not directly saved in db
        em.detach(updatedSession);
        updatedSession
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .sessionStartTime(UPDATED_SESSION_START_TIME)
            .sessionEndTime(UPDATED_SESSION_END_TIME)
            .isActive(UPDATED_IS_ACTIVE)
            .sessionSize(UPDATED_SESSION_SIZE)
            .price(UPDATED_PRICE)
            .currency(UPDATED_CURRENCY)
            .targetedAge(UPDATED_TARGETED_AGE)
            .targetedGender(UPDATED_TARGETED_GENDER)
            .thumbnail(UPDATED_THUMBNAIL)
            .thumbnailContentType(UPDATED_THUMBNAIL_CONTENT_TYPE)
            .planningType(UPDATED_PLANNING_TYPE)
            .onceDate(UPDATED_ONCE_DATE)
            .monday(UPDATED_MONDAY)
            .tuesday(UPDATED_TUESDAY)
            .wednesday(UPDATED_WEDNESDAY)
            .thursday(UPDATED_THURSDAY)
            .friday(UPDATED_FRIDAY)
            .saturday(UPDATED_SATURDAY)
            .sanday(UPDATED_SANDAY)
            .periodStartDate(UPDATED_PERIOD_START_DATE)
            .periodeEndDate(UPDATED_PERIODE_END_DATE)
            .noPeriodeEndDate(UPDATED_NO_PERIODE_END_DATE);

        restSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSession.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSession))
            )
            .andExpect(status().isOk());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
        Session testSession = sessionList.get(sessionList.size() - 1);
        assertThat(testSession.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSession.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSession.getSessionStartTime()).isEqualTo(UPDATED_SESSION_START_TIME);
        assertThat(testSession.getSessionEndTime()).isEqualTo(UPDATED_SESSION_END_TIME);
        assertThat(testSession.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testSession.getSessionSize()).isEqualTo(UPDATED_SESSION_SIZE);
        assertThat(testSession.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testSession.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testSession.getTargetedAge()).isEqualTo(UPDATED_TARGETED_AGE);
        assertThat(testSession.getTargetedGender()).isEqualTo(UPDATED_TARGETED_GENDER);
        assertThat(testSession.getThumbnail()).isEqualTo(UPDATED_THUMBNAIL);
        assertThat(testSession.getThumbnailContentType()).isEqualTo(UPDATED_THUMBNAIL_CONTENT_TYPE);
        assertThat(testSession.getPlanningType()).isEqualTo(UPDATED_PLANNING_TYPE);
        assertThat(testSession.getOnceDate()).isEqualTo(UPDATED_ONCE_DATE);
        assertThat(testSession.getMonday()).isEqualTo(UPDATED_MONDAY);
        assertThat(testSession.getTuesday()).isEqualTo(UPDATED_TUESDAY);
        assertThat(testSession.getWednesday()).isEqualTo(UPDATED_WEDNESDAY);
        assertThat(testSession.getThursday()).isEqualTo(UPDATED_THURSDAY);
        assertThat(testSession.getFriday()).isEqualTo(UPDATED_FRIDAY);
        assertThat(testSession.getSaturday()).isEqualTo(UPDATED_SATURDAY);
        assertThat(testSession.getSanday()).isEqualTo(UPDATED_SANDAY);
        assertThat(testSession.getPeriodStartDate()).isEqualTo(UPDATED_PERIOD_START_DATE);
        assertThat(testSession.getPeriodeEndDate()).isEqualTo(UPDATED_PERIODE_END_DATE);
        assertThat(testSession.getNoPeriodeEndDate()).isEqualTo(UPDATED_NO_PERIODE_END_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Session> sessionSearchList = IterableUtils.toList(sessionSearchRepository.findAll());
                Session testSessionSearch = sessionSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSessionSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testSessionSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testSessionSearch.getSessionStartTime()).isEqualTo(UPDATED_SESSION_START_TIME);
                assertThat(testSessionSearch.getSessionEndTime()).isEqualTo(UPDATED_SESSION_END_TIME);
                assertThat(testSessionSearch.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
                assertThat(testSessionSearch.getSessionSize()).isEqualTo(UPDATED_SESSION_SIZE);
                assertThat(testSessionSearch.getPrice()).isEqualTo(UPDATED_PRICE);
                assertThat(testSessionSearch.getCurrency()).isEqualTo(UPDATED_CURRENCY);
                assertThat(testSessionSearch.getTargetedAge()).isEqualTo(UPDATED_TARGETED_AGE);
                assertThat(testSessionSearch.getTargetedGender()).isEqualTo(UPDATED_TARGETED_GENDER);
                assertThat(testSessionSearch.getThumbnail()).isEqualTo(UPDATED_THUMBNAIL);
                assertThat(testSessionSearch.getThumbnailContentType()).isEqualTo(UPDATED_THUMBNAIL_CONTENT_TYPE);
                assertThat(testSessionSearch.getPlanningType()).isEqualTo(UPDATED_PLANNING_TYPE);
                assertThat(testSessionSearch.getOnceDate()).isEqualTo(UPDATED_ONCE_DATE);
                assertThat(testSessionSearch.getMonday()).isEqualTo(UPDATED_MONDAY);
                assertThat(testSessionSearch.getTuesday()).isEqualTo(UPDATED_TUESDAY);
                assertThat(testSessionSearch.getWednesday()).isEqualTo(UPDATED_WEDNESDAY);
                assertThat(testSessionSearch.getThursday()).isEqualTo(UPDATED_THURSDAY);
                assertThat(testSessionSearch.getFriday()).isEqualTo(UPDATED_FRIDAY);
                assertThat(testSessionSearch.getSaturday()).isEqualTo(UPDATED_SATURDAY);
                assertThat(testSessionSearch.getSanday()).isEqualTo(UPDATED_SANDAY);
                assertThat(testSessionSearch.getPeriodStartDate()).isEqualTo(UPDATED_PERIOD_START_DATE);
                assertThat(testSessionSearch.getPeriodeEndDate()).isEqualTo(UPDATED_PERIODE_END_DATE);
                assertThat(testSessionSearch.getNoPeriodeEndDate()).isEqualTo(UPDATED_NO_PERIODE_END_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingSession() throws Exception {
        int databaseSizeBeforeUpdate = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        session.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, session.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(session))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSession() throws Exception {
        int databaseSizeBeforeUpdate = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(session))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSession() throws Exception {
        int databaseSizeBeforeUpdate = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSessionWithPatch() throws Exception {
        // Initialize the database
        sessionRepository.saveAndFlush(session);

        int databaseSizeBeforeUpdate = sessionRepository.findAll().size();

        // Update the session using partial update
        Session partialUpdatedSession = new Session();
        partialUpdatedSession.setId(session.getId());

        partialUpdatedSession
            .title(UPDATED_TITLE)
            .sessionEndTime(UPDATED_SESSION_END_TIME)
            .sessionSize(UPDATED_SESSION_SIZE)
            .currency(UPDATED_CURRENCY)
            .targetedAge(UPDATED_TARGETED_AGE)
            .targetedGender(UPDATED_TARGETED_GENDER)
            .wednesday(UPDATED_WEDNESDAY)
            .thursday(UPDATED_THURSDAY)
            .friday(UPDATED_FRIDAY)
            .saturday(UPDATED_SATURDAY)
            .periodeEndDate(UPDATED_PERIODE_END_DATE)
            .noPeriodeEndDate(UPDATED_NO_PERIODE_END_DATE);

        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSession))
            )
            .andExpect(status().isOk());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
        Session testSession = sessionList.get(sessionList.size() - 1);
        assertThat(testSession.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSession.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSession.getSessionStartTime()).isEqualTo(DEFAULT_SESSION_START_TIME);
        assertThat(testSession.getSessionEndTime()).isEqualTo(UPDATED_SESSION_END_TIME);
        assertThat(testSession.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testSession.getSessionSize()).isEqualTo(UPDATED_SESSION_SIZE);
        assertThat(testSession.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testSession.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testSession.getTargetedAge()).isEqualTo(UPDATED_TARGETED_AGE);
        assertThat(testSession.getTargetedGender()).isEqualTo(UPDATED_TARGETED_GENDER);
        assertThat(testSession.getThumbnail()).isEqualTo(DEFAULT_THUMBNAIL);
        assertThat(testSession.getThumbnailContentType()).isEqualTo(DEFAULT_THUMBNAIL_CONTENT_TYPE);
        assertThat(testSession.getPlanningType()).isEqualTo(DEFAULT_PLANNING_TYPE);
        assertThat(testSession.getOnceDate()).isEqualTo(DEFAULT_ONCE_DATE);
        assertThat(testSession.getMonday()).isEqualTo(DEFAULT_MONDAY);
        assertThat(testSession.getTuesday()).isEqualTo(DEFAULT_TUESDAY);
        assertThat(testSession.getWednesday()).isEqualTo(UPDATED_WEDNESDAY);
        assertThat(testSession.getThursday()).isEqualTo(UPDATED_THURSDAY);
        assertThat(testSession.getFriday()).isEqualTo(UPDATED_FRIDAY);
        assertThat(testSession.getSaturday()).isEqualTo(UPDATED_SATURDAY);
        assertThat(testSession.getSanday()).isEqualTo(DEFAULT_SANDAY);
        assertThat(testSession.getPeriodStartDate()).isEqualTo(DEFAULT_PERIOD_START_DATE);
        assertThat(testSession.getPeriodeEndDate()).isEqualTo(UPDATED_PERIODE_END_DATE);
        assertThat(testSession.getNoPeriodeEndDate()).isEqualTo(UPDATED_NO_PERIODE_END_DATE);
    }

    @Test
    @Transactional
    void fullUpdateSessionWithPatch() throws Exception {
        // Initialize the database
        sessionRepository.saveAndFlush(session);

        int databaseSizeBeforeUpdate = sessionRepository.findAll().size();

        // Update the session using partial update
        Session partialUpdatedSession = new Session();
        partialUpdatedSession.setId(session.getId());

        partialUpdatedSession
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .sessionStartTime(UPDATED_SESSION_START_TIME)
            .sessionEndTime(UPDATED_SESSION_END_TIME)
            .isActive(UPDATED_IS_ACTIVE)
            .sessionSize(UPDATED_SESSION_SIZE)
            .price(UPDATED_PRICE)
            .currency(UPDATED_CURRENCY)
            .targetedAge(UPDATED_TARGETED_AGE)
            .targetedGender(UPDATED_TARGETED_GENDER)
            .thumbnail(UPDATED_THUMBNAIL)
            .thumbnailContentType(UPDATED_THUMBNAIL_CONTENT_TYPE)
            .planningType(UPDATED_PLANNING_TYPE)
            .onceDate(UPDATED_ONCE_DATE)
            .monday(UPDATED_MONDAY)
            .tuesday(UPDATED_TUESDAY)
            .wednesday(UPDATED_WEDNESDAY)
            .thursday(UPDATED_THURSDAY)
            .friday(UPDATED_FRIDAY)
            .saturday(UPDATED_SATURDAY)
            .sanday(UPDATED_SANDAY)
            .periodStartDate(UPDATED_PERIOD_START_DATE)
            .periodeEndDate(UPDATED_PERIODE_END_DATE)
            .noPeriodeEndDate(UPDATED_NO_PERIODE_END_DATE);

        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSession))
            )
            .andExpect(status().isOk());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
        Session testSession = sessionList.get(sessionList.size() - 1);
        assertThat(testSession.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSession.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSession.getSessionStartTime()).isEqualTo(UPDATED_SESSION_START_TIME);
        assertThat(testSession.getSessionEndTime()).isEqualTo(UPDATED_SESSION_END_TIME);
        assertThat(testSession.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testSession.getSessionSize()).isEqualTo(UPDATED_SESSION_SIZE);
        assertThat(testSession.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testSession.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testSession.getTargetedAge()).isEqualTo(UPDATED_TARGETED_AGE);
        assertThat(testSession.getTargetedGender()).isEqualTo(UPDATED_TARGETED_GENDER);
        assertThat(testSession.getThumbnail()).isEqualTo(UPDATED_THUMBNAIL);
        assertThat(testSession.getThumbnailContentType()).isEqualTo(UPDATED_THUMBNAIL_CONTENT_TYPE);
        assertThat(testSession.getPlanningType()).isEqualTo(UPDATED_PLANNING_TYPE);
        assertThat(testSession.getOnceDate()).isEqualTo(UPDATED_ONCE_DATE);
        assertThat(testSession.getMonday()).isEqualTo(UPDATED_MONDAY);
        assertThat(testSession.getTuesday()).isEqualTo(UPDATED_TUESDAY);
        assertThat(testSession.getWednesday()).isEqualTo(UPDATED_WEDNESDAY);
        assertThat(testSession.getThursday()).isEqualTo(UPDATED_THURSDAY);
        assertThat(testSession.getFriday()).isEqualTo(UPDATED_FRIDAY);
        assertThat(testSession.getSaturday()).isEqualTo(UPDATED_SATURDAY);
        assertThat(testSession.getSanday()).isEqualTo(UPDATED_SANDAY);
        assertThat(testSession.getPeriodStartDate()).isEqualTo(UPDATED_PERIOD_START_DATE);
        assertThat(testSession.getPeriodeEndDate()).isEqualTo(UPDATED_PERIODE_END_DATE);
        assertThat(testSession.getNoPeriodeEndDate()).isEqualTo(UPDATED_NO_PERIODE_END_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingSession() throws Exception {
        int databaseSizeBeforeUpdate = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        session.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, session.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(session))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSession() throws Exception {
        int databaseSizeBeforeUpdate = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(session))
            )
            .andExpect(status().isBadRequest());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSession() throws Exception {
        int databaseSizeBeforeUpdate = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        session.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(session)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Session in the database
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSession() throws Exception {
        // Initialize the database
        sessionRepository.saveAndFlush(session);
        sessionRepository.save(session);
        sessionSearchRepository.save(session);

        int databaseSizeBeforeDelete = sessionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the session
        restSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, session.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Session> sessionList = sessionRepository.findAll();
        assertThat(sessionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSession() throws Exception {
        // Initialize the database
        session = sessionRepository.saveAndFlush(session);
        sessionSearchRepository.save(session);

        // Search the session
        restSessionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + session.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(session.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].sessionStartTime").value(hasItem(sameInstant(DEFAULT_SESSION_START_TIME))))
            .andExpect(jsonPath("$.[*].sessionEndTime").value(hasItem(sameInstant(DEFAULT_SESSION_END_TIME))))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].sessionSize").value(hasItem(DEFAULT_SESSION_SIZE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].targetedAge").value(hasItem(DEFAULT_TARGETED_AGE)))
            .andExpect(jsonPath("$.[*].targetedGender").value(hasItem(DEFAULT_TARGETED_GENDER.booleanValue())))
            .andExpect(jsonPath("$.[*].thumbnailContentType").value(hasItem(DEFAULT_THUMBNAIL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].thumbnail").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_THUMBNAIL))))
            .andExpect(jsonPath("$.[*].planningType").value(hasItem(DEFAULT_PLANNING_TYPE)))
            .andExpect(jsonPath("$.[*].onceDate").value(hasItem(sameInstant(DEFAULT_ONCE_DATE))))
            .andExpect(jsonPath("$.[*].monday").value(hasItem(DEFAULT_MONDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].tuesday").value(hasItem(DEFAULT_TUESDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].wednesday").value(hasItem(DEFAULT_WEDNESDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].thursday").value(hasItem(DEFAULT_THURSDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].friday").value(hasItem(DEFAULT_FRIDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].saturday").value(hasItem(DEFAULT_SATURDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].sanday").value(hasItem(DEFAULT_SANDAY.booleanValue())))
            .andExpect(jsonPath("$.[*].periodStartDate").value(hasItem(DEFAULT_PERIOD_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].periodeEndDate").value(hasItem(DEFAULT_PERIODE_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].noPeriodeEndDate").value(hasItem(DEFAULT_NO_PERIODE_END_DATE.booleanValue())));
    }
}
