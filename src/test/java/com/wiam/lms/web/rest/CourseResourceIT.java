package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Course;
import com.wiam.lms.repository.CourseRepository;
import com.wiam.lms.repository.search.CourseSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CourseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CourseResourceIT {

    private static final String DEFAULT_TITLE_AR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_AR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_LAT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_LAT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_SUB_TITLES = "AAAAAAAAAA";
    private static final String UPDATED_SUB_TITLES = "BBBBBBBBBB";

    private static final String DEFAULT_REQUIREMENT = "AAAAAAAAAA";
    private static final String UPDATED_REQUIREMENT = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;

    private static final String DEFAULT_OPTION = "AAAAAAAAAA";
    private static final String UPDATED_OPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_TYPE = false;
    private static final Boolean UPDATED_TYPE = true;

    private static final byte[] DEFAULT_IMAGE_LINK = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE_LINK = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_LINK_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_LINK_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_VIDEO_LINK = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_LINK = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 0D;
    private static final Double UPDATED_PRICE = 1D;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final LocalDate DEFAULT_ACTIVATE_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ACTIVATE_AT = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_IS_CONFIRMED = false;
    private static final Boolean UPDATED_IS_CONFIRMED = true;

    private static final LocalDate DEFAULT_CONFIRMED_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CONFIRMED_AT = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/courses/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSearchRepository courseSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseMockMvc;

    private Course course;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createEntity(EntityManager em) {
        Course course = new Course()
            .titleAr(DEFAULT_TITLE_AR)
            .titleLat(DEFAULT_TITLE_LAT)
            .description(DEFAULT_DESCRIPTION)
            .subTitles(DEFAULT_SUB_TITLES)
            .requirement(DEFAULT_REQUIREMENT)
            .duration(DEFAULT_DURATION)
            .option(DEFAULT_OPTION)
            .type(DEFAULT_TYPE)
            .imageLink(DEFAULT_IMAGE_LINK)
            .imageLinkContentType(DEFAULT_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(DEFAULT_VIDEO_LINK)
            .price(DEFAULT_PRICE)
            .isActive(DEFAULT_IS_ACTIVE)
            .activateAt(DEFAULT_ACTIVATE_AT)
            .isConfirmed(DEFAULT_IS_CONFIRMED)
            .confirmedAt(DEFAULT_CONFIRMED_AT);
        return course;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createUpdatedEntity(EntityManager em) {
        Course course = new Course()
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .subTitles(UPDATED_SUB_TITLES)
            .requirement(UPDATED_REQUIREMENT)
            .duration(UPDATED_DURATION)
            .option(UPDATED_OPTION)
            .type(UPDATED_TYPE)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK)
            .price(UPDATED_PRICE)
            .isActive(UPDATED_IS_ACTIVE)
            .activateAt(UPDATED_ACTIVATE_AT)
            .isConfirmed(UPDATED_IS_CONFIRMED)
            .confirmedAt(UPDATED_CONFIRMED_AT);
        return course;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        courseSearchRepository.deleteAll();
        assertThat(courseSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        course = createEntity(em);
    }

    @Test
    @Transactional
    void createCourse() throws Exception {
        int databaseSizeBeforeCreate = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        // Create the Course
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isCreated());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testCourse.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
        assertThat(testCourse.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCourse.getSubTitles()).isEqualTo(DEFAULT_SUB_TITLES);
        assertThat(testCourse.getRequirement()).isEqualTo(DEFAULT_REQUIREMENT);
        assertThat(testCourse.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testCourse.getOption()).isEqualTo(DEFAULT_OPTION);
        assertThat(testCourse.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCourse.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
        assertThat(testCourse.getImageLinkContentType()).isEqualTo(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testCourse.getVideoLink()).isEqualTo(DEFAULT_VIDEO_LINK);
        assertThat(testCourse.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testCourse.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testCourse.getActivateAt()).isEqualTo(DEFAULT_ACTIVATE_AT);
        assertThat(testCourse.getIsConfirmed()).isEqualTo(DEFAULT_IS_CONFIRMED);
        assertThat(testCourse.getConfirmedAt()).isEqualTo(DEFAULT_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void createCourseWithExistingId() throws Exception {
        // Create the Course with an existing ID
        course.setId(1L);

        int databaseSizeBeforeCreate = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleArIsRequired() throws Exception {
        int databaseSizeBeforeTest = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        // set the field null
        course.setTitleAr(null);

        // Create the Course, which fails.

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isBadRequest());

        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleLatIsRequired() throws Exception {
        int databaseSizeBeforeTest = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        // set the field null
        course.setTitleLat(null);

        // Create the Course, which fails.

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isBadRequest());

        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCourses() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].subTitles").value(hasItem(DEFAULT_SUB_TITLES)))
            .andExpect(jsonPath("$.[*].requirement").value(hasItem(DEFAULT_REQUIREMENT)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].option").value(hasItem(DEFAULT_OPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.booleanValue())))
            .andExpect(jsonPath("$.[*].imageLinkContentType").value(hasItem(DEFAULT_IMAGE_LINK_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageLink").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK))))
            .andExpect(jsonPath("$.[*].videoLink").value(hasItem(DEFAULT_VIDEO_LINK)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].activateAt").value(hasItem(DEFAULT_ACTIVATE_AT.toString())))
            .andExpect(jsonPath("$.[*].isConfirmed").value(hasItem(DEFAULT_IS_CONFIRMED.booleanValue())))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())));
    }

    @Test
    @Transactional
    void getCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get the course
        restCourseMockMvc
            .perform(get(ENTITY_API_URL_ID, course.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(course.getId().intValue()))
            .andExpect(jsonPath("$.titleAr").value(DEFAULT_TITLE_AR))
            .andExpect(jsonPath("$.titleLat").value(DEFAULT_TITLE_LAT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.subTitles").value(DEFAULT_SUB_TITLES))
            .andExpect(jsonPath("$.requirement").value(DEFAULT_REQUIREMENT))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION))
            .andExpect(jsonPath("$.option").value(DEFAULT_OPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.booleanValue()))
            .andExpect(jsonPath("$.imageLinkContentType").value(DEFAULT_IMAGE_LINK_CONTENT_TYPE))
            .andExpect(jsonPath("$.imageLink").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK)))
            .andExpect(jsonPath("$.videoLink").value(DEFAULT_VIDEO_LINK))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.activateAt").value(DEFAULT_ACTIVATE_AT.toString()))
            .andExpect(jsonPath("$.isConfirmed").value(DEFAULT_IS_CONFIRMED.booleanValue()))
            .andExpect(jsonPath("$.confirmedAt").value(DEFAULT_CONFIRMED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCourse() throws Exception {
        // Get the course
        restCourseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        courseSearchRepository.save(course);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());

        // Update the course
        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCourse are not directly saved in db
        em.detach(updatedCourse);
        updatedCourse
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .subTitles(UPDATED_SUB_TITLES)
            .requirement(UPDATED_REQUIREMENT)
            .duration(UPDATED_DURATION)
            .option(UPDATED_OPTION)
            .type(UPDATED_TYPE)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK)
            .price(UPDATED_PRICE)
            .isActive(UPDATED_IS_ACTIVE)
            .activateAt(UPDATED_ACTIVATE_AT)
            .isConfirmed(UPDATED_IS_CONFIRMED)
            .confirmedAt(UPDATED_CONFIRMED_AT);

        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCourse.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testCourse.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testCourse.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCourse.getSubTitles()).isEqualTo(UPDATED_SUB_TITLES);
        assertThat(testCourse.getRequirement()).isEqualTo(UPDATED_REQUIREMENT);
        assertThat(testCourse.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testCourse.getOption()).isEqualTo(UPDATED_OPTION);
        assertThat(testCourse.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCourse.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testCourse.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testCourse.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
        assertThat(testCourse.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCourse.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testCourse.getActivateAt()).isEqualTo(UPDATED_ACTIVATE_AT);
        assertThat(testCourse.getIsConfirmed()).isEqualTo(UPDATED_IS_CONFIRMED);
        assertThat(testCourse.getConfirmedAt()).isEqualTo(UPDATED_CONFIRMED_AT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Course> courseSearchList = IterableUtils.toList(courseSearchRepository.findAll());
                Course testCourseSearch = courseSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCourseSearch.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
                assertThat(testCourseSearch.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
                assertThat(testCourseSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testCourseSearch.getSubTitles()).isEqualTo(UPDATED_SUB_TITLES);
                assertThat(testCourseSearch.getRequirement()).isEqualTo(UPDATED_REQUIREMENT);
                assertThat(testCourseSearch.getDuration()).isEqualTo(UPDATED_DURATION);
                assertThat(testCourseSearch.getOption()).isEqualTo(UPDATED_OPTION);
                assertThat(testCourseSearch.getType()).isEqualTo(UPDATED_TYPE);
                assertThat(testCourseSearch.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
                assertThat(testCourseSearch.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
                assertThat(testCourseSearch.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
                assertThat(testCourseSearch.getPrice()).isEqualTo(UPDATED_PRICE);
                assertThat(testCourseSearch.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
                assertThat(testCourseSearch.getActivateAt()).isEqualTo(UPDATED_ACTIVATE_AT);
                assertThat(testCourseSearch.getIsConfirmed()).isEqualTo(UPDATED_IS_CONFIRMED);
                assertThat(testCourseSearch.getConfirmedAt()).isEqualTo(UPDATED_CONFIRMED_AT);
            });
    }

    @Test
    @Transactional
    void putNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        course.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, course.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(course))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        course.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(course))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        course.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .requirement(UPDATED_REQUIREMENT)
            .duration(UPDATED_DURATION)
            .option(UPDATED_OPTION)
            .type(UPDATED_TYPE)
            .price(UPDATED_PRICE)
            .confirmedAt(UPDATED_CONFIRMED_AT);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testCourse.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testCourse.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCourse.getSubTitles()).isEqualTo(DEFAULT_SUB_TITLES);
        assertThat(testCourse.getRequirement()).isEqualTo(UPDATED_REQUIREMENT);
        assertThat(testCourse.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testCourse.getOption()).isEqualTo(UPDATED_OPTION);
        assertThat(testCourse.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCourse.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
        assertThat(testCourse.getImageLinkContentType()).isEqualTo(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testCourse.getVideoLink()).isEqualTo(DEFAULT_VIDEO_LINK);
        assertThat(testCourse.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCourse.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testCourse.getActivateAt()).isEqualTo(DEFAULT_ACTIVATE_AT);
        assertThat(testCourse.getIsConfirmed()).isEqualTo(DEFAULT_IS_CONFIRMED);
        assertThat(testCourse.getConfirmedAt()).isEqualTo(UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void fullUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .subTitles(UPDATED_SUB_TITLES)
            .requirement(UPDATED_REQUIREMENT)
            .duration(UPDATED_DURATION)
            .option(UPDATED_OPTION)
            .type(UPDATED_TYPE)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK)
            .price(UPDATED_PRICE)
            .isActive(UPDATED_IS_ACTIVE)
            .activateAt(UPDATED_ACTIVATE_AT)
            .isConfirmed(UPDATED_IS_CONFIRMED)
            .confirmedAt(UPDATED_CONFIRMED_AT);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testCourse.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testCourse.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCourse.getSubTitles()).isEqualTo(UPDATED_SUB_TITLES);
        assertThat(testCourse.getRequirement()).isEqualTo(UPDATED_REQUIREMENT);
        assertThat(testCourse.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testCourse.getOption()).isEqualTo(UPDATED_OPTION);
        assertThat(testCourse.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCourse.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testCourse.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testCourse.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
        assertThat(testCourse.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCourse.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testCourse.getActivateAt()).isEqualTo(UPDATED_ACTIVATE_AT);
        assertThat(testCourse.getIsConfirmed()).isEqualTo(UPDATED_IS_CONFIRMED);
        assertThat(testCourse.getConfirmedAt()).isEqualTo(UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        course.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, course.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(course))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        course.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(course))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        course.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);
        courseRepository.save(course);
        courseSearchRepository.save(course);

        int databaseSizeBeforeDelete = courseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the course
        restCourseMockMvc
            .perform(delete(ENTITY_API_URL_ID, course.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(courseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCourse() throws Exception {
        // Initialize the database
        course = courseRepository.saveAndFlush(course);
        courseSearchRepository.save(course);

        // Search the course
        restCourseMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + course.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].subTitles").value(hasItem(DEFAULT_SUB_TITLES)))
            .andExpect(jsonPath("$.[*].requirement").value(hasItem(DEFAULT_REQUIREMENT)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].option").value(hasItem(DEFAULT_OPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.booleanValue())))
            .andExpect(jsonPath("$.[*].imageLinkContentType").value(hasItem(DEFAULT_IMAGE_LINK_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageLink").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK))))
            .andExpect(jsonPath("$.[*].videoLink").value(hasItem(DEFAULT_VIDEO_LINK)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].activateAt").value(hasItem(DEFAULT_ACTIVATE_AT.toString())))
            .andExpect(jsonPath("$.[*].isConfirmed").value(hasItem(DEFAULT_IS_CONFIRMED.booleanValue())))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())));
    }
}
