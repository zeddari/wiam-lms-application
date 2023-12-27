package com.wiam.lms.web.rest;

import static com.wiam.lms.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wiam.lms.IntegrationTest;
import com.wiam.lms.domain.Payment;
import com.wiam.lms.domain.enumeration.PaymentType;
import com.wiam.lms.repository.PaymentRepository;
import com.wiam.lms.repository.search.PaymentSearchRepository;
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
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentResourceIT {

    private static final String DEFAULT_PAYMENT_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_METHOD = "BBBBBBBBBB";

    private static final String DEFAULT_PAIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_PAIED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_MODE = "AAAAAAAAAA";
    private static final String UPDATED_MODE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_POOF = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_POOF = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_POOF_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_POOF_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_PAID_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_PAID_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_AMOUNT = "AAAAAAAAAA";
    private static final String UPDATED_AMOUNT = "BBBBBBBBBB";

    private static final PaymentType DEFAULT_TYPE = PaymentType.REGISTER;
    private static final PaymentType UPDATED_TYPE = PaymentType.MONTHLY_FEES;

    private static final Integer DEFAULT_FROM_MONTH = 1;
    private static final Integer UPDATED_FROM_MONTH = 2;

    private static final Integer DEFAULT_TO_MONTH = 1;
    private static final Integer UPDATED_TO_MONTH = 2;

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/payments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentSearchRepository paymentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMockMvc;

    private Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment()
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .paiedBy(DEFAULT_PAIED_BY)
            .mode(DEFAULT_MODE)
            .poof(DEFAULT_POOF)
            .poofContentType(DEFAULT_POOF_CONTENT_TYPE)
            .paidAt(DEFAULT_PAID_AT)
            .amount(DEFAULT_AMOUNT)
            .type(DEFAULT_TYPE)
            .fromMonth(DEFAULT_FROM_MONTH)
            .toMonth(DEFAULT_TO_MONTH)
            .details(DEFAULT_DETAILS);
        return payment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity(EntityManager em) {
        Payment payment = new Payment()
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paiedBy(UPDATED_PAIED_BY)
            .mode(UPDATED_MODE)
            .poof(UPDATED_POOF)
            .poofContentType(UPDATED_POOF_CONTENT_TYPE)
            .paidAt(UPDATED_PAID_AT)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .fromMonth(UPDATED_FROM_MONTH)
            .toMonth(UPDATED_TO_MONTH)
            .details(UPDATED_DETAILS);
        return payment;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        paymentSearchRepository.deleteAll();
        assertThat(paymentSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        payment = createEntity(em);
    }

    @Test
    @Transactional
    void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // Create the Payment
        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isCreated());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(DEFAULT_PAYMENT_METHOD);
        assertThat(testPayment.getPaiedBy()).isEqualTo(DEFAULT_PAIED_BY);
        assertThat(testPayment.getMode()).isEqualTo(DEFAULT_MODE);
        assertThat(testPayment.getPoof()).isEqualTo(DEFAULT_POOF);
        assertThat(testPayment.getPoofContentType()).isEqualTo(DEFAULT_POOF_CONTENT_TYPE);
        assertThat(testPayment.getPaidAt()).isEqualTo(DEFAULT_PAID_AT);
        assertThat(testPayment.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPayment.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testPayment.getFromMonth()).isEqualTo(DEFAULT_FROM_MONTH);
        assertThat(testPayment.getToMonth()).isEqualTo(DEFAULT_TO_MONTH);
        assertThat(testPayment.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    @Transactional
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        payment.setId(1L);

        int databaseSizeBeforeCreate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPaymentMethodIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setPaymentMethod(null);

        // Create the Payment, which fails.

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPaiedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setPaiedBy(null);

        // Create the Payment, which fails.

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkModeIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setMode(null);

        // Create the Payment, which fails.

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPaidAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setPaidAt(null);

        // Create the Payment, which fails.

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setType(null);

        // Create the Payment, which fails.

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFromMonthIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setFromMonth(null);

        // Create the Payment, which fails.

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkToMonthIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setToMonth(null);

        // Create the Payment, which fails.

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPayments() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD)))
            .andExpect(jsonPath("$.[*].paiedBy").value(hasItem(DEFAULT_PAIED_BY)))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE)))
            .andExpect(jsonPath("$.[*].poofContentType").value(hasItem(DEFAULT_POOF_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].poof").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_POOF))))
            .andExpect(jsonPath("$.[*].paidAt").value(hasItem(sameInstant(DEFAULT_PAID_AT))))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fromMonth").value(hasItem(DEFAULT_FROM_MONTH)))
            .andExpect(jsonPath("$.[*].toMonth").value(hasItem(DEFAULT_TO_MONTH)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }

    @Test
    @Transactional
    void getPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get the payment
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(payment.getId().intValue()))
            .andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD))
            .andExpect(jsonPath("$.paiedBy").value(DEFAULT_PAIED_BY))
            .andExpect(jsonPath("$.mode").value(DEFAULT_MODE))
            .andExpect(jsonPath("$.poofContentType").value(DEFAULT_POOF_CONTENT_TYPE))
            .andExpect(jsonPath("$.poof").value(Base64.getEncoder().encodeToString(DEFAULT_POOF)))
            .andExpect(jsonPath("$.paidAt").value(sameInstant(DEFAULT_PAID_AT)))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.fromMonth").value(DEFAULT_FROM_MONTH))
            .andExpect(jsonPath("$.toMonth").value(DEFAULT_TO_MONTH))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPayment() throws Exception {
        // Get the payment
        restPaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        paymentSearchRepository.save(payment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPayment are not directly saved in db
        em.detach(updatedPayment);
        updatedPayment
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paiedBy(UPDATED_PAIED_BY)
            .mode(UPDATED_MODE)
            .poof(UPDATED_POOF)
            .poofContentType(UPDATED_POOF_CONTENT_TYPE)
            .paidAt(UPDATED_PAID_AT)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .fromMonth(UPDATED_FROM_MONTH)
            .toMonth(UPDATED_TO_MONTH)
            .details(UPDATED_DETAILS);

        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPayment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
        assertThat(testPayment.getPaiedBy()).isEqualTo(UPDATED_PAIED_BY);
        assertThat(testPayment.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testPayment.getPoof()).isEqualTo(UPDATED_POOF);
        assertThat(testPayment.getPoofContentType()).isEqualTo(UPDATED_POOF_CONTENT_TYPE);
        assertThat(testPayment.getPaidAt()).isEqualTo(UPDATED_PAID_AT);
        assertThat(testPayment.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPayment.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testPayment.getFromMonth()).isEqualTo(UPDATED_FROM_MONTH);
        assertThat(testPayment.getToMonth()).isEqualTo(UPDATED_TO_MONTH);
        assertThat(testPayment.getDetails()).isEqualTo(UPDATED_DETAILS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Payment> paymentSearchList = IterableUtils.toList(paymentSearchRepository.findAll());
                Payment testPaymentSearch = paymentSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPaymentSearch.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
                assertThat(testPaymentSearch.getPaiedBy()).isEqualTo(UPDATED_PAIED_BY);
                assertThat(testPaymentSearch.getMode()).isEqualTo(UPDATED_MODE);
                assertThat(testPaymentSearch.getPoof()).isEqualTo(UPDATED_POOF);
                assertThat(testPaymentSearch.getPoofContentType()).isEqualTo(UPDATED_POOF_CONTENT_TYPE);
                assertThat(testPaymentSearch.getPaidAt()).isEqualTo(UPDATED_PAID_AT);
                assertThat(testPaymentSearch.getAmount()).isEqualTo(UPDATED_AMOUNT);
                assertThat(testPaymentSearch.getType()).isEqualTo(UPDATED_TYPE);
                assertThat(testPaymentSearch.getFromMonth()).isEqualTo(UPDATED_FROM_MONTH);
                assertThat(testPaymentSearch.getToMonth()).isEqualTo(UPDATED_TO_MONTH);
                assertThat(testPaymentSearch.getDetails()).isEqualTo(UPDATED_DETAILS);
            });
    }

    @Test
    @Transactional
    void putNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, payment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(payment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(payment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paiedBy(UPDATED_PAIED_BY)
            .poof(UPDATED_POOF)
            .poofContentType(UPDATED_POOF_CONTENT_TYPE)
            .paidAt(UPDATED_PAID_AT)
            .type(UPDATED_TYPE)
            .toMonth(UPDATED_TO_MONTH);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
        assertThat(testPayment.getPaiedBy()).isEqualTo(UPDATED_PAIED_BY);
        assertThat(testPayment.getMode()).isEqualTo(DEFAULT_MODE);
        assertThat(testPayment.getPoof()).isEqualTo(UPDATED_POOF);
        assertThat(testPayment.getPoofContentType()).isEqualTo(UPDATED_POOF_CONTENT_TYPE);
        assertThat(testPayment.getPaidAt()).isEqualTo(UPDATED_PAID_AT);
        assertThat(testPayment.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPayment.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testPayment.getFromMonth()).isEqualTo(DEFAULT_FROM_MONTH);
        assertThat(testPayment.getToMonth()).isEqualTo(UPDATED_TO_MONTH);
        assertThat(testPayment.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    @Transactional
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paiedBy(UPDATED_PAIED_BY)
            .mode(UPDATED_MODE)
            .poof(UPDATED_POOF)
            .poofContentType(UPDATED_POOF_CONTENT_TYPE)
            .paidAt(UPDATED_PAID_AT)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .fromMonth(UPDATED_FROM_MONTH)
            .toMonth(UPDATED_TO_MONTH)
            .details(UPDATED_DETAILS);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
        assertThat(testPayment.getPaiedBy()).isEqualTo(UPDATED_PAIED_BY);
        assertThat(testPayment.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testPayment.getPoof()).isEqualTo(UPDATED_POOF);
        assertThat(testPayment.getPoofContentType()).isEqualTo(UPDATED_POOF_CONTENT_TYPE);
        assertThat(testPayment.getPaidAt()).isEqualTo(UPDATED_PAID_AT);
        assertThat(testPayment.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPayment.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testPayment.getFromMonth()).isEqualTo(UPDATED_FROM_MONTH);
        assertThat(testPayment.getToMonth()).isEqualTo(UPDATED_TO_MONTH);
        assertThat(testPayment.getDetails()).isEqualTo(UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void patchNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, payment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(payment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(payment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);
        paymentRepository.save(payment);
        paymentSearchRepository.save(payment);

        int databaseSizeBeforeDelete = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the payment
        restPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, payment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPayment() throws Exception {
        // Initialize the database
        payment = paymentRepository.saveAndFlush(payment);
        paymentSearchRepository.save(payment);

        // Search the payment
        restPaymentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD)))
            .andExpect(jsonPath("$.[*].paiedBy").value(hasItem(DEFAULT_PAIED_BY)))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE)))
            .andExpect(jsonPath("$.[*].poofContentType").value(hasItem(DEFAULT_POOF_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].poof").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_POOF))))
            .andExpect(jsonPath("$.[*].paidAt").value(hasItem(sameInstant(DEFAULT_PAID_AT))))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fromMonth").value(hasItem(DEFAULT_FROM_MONTH)))
            .andExpect(jsonPath("$.[*].toMonth").value(hasItem(DEFAULT_TO_MONTH)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }
}
