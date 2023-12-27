package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Part;
import com.wiam.lms.repository.PartRepository;
import com.wiam.lms.repository.search.PartSearchRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PartResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartResourceIT {

    private static final String DEFAULT_TITLE_AR = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_AR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_LAT = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_LAT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;

    private static final byte[] DEFAULT_IMAGE_LINK = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE_LINK = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_LINK_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_LINK_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_VIDEO_LINK = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_LINK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/parts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/parts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private PartSearchRepository partSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartMockMvc;

    private Part part;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Part createEntity(EntityManager em) {
        Part part = new Part()
            .titleAr(DEFAULT_TITLE_AR)
            .titleLat(DEFAULT_TITLE_LAT)
            .description(DEFAULT_DESCRIPTION)
            .duration(DEFAULT_DURATION)
            .imageLink(DEFAULT_IMAGE_LINK)
            .imageLinkContentType(DEFAULT_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(DEFAULT_VIDEO_LINK);
        return part;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Part createUpdatedEntity(EntityManager em) {
        Part part = new Part()
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .duration(UPDATED_DURATION)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK);
        return part;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        partSearchRepository.deleteAll();
        assertThat(partSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        part = createEntity(em);
    }

    @Test
    @Transactional
    void createPart() throws Exception {
        int databaseSizeBeforeCreate = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        // Create the Part
        restPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isCreated());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getTitleAr()).isEqualTo(DEFAULT_TITLE_AR);
        assertThat(testPart.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
        assertThat(testPart.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPart.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testPart.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
        assertThat(testPart.getImageLinkContentType()).isEqualTo(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testPart.getVideoLink()).isEqualTo(DEFAULT_VIDEO_LINK);
    }

    @Test
    @Transactional
    void createPartWithExistingId() throws Exception {
        // Create the Part with an existing ID
        part.setId(1L);

        int databaseSizeBeforeCreate = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleArIsRequired() throws Exception {
        int databaseSizeBeforeTest = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        // set the field null
        part.setTitleAr(null);

        // Create the Part, which fails.

        restPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isBadRequest());

        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleLatIsRequired() throws Exception {
        int databaseSizeBeforeTest = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        // set the field null
        part.setTitleLat(null);

        // Create the Part, which fails.

        restPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isBadRequest());

        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllParts() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList
        restPartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(part.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].imageLinkContentType").value(hasItem(DEFAULT_IMAGE_LINK_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageLink").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK))))
            .andExpect(jsonPath("$.[*].videoLink").value(hasItem(DEFAULT_VIDEO_LINK)));
    }

    @Test
    @Transactional
    void getPart() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get the part
        restPartMockMvc
            .perform(get(ENTITY_API_URL_ID, part.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(part.getId().intValue()))
            .andExpect(jsonPath("$.titleAr").value(DEFAULT_TITLE_AR))
            .andExpect(jsonPath("$.titleLat").value(DEFAULT_TITLE_LAT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION))
            .andExpect(jsonPath("$.imageLinkContentType").value(DEFAULT_IMAGE_LINK_CONTENT_TYPE))
            .andExpect(jsonPath("$.imageLink").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK)))
            .andExpect(jsonPath("$.videoLink").value(DEFAULT_VIDEO_LINK));
    }

    @Test
    @Transactional
    void getNonExistingPart() throws Exception {
        // Get the part
        restPartMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPart() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        partSearchRepository.save(part);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());

        // Update the part
        Part updatedPart = partRepository.findById(part.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPart are not directly saved in db
        em.detach(updatedPart);
        updatedPart
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .duration(UPDATED_DURATION)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK);

        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPart.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testPart.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testPart.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPart.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testPart.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testPart.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testPart.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Part> partSearchList = IterableUtils.toList(partSearchRepository.findAll());
                Part testPartSearch = partSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPartSearch.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
                assertThat(testPartSearch.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
                assertThat(testPartSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testPartSearch.getDuration()).isEqualTo(UPDATED_DURATION);
                assertThat(testPartSearch.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
                assertThat(testPartSearch.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
                assertThat(testPartSearch.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
            });
    }

    @Test
    @Transactional
    void putNonExistingPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        part.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, part.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(part))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        part.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(part))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        part.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePartWithPatch() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeUpdate = partRepository.findAll().size();

        // Update the part using partial update
        Part partialUpdatedPart = new Part();
        partialUpdatedPart.setId(part.getId());

        partialUpdatedPart
            .titleAr(UPDATED_TITLE_AR)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK);

        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPart.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testPart.getTitleLat()).isEqualTo(DEFAULT_TITLE_LAT);
        assertThat(testPart.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPart.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testPart.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testPart.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testPart.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
    }

    @Test
    @Transactional
    void fullUpdatePartWithPatch() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeUpdate = partRepository.findAll().size();

        // Update the part using partial update
        Part partialUpdatedPart = new Part();
        partialUpdatedPart.setId(part.getId());

        partialUpdatedPart
            .titleAr(UPDATED_TITLE_AR)
            .titleLat(UPDATED_TITLE_LAT)
            .description(UPDATED_DESCRIPTION)
            .duration(UPDATED_DURATION)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE)
            .videoLink(UPDATED_VIDEO_LINK);

        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPart.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getTitleAr()).isEqualTo(UPDATED_TITLE_AR);
        assertThat(testPart.getTitleLat()).isEqualTo(UPDATED_TITLE_LAT);
        assertThat(testPart.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPart.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testPart.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testPart.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        assertThat(testPart.getVideoLink()).isEqualTo(UPDATED_VIDEO_LINK);
    }

    @Test
    @Transactional
    void patchNonExistingPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        part.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, part.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(part))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        part.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(part))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        part.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePart() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);
        partRepository.save(part);
        partSearchRepository.save(part);

        int databaseSizeBeforeDelete = partRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the part
        restPartMockMvc
            .perform(delete(ENTITY_API_URL_ID, part.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(partSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPart() throws Exception {
        // Initialize the database
        part = partRepository.saveAndFlush(part);
        partSearchRepository.save(part);

        // Search the part
        restPartMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + part.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(part.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleAr").value(hasItem(DEFAULT_TITLE_AR)))
            .andExpect(jsonPath("$.[*].titleLat").value(hasItem(DEFAULT_TITLE_LAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].imageLinkContentType").value(hasItem(DEFAULT_IMAGE_LINK_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageLink").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE_LINK))))
            .andExpect(jsonPath("$.[*].videoLink").value(hasItem(DEFAULT_VIDEO_LINK)));
    }
}
