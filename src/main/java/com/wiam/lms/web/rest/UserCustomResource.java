package com.wiam.lms.web.rest;

import com.wiam.lms.domain.UserCustom;
import com.wiam.lms.repository.UserCustomRepository;
import com.wiam.lms.repository.search.UserCustomSearchRepository;
import com.wiam.lms.web.rest.errors.BadRequestAlertException;
import com.wiam.lms.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.wiam.lms.domain.UserCustom}.
 */
@RestController
@RequestMapping("/api/user-customs")
@Transactional
public class UserCustomResource {

    private final Logger log = LoggerFactory.getLogger(UserCustomResource.class);

    private static final String ENTITY_NAME = "userCustom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserCustomRepository userCustomRepository;

    private final UserCustomSearchRepository userCustomSearchRepository;

    public UserCustomResource(UserCustomRepository userCustomRepository, UserCustomSearchRepository userCustomSearchRepository) {
        this.userCustomRepository = userCustomRepository;
        this.userCustomSearchRepository = userCustomSearchRepository;
    }

    /**
     * {@code POST  /user-customs} : Create a new userCustom.
     *
     * @param userCustom the userCustom to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userCustom, or with status {@code 400 (Bad Request)} if the userCustom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserCustom> createUserCustom(@Valid @RequestBody UserCustom userCustom) throws URISyntaxException {
        log.debug("REST request to save UserCustom : {}", userCustom);
        if (userCustom.getId() != null) {
            throw new BadRequestAlertException("A new userCustom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserCustom result = userCustomRepository.save(userCustom);
        userCustomSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/user-customs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-customs/:id} : Updates an existing userCustom.
     *
     * @param id the id of the userCustom to save.
     * @param userCustom the userCustom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userCustom,
     * or with status {@code 400 (Bad Request)} if the userCustom is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userCustom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserCustom> updateUserCustom(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserCustom userCustom
    ) throws URISyntaxException {
        log.debug("REST request to update UserCustom : {}, {}", id, userCustom);
        if (userCustom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userCustom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userCustomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserCustom result = userCustomRepository.save(userCustom);
        userCustomSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userCustom.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-customs/:id} : Partial updates given fields of an existing userCustom, field will ignore if it is null
     *
     * @param id the id of the userCustom to save.
     * @param userCustom the userCustom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userCustom,
     * or with status {@code 400 (Bad Request)} if the userCustom is not valid,
     * or with status {@code 404 (Not Found)} if the userCustom is not found,
     * or with status {@code 500 (Internal Server Error)} if the userCustom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserCustom> partialUpdateUserCustom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserCustom userCustom
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserCustom partially : {}, {}", id, userCustom);
        if (userCustom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userCustom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userCustomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserCustom> result = userCustomRepository
            .findById(userCustom.getId())
            .map(existingUserCustom -> {
                if (userCustom.getFirstName() != null) {
                    existingUserCustom.setFirstName(userCustom.getFirstName());
                }
                if (userCustom.getLastName() != null) {
                    existingUserCustom.setLastName(userCustom.getLastName());
                }
                if (userCustom.getEmail() != null) {
                    existingUserCustom.setEmail(userCustom.getEmail());
                }
                if (userCustom.getAccountName() != null) {
                    existingUserCustom.setAccountName(userCustom.getAccountName());
                }
                if (userCustom.getRole() != null) {
                    existingUserCustom.setRole(userCustom.getRole());
                }
                if (userCustom.getStatus() != null) {
                    existingUserCustom.setStatus(userCustom.getStatus());
                }
                if (userCustom.getPassword() != null) {
                    existingUserCustom.setPassword(userCustom.getPassword());
                }
                if (userCustom.getPhoneNumber1() != null) {
                    existingUserCustom.setPhoneNumber1(userCustom.getPhoneNumber1());
                }
                if (userCustom.getPhoneNumver2() != null) {
                    existingUserCustom.setPhoneNumver2(userCustom.getPhoneNumver2());
                }
                if (userCustom.getSex() != null) {
                    existingUserCustom.setSex(userCustom.getSex());
                }
                if (userCustom.getCountryInternalId() != null) {
                    existingUserCustom.setCountryInternalId(userCustom.getCountryInternalId());
                }
                if (userCustom.getNationalityId() != null) {
                    existingUserCustom.setNationalityId(userCustom.getNationalityId());
                }
                if (userCustom.getBirthDay() != null) {
                    existingUserCustom.setBirthDay(userCustom.getBirthDay());
                }
                if (userCustom.getPhoto() != null) {
                    existingUserCustom.setPhoto(userCustom.getPhoto());
                }
                if (userCustom.getPhotoContentType() != null) {
                    existingUserCustom.setPhotoContentType(userCustom.getPhotoContentType());
                }
                if (userCustom.getAddress() != null) {
                    existingUserCustom.setAddress(userCustom.getAddress());
                }
                if (userCustom.getFacebook() != null) {
                    existingUserCustom.setFacebook(userCustom.getFacebook());
                }
                if (userCustom.getTelegramUserCustomId() != null) {
                    existingUserCustom.setTelegramUserCustomId(userCustom.getTelegramUserCustomId());
                }
                if (userCustom.getTelegramUserCustomName() != null) {
                    existingUserCustom.setTelegramUserCustomName(userCustom.getTelegramUserCustomName());
                }
                if (userCustom.getBiography() != null) {
                    existingUserCustom.setBiography(userCustom.getBiography());
                }
                if (userCustom.getBankAccountDetails() != null) {
                    existingUserCustom.setBankAccountDetails(userCustom.getBankAccountDetails());
                }
                if (userCustom.getCertificate() != null) {
                    existingUserCustom.setCertificate(userCustom.getCertificate());
                }
                if (userCustom.getCertificateContentType() != null) {
                    existingUserCustom.setCertificateContentType(userCustom.getCertificateContentType());
                }
                if (userCustom.getJobInternalId() != null) {
                    existingUserCustom.setJobInternalId(userCustom.getJobInternalId());
                }
                if (userCustom.getCreationDate() != null) {
                    existingUserCustom.setCreationDate(userCustom.getCreationDate());
                }
                if (userCustom.getModificationDate() != null) {
                    existingUserCustom.setModificationDate(userCustom.getModificationDate());
                }
                if (userCustom.getDeletionDate() != null) {
                    existingUserCustom.setDeletionDate(userCustom.getDeletionDate());
                }

                return existingUserCustom;
            })
            .map(userCustomRepository::save)
            .map(savedUserCustom -> {
                userCustomSearchRepository.index(savedUserCustom);
                return savedUserCustom;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userCustom.getId().toString())
        );
    }

    /**
     * {@code GET  /user-customs} : get all the userCustoms.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userCustoms in body.
     */
    @GetMapping("")
    public List<UserCustom> getAllUserCustoms(
        @RequestParam(name = "filter", required = false) String filter,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        if ("sponsor-is-null".equals(filter)) {
            log.debug("REST request to get all UserCustoms where sponsor is null");
            return StreamSupport
                .stream(userCustomRepository.findAll().spliterator(), false)
                .filter(userCustom -> userCustom.getSponsor() == null)
                .toList();
        }

        if ("employee-is-null".equals(filter)) {
            log.debug("REST request to get all UserCustoms where employee is null");
            return StreamSupport
                .stream(userCustomRepository.findAll().spliterator(), false)
                .filter(userCustom -> userCustom.getEmployee() == null)
                .toList();
        }

        if ("professor-is-null".equals(filter)) {
            log.debug("REST request to get all UserCustoms where professor is null");
            return StreamSupport
                .stream(userCustomRepository.findAll().spliterator(), false)
                .filter(userCustom -> userCustom.getProfessor() == null)
                .toList();
        }

        if ("student-is-null".equals(filter)) {
            log.debug("REST request to get all UserCustoms where student is null");
            return StreamSupport
                .stream(userCustomRepository.findAll().spliterator(), false)
                .filter(userCustom -> userCustom.getStudent() == null)
                .toList();
        }
        log.debug("REST request to get all UserCustoms");
        if (eagerload) {
            return userCustomRepository.findAllWithEagerRelationships();
        } else {
            return userCustomRepository.findAll();
        }
    }

    /**
     * {@code GET  /user-customs/:id} : get the "id" userCustom.
     *
     * @param id the id of the userCustom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userCustom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserCustom> getUserCustom(@PathVariable("id") Long id) {
        log.debug("REST request to get UserCustom : {}", id);
        Optional<UserCustom> userCustom = userCustomRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(userCustom);
    }

    /**
     * {@code DELETE  /user-customs/:id} : delete the "id" userCustom.
     *
     * @param id the id of the userCustom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserCustom(@PathVariable("id") Long id) {
        log.debug("REST request to delete UserCustom : {}", id);
        userCustomRepository.deleteById(id);
        userCustomSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /user-customs/_search?query=:query} : search for the userCustom corresponding
     * to the query.
     *
     * @param query the query of the userCustom search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<UserCustom> searchUserCustoms(@RequestParam("query") String query) {
        log.debug("REST request to search UserCustoms for query {}", query);
        try {
            return StreamSupport.stream(userCustomSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
