package com.wiam.lms.web.rest;

import static com.wiam.lms.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Tickets;
import com.wiam.lms.repository.TicketsRepository;
import com.wiam.lms.repository.search.TicketsSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link TicketsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketsResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_JUSTIF_DOC = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_JUSTIF_DOC = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_JUSTIF_DOC_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_JUSTIF_DOC_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_DATE_TICKET = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_TICKET = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DATE_PROCESS = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_PROCESS = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_PROCESSED = false;
    private static final Boolean UPDATED_PROCESSED = true;

    private static final String ENTITY_API_URL = "/api/tickets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/tickets/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TicketsRepository ticketsRepository;

    @Autowired
    private TicketsSearchRepository ticketsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketsMockMvc;

    private Tickets tickets;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tickets createEntity(EntityManager em) {
        Tickets tickets = new Tickets()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .justifDoc(DEFAULT_JUSTIF_DOC)
            .justifDocContentType(DEFAULT_JUSTIF_DOC_CONTENT_TYPE)
            .dateTicket(DEFAULT_DATE_TICKET)
            .dateProcess(DEFAULT_DATE_PROCESS)
            .processed(DEFAULT_PROCESSED);
        return tickets;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tickets createUpdatedEntity(EntityManager em) {
        Tickets tickets = new Tickets()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .justifDoc(UPDATED_JUSTIF_DOC)
            .justifDocContentType(UPDATED_JUSTIF_DOC_CONTENT_TYPE)
            .dateTicket(UPDATED_DATE_TICKET)
            .dateProcess(UPDATED_DATE_PROCESS)
            .processed(UPDATED_PROCESSED);
        return tickets;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        ticketsSearchRepository.deleteAll();
        assertThat(ticketsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        tickets = createEntity(em);
    }

    @Test
    @Transactional
    void createTickets() throws Exception {
        int databaseSizeBeforeCreate = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        // Create the Tickets
        restTicketsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tickets)))
            .andExpect(status().isCreated());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Tickets testTickets = ticketsList.get(ticketsList.size() - 1);
        assertThat(testTickets.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTickets.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTickets.getJustifDoc()).isEqualTo(DEFAULT_JUSTIF_DOC);
        assertThat(testTickets.getJustifDocContentType()).isEqualTo(DEFAULT_JUSTIF_DOC_CONTENT_TYPE);
        assertThat(testTickets.getDateTicket()).isEqualTo(DEFAULT_DATE_TICKET);
        assertThat(testTickets.getDateProcess()).isEqualTo(DEFAULT_DATE_PROCESS);
        assertThat(testTickets.getProcessed()).isEqualTo(DEFAULT_PROCESSED);
    }

    @Test
    @Transactional
    void createTicketsWithExistingId() throws Exception {
        // Create the Tickets with an existing ID
        tickets.setId(1L);

        int databaseSizeBeforeCreate = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tickets)))
            .andExpect(status().isBadRequest());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        // set the field null
        tickets.setTitle(null);

        // Create the Tickets, which fails.

        restTicketsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tickets)))
            .andExpect(status().isBadRequest());

        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDateTicketIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        // set the field null
        tickets.setDateTicket(null);

        // Create the Tickets, which fails.

        restTicketsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tickets)))
            .andExpect(status().isBadRequest());

        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTickets() throws Exception {
        // Initialize the database
        ticketsRepository.saveAndFlush(tickets);

        // Get all the ticketsList
        restTicketsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tickets.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].justifDocContentType").value(hasItem(DEFAULT_JUSTIF_DOC_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].justifDoc").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_JUSTIF_DOC))))
            .andExpect(jsonPath("$.[*].dateTicket").value(hasItem(sameInstant(DEFAULT_DATE_TICKET))))
            .andExpect(jsonPath("$.[*].dateProcess").value(hasItem(sameInstant(DEFAULT_DATE_PROCESS))))
            .andExpect(jsonPath("$.[*].processed").value(hasItem(DEFAULT_PROCESSED.booleanValue())));
    }

    @Test
    @Transactional
    void getTickets() throws Exception {
        // Initialize the database
        ticketsRepository.saveAndFlush(tickets);

        // Get the tickets
        restTicketsMockMvc
            .perform(get(ENTITY_API_URL_ID, tickets.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tickets.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.justifDocContentType").value(DEFAULT_JUSTIF_DOC_CONTENT_TYPE))
            .andExpect(jsonPath("$.justifDoc").value(Base64.getEncoder().encodeToString(DEFAULT_JUSTIF_DOC)))
            .andExpect(jsonPath("$.dateTicket").value(sameInstant(DEFAULT_DATE_TICKET)))
            .andExpect(jsonPath("$.dateProcess").value(sameInstant(DEFAULT_DATE_PROCESS)))
            .andExpect(jsonPath("$.processed").value(DEFAULT_PROCESSED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingTickets() throws Exception {
        // Get the tickets
        restTicketsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTickets() throws Exception {
        // Initialize the database
        ticketsRepository.saveAndFlush(tickets);

        int databaseSizeBeforeUpdate = ticketsRepository.findAll().size();
        ticketsSearchRepository.save(tickets);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());

        // Update the tickets
        Tickets updatedTickets = ticketsRepository.findById(tickets.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTickets are not directly saved in db
        em.detach(updatedTickets);
        updatedTickets
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .justifDoc(UPDATED_JUSTIF_DOC)
            .justifDocContentType(UPDATED_JUSTIF_DOC_CONTENT_TYPE)
            .dateTicket(UPDATED_DATE_TICKET)
            .dateProcess(UPDATED_DATE_PROCESS)
            .processed(UPDATED_PROCESSED);

        restTicketsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTickets.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTickets))
            )
            .andExpect(status().isOk());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeUpdate);
        Tickets testTickets = ticketsList.get(ticketsList.size() - 1);
        assertThat(testTickets.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTickets.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTickets.getJustifDoc()).isEqualTo(UPDATED_JUSTIF_DOC);
        assertThat(testTickets.getJustifDocContentType()).isEqualTo(UPDATED_JUSTIF_DOC_CONTENT_TYPE);
        assertThat(testTickets.getDateTicket()).isEqualTo(UPDATED_DATE_TICKET);
        assertThat(testTickets.getDateProcess()).isEqualTo(UPDATED_DATE_PROCESS);
        assertThat(testTickets.getProcessed()).isEqualTo(UPDATED_PROCESSED);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Tickets> ticketsSearchList = IterableUtils.toList(ticketsSearchRepository.findAll());
                Tickets testTicketsSearch = ticketsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTicketsSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testTicketsSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testTicketsSearch.getJustifDoc()).isEqualTo(UPDATED_JUSTIF_DOC);
                assertThat(testTicketsSearch.getJustifDocContentType()).isEqualTo(UPDATED_JUSTIF_DOC_CONTENT_TYPE);
                assertThat(testTicketsSearch.getDateTicket()).isEqualTo(UPDATED_DATE_TICKET);
                assertThat(testTicketsSearch.getDateProcess()).isEqualTo(UPDATED_DATE_PROCESS);
                assertThat(testTicketsSearch.getProcessed()).isEqualTo(UPDATED_PROCESSED);
            });
    }

    @Test
    @Transactional
    void putNonExistingTickets() throws Exception {
        int databaseSizeBeforeUpdate = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        tickets.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tickets.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tickets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTickets() throws Exception {
        int databaseSizeBeforeUpdate = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        tickets.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tickets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTickets() throws Exception {
        int databaseSizeBeforeUpdate = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        tickets.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tickets)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTicketsWithPatch() throws Exception {
        // Initialize the database
        ticketsRepository.saveAndFlush(tickets);

        int databaseSizeBeforeUpdate = ticketsRepository.findAll().size();

        // Update the tickets using partial update
        Tickets partialUpdatedTickets = new Tickets();
        partialUpdatedTickets.setId(tickets.getId());

        partialUpdatedTickets
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .justifDoc(UPDATED_JUSTIF_DOC)
            .justifDocContentType(UPDATED_JUSTIF_DOC_CONTENT_TYPE);

        restTicketsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTickets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTickets))
            )
            .andExpect(status().isOk());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeUpdate);
        Tickets testTickets = ticketsList.get(ticketsList.size() - 1);
        assertThat(testTickets.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTickets.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTickets.getJustifDoc()).isEqualTo(UPDATED_JUSTIF_DOC);
        assertThat(testTickets.getJustifDocContentType()).isEqualTo(UPDATED_JUSTIF_DOC_CONTENT_TYPE);
        assertThat(testTickets.getDateTicket()).isEqualTo(DEFAULT_DATE_TICKET);
        assertThat(testTickets.getDateProcess()).isEqualTo(DEFAULT_DATE_PROCESS);
        assertThat(testTickets.getProcessed()).isEqualTo(DEFAULT_PROCESSED);
    }

    @Test
    @Transactional
    void fullUpdateTicketsWithPatch() throws Exception {
        // Initialize the database
        ticketsRepository.saveAndFlush(tickets);

        int databaseSizeBeforeUpdate = ticketsRepository.findAll().size();

        // Update the tickets using partial update
        Tickets partialUpdatedTickets = new Tickets();
        partialUpdatedTickets.setId(tickets.getId());

        partialUpdatedTickets
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .justifDoc(UPDATED_JUSTIF_DOC)
            .justifDocContentType(UPDATED_JUSTIF_DOC_CONTENT_TYPE)
            .dateTicket(UPDATED_DATE_TICKET)
            .dateProcess(UPDATED_DATE_PROCESS)
            .processed(UPDATED_PROCESSED);

        restTicketsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTickets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTickets))
            )
            .andExpect(status().isOk());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeUpdate);
        Tickets testTickets = ticketsList.get(ticketsList.size() - 1);
        assertThat(testTickets.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTickets.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTickets.getJustifDoc()).isEqualTo(UPDATED_JUSTIF_DOC);
        assertThat(testTickets.getJustifDocContentType()).isEqualTo(UPDATED_JUSTIF_DOC_CONTENT_TYPE);
        assertThat(testTickets.getDateTicket()).isEqualTo(UPDATED_DATE_TICKET);
        assertThat(testTickets.getDateProcess()).isEqualTo(UPDATED_DATE_PROCESS);
        assertThat(testTickets.getProcessed()).isEqualTo(UPDATED_PROCESSED);
    }

    @Test
    @Transactional
    void patchNonExistingTickets() throws Exception {
        int databaseSizeBeforeUpdate = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        tickets.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tickets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tickets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTickets() throws Exception {
        int databaseSizeBeforeUpdate = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        tickets.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tickets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTickets() throws Exception {
        int databaseSizeBeforeUpdate = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        tickets.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tickets)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tickets in the database
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTickets() throws Exception {
        // Initialize the database
        ticketsRepository.saveAndFlush(tickets);
        ticketsRepository.save(tickets);
        ticketsSearchRepository.save(tickets);

        int databaseSizeBeforeDelete = ticketsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the tickets
        restTicketsMockMvc
            .perform(delete(ENTITY_API_URL_ID, tickets.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tickets> ticketsList = ticketsRepository.findAll();
        assertThat(ticketsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTickets() throws Exception {
        // Initialize the database
        tickets = ticketsRepository.saveAndFlush(tickets);
        ticketsSearchRepository.save(tickets);

        // Search the tickets
        restTicketsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + tickets.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tickets.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].justifDocContentType").value(hasItem(DEFAULT_JUSTIF_DOC_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].justifDoc").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_JUSTIF_DOC))))
            .andExpect(jsonPath("$.[*].dateTicket").value(hasItem(sameInstant(DEFAULT_DATE_TICKET))))
            .andExpect(jsonPath("$.[*].dateProcess").value(hasItem(sameInstant(DEFAULT_DATE_PROCESS))))
            .andExpect(jsonPath("$.[*].processed").value(hasItem(DEFAULT_PROCESSED.booleanValue())));
    }
}
