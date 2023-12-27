package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Sponsoring;
import com.wiam.lms.repository.SponsoringRepository;
import com.wiam.lms.repository.search.SponsoringSearchRepository;
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
 * Integration tests for the {@link SponsoringResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SponsoringResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Double DEFAULT_AMOUNT = 0D;
    private static final Double UPDATED_AMOUNT = 1D;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_IS_ALWAYS = false;
    private static final Boolean UPDATED_IS_ALWAYS = true;

    private static final String ENTITY_API_URL = "/api/sponsorings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sponsorings/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SponsoringRepository sponsoringRepository;

    @Autowired
    private SponsoringSearchRepository sponsoringSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSponsoringMockMvc;

    private Sponsoring sponsoring;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sponsoring createEntity(EntityManager em) {
        Sponsoring sponsoring = new Sponsoring()
            .message(DEFAULT_MESSAGE)
            .amount(DEFAULT_AMOUNT)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .isAlways(DEFAULT_IS_ALWAYS);
        return sponsoring;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sponsoring createUpdatedEntity(EntityManager em) {
        Sponsoring sponsoring = new Sponsoring()
            .message(UPDATED_MESSAGE)
            .amount(UPDATED_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .isAlways(UPDATED_IS_ALWAYS);
        return sponsoring;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        sponsoringSearchRepository.deleteAll();
        assertThat(sponsoringSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        sponsoring = createEntity(em);
    }

    @Test
    @Transactional
    void createSponsoring() throws Exception {
        int databaseSizeBeforeCreate = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        // Create the Sponsoring
        restSponsoringMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sponsoring)))
            .andExpect(status().isCreated());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Sponsoring testSponsoring = sponsoringList.get(sponsoringList.size() - 1);
        assertThat(testSponsoring.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testSponsoring.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testSponsoring.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testSponsoring.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testSponsoring.getIsAlways()).isEqualTo(DEFAULT_IS_ALWAYS);
    }

    @Test
    @Transactional
    void createSponsoringWithExistingId() throws Exception {
        // Create the Sponsoring with an existing ID
        sponsoring.setId(1L);

        int databaseSizeBeforeCreate = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSponsoringMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sponsoring)))
            .andExpect(status().isBadRequest());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        // set the field null
        sponsoring.setAmount(null);

        // Create the Sponsoring, which fails.

        restSponsoringMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sponsoring)))
            .andExpect(status().isBadRequest());

        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSponsorings() throws Exception {
        // Initialize the database
        sponsoringRepository.saveAndFlush(sponsoring);

        // Get all the sponsoringList
        restSponsoringMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sponsoring.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].isAlways").value(hasItem(DEFAULT_IS_ALWAYS.booleanValue())));
    }

    @Test
    @Transactional
    void getSponsoring() throws Exception {
        // Initialize the database
        sponsoringRepository.saveAndFlush(sponsoring);

        // Get the sponsoring
        restSponsoringMockMvc
            .perform(get(ENTITY_API_URL_ID, sponsoring.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sponsoring.getId().intValue()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.isAlways").value(DEFAULT_IS_ALWAYS.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingSponsoring() throws Exception {
        // Get the sponsoring
        restSponsoringMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSponsoring() throws Exception {
        // Initialize the database
        sponsoringRepository.saveAndFlush(sponsoring);

        int databaseSizeBeforeUpdate = sponsoringRepository.findAll().size();
        sponsoringSearchRepository.save(sponsoring);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());

        // Update the sponsoring
        Sponsoring updatedSponsoring = sponsoringRepository.findById(sponsoring.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSponsoring are not directly saved in db
        em.detach(updatedSponsoring);
        updatedSponsoring
            .message(UPDATED_MESSAGE)
            .amount(UPDATED_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .isAlways(UPDATED_IS_ALWAYS);

        restSponsoringMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSponsoring.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSponsoring))
            )
            .andExpect(status().isOk());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeUpdate);
        Sponsoring testSponsoring = sponsoringList.get(sponsoringList.size() - 1);
        assertThat(testSponsoring.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testSponsoring.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testSponsoring.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testSponsoring.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testSponsoring.getIsAlways()).isEqualTo(UPDATED_IS_ALWAYS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Sponsoring> sponsoringSearchList = IterableUtils.toList(sponsoringSearchRepository.findAll());
                Sponsoring testSponsoringSearch = sponsoringSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSponsoringSearch.getMessage()).isEqualTo(UPDATED_MESSAGE);
                assertThat(testSponsoringSearch.getAmount()).isEqualTo(UPDATED_AMOUNT);
                assertThat(testSponsoringSearch.getStartDate()).isEqualTo(UPDATED_START_DATE);
                assertThat(testSponsoringSearch.getEndDate()).isEqualTo(UPDATED_END_DATE);
                assertThat(testSponsoringSearch.getIsAlways()).isEqualTo(UPDATED_IS_ALWAYS);
            });
    }

    @Test
    @Transactional
    void putNonExistingSponsoring() throws Exception {
        int databaseSizeBeforeUpdate = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        sponsoring.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSponsoringMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sponsoring.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sponsoring))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSponsoring() throws Exception {
        int databaseSizeBeforeUpdate = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        sponsoring.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSponsoringMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sponsoring))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSponsoring() throws Exception {
        int databaseSizeBeforeUpdate = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        sponsoring.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSponsoringMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sponsoring)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSponsoringWithPatch() throws Exception {
        // Initialize the database
        sponsoringRepository.saveAndFlush(sponsoring);

        int databaseSizeBeforeUpdate = sponsoringRepository.findAll().size();

        // Update the sponsoring using partial update
        Sponsoring partialUpdatedSponsoring = new Sponsoring();
        partialUpdatedSponsoring.setId(sponsoring.getId());

        partialUpdatedSponsoring.message(UPDATED_MESSAGE);

        restSponsoringMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSponsoring.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSponsoring))
            )
            .andExpect(status().isOk());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeUpdate);
        Sponsoring testSponsoring = sponsoringList.get(sponsoringList.size() - 1);
        assertThat(testSponsoring.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testSponsoring.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testSponsoring.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testSponsoring.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testSponsoring.getIsAlways()).isEqualTo(DEFAULT_IS_ALWAYS);
    }

    @Test
    @Transactional
    void fullUpdateSponsoringWithPatch() throws Exception {
        // Initialize the database
        sponsoringRepository.saveAndFlush(sponsoring);

        int databaseSizeBeforeUpdate = sponsoringRepository.findAll().size();

        // Update the sponsoring using partial update
        Sponsoring partialUpdatedSponsoring = new Sponsoring();
        partialUpdatedSponsoring.setId(sponsoring.getId());

        partialUpdatedSponsoring
            .message(UPDATED_MESSAGE)
            .amount(UPDATED_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .isAlways(UPDATED_IS_ALWAYS);

        restSponsoringMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSponsoring.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSponsoring))
            )
            .andExpect(status().isOk());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeUpdate);
        Sponsoring testSponsoring = sponsoringList.get(sponsoringList.size() - 1);
        assertThat(testSponsoring.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testSponsoring.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testSponsoring.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testSponsoring.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testSponsoring.getIsAlways()).isEqualTo(UPDATED_IS_ALWAYS);
    }

    @Test
    @Transactional
    void patchNonExistingSponsoring() throws Exception {
        int databaseSizeBeforeUpdate = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        sponsoring.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSponsoringMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sponsoring.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sponsoring))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSponsoring() throws Exception {
        int databaseSizeBeforeUpdate = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        sponsoring.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSponsoringMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sponsoring))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSponsoring() throws Exception {
        int databaseSizeBeforeUpdate = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        sponsoring.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSponsoringMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sponsoring))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sponsoring in the database
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSponsoring() throws Exception {
        // Initialize the database
        sponsoringRepository.saveAndFlush(sponsoring);
        sponsoringRepository.save(sponsoring);
        sponsoringSearchRepository.save(sponsoring);

        int databaseSizeBeforeDelete = sponsoringRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the sponsoring
        restSponsoringMockMvc
            .perform(delete(ENTITY_API_URL_ID, sponsoring.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sponsoring> sponsoringList = sponsoringRepository.findAll();
        assertThat(sponsoringList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsoringSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSponsoring() throws Exception {
        // Initialize the database
        sponsoring = sponsoringRepository.saveAndFlush(sponsoring);
        sponsoringSearchRepository.save(sponsoring);

        // Search the sponsoring
        restSponsoringMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sponsoring.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sponsoring.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].isAlways").value(hasItem(DEFAULT_IS_ALWAYS.booleanValue())));
    }
}
