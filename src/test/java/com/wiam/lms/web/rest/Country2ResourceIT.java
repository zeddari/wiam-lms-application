package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Country2;
import com.wiam.lms.repository.Country2Repository;
import com.wiam.lms.repository.search.Country2SearchRepository;
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
 * Integration tests for the {@link Country2Resource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class Country2ResourceIT {

    private static final String DEFAULT_COUNTRY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/country-2-s";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/country-2-s/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private Country2Repository country2Repository;

    @Autowired
    private Country2SearchRepository country2SearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCountry2MockMvc;

    private Country2 country2;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country2 createEntity(EntityManager em) {
        Country2 country2 = new Country2().countryName(DEFAULT_COUNTRY_NAME);
        return country2;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country2 createUpdatedEntity(EntityManager em) {
        Country2 country2 = new Country2().countryName(UPDATED_COUNTRY_NAME);
        return country2;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        country2SearchRepository.deleteAll();
        assertThat(country2SearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        country2 = createEntity(em);
    }

    @Test
    @Transactional
    void createCountry2() throws Exception {
        int databaseSizeBeforeCreate = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());
        // Create the Country2
        restCountry2MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(country2)))
            .andExpect(status().isCreated());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Country2 testCountry2 = country2List.get(country2List.size() - 1);
        assertThat(testCountry2.getCountryName()).isEqualTo(DEFAULT_COUNTRY_NAME);
    }

    @Test
    @Transactional
    void createCountry2WithExistingId() throws Exception {
        // Create the Country2 with an existing ID
        country2.setId(1L);

        int databaseSizeBeforeCreate = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCountry2MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(country2)))
            .andExpect(status().isBadRequest());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCountryNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());
        // set the field null
        country2.setCountryName(null);

        // Create the Country2, which fails.

        restCountry2MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(country2)))
            .andExpect(status().isBadRequest());

        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCountry2s() throws Exception {
        // Initialize the database
        country2Repository.saveAndFlush(country2);

        // Get all the country2List
        restCountry2MockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(country2.getId().intValue())))
            .andExpect(jsonPath("$.[*].countryName").value(hasItem(DEFAULT_COUNTRY_NAME)));
    }

    @Test
    @Transactional
    void getCountry2() throws Exception {
        // Initialize the database
        country2Repository.saveAndFlush(country2);

        // Get the country2
        restCountry2MockMvc
            .perform(get(ENTITY_API_URL_ID, country2.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(country2.getId().intValue()))
            .andExpect(jsonPath("$.countryName").value(DEFAULT_COUNTRY_NAME));
    }

    @Test
    @Transactional
    void getNonExistingCountry2() throws Exception {
        // Get the country2
        restCountry2MockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCountry2() throws Exception {
        // Initialize the database
        country2Repository.saveAndFlush(country2);

        int databaseSizeBeforeUpdate = country2Repository.findAll().size();
        country2SearchRepository.save(country2);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());

        // Update the country2
        Country2 updatedCountry2 = country2Repository.findById(country2.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCountry2 are not directly saved in db
        em.detach(updatedCountry2);
        updatedCountry2.countryName(UPDATED_COUNTRY_NAME);

        restCountry2MockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCountry2.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCountry2))
            )
            .andExpect(status().isOk());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeUpdate);
        Country2 testCountry2 = country2List.get(country2List.size() - 1);
        assertThat(testCountry2.getCountryName()).isEqualTo(UPDATED_COUNTRY_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Country2> country2SearchList = IterableUtils.toList(country2SearchRepository.findAll());
                Country2 testCountry2Search = country2SearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCountry2Search.getCountryName()).isEqualTo(UPDATED_COUNTRY_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingCountry2() throws Exception {
        int databaseSizeBeforeUpdate = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());
        country2.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCountry2MockMvc
            .perform(
                put(ENTITY_API_URL_ID, country2.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(country2))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCountry2() throws Exception {
        int databaseSizeBeforeUpdate = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());
        country2.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountry2MockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(country2))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCountry2() throws Exception {
        int databaseSizeBeforeUpdate = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());
        country2.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountry2MockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(country2)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCountry2WithPatch() throws Exception {
        // Initialize the database
        country2Repository.saveAndFlush(country2);

        int databaseSizeBeforeUpdate = country2Repository.findAll().size();

        // Update the country2 using partial update
        Country2 partialUpdatedCountry2 = new Country2();
        partialUpdatedCountry2.setId(country2.getId());

        partialUpdatedCountry2.countryName(UPDATED_COUNTRY_NAME);

        restCountry2MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCountry2.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCountry2))
            )
            .andExpect(status().isOk());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeUpdate);
        Country2 testCountry2 = country2List.get(country2List.size() - 1);
        assertThat(testCountry2.getCountryName()).isEqualTo(UPDATED_COUNTRY_NAME);
    }

    @Test
    @Transactional
    void fullUpdateCountry2WithPatch() throws Exception {
        // Initialize the database
        country2Repository.saveAndFlush(country2);

        int databaseSizeBeforeUpdate = country2Repository.findAll().size();

        // Update the country2 using partial update
        Country2 partialUpdatedCountry2 = new Country2();
        partialUpdatedCountry2.setId(country2.getId());

        partialUpdatedCountry2.countryName(UPDATED_COUNTRY_NAME);

        restCountry2MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCountry2.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCountry2))
            )
            .andExpect(status().isOk());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeUpdate);
        Country2 testCountry2 = country2List.get(country2List.size() - 1);
        assertThat(testCountry2.getCountryName()).isEqualTo(UPDATED_COUNTRY_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingCountry2() throws Exception {
        int databaseSizeBeforeUpdate = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());
        country2.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCountry2MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, country2.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(country2))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCountry2() throws Exception {
        int databaseSizeBeforeUpdate = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());
        country2.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountry2MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(country2))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCountry2() throws Exception {
        int databaseSizeBeforeUpdate = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());
        country2.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountry2MockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(country2)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Country2 in the database
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCountry2() throws Exception {
        // Initialize the database
        country2Repository.saveAndFlush(country2);
        country2Repository.save(country2);
        country2SearchRepository.save(country2);

        int databaseSizeBeforeDelete = country2Repository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the country2
        restCountry2MockMvc
            .perform(delete(ENTITY_API_URL_ID, country2.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Country2> country2List = country2Repository.findAll();
        assertThat(country2List).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(country2SearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCountry2() throws Exception {
        // Initialize the database
        country2 = country2Repository.saveAndFlush(country2);
        country2SearchRepository.save(country2);

        // Search the country2
        restCountry2MockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + country2.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(country2.getId().intValue())))
            .andExpect(jsonPath("$.[*].countryName").value(hasItem(DEFAULT_COUNTRY_NAME)));
    }
}
