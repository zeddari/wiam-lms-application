package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Certificate;
import com.wiam.lms.repository.CertificateRepository;
import com.wiam.lms.repository.search.CertificateSearchRepository;
import com.wiam.lms.web.rest.errors.BadRequestAlertException;
import com.wiam.lms.web.rest.errors.ElasticsearchExceptionMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.wiam.lms.domain.Certificate}.
 */
@RestController
@RequestMapping("/api/certificates")
@Transactional
public class CertificateResource {

    private final Logger log = LoggerFactory.getLogger(CertificateResource.class);

    private static final String ENTITY_NAME = "certificate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CertificateRepository certificateRepository;

    private final CertificateSearchRepository certificateSearchRepository;

    public CertificateResource(CertificateRepository certificateRepository, CertificateSearchRepository certificateSearchRepository) {
        this.certificateRepository = certificateRepository;
        this.certificateSearchRepository = certificateSearchRepository;
    }

    /**
     * {@code POST  /certificates} : Create a new certificate.
     *
     * @param certificate the certificate to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certificate, or with status {@code 400 (Bad Request)} if the certificate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Certificate> createCertificate(@RequestBody Certificate certificate) throws URISyntaxException {
        log.debug("REST request to save Certificate : {}", certificate);
        if (certificate.getId() != null) {
            throw new BadRequestAlertException("A new certificate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Certificate result = certificateRepository.save(certificate);
        certificateSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/certificates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /certificates/:id} : Updates an existing certificate.
     *
     * @param id the id of the certificate to save.
     * @param certificate the certificate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificate,
     * or with status {@code 400 (Bad Request)} if the certificate is not valid,
     * or with status {@code 500 (Internal Server Error)} if the certificate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Certificate> updateCertificate(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Certificate certificate
    ) throws URISyntaxException {
        log.debug("REST request to update Certificate : {}, {}", id, certificate);
        if (certificate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Certificate result = certificateRepository.save(certificate);
        certificateSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificate.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /certificates/:id} : Partial updates given fields of an existing certificate, field will ignore if it is null
     *
     * @param id the id of the certificate to save.
     * @param certificate the certificate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificate,
     * or with status {@code 400 (Bad Request)} if the certificate is not valid,
     * or with status {@code 404 (Not Found)} if the certificate is not found,
     * or with status {@code 500 (Internal Server Error)} if the certificate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Certificate> partialUpdateCertificate(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Certificate certificate
    ) throws URISyntaxException {
        log.debug("REST request to partial update Certificate partially : {}, {}", id, certificate);
        if (certificate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificate.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Certificate> result = certificateRepository
            .findById(certificate.getId())
            .map(existingCertificate -> {
                if (certificate.getCoteryName() != null) {
                    existingCertificate.setCoteryName(certificate.getCoteryName());
                }
                if (certificate.getStudentFullName() != null) {
                    existingCertificate.setStudentFullName(certificate.getStudentFullName());
                }
                if (certificate.getCertificateType() != null) {
                    existingCertificate.setCertificateType(certificate.getCertificateType());
                }

                return existingCertificate;
            })
            .map(certificateRepository::save)
            .map(savedCertificate -> {
                certificateSearchRepository.index(savedCertificate);
                return savedCertificate;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificate.getId().toString())
        );
    }

    /**
     * {@code GET  /certificates} : get all the certificates.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of certificates in body.
     */
    @GetMapping("")
    public List<Certificate> getAllCertificates() {
        log.debug("REST request to get all Certificates");
        return certificateRepository.findAll();
    }

    /**
     * {@code GET  /certificates/:id} : get the "id" certificate.
     *
     * @param id the id of the certificate to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certificate, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificate(@PathVariable("id") Long id) {
        log.debug("REST request to get Certificate : {}", id);
        Optional<Certificate> certificate = certificateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(certificate);
    }

    /**
     * {@code DELETE  /certificates/:id} : delete the "id" certificate.
     *
     * @param id the id of the certificate to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable("id") Long id) {
        log.debug("REST request to delete Certificate : {}", id);
        certificateRepository.deleteById(id);
        certificateSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /certificates/_search?query=:query} : search for the certificate corresponding
     * to the query.
     *
     * @param query the query of the certificate search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Certificate> searchCertificates(@RequestParam("query") String query) {
        log.debug("REST request to search Certificates for query {}", query);
        try {
            return StreamSupport.stream(certificateSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
