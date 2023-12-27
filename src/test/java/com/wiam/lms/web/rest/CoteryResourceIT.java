package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Cotery;
import com.wiam.lms.domain.enumeration.Attendance;
import com.wiam.lms.repository.CoteryRepository;
import com.wiam.lms.repository.search.CoterySearchRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link CoteryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CoteryResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_COTERY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COTERY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_STUDENT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_STUDENT_FULL_NAME = "BBBBBBBBBB";

    private static final Attendance DEFAULT_ATTENDANCE_STATUS = Attendance.PRESENT;
    private static final Attendance UPDATED_ATTENDANCE_STATUS = Attendance.ABSENT;

    private static final String ENTITY_API_URL = "/api/coteries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/coteries/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CoteryRepository coteryRepository;

    @Autowired
    private CoterySearchRepository coterySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCoteryMockMvc;

    private Cotery cotery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cotery createEntity(EntityManager em) {
        Cotery cotery = new Cotery()
            .date(DEFAULT_DATE)
            .coteryName(DEFAULT_COTERY_NAME)
            .studentFullName(DEFAULT_STUDENT_FULL_NAME)
            .attendanceStatus(DEFAULT_ATTENDANCE_STATUS);
        return cotery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cotery createUpdatedEntity(EntityManager em) {
        Cotery cotery = new Cotery()
            .date(UPDATED_DATE)
            .coteryName(UPDATED_COTERY_NAME)
            .studentFullName(UPDATED_STUDENT_FULL_NAME)
            .attendanceStatus(UPDATED_ATTENDANCE_STATUS);
        return cotery;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        coterySearchRepository.deleteAll();
        assertThat(coterySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        cotery = createEntity(em);
    }

    @Test
    @Transactional
    void createCotery() throws Exception {
        int databaseSizeBeforeCreate = coteryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());
        // Create the Cotery
        restCoteryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cotery)))
            .andExpect(status().isCreated());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Cotery testCotery = coteryList.get(coteryList.size() - 1);
        assertThat(testCotery.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testCotery.getCoteryName()).isEqualTo(DEFAULT_COTERY_NAME);
        assertThat(testCotery.getStudentFullName()).isEqualTo(DEFAULT_STUDENT_FULL_NAME);
        assertThat(testCotery.getAttendanceStatus()).isEqualTo(DEFAULT_ATTENDANCE_STATUS);
    }

    @Test
    @Transactional
    void createCoteryWithExistingId() throws Exception {
        // Create the Cotery with an existing ID
        cotery.setId(1L);

        int databaseSizeBeforeCreate = coteryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCoteryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cotery)))
            .andExpect(status().isBadRequest());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCoteries() throws Exception {
        // Initialize the database
        coteryRepository.saveAndFlush(cotery);

        // Get all the coteryList
        restCoteryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cotery.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].coteryName").value(hasItem(DEFAULT_COTERY_NAME)))
            .andExpect(jsonPath("$.[*].studentFullName").value(hasItem(DEFAULT_STUDENT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].attendanceStatus").value(hasItem(DEFAULT_ATTENDANCE_STATUS.toString())));
    }

    @Test
    @Transactional
    void getCotery() throws Exception {
        // Initialize the database
        coteryRepository.saveAndFlush(cotery);

        // Get the cotery
        restCoteryMockMvc
            .perform(get(ENTITY_API_URL_ID, cotery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cotery.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.coteryName").value(DEFAULT_COTERY_NAME))
            .andExpect(jsonPath("$.studentFullName").value(DEFAULT_STUDENT_FULL_NAME))
            .andExpect(jsonPath("$.attendanceStatus").value(DEFAULT_ATTENDANCE_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCotery() throws Exception {
        // Get the cotery
        restCoteryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCotery() throws Exception {
        // Initialize the database
        coteryRepository.saveAndFlush(cotery);

        int databaseSizeBeforeUpdate = coteryRepository.findAll().size();
        coterySearchRepository.save(cotery);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());

        // Update the cotery
        Cotery updatedCotery = coteryRepository.findById(cotery.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCotery are not directly saved in db
        em.detach(updatedCotery);
        updatedCotery
            .date(UPDATED_DATE)
            .coteryName(UPDATED_COTERY_NAME)
            .studentFullName(UPDATED_STUDENT_FULL_NAME)
            .attendanceStatus(UPDATED_ATTENDANCE_STATUS);

        restCoteryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCotery.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCotery))
            )
            .andExpect(status().isOk());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeUpdate);
        Cotery testCotery = coteryList.get(coteryList.size() - 1);
        assertThat(testCotery.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCotery.getCoteryName()).isEqualTo(UPDATED_COTERY_NAME);
        assertThat(testCotery.getStudentFullName()).isEqualTo(UPDATED_STUDENT_FULL_NAME);
        assertThat(testCotery.getAttendanceStatus()).isEqualTo(UPDATED_ATTENDANCE_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Cotery> coterySearchList = IterableUtils.toList(coterySearchRepository.findAll());
                Cotery testCoterySearch = coterySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCoterySearch.getDate()).isEqualTo(UPDATED_DATE);
                assertThat(testCoterySearch.getCoteryName()).isEqualTo(UPDATED_COTERY_NAME);
                assertThat(testCoterySearch.getStudentFullName()).isEqualTo(UPDATED_STUDENT_FULL_NAME);
                assertThat(testCoterySearch.getAttendanceStatus()).isEqualTo(UPDATED_ATTENDANCE_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingCotery() throws Exception {
        int databaseSizeBeforeUpdate = coteryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());
        cotery.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoteryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cotery.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cotery))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCotery() throws Exception {
        int databaseSizeBeforeUpdate = coteryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());
        cotery.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoteryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cotery))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCotery() throws Exception {
        int databaseSizeBeforeUpdate = coteryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());
        cotery.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoteryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cotery)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCoteryWithPatch() throws Exception {
        // Initialize the database
        coteryRepository.saveAndFlush(cotery);

        int databaseSizeBeforeUpdate = coteryRepository.findAll().size();

        // Update the cotery using partial update
        Cotery partialUpdatedCotery = new Cotery();
        partialUpdatedCotery.setId(cotery.getId());

        partialUpdatedCotery.studentFullName(UPDATED_STUDENT_FULL_NAME);

        restCoteryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCotery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCotery))
            )
            .andExpect(status().isOk());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeUpdate);
        Cotery testCotery = coteryList.get(coteryList.size() - 1);
        assertThat(testCotery.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testCotery.getCoteryName()).isEqualTo(DEFAULT_COTERY_NAME);
        assertThat(testCotery.getStudentFullName()).isEqualTo(UPDATED_STUDENT_FULL_NAME);
        assertThat(testCotery.getAttendanceStatus()).isEqualTo(DEFAULT_ATTENDANCE_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateCoteryWithPatch() throws Exception {
        // Initialize the database
        coteryRepository.saveAndFlush(cotery);

        int databaseSizeBeforeUpdate = coteryRepository.findAll().size();

        // Update the cotery using partial update
        Cotery partialUpdatedCotery = new Cotery();
        partialUpdatedCotery.setId(cotery.getId());

        partialUpdatedCotery
            .date(UPDATED_DATE)
            .coteryName(UPDATED_COTERY_NAME)
            .studentFullName(UPDATED_STUDENT_FULL_NAME)
            .attendanceStatus(UPDATED_ATTENDANCE_STATUS);

        restCoteryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCotery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCotery))
            )
            .andExpect(status().isOk());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeUpdate);
        Cotery testCotery = coteryList.get(coteryList.size() - 1);
        assertThat(testCotery.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCotery.getCoteryName()).isEqualTo(UPDATED_COTERY_NAME);
        assertThat(testCotery.getStudentFullName()).isEqualTo(UPDATED_STUDENT_FULL_NAME);
        assertThat(testCotery.getAttendanceStatus()).isEqualTo(UPDATED_ATTENDANCE_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingCotery() throws Exception {
        int databaseSizeBeforeUpdate = coteryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());
        cotery.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoteryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cotery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cotery))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCotery() throws Exception {
        int databaseSizeBeforeUpdate = coteryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());
        cotery.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoteryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cotery))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCotery() throws Exception {
        int databaseSizeBeforeUpdate = coteryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());
        cotery.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoteryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cotery)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cotery in the database
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCotery() throws Exception {
        // Initialize the database
        coteryRepository.saveAndFlush(cotery);
        coteryRepository.save(cotery);
        coterySearchRepository.save(cotery);

        int databaseSizeBeforeDelete = coteryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coterySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the cotery
        restCoteryMockMvc
            .perform(delete(ENTITY_API_URL_ID, cotery.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cotery> coteryList = coteryRepository.findAll();
        assertThat(coteryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coterySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCotery() throws Exception {
        // Initialize the database
        cotery = coteryRepository.saveAndFlush(cotery);
        coterySearchRepository.save(cotery);

        // Search the cotery
        restCoteryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + cotery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cotery.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].coteryName").value(hasItem(DEFAULT_COTERY_NAME)))
            .andExpect(jsonPath("$.[*].studentFullName").value(hasItem(DEFAULT_STUDENT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].attendanceStatus").value(hasItem(DEFAULT_ATTENDANCE_STATUS.toString())));
    }
}
