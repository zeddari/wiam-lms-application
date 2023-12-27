package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Project;
import com.wiam.lms.repository.ProjectRepository;
import com.wiam.lms.repository.search.ProjectSearchRepository;
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
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectResourceIT {

    private static final String DEFAULT_TITLE_AR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_AR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_LAT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_LAT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_GOALS = "AAAAAAAAAA";
    private static final String UPDATED_GOALS = "BBBBBBBBBB";

    private static final String DEFAULT_REQUIREMENT = "AAAAAAAAAA";
    private static final String UPDATED_REQUIREMENT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE_LINK = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE_LINK = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_LINK_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_LINK_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_VIDEO_LINK = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_LINK = "BBBBBBBBBB";

    private static final Double DEFAULT_BUDGET = 0D;
    private static final Double UPDATED_BUDGET = 1D;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final LocalDate DEFAULT_ACTIVATE_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ACTIVATE_AT = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/projects/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectSearchRepository projectSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMockMvc;

    private Project project;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createEntity(EntityManager em) {
        Project project = new Project()
            .titleAr(DEFAULT_TITLE_AR)
            .titleLat(DEFAULT_TITLE_LAT)
            .description(DEFAULT_DESCRIPTION)
            .goals(DEFAULT_GOALS)
            .requirement(DEFAULT_REQUIREMENT)
            .imageLink(DEFAULT_IMAGE_LINK)
            .imageLinkContentType(DEFAULT_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(DEFAULT_VIDEO_LINK)
            .budget(DEFAULT_BUDGET)
            .isActive(DEFAULT_IS_ACTIVE)
            .activateAt(DEFAULT_ACTIVATE_AT)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return project;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createUpdatedEntity(EntityManager em) {
        Project project = new Project()
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .goals(UPDATED_GOALS)
            .requirement(UPDATED_REQUIREMENT)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK)
            .budget(UPDATED_BUDGET)
            .isActive(UPDATED_IS_ACTIVE)
            .activateAt(UPDATED_ACTIVATE_AT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        return project;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        projectSearchRepository.deleteAll();
        assertThat(projectSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        project = createEntity(em);
    }

    @Test
    @Transactional
    void createProject() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        // Create the Project
        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isCreated());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testProject.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
        assertThat(testProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProject.getGoals()).isEqualTo(DEFAULT_GOALS);
        assertThat(testProject.getRequirement()).isEqualTo(DEFAULT_REQUIREMENT);
        assertThat(testProject.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
        assertThat(testProject.getImageLinkContentType()).isEqualTo(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testProject.getVideoLink()).isEqualTo(DEFAULT_VIDEO_LINK);
        assertThat(testProject.getBudget()).isEqualTo(DEFAULT_BUDGET);
        assertThat(testProject.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testProject.getActivateAt()).isEqualTo(DEFAULT_ACTIVATE_AT);
        assertThat(testProject.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testProject.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void createProjectWithExistingId() throws Exception {
        // Create the Project with an existing ID
        project.setId(1L);

        int databaseSizeBeforeCreate = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleArIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        // set the field null
        project.setTitleAr(null);

        // Create the Project, which fails.

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isBadRequest());

        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleLatIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        // set the field null
        project.setTitleLat(null);

        // Create the Project, which fails.

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isBadRequest());

        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkBudgetIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        // set the field null
        project.setBudget(null);

        // Create the Project, which fails.

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isBadRequest());

        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProjects() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].goals").value(hasItem(DEFAULT_GOALS)))
            .andExpect(jsonPath("$.[*].requirement").value(hasItem(DEFAULT_REQUIREMENT)))
            .andExpect(jsonPath("$.[*].imageLinkContentType").value(hasItem(DEFAULT_IMAGE_LINK_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageLink").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK))))
            .andExpect(jsonPath("$.[*].videoLink").value(hasItem(DEFAULT_VIDEO_LINK)))
            .andExpect(jsonPath("$.[*].budget").value(hasItem(DEFAULT_BUDGET.doubleValue())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].activateAt").value(hasItem(DEFAULT_ACTIVATE_AT.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    void getProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get the project
        restProjectMockMvc
            .perform(get(ENTITY_API_URL_ID, project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.titleAr").value(DEFAULT_TITLE_AR))
            .andExpect(jsonPath("$.titleLat").value(DEFAULT_TITLE_LAT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.goals").value(DEFAULT_GOALS))
            .andExpect(jsonPath("$.requirement").value(DEFAULT_REQUIREMENT))
            .andExpect(jsonPath("$.imageLinkContentType").value(DEFAULT_IMAGE_LINK_CONTENT_TYPE))
            .andExpect(jsonPath("$.imageLink").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK)))
            .andExpect(jsonPath("$.videoLink").value(DEFAULT_VIDEO_LINK))
            .andExpect(jsonPath("$.budget").value(DEFAULT_BUDGET.doubleValue()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.activateAt").value(DEFAULT_ACTIVATE_AT.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProject() throws Exception {
        // Get the project
        restProjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        projectSearchRepository.save(project);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());

        // Update the project
        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProject are not directly saved in db
        em.detach(updatedProject);
        updatedProject
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .goals(UPDATED_GOALS)
            .requirement(UPDATED_REQUIREMENT)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK)
            .budget(UPDATED_BUDGET)
            .isActive(UPDATED_IS_ACTIVE)
            .activateAt(UPDATED_ACTIVATE_AT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProject.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testProject.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProject.getGoals()).isEqualTo(UPDATED_GOALS);
        assertThat(testProject.getRequirement()).isEqualTo(UPDATED_REQUIREMENT);
        assertThat(testProject.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testProject.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testProject.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
        assertThat(testProject.getBudget()).isEqualTo(UPDATED_BUDGET);
        assertThat(testProject.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testProject.getActivateAt()).isEqualTo(UPDATED_ACTIVATE_AT);
        assertThat(testProject.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testProject.getEndDate()).isEqualTo(UPDATED_END_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Project> projectSearchList = IterableUtils.toList(projectSearchRepository.findAll());
                Project testProjectSearch = projectSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProjectSearch.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
                assertThat(testProjectSearch.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
                assertThat(testProjectSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testProjectSearch.getGoals()).isEqualTo(UPDATED_GOALS);
                assertThat(testProjectSearch.getRequirement()).isEqualTo(UPDATED_REQUIREMENT);
                assertThat(testProjectSearch.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
                assertThat(testProjectSearch.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
                assertThat(testProjectSearch.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
                assertThat(testProjectSearch.getBudget()).isEqualTo(UPDATED_BUDGET);
                assertThat(testProjectSearch.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
                assertThat(testProjectSearch.getActivateAt()).isEqualTo(UPDATED_ACTIVATE_AT);
                assertThat(testProjectSearch.getStartDate()).isEqualTo(UPDATED_START_DATE);
                assertThat(testProjectSearch.getEndDate()).isEqualTo(UPDATED_END_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, project.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .requirement(UPDATED_REQUIREMENT)
            .videoLink(UPDATED_VIDEO_LINK)
            .budget(UPDATED_BUDGET)
            .startDate(UPDATED_START_DATE);

        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testProject.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProject.getGoals()).isEqualTo(DEFAULT_GOALS);
        assertThat(testProject.getRequirement()).isEqualTo(UPDATED_REQUIREMENT);
        assertThat(testProject.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
        assertThat(testProject.getImageLinkContentType()).isEqualTo(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testProject.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
        assertThat(testProject.getBudget()).isEqualTo(UPDATED_BUDGET);
        assertThat(testProject.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testProject.getActivateAt()).isEqualTo(DEFAULT_ACTIVATE_AT);
        assertThat(testProject.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testProject.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void fullUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .goals(UPDATED_GOALS)
            .requirement(UPDATED_REQUIREMENT)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK)
            .budget(UPDATED_BUDGET)
            .isActive(UPDATED_IS_ACTIVE)
            .activateAt(UPDATED_ACTIVATE_AT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testProject.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProject.getGoals()).isEqualTo(UPDATED_GOALS);
        assertThat(testProject.getRequirement()).isEqualTo(UPDATED_REQUIREMENT);
        assertThat(testProject.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testProject.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testProject.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
        assertThat(testProject.getBudget()).isEqualTo(UPDATED_BUDGET);
        assertThat(testProject.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testProject.getActivateAt()).isEqualTo(UPDATED_ACTIVATE_AT);
        assertThat(testProject.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testProject.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, project.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(project))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(project)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        projectRepository.save(project);
        projectSearchRepository.save(project);

        int databaseSizeBeforeDelete = projectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the project
        restProjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, project.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProject() throws Exception {
        // Initialize the database
        project = projectRepository.saveAndFlush(project);
        projectSearchRepository.save(project);

        // Search the project
        restProjectMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].goals").value(hasItem(DEFAULT_GOALS)))
            .andExpect(jsonPath("$.[*].requirement").value(hasItem(DEFAULT_REQUIREMENT)))
            .andExpect(jsonPath("$.[*].imageLinkContentType").value(hasItem(DEFAULT_IMAGE_LINK_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageLink").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK))))
            .andExpect(jsonPath("$.[*].videoLink").value(hasItem(DEFAULT_VIDEO_LINK)))
            .andExpect(jsonPath("$.[*].budget").value(hasItem(DEFAULT_BUDGET.doubleValue())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].activateAt").value(hasItem(DEFAULT_ACTIVATE_AT.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }
}
