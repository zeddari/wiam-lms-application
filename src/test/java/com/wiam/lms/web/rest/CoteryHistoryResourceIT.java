package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.CoteryHistory;
import com.wiam.lms.domain.enumeration.Attendance;
import com.wiam.lms.repository.CoteryHistoryRepository;
import com.wiam.lms.repository.search.CoteryHistorySearchRepository;
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
 * Integration tests for the {@link CoteryHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CoteryHistoryResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_COTERY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COTERY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_STUDENT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_STUDENT_FULL_NAME = "BBBBBBBBBB";

    private static final Attendance DEFAULT_ATTENDANCE_STATUS = Attendance.PRESENT;
    private static final Attendance UPDATED_ATTENDANCE_STATUS = Attendance.ABSENT;

    private static final String ENTITY_API_URL = "/api/cotery-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cotery-histories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CoteryHistoryRepository coteryHistoryRepository;

    @Autowired
    private CoteryHistorySearchRepository coteryHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCoteryHistoryMockMvc;

    private CoteryHistory coteryHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CoteryHistory createEntity(EntityManager em) {
        CoteryHistory coteryHistory = new CoteryHistory()
            .date(DEFAULT_DATE)
            .coteryName(DEFAULT_COTERY_NAME)
            .studentFullName(DEFAULT_STUDENT_FULL_NAME)
            .attendanceStatus(DEFAULT_ATTENDANCE_STATUS);
        return coteryHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CoteryHistory createUpdatedEntity(EntityManager em) {
        CoteryHistory coteryHistory = new CoteryHistory()
            .date(UPDATED_DATE)
            .coteryName(UPDATED_COTERY_NAME)
            .studentFullName(UPDATED_STUDENT_FULL_NAME)
            .attendanceStatus(UPDATED_ATTENDANCE_STATUS);
        return coteryHistory;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        coteryHistorySearchRepository.deleteAll();
        assertThat(coteryHistorySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        coteryHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createCoteryHistory() throws Exception {
        int databaseSizeBeforeCreate = coteryHistoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        // Create the CoteryHistory
        restCoteryHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coteryHistory)))
            .andExpect(status().isCreated());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        CoteryHistory testCoteryHistory = coteryHistoryList.get(coteryHistoryList.size() - 1);
        assertThat(testCoteryHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testCoteryHistory.getCoteryName()).isEqualTo(DEFAULT_COTERY_NAME);
        assertThat(testCoteryHistory.getStudentFullName()).isEqualTo(DEFAULT_STUDENT_FULL_NAME);
        assertThat(testCoteryHistory.getAttendanceStatus()).isEqualTo(DEFAULT_ATTENDANCE_STATUS);
    }

    @Test
    @Transactional
    void createCoteryHistoryWithExistingId() throws Exception {
        // Create the CoteryHistory with an existing ID
        coteryHistory.setId(1L);

        int databaseSizeBeforeCreate = coteryHistoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCoteryHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coteryHistory)))
            .andExpect(status().isBadRequest());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCoteryHistories() throws Exception {
        // Initialize the database
        coteryHistoryRepository.saveAndFlush(coteryHistory);

        // Get all the coteryHistoryList
        restCoteryHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coteryHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].coteryName").value(hasItem(DEFAULT_COTERY_NAME)))
            .andExpect(jsonPath("$.[*].studentFullName").value(hasItem(DEFAULT_STUDENT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].attendanceStatus").value(hasItem(DEFAULT_ATTENDANCE_STATUS.toString())));
    }

    @Test
    @Transactional
    void getCoteryHistory() throws Exception {
        // Initialize the database
        coteryHistoryRepository.saveAndFlush(coteryHistory);

        // Get the coteryHistory
        restCoteryHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, coteryHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(coteryHistory.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.coteryName").value(DEFAULT_COTERY_NAME))
            .andExpect(jsonPath("$.studentFullName").value(DEFAULT_STUDENT_FULL_NAME))
            .andExpect(jsonPath("$.attendanceStatus").value(DEFAULT_ATTENDANCE_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCoteryHistory() throws Exception {
        // Get the coteryHistory
        restCoteryHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCoteryHistory() throws Exception {
        // Initialize the database
        coteryHistoryRepository.saveAndFlush(coteryHistory);

        int databaseSizeBeforeUpdate = coteryHistoryRepository.findAll().size();
        coteryHistorySearchRepository.save(coteryHistory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());

        // Update the coteryHistory
        CoteryHistory updatedCoteryHistory = coteryHistoryRepository.findById(coteryHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCoteryHistory are not directly saved in db
        em.detach(updatedCoteryHistory);
        updatedCoteryHistory
            .date(UPDATED_DATE)
            .coteryName(UPDATED_COTERY_NAME)
            .studentFullName(UPDATED_STUDENT_FULL_NAME)
            .attendanceStatus(UPDATED_ATTENDANCE_STATUS);

        restCoteryHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCoteryHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCoteryHistory))
            )
            .andExpect(status().isOk());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeUpdate);
        CoteryHistory testCoteryHistory = coteryHistoryList.get(coteryHistoryList.size() - 1);
        assertThat(testCoteryHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCoteryHistory.getCoteryName()).isEqualTo(UPDATED_COTERY_NAME);
        assertThat(testCoteryHistory.getStudentFullName()).isEqualTo(UPDATED_STUDENT_FULL_NAME);
        assertThat(testCoteryHistory.getAttendanceStatus()).isEqualTo(UPDATED_ATTENDANCE_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CoteryHistory> coteryHistorySearchList = IterableUtils.toList(coteryHistorySearchRepository.findAll());
                CoteryHistory testCoteryHistorySearch = coteryHistorySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCoteryHistorySearch.getDate()).isEqualTo(UPDATED_DATE);
                assertThat(testCoteryHistorySearch.getCoteryName()).isEqualTo(UPDATED_COTERY_NAME);
                assertThat(testCoteryHistorySearch.getStudentFullName()).isEqualTo(UPDATED_STUDENT_FULL_NAME);
                assertThat(testCoteryHistorySearch.getAttendanceStatus()).isEqualTo(UPDATED_ATTENDANCE_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingCoteryHistory() throws Exception {
        int databaseSizeBeforeUpdate = coteryHistoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        coteryHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoteryHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, coteryHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(coteryHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCoteryHistory() throws Exception {
        int databaseSizeBeforeUpdate = coteryHistoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        coteryHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoteryHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(coteryHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCoteryHistory() throws Exception {
        int databaseSizeBeforeUpdate = coteryHistoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        coteryHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoteryHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coteryHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCoteryHistoryWithPatch() throws Exception {
        // Initialize the database
        coteryHistoryRepository.saveAndFlush(coteryHistory);

        int databaseSizeBeforeUpdate = coteryHistoryRepository.findAll().size();

        // Update the coteryHistory using partial update
        CoteryHistory partialUpdatedCoteryHistory = new CoteryHistory();
        partialUpdatedCoteryHistory.setId(coteryHistory.getId());

        partialUpdatedCoteryHistory.date(UPDATED_DATE).coteryName(UPDATED_COTERY_NAME).studentFullName(UPDATED_STUDENT_FULL_NAME);

        restCoteryHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoteryHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCoteryHistory))
            )
            .andExpect(status().isOk());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeUpdate);
        CoteryHistory testCoteryHistory = coteryHistoryList.get(coteryHistoryList.size() - 1);
        assertThat(testCoteryHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCoteryHistory.getCoteryName()).isEqualTo(UPDATED_COTERY_NAME);
        assertThat(testCoteryHistory.getStudentFullName()).isEqualTo(UPDATED_STUDENT_FULL_NAME);
        assertThat(testCoteryHistory.getAttendanceStatus()).isEqualTo(DEFAULT_ATTENDANCE_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateCoteryHistoryWithPatch() throws Exception {
        // Initialize the database
        coteryHistoryRepository.saveAndFlush(coteryHistory);

        int databaseSizeBeforeUpdate = coteryHistoryRepository.findAll().size();

        // Update the coteryHistory using partial update
        CoteryHistory partialUpdatedCoteryHistory = new CoteryHistory();
        partialUpdatedCoteryHistory.setId(coteryHistory.getId());

        partialUpdatedCoteryHistory
            .date(UPDATED_DATE)
            .coteryName(UPDATED_COTERY_NAME)
            .studentFullName(UPDATED_STUDENT_FULL_NAME)
            .attendanceStatus(UPDATED_ATTENDANCE_STATUS);

        restCoteryHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoteryHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCoteryHistory))
            )
            .andExpect(status().isOk());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeUpdate);
        CoteryHistory testCoteryHistory = coteryHistoryList.get(coteryHistoryList.size() - 1);
        assertThat(testCoteryHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCoteryHistory.getCoteryName()).isEqualTo(UPDATED_COTERY_NAME);
        assertThat(testCoteryHistory.getStudentFullName()).isEqualTo(UPDATED_STUDENT_FULL_NAME);
        assertThat(testCoteryHistory.getAttendanceStatus()).isEqualTo(UPDATED_ATTENDANCE_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingCoteryHistory() throws Exception {
        int databaseSizeBeforeUpdate = coteryHistoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        coteryHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoteryHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, coteryHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(coteryHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCoteryHistory() throws Exception {
        int databaseSizeBeforeUpdate = coteryHistoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        coteryHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoteryHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(coteryHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCoteryHistory() throws Exception {
        int databaseSizeBeforeUpdate = coteryHistoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        coteryHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoteryHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(coteryHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CoteryHistory in the database
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCoteryHistory() throws Exception {
        // Initialize the database
        coteryHistoryRepository.saveAndFlush(coteryHistory);
        coteryHistoryRepository.save(coteryHistory);
        coteryHistorySearchRepository.save(coteryHistory);

        int databaseSizeBeforeDelete = coteryHistoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the coteryHistory
        restCoteryHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, coteryHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CoteryHistory> coteryHistoryList = coteryHistoryRepository.findAll();
        assertThat(coteryHistoryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(coteryHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCoteryHistory() throws Exception {
        // Initialize the database
        coteryHistory = coteryHistoryRepository.saveAndFlush(coteryHistory);
        coteryHistorySearchRepository.save(coteryHistory);

        // Search the coteryHistory
        restCoteryHistoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + coteryHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coteryHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].coteryName").value(hasItem(DEFAULT_COTERY_NAME)))
            .andExpect(jsonPath("$.[*].studentFullName").value(hasItem(DEFAULT_STUDENT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].attendanceStatus").value(hasItem(DEFAULT_ATTENDANCE_STATUS.toString())));
    }
}
