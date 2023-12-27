package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Sponsor;
import com.wiam.lms.repository.SponsorRepository;
import com.wiam.lms.repository.search.SponsorSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link SponsorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SponsorResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE_NUMBER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_GENDER = false;
    private static final Boolean UPDATED_GENDER = true;

    private static final String DEFAULT_ABOUT = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE_LINK = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE_LINK = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_LINK_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_LINK_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTHDATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTHDATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_LAST_DEGREE = "AAAAAAAAAA";
    private static final String UPDATED_LAST_DEGREE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sponsors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sponsors/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SponsorRepository sponsorRepository;

    @Mock
    private SponsorRepository sponsorRepositoryMock;

    @Autowired
    private SponsorSearchRepository sponsorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSponsorMockMvc;

    private Sponsor sponsor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sponsor createEntity(EntityManager em) {
        Sponsor sponsor = new Sponsor()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .mobileNumber(DEFAULT_MOBILE_NUMBER)
            .gender(DEFAULT_GENDER)
            .about(DEFAULT_ABOUT)
            .imageLink(DEFAULT_IMAGE_LINK)
            .imageLinkContentType(DEFAULT_IMAGE_LINK_CONTENT_TYPE)
            .code(DEFAULT_CODE)
            .birthdate(DEFAULT_BIRTHDATE)
            .lastDegree(DEFAULT_LAST_DEGREE);
        return sponsor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sponsor createUpdatedEntity(EntityManager em) {
        Sponsor sponsor = new Sponsor()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .mobileNumber(UPDATED_MOBILE_NUMBER)
            .gender(UPDATED_GENDER)
            .about(UPDATED_ABOUT)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .code(UPDATED_CODE)
            .birthdate(UPDATED_BIRTHDATE)
            .lastDegree(UPDATED_LAST_DEGREE);
        return sponsor;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        sponsorSearchRepository.deleteAll();
        assertThat(sponsorSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        sponsor = createEntity(em);
    }

    @Test
    @Transactional
    void createSponsor() throws Exception {
        int databaseSizeBeforeCreate = sponsorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        // Create the Sponsor
        restSponsorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sponsor)))
            .andExpect(status().isCreated());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Sponsor testSponsor = sponsorList.get(sponsorList.size() - 1);
        assertThat(testSponsor.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testSponsor.getMobileNumber()).isEqualTo(DEFAULT_MOBILE_NUMBER);
        assertThat(testSponsor.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testSponsor.getAbout()).isEqualTo(DEFAULT_ABOUT);
        assertThat(testSponsor.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
        assertThat(testSponsor.getImageLinkContentType()).isEqualTo(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testSponsor.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testSponsor.getBirthdate()).isEqualTo(DEFAULT_BIRTHDATE);
        assertThat(testSponsor.getLastDegree()).isEqualTo(DEFAULT_LAST_DEGREE);
    }

    @Test
    @Transactional
    void createSponsorWithExistingId() throws Exception {
        // Create the Sponsor with an existing ID
        sponsor.setId(1L);

        int databaseSizeBeforeCreate = sponsorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSponsorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sponsor)))
            .andExpect(status().isBadRequest());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSponsors() throws Exception {
        // Initialize the database
        sponsorRepository.saveAndFlush(sponsor);

        // Get all the sponsorList
        restSponsorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sponsor.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].mobileNumber").value(hasItem(DEFAULT_MOBILE_NUMBER)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.booleanValue())))
            .andExpect(jsonPath("$.[*].about").value(hasItem(DEFAULT_ABOUT)))
            .andExpect(jsonPath("$.[*].imageLinkContentType").value(hasItem(DEFAULT_IMAGE_LINK_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageLink").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK))))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].birthdate").value(hasItem(DEFAULT_BIRTHDATE.toString())))
            .andExpect(jsonPath("$.[*].lastDegree").value(hasItem(DEFAULT_LAST_DEGREE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSponsorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(sponsorRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSponsorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sponsorRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSponsorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sponsorRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSponsorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sponsorRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSponsor() throws Exception {
        // Initialize the database
        sponsorRepository.saveAndFlush(sponsor);

        // Get the sponsor
        restSponsorMockMvc
            .perform(get(ENTITY_API_URL_ID, sponsor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sponsor.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.mobileNumber").value(DEFAULT_MOBILE_NUMBER))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.booleanValue()))
            .andExpect(jsonPath("$.about").value(DEFAULT_ABOUT))
            .andExpect(jsonPath("$.imageLinkContentType").value(DEFAULT_IMAGE_LINK_CONTENT_TYPE))
            .andExpect(jsonPath("$.imageLink").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK)))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.birthdate").value(DEFAULT_BIRTHDATE.toString()))
            .andExpect(jsonPath("$.lastDegree").value(DEFAULT_LAST_DEGREE));
    }

    @Test
    @Transactional
    void getNonExistingSponsor() throws Exception {
        // Get the sponsor
        restSponsorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSponsor() throws Exception {
        // Initialize the database
        sponsorRepository.saveAndFlush(sponsor);

        int databaseSizeBeforeUpdate = sponsorRepository.findAll().size();
        sponsorSearchRepository.save(sponsor);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());

        // Update the sponsor
        Sponsor updatedSponsor = sponsorRepository.findById(sponsor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSponsor are not directly saved in db
        em.detach(updatedSponsor);
        updatedSponsor
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .mobileNumber(UPDATED_MOBILE_NUMBER)
            .gender(UPDATED_GENDER)
            .about(UPDATED_ABOUT)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .code(UPDATED_CODE)
            .birthdate(UPDATED_BIRTHDATE)
            .lastDegree(UPDATED_LAST_DEGREE);

        restSponsorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSponsor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSponsor))
            )
            .andExpect(status().isOk());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeUpdate);
        Sponsor testSponsor = sponsorList.get(sponsorList.size() - 1);
        assertThat(testSponsor.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testSponsor.getMobileNumber()).isEqualTo(UPDATED_MOBILE_NUMBER);
        assertThat(testSponsor.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testSponsor.getAbout()).isEqualTo(UPDATED_ABOUT);
        assertThat(testSponsor.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testSponsor.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testSponsor.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSponsor.getBirthdate()).isEqualTo(UPDATED_BIRTHDATE);
        assertThat(testSponsor.getLastDegree()).isEqualTo(UPDATED_LAST_DEGREE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Sponsor> sponsorSearchList = IterableUtils.toList(sponsorSearchRepository.findAll());
                Sponsor testSponsorSearch = sponsorSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSponsorSearch.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
                assertThat(testSponsorSearch.getMobileNumber()).isEqualTo(UPDATED_MOBILE_NUMBER);
                assertThat(testSponsorSearch.getGender()).isEqualTo(UPDATED_GENDER);
                assertThat(testSponsorSearch.getAbout()).isEqualTo(UPDATED_ABOUT);
                assertThat(testSponsorSearch.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
                assertThat(testSponsorSearch.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
                assertThat(testSponsorSearch.getCode()).isEqualTo(UPDATED_CODE);
                assertThat(testSponsorSearch.getBirthdate()).isEqualTo(UPDATED_BIRTHDATE);
                assertThat(testSponsorSearch.getLastDegree()).isEqualTo(UPDATED_LAST_DEGREE);
            });
    }

    @Test
    @Transactional
    void putNonExistingSponsor() throws Exception {
        int databaseSizeBeforeUpdate = sponsorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        sponsor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSponsorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sponsor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sponsor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSponsor() throws Exception {
        int databaseSizeBeforeUpdate = sponsorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        sponsor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSponsorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sponsor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSponsor() throws Exception {
        int databaseSizeBeforeUpdate = sponsorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        sponsor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSponsorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sponsor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSponsorWithPatch() throws Exception {
        // Initialize the database
        sponsorRepository.saveAndFlush(sponsor);

        int databaseSizeBeforeUpdate = sponsorRepository.findAll().size();

        // Update the sponsor using partial update
        Sponsor partialUpdatedSponsor = new Sponsor();
        partialUpdatedSponsor.setId(sponsor.getId());

        partialUpdatedSponsor.gender(UPDATED_GENDER).code(UPDATED_CODE);

        restSponsorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSponsor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSponsor))
            )
            .andExpect(status().isOk());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeUpdate);
        Sponsor testSponsor = sponsorList.get(sponsorList.size() - 1);
        assertThat(testSponsor.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testSponsor.getMobileNumber()).isEqualTo(DEFAULT_MOBILE_NUMBER);
        assertThat(testSponsor.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testSponsor.getAbout()).isEqualTo(DEFAULT_ABOUT);
        assertThat(testSponsor.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
        assertThat(testSponsor.getImageLinkContentType()).isEqualTo(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testSponsor.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSponsor.getBirthdate()).isEqualTo(DEFAULT_BIRTHDATE);
        assertThat(testSponsor.getLastDegree()).isEqualTo(DEFAULT_LAST_DEGREE);
    }

    @Test
    @Transactional
    void fullUpdateSponsorWithPatch() throws Exception {
        // Initialize the database
        sponsorRepository.saveAndFlush(sponsor);

        int databaseSizeBeforeUpdate = sponsorRepository.findAll().size();

        // Update the sponsor using partial update
        Sponsor partialUpdatedSponsor = new Sponsor();
        partialUpdatedSponsor.setId(sponsor.getId());

        partialUpdatedSponsor
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .mobileNumber(UPDATED_MOBILE_NUMBER)
            .gender(UPDATED_GENDER)
            .about(UPDATED_ABOUT)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .code(UPDATED_CODE)
            .birthdate(UPDATED_BIRTHDATE)
            .lastDegree(UPDATED_LAST_DEGREE);

        restSponsorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSponsor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSponsor))
            )
            .andExpect(status().isOk());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeUpdate);
        Sponsor testSponsor = sponsorList.get(sponsorList.size() - 1);
        assertThat(testSponsor.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testSponsor.getMobileNumber()).isEqualTo(UPDATED_MOBILE_NUMBER);
        assertThat(testSponsor.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testSponsor.getAbout()).isEqualTo(UPDATED_ABOUT);
        assertThat(testSponsor.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testSponsor.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testSponsor.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSponsor.getBirthdate()).isEqualTo(UPDATED_BIRTHDATE);
        assertThat(testSponsor.getLastDegree()).isEqualTo(UPDATED_LAST_DEGREE);
    }

    @Test
    @Transactional
    void patchNonExistingSponsor() throws Exception {
        int databaseSizeBeforeUpdate = sponsorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        sponsor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSponsorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sponsor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sponsor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSponsor() throws Exception {
        int databaseSizeBeforeUpdate = sponsorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        sponsor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSponsorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sponsor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSponsor() throws Exception {
        int databaseSizeBeforeUpdate = sponsorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        sponsor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSponsorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sponsor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sponsor in the database
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSponsor() throws Exception {
        // Initialize the database
        sponsorRepository.saveAndFlush(sponsor);
        sponsorRepository.save(sponsor);
        sponsorSearchRepository.save(sponsor);

        int databaseSizeBeforeDelete = sponsorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the sponsor
        restSponsorMockMvc
            .perform(delete(ENTITY_API_URL_ID, sponsor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sponsor> sponsorList = sponsorRepository.findAll();
        assertThat(sponsorList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(sponsorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSponsor() throws Exception {
        // Initialize the database
        sponsor = sponsorRepository.saveAndFlush(sponsor);
        sponsorSearchRepository.save(sponsor);

        // Search the sponsor
        restSponsorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sponsor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sponsor.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].mobileNumber").value(hasItem(DEFAULT_MOBILE_NUMBER)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.booleanValue())))
            .andExpect(jsonPath("$.[*].about").value(hasItem(DEFAULT_ABOUT)))
            .andExpect(jsonPath("$.[*].imageLinkContentType").value(hasItem(DEFAULT_IMAGE_LINK_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageLink").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK))))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].birthdate").value(hasItem(DEFAULT_BIRTHDATE.toString())))
            .andExpect(jsonPath("$.[*].lastDegree").value(hasItem(DEFAULT_LAST_DEGREE)));
    }
}
