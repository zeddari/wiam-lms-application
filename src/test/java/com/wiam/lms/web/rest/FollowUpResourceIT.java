package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.FollowUp;
import com.wiam.lms.domain.enumeration.Sourate;
import com.wiam.lms.domain.enumeration.Sourate;
import com.wiam.lms.domain.enumeration.Tilawa;
import com.wiam.lms.repository.FollowUpRepository;
import com.wiam.lms.repository.search.FollowUpSearchRepository;
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
 * Integration tests for the {@link FollowUpResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FollowUpResourceIT {

    private static final Sourate DEFAULT_FROM_SOURATE = Sourate.FATIHA;
    private static final Sourate UPDATED_FROM_SOURATE = Sourate.BA9ARA;

    private static final Integer DEFAULT_FROM_AYA = 1;
    private static final Integer UPDATED_FROM_AYA = 2;

    private static final Sourate DEFAULT_TO_SOURATE = Sourate.FATIHA;
    private static final Sourate UPDATED_TO_SOURATE = Sourate.BA9ARA;

    private static final Integer DEFAULT_TO_AYA = 1;
    private static final Integer UPDATED_TO_AYA = 2;

    private static final Tilawa DEFAULT_TILAWA_TYPE = Tilawa.HIFD;
    private static final Tilawa UPDATED_TILAWA_TYPE = Tilawa.MORAJA3A;

    private static final String DEFAULT_NOTATION = "AAAAAAAAAA";
    private static final String UPDATED_NOTATION = "BBBBBBBBBB";

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/follow-ups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/follow-ups/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FollowUpRepository followUpRepository;

    @Autowired
    private FollowUpSearchRepository followUpSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFollowUpMockMvc;

    private FollowUp followUp;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FollowUp createEntity(EntityManager em) {
        FollowUp followUp = new FollowUp()
            .fromSourate(DEFAULT_FROM_SOURATE)
            .fromAya(DEFAULT_FROM_AYA)
            .toSourate(DEFAULT_TO_SOURATE)
            .toAya(DEFAULT_TO_AYA)
            .tilawaType(DEFAULT_TILAWA_TYPE)
            .notation(DEFAULT_NOTATION)
            .remarks(DEFAULT_REMARKS);
        return followUp;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FollowUp createUpdatedEntity(EntityManager em) {
        FollowUp followUp = new FollowUp()
            .fromSourate(UPDATED_FROM_SOURATE)
            .fromAya(UPDATED_FROM_AYA)
            .toSourate(UPDATED_TO_SOURATE)
            .toAya(UPDATED_TO_AYA)
            .tilawaType(UPDATED_TILAWA_TYPE)
            .notation(UPDATED_NOTATION)
            .remarks(UPDATED_REMARKS);
        return followUp;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        followUpSearchRepository.deleteAll();
        assertThat(followUpSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        followUp = createEntity(em);
    }

    @Test
    @Transactional
    void createFollowUp() throws Exception {
        int databaseSizeBeforeCreate = followUpRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        // Create the FollowUp
        restFollowUpMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(followUp)))
            .andExpect(status().isCreated());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        FollowUp testFollowUp = followUpList.get(followUpList.size() - 1);
        assertThat(testFollowUp.getFromSourate()).isEqualTo(DEFAULT_FROM_SOURATE);
        assertThat(testFollowUp.getFromAya()).isEqualTo(DEFAULT_FROM_AYA);
        assertThat(testFollowUp.getToSourate()).isEqualTo(DEFAULT_TO_SOURATE);
        assertThat(testFollowUp.getToAya()).isEqualTo(DEFAULT_TO_AYA);
        assertThat(testFollowUp.getTilawaType()).isEqualTo(DEFAULT_TILAWA_TYPE);
        assertThat(testFollowUp.getNotation()).isEqualTo(DEFAULT_NOTATION);
        assertThat(testFollowUp.getRemarks()).isEqualTo(DEFAULT_REMARKS);
    }

    @Test
    @Transactional
    void createFollowUpWithExistingId() throws Exception {
        // Create the FollowUp with an existing ID
        followUp.setId(1L);

        int databaseSizeBeforeCreate = followUpRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFollowUpMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(followUp)))
            .andExpect(status().isBadRequest());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllFollowUps() throws Exception {
        // Initialize the database
        followUpRepository.saveAndFlush(followUp);

        // Get all the followUpList
        restFollowUpMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(followUp.getId().intValue())))
            .andExpect(jsonPath("$.[*].fromSourate").value(hasItem(DEFAULT_FROM_SOURATE.toString())))
            .andExpect(jsonPath("$.[*].fromAya").value(hasItem(DEFAULT_FROM_AYA)))
            .andExpect(jsonPath("$.[*].toSourate").value(hasItem(DEFAULT_TO_SOURATE.toString())))
            .andExpect(jsonPath("$.[*].toAya").value(hasItem(DEFAULT_TO_AYA)))
            .andExpect(jsonPath("$.[*].tilawaType").value(hasItem(DEFAULT_TILAWA_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notation").value(hasItem(DEFAULT_NOTATION)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getFollowUp() throws Exception {
        // Initialize the database
        followUpRepository.saveAndFlush(followUp);

        // Get the followUp
        restFollowUpMockMvc
            .perform(get(ENTITY_API_URL_ID, followUp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(followUp.getId().intValue()))
            .andExpect(jsonPath("$.fromSourate").value(DEFAULT_FROM_SOURATE.toString()))
            .andExpect(jsonPath("$.fromAya").value(DEFAULT_FROM_AYA))
            .andExpect(jsonPath("$.toSourate").value(DEFAULT_TO_SOURATE.toString()))
            .andExpect(jsonPath("$.toAya").value(DEFAULT_TO_AYA))
            .andExpect(jsonPath("$.tilawaType").value(DEFAULT_TILAWA_TYPE.toString()))
            .andExpect(jsonPath("$.notation").value(DEFAULT_NOTATION))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingFollowUp() throws Exception {
        // Get the followUp
        restFollowUpMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFollowUp() throws Exception {
        // Initialize the database
        followUpRepository.saveAndFlush(followUp);

        int databaseSizeBeforeUpdate = followUpRepository.findAll().size();
        followUpSearchRepository.save(followUp);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());

        // Update the followUp
        FollowUp updatedFollowUp = followUpRepository.findById(followUp.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFollowUp are not directly saved in db
        em.detach(updatedFollowUp);
        updatedFollowUp
            .fromSourate(UPDATED_FROM_SOURATE)
            .fromAya(UPDATED_FROM_AYA)
            .toSourate(UPDATED_TO_SOURATE)
            .toAya(UPDATED_TO_AYA)
            .tilawaType(UPDATED_TILAWA_TYPE)
            .notation(UPDATED_NOTATION)
            .remarks(UPDATED_REMARKS);

        restFollowUpMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFollowUp.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFollowUp))
            )
            .andExpect(status().isOk());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeUpdate);
        FollowUp testFollowUp = followUpList.get(followUpList.size() - 1);
        assertThat(testFollowUp.getFromSourate()).isEqualTo(UPDATED_FROM_SOURATE);
        assertThat(testFollowUp.getFromAya()).isEqualTo(UPDATED_FROM_AYA);
        assertThat(testFollowUp.getToSourate()).isEqualTo(UPDATED_TO_SOURATE);
        assertThat(testFollowUp.getToAya()).isEqualTo(UPDATED_TO_AYA);
        assertThat(testFollowUp.getTilawaType()).isEqualTo(UPDATED_TILAWA_TYPE);
        assertThat(testFollowUp.getNotation()).isEqualTo(UPDATED_NOTATION);
        assertThat(testFollowUp.getRemarks()).isEqualTo(UPDATED_REMARKS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<FollowUp> followUpSearchList = IterableUtils.toList(followUpSearchRepository.findAll());
                FollowUp testFollowUpSearch = followUpSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFollowUpSearch.getFromSourate()).isEqualTo(UPDATED_FROM_SOURATE);
                assertThat(testFollowUpSearch.getFromAya()).isEqualTo(UPDATED_FROM_AYA);
                assertThat(testFollowUpSearch.getToSourate()).isEqualTo(UPDATED_TO_SOURATE);
                assertThat(testFollowUpSearch.getToAya()).isEqualTo(UPDATED_TO_AYA);
                assertThat(testFollowUpSearch.getTilawaType()).isEqualTo(UPDATED_TILAWA_TYPE);
                assertThat(testFollowUpSearch.getNotation()).isEqualTo(UPDATED_NOTATION);
                assertThat(testFollowUpSearch.getRemarks()).isEqualTo(UPDATED_REMARKS);
            });
    }

    @Test
    @Transactional
    void putNonExistingFollowUp() throws Exception {
        int databaseSizeBeforeUpdate = followUpRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        followUp.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowUpMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followUp.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(followUp))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchFollowUp() throws Exception {
        int databaseSizeBeforeUpdate = followUpRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        followUp.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowUpMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(followUp))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFollowUp() throws Exception {
        int databaseSizeBeforeUpdate = followUpRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        followUp.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowUpMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(followUp)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateFollowUpWithPatch() throws Exception {
        // Initialize the database
        followUpRepository.saveAndFlush(followUp);

        int databaseSizeBeforeUpdate = followUpRepository.findAll().size();

        // Update the followUp using partial update
        FollowUp partialUpdatedFollowUp = new FollowUp();
        partialUpdatedFollowUp.setId(followUp.getId());

        partialUpdatedFollowUp.fromSourate(UPDATED_FROM_SOURATE).fromAya(UPDATED_FROM_AYA).toSourate(UPDATED_TO_SOURATE);

        restFollowUpMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollowUp.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFollowUp))
            )
            .andExpect(status().isOk());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeUpdate);
        FollowUp testFollowUp = followUpList.get(followUpList.size() - 1);
        assertThat(testFollowUp.getFromSourate()).isEqualTo(UPDATED_FROM_SOURATE);
        assertThat(testFollowUp.getFromAya()).isEqualTo(UPDATED_FROM_AYA);
        assertThat(testFollowUp.getToSourate()).isEqualTo(UPDATED_TO_SOURATE);
        assertThat(testFollowUp.getToAya()).isEqualTo(DEFAULT_TO_AYA);
        assertThat(testFollowUp.getTilawaType()).isEqualTo(DEFAULT_TILAWA_TYPE);
        assertThat(testFollowUp.getNotation()).isEqualTo(DEFAULT_NOTATION);
        assertThat(testFollowUp.getRemarks()).isEqualTo(DEFAULT_REMARKS);
    }

    @Test
    @Transactional
    void fullUpdateFollowUpWithPatch() throws Exception {
        // Initialize the database
        followUpRepository.saveAndFlush(followUp);

        int databaseSizeBeforeUpdate = followUpRepository.findAll().size();

        // Update the followUp using partial update
        FollowUp partialUpdatedFollowUp = new FollowUp();
        partialUpdatedFollowUp.setId(followUp.getId());

        partialUpdatedFollowUp
            .fromSourate(UPDATED_FROM_SOURATE)
            .fromAya(UPDATED_FROM_AYA)
            .toSourate(UPDATED_TO_SOURATE)
            .toAya(UPDATED_TO_AYA)
            .tilawaType(UPDATED_TILAWA_TYPE)
            .notation(UPDATED_NOTATION)
            .remarks(UPDATED_REMARKS);

        restFollowUpMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollowUp.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFollowUp))
            )
            .andExpect(status().isOk());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeUpdate);
        FollowUp testFollowUp = followUpList.get(followUpList.size() - 1);
        assertThat(testFollowUp.getFromSourate()).isEqualTo(UPDATED_FROM_SOURATE);
        assertThat(testFollowUp.getFromAya()).isEqualTo(UPDATED_FROM_AYA);
        assertThat(testFollowUp.getToSourate()).isEqualTo(UPDATED_TO_SOURATE);
        assertThat(testFollowUp.getToAya()).isEqualTo(UPDATED_TO_AYA);
        assertThat(testFollowUp.getTilawaType()).isEqualTo(UPDATED_TILAWA_TYPE);
        assertThat(testFollowUp.getNotation()).isEqualTo(UPDATED_NOTATION);
        assertThat(testFollowUp.getRemarks()).isEqualTo(UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void patchNonExistingFollowUp() throws Exception {
        int databaseSizeBeforeUpdate = followUpRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        followUp.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowUpMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, followUp.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(followUp))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFollowUp() throws Exception {
        int databaseSizeBeforeUpdate = followUpRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        followUp.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowUpMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(followUp))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFollowUp() throws Exception {
        int databaseSizeBeforeUpdate = followUpRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        followUp.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowUpMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(followUp)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FollowUp in the database
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteFollowUp() throws Exception {
        // Initialize the database
        followUpRepository.saveAndFlush(followUp);
        followUpRepository.save(followUp);
        followUpSearchRepository.save(followUp);

        int databaseSizeBeforeDelete = followUpRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the followUp
        restFollowUpMockMvc
            .perform(delete(ENTITY_API_URL_ID, followUp.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FollowUp> followUpList = followUpRepository.findAll();
        assertThat(followUpList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(followUpSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchFollowUp() throws Exception {
        // Initialize the database
        followUp = followUpRepository.saveAndFlush(followUp);
        followUpSearchRepository.save(followUp);

        // Search the followUp
        restFollowUpMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + followUp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(followUp.getId().intValue())))
            .andExpect(jsonPath("$.[*].fromSourate").value(hasItem(DEFAULT_FROM_SOURATE.toString())))
            .andExpect(jsonPath("$.[*].fromAya").value(hasItem(DEFAULT_FROM_AYA)))
            .andExpect(jsonPath("$.[*].toSourate").value(hasItem(DEFAULT_TO_SOURATE.toString())))
            .andExpect(jsonPath("$.[*].toAya").value(hasItem(DEFAULT_TO_AYA)))
            .andExpect(jsonPath("$.[*].tilawaType").value(hasItem(DEFAULT_TILAWA_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notation").value(hasItem(DEFAULT_NOTATION)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }
}
