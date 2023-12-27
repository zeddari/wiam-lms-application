package com.wiam.lms.web.rest;

import static com.wiam.lms.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.LeaveRequest;
import com.wiam.lms.repository.LeaveRequestRepository;
import com.wiam.lms.repository.search.LeaveRequestSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link LeaveRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LeaveRequestResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_FROM = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_FROM = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_TO_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TO_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/leave-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/leave-requests/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveRequestSearchRepository leaveRequestSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLeaveRequestMockMvc;

    private LeaveRequest leaveRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LeaveRequest createEntity(EntityManager em) {
        LeaveRequest leaveRequest = new LeaveRequest()
            .title(DEFAULT_TITLE)
            .from(DEFAULT_FROM)
            .toDate(DEFAULT_TO_DATE)
            .details(DEFAULT_DETAILS);
        return leaveRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LeaveRequest createUpdatedEntity(EntityManager em) {
        LeaveRequest leaveRequest = new LeaveRequest()
            .title(UPDATED_TITLE)
            .from(UPDATED_FROM)
            .toDate(UPDATED_TO_DATE)
            .details(UPDATED_DETAILS);
        return leaveRequest;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        leaveRequestSearchRepository.deleteAll();
        assertThat(leaveRequestSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        leaveRequest = createEntity(em);
    }

    @Test
    @Transactional
    void createLeaveRequest() throws Exception {
        int databaseSizeBeforeCreate = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        // Create the LeaveRequest
        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leaveRequest)))
            .andExpect(status().isCreated());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        LeaveRequest testLeaveRequest = leaveRequestList.get(leaveRequestList.size() - 1);
        assertThat(testLeaveRequest.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testLeaveRequest.getFrom()).isEqualTo(DEFAULT_FROM);
        assertThat(testLeaveRequest.getToDate()).isEqualTo(DEFAULT_TO_DATE);
        assertThat(testLeaveRequest.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    @Transactional
    void createLeaveRequestWithExistingId() throws Exception {
        // Create the LeaveRequest with an existing ID
        leaveRequest.setId(1L);

        int databaseSizeBeforeCreate = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leaveRequest)))
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFromIsRequired() throws Exception {
        int databaseSizeBeforeTest = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        // set the field null
        leaveRequest.setFrom(null);

        // Create the LeaveRequest, which fails.

        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leaveRequest)))
            .andExpect(status().isBadRequest());

        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkToDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        // set the field null
        leaveRequest.setToDate(null);

        // Create the LeaveRequest, which fails.

        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leaveRequest)))
            .andExpect(status().isBadRequest());

        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllLeaveRequests() throws Exception {
        // Initialize the database
        leaveRequestRepository.saveAndFlush(leaveRequest);

        // Get all the leaveRequestList
        restLeaveRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(leaveRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].from").value(hasItem(sameInstant(DEFAULT_FROM))))
            .andExpect(jsonPath("$.[*].toDate").value(hasItem(sameInstant(DEFAULT_TO_DATE))))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }

    @Test
    @Transactional
    void getLeaveRequest() throws Exception {
        // Initialize the database
        leaveRequestRepository.saveAndFlush(leaveRequest);

        // Get the leaveRequest
        restLeaveRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, leaveRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(leaveRequest.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.from").value(sameInstant(DEFAULT_FROM)))
            .andExpect(jsonPath("$.toDate").value(sameInstant(DEFAULT_TO_DATE)))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLeaveRequest() throws Exception {
        // Get the leaveRequest
        restLeaveRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLeaveRequest() throws Exception {
        // Initialize the database
        leaveRequestRepository.saveAndFlush(leaveRequest);

        int databaseSizeBeforeUpdate = leaveRequestRepository.findAll().size();
        leaveRequestSearchRepository.save(leaveRequest);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());

        // Update the leaveRequest
        LeaveRequest updatedLeaveRequest = leaveRequestRepository.findById(leaveRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLeaveRequest are not directly saved in db
        em.detach(updatedLeaveRequest);
        updatedLeaveRequest.title(UPDATED_TITLE).from(UPDATED_FROM).toDate(UPDATED_TO_DATE).details(UPDATED_DETAILS);

        restLeaveRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLeaveRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLeaveRequest))
            )
            .andExpect(status().isOk());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeUpdate);
        LeaveRequest testLeaveRequest = leaveRequestList.get(leaveRequestList.size() - 1);
        assertThat(testLeaveRequest.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLeaveRequest.getFrom()).isEqualTo(UPDATED_FROM);
        assertThat(testLeaveRequest.getToDate()).isEqualTo(UPDATED_TO_DATE);
        assertThat(testLeaveRequest.getDetails()).isEqualTo(UPDATED_DETAILS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<LeaveRequest> leaveRequestSearchList = IterableUtils.toList(leaveRequestSearchRepository.findAll());
                LeaveRequest testLeaveRequestSearch = leaveRequestSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testLeaveRequestSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testLeaveRequestSearch.getFrom()).isEqualTo(UPDATED_FROM);
                assertThat(testLeaveRequestSearch.getToDate()).isEqualTo(UPDATED_TO_DATE);
                assertThat(testLeaveRequestSearch.getDetails()).isEqualTo(UPDATED_DETAILS);
            });
    }

    @Test
    @Transactional
    void putNonExistingLeaveRequest() throws Exception {
        int databaseSizeBeforeUpdate = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        leaveRequest.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, leaveRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(leaveRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchLeaveRequest() throws Exception {
        int databaseSizeBeforeUpdate = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        leaveRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(leaveRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLeaveRequest() throws Exception {
        int databaseSizeBeforeUpdate = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        leaveRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leaveRequest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateLeaveRequestWithPatch() throws Exception {
        // Initialize the database
        leaveRequestRepository.saveAndFlush(leaveRequest);

        int databaseSizeBeforeUpdate = leaveRequestRepository.findAll().size();

        // Update the leaveRequest using partial update
        LeaveRequest partialUpdatedLeaveRequest = new LeaveRequest();
        partialUpdatedLeaveRequest.setId(leaveRequest.getId());

        partialUpdatedLeaveRequest.from(UPDATED_FROM).toDate(UPDATED_TO_DATE);

        restLeaveRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeaveRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLeaveRequest))
            )
            .andExpect(status().isOk());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeUpdate);
        LeaveRequest testLeaveRequest = leaveRequestList.get(leaveRequestList.size() - 1);
        assertThat(testLeaveRequest.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testLeaveRequest.getFrom()).isEqualTo(UPDATED_FROM);
        assertThat(testLeaveRequest.getToDate()).isEqualTo(UPDATED_TO_DATE);
        assertThat(testLeaveRequest.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    @Transactional
    void fullUpdateLeaveRequestWithPatch() throws Exception {
        // Initialize the database
        leaveRequestRepository.saveAndFlush(leaveRequest);

        int databaseSizeBeforeUpdate = leaveRequestRepository.findAll().size();

        // Update the leaveRequest using partial update
        LeaveRequest partialUpdatedLeaveRequest = new LeaveRequest();
        partialUpdatedLeaveRequest.setId(leaveRequest.getId());

        partialUpdatedLeaveRequest.title(UPDATED_TITLE).from(UPDATED_FROM).toDate(UPDATED_TO_DATE).details(UPDATED_DETAILS);

        restLeaveRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeaveRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLeaveRequest))
            )
            .andExpect(status().isOk());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeUpdate);
        LeaveRequest testLeaveRequest = leaveRequestList.get(leaveRequestList.size() - 1);
        assertThat(testLeaveRequest.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLeaveRequest.getFrom()).isEqualTo(UPDATED_FROM);
        assertThat(testLeaveRequest.getToDate()).isEqualTo(UPDATED_TO_DATE);
        assertThat(testLeaveRequest.getDetails()).isEqualTo(UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void patchNonExistingLeaveRequest() throws Exception {
        int databaseSizeBeforeUpdate = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        leaveRequest.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, leaveRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(leaveRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLeaveRequest() throws Exception {
        int databaseSizeBeforeUpdate = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        leaveRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(leaveRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLeaveRequest() throws Exception {
        int databaseSizeBeforeUpdate = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        leaveRequest.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(leaveRequest))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LeaveRequest in the database
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteLeaveRequest() throws Exception {
        // Initialize the database
        leaveRequestRepository.saveAndFlush(leaveRequest);
        leaveRequestRepository.save(leaveRequest);
        leaveRequestSearchRepository.save(leaveRequest);

        int databaseSizeBeforeDelete = leaveRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the leaveRequest
        restLeaveRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, leaveRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll();
        assertThat(leaveRequestList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leaveRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchLeaveRequest() throws Exception {
        // Initialize the database
        leaveRequest = leaveRequestRepository.saveAndFlush(leaveRequest);
        leaveRequestSearchRepository.save(leaveRequest);

        // Search the leaveRequest
        restLeaveRequestMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + leaveRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(leaveRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].from").value(hasItem(sameInstant(DEFAULT_FROM))))
            .andExpect(jsonPath("$.[*].toDate").value(hasItem(sameInstant(DEFAULT_TO_DATE))))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }
}
