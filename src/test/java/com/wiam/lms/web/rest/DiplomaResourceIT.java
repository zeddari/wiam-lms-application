package com.wiam.lms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Diploma;
import com.wiam.lms.repository.DiplomaRepository;
import com.wiam.lms.repository.search.DiplomaSearchRepository;
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
 * Integration tests for the {@link DiplomaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DiplomaResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_DETAIL = "AAAAAAAAAA";
    private static final String UPDATED_DETAIL = "BBBBBBBBBB";

    private static final String DEFAULT_SUPERVISOR = "AAAAAAAAAA";
    private static final String UPDATED_SUPERVISOR = "BBBBBBBBBB";

    private static final String DEFAULT_GRADE = "AAAAAAAAAA";
    private static final String UPDATED_GRADE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_GRADUATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_GRADUATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_SCHOOL = "AAAAAAAAAA";
    private static final String UPDATED_SCHOOL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/diplomas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/diplomas/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DiplomaRepository diplomaRepository;

    @Autowired
    private DiplomaSearchRepository diplomaSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDiplomaMockMvc;

    private Diploma diploma;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Diploma createEntity(EntityManager em) {
        Diploma diploma = new Diploma()
            .title(DEFAULT_TITLE)
            .subject(DEFAULT_SUBJECT)
            .detail(DEFAULT_DETAIL)
            .supervisor(DEFAULT_SUPERVISOR)
            .grade(DEFAULT_GRADE)
            .graduationDate(DEFAULT_GRADUATION_DATE)
            .school(DEFAULT_SCHOOL);
        return diploma;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Diploma createUpdatedEntity(EntityManager em) {
        Diploma diploma = new Diploma()
            .title(UPDATED_TITLE)
            .subject(UPDATED_SUBJECT)
            .detail(UPDATED_DETAIL)
            .supervisor(UPDATED_SUPERVISOR)
            .grade(UPDATED_GRADE)
            .graduationDate(UPDATED_GRADUATION_DATE)
            .school(UPDATED_SCHOOL);
        return diploma;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        diplomaSearchRepository.deleteAll();
        assertThat(diplomaSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        diploma = createEntity(em);
    }

    @Test
    @Transactional
    void createDiploma() throws Exception {
        int databaseSizeBeforeCreate = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        // Create the Diploma
        restDiplomaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diploma)))
            .andExpect(status().isCreated());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Diploma testDiploma = diplomaList.get(diplomaList.size() - 1);
        assertThat(testDiploma.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testDiploma.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testDiploma.getDetail()).isEqualTo(DEFAULT_DETAIL);
        assertThat(testDiploma.getSupervisor()).isEqualTo(DEFAULT_SUPERVISOR);
        assertThat(testDiploma.getGrade()).isEqualTo(DEFAULT_GRADE);
        assertThat(testDiploma.getGraduationDate()).isEqualTo(DEFAULT_GRADUATION_DATE);
        assertThat(testDiploma.getSchool()).isEqualTo(DEFAULT_SCHOOL);
    }

    @Test
    @Transactional
    void createDiplomaWithExistingId() throws Exception {
        // Create the Diploma with an existing ID
        diploma.setId(1L);

        int databaseSizeBeforeCreate = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiplomaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diploma)))
            .andExpect(status().isBadRequest());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        // set the field null
        diploma.setTitle(null);

        // Create the Diploma, which fails.

        restDiplomaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diploma)))
            .andExpect(status().isBadRequest());

        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDiplomas() throws Exception {
        // Initialize the database
        diplomaRepository.saveAndFlush(diploma);

        // Get all the diplomaList
        restDiplomaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diploma.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].detail").value(hasItem(DEFAULT_DETAIL)))
            .andExpect(jsonPath("$.[*].supervisor").value(hasItem(DEFAULT_SUPERVISOR)))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE)))
            .andExpect(jsonPath("$.[*].graduationDate").value(hasItem(DEFAULT_GRADUATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].school").value(hasItem(DEFAULT_SCHOOL)));
    }

    @Test
    @Transactional
    void getDiploma() throws Exception {
        // Initialize the database
        diplomaRepository.saveAndFlush(diploma);

        // Get the diploma
        restDiplomaMockMvc
            .perform(get(ENTITY_API_URL_ID, diploma.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(diploma.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.detail").value(DEFAULT_DETAIL))
            .andExpect(jsonPath("$.supervisor").value(DEFAULT_SUPERVISOR))
            .andExpect(jsonPath("$.grade").value(DEFAULT_GRADE))
            .andExpect(jsonPath("$.graduationDate").value(DEFAULT_GRADUATION_DATE.toString()))
            .andExpect(jsonPath("$.school").value(DEFAULT_SCHOOL));
    }

    @Test
    @Transactional
    void getNonExistingDiploma() throws Exception {
        // Get the diploma
        restDiplomaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDiploma() throws Exception {
        // Initialize the database
        diplomaRepository.saveAndFlush(diploma);

        int databaseSizeBeforeUpdate = diplomaRepository.findAll().size();
        diplomaSearchRepository.save(diploma);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());

        // Update the diploma
        Diploma updatedDiploma = diplomaRepository.findById(diploma.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDiploma are not directly saved in db
        em.detach(updatedDiploma);
        updatedDiploma
            .title(UPDATED_TITLE)
            .subject(UPDATED_SUBJECT)
            .detail(UPDATED_DETAIL)
            .supervisor(UPDATED_SUPERVISOR)
            .grade(UPDATED_GRADE)
            .graduationDate(UPDATED_GRADUATION_DATE)
            .school(UPDATED_SCHOOL);

        restDiplomaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDiploma.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDiploma))
            )
            .andExpect(status().isOk());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeUpdate);
        Diploma testDiploma = diplomaList.get(diplomaList.size() - 1);
        assertThat(testDiploma.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testDiploma.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testDiploma.getDetail()).isEqualTo(UPDATED_DETAIL);
        assertThat(testDiploma.getSupervisor()).isEqualTo(UPDATED_SUPERVISOR);
        assertThat(testDiploma.getGrade()).isEqualTo(UPDATED_GRADE);
        assertThat(testDiploma.getGraduationDate()).isEqualTo(UPDATED_GRADUATION_DATE);
        assertThat(testDiploma.getSchool()).isEqualTo(UPDATED_SCHOOL);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Diploma> diplomaSearchList = IterableUtils.toList(diplomaSearchRepository.findAll());
                Diploma testDiplomaSearch = diplomaSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testDiplomaSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testDiplomaSearch.getSubject()).isEqualTo(UPDATED_SUBJECT);
                assertThat(testDiplomaSearch.getDetail()).isEqualTo(UPDATED_DETAIL);
                assertThat(testDiplomaSearch.getSupervisor()).isEqualTo(UPDATED_SUPERVISOR);
                assertThat(testDiplomaSearch.getGrade()).isEqualTo(UPDATED_GRADE);
                assertThat(testDiplomaSearch.getGraduationDate()).isEqualTo(UPDATED_GRADUATION_DATE);
                assertThat(testDiplomaSearch.getSchool()).isEqualTo(UPDATED_SCHOOL);
            });
    }

    @Test
    @Transactional
    void putNonExistingDiploma() throws Exception {
        int databaseSizeBeforeUpdate = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        diploma.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiplomaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, diploma.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(diploma))
            )
            .andExpect(status().isBadRequest());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDiploma() throws Exception {
        int databaseSizeBeforeUpdate = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        diploma.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiplomaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(diploma))
            )
            .andExpect(status().isBadRequest());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDiploma() throws Exception {
        int databaseSizeBeforeUpdate = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        diploma.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiplomaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diploma)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDiplomaWithPatch() throws Exception {
        // Initialize the database
        diplomaRepository.saveAndFlush(diploma);

        int databaseSizeBeforeUpdate = diplomaRepository.findAll().size();

        // Update the diploma using partial update
        Diploma partialUpdatedDiploma = new Diploma();
        partialUpdatedDiploma.setId(diploma.getId());

        partialUpdatedDiploma.title(UPDATED_TITLE).subject(UPDATED_SUBJECT).detail(UPDATED_DETAIL).graduationDate(UPDATED_GRADUATION_DATE);

        restDiplomaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiploma.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiploma))
            )
            .andExpect(status().isOk());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeUpdate);
        Diploma testDiploma = diplomaList.get(diplomaList.size() - 1);
        assertThat(testDiploma.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testDiploma.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testDiploma.getDetail()).isEqualTo(UPDATED_DETAIL);
        assertThat(testDiploma.getSupervisor()).isEqualTo(DEFAULT_SUPERVISOR);
        assertThat(testDiploma.getGrade()).isEqualTo(DEFAULT_GRADE);
        assertThat(testDiploma.getGraduationDate()).isEqualTo(UPDATED_GRADUATION_DATE);
        assertThat(testDiploma.getSchool()).isEqualTo(DEFAULT_SCHOOL);
    }

    @Test
    @Transactional
    void fullUpdateDiplomaWithPatch() throws Exception {
        // Initialize the database
        diplomaRepository.saveAndFlush(diploma);

        int databaseSizeBeforeUpdate = diplomaRepository.findAll().size();

        // Update the diploma using partial update
        Diploma partialUpdatedDiploma = new Diploma();
        partialUpdatedDiploma.setId(diploma.getId());

        partialUpdatedDiploma
            .title(UPDATED_TITLE)
            .subject(UPDATED_SUBJECT)
            .detail(UPDATED_DETAIL)
            .supervisor(UPDATED_SUPERVISOR)
            .grade(UPDATED_GRADE)
            .graduationDate(UPDATED_GRADUATION_DATE)
            .school(UPDATED_SCHOOL);

        restDiplomaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiploma.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiploma))
            )
            .andExpect(status().isOk());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeUpdate);
        Diploma testDiploma = diplomaList.get(diplomaList.size() - 1);
        assertThat(testDiploma.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testDiploma.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testDiploma.getDetail()).isEqualTo(UPDATED_DETAIL);
        assertThat(testDiploma.getSupervisor()).isEqualTo(UPDATED_SUPERVISOR);
        assertThat(testDiploma.getGrade()).isEqualTo(UPDATED_GRADE);
        assertThat(testDiploma.getGraduationDate()).isEqualTo(UPDATED_GRADUATION_DATE);
        assertThat(testDiploma.getSchool()).isEqualTo(UPDATED_SCHOOL);
    }

    @Test
    @Transactional
    void patchNonExistingDiploma() throws Exception {
        int databaseSizeBeforeUpdate = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        diploma.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiplomaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, diploma.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(diploma))
            )
            .andExpect(status().isBadRequest());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDiploma() throws Exception {
        int databaseSizeBeforeUpdate = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        diploma.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiplomaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(diploma))
            )
            .andExpect(status().isBadRequest());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDiploma() throws Exception {
        int databaseSizeBeforeUpdate = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        diploma.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiplomaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(diploma)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Diploma in the database
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDiploma() throws Exception {
        // Initialize the database
        diplomaRepository.saveAndFlush(diploma);
        diplomaRepository.save(diploma);
        diplomaSearchRepository.save(diploma);

        int databaseSizeBeforeDelete = diplomaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the diploma
        restDiplomaMockMvc
            .perform(delete(ENTITY_API_URL_ID, diploma.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Diploma> diplomaList = diplomaRepository.findAll();
        assertThat(diplomaList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDiploma() throws Exception {
        // Initialize the database
        diploma = diplomaRepository.saveAndFlush(diploma);
        diplomaSearchRepository.save(diploma);

        // Search the diploma
        restDiplomaMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + diploma.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diploma.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].detail").value(hasItem(DEFAULT_DETAIL)))
            .andExpect(jsonPath("$.[*].supervisor").value(hasItem(DEFAULT_SUPERVISOR)))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE)))
            .andExpect(jsonPath("$.[*].graduationDate").value(hasItem(DEFAULT_GRADUATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].school").value(hasItem(DEFAULT_SCHOOL)));
    }
}
