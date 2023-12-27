package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Country2;
import com.wiam.lms.repository.Country2Repository;
import com.wiam.lms.repository.search.Country2SearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Country2}.
 */
@RestController
@RequestMapping("/api/country-2-s")
@Transactional
public class Country2Resource {

    private final Logger log = LoggerFactory.getLogger(Country2Resource.class);

    private static final String ENTITY_NAME = "country2";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Country2Repository country2Repository;

    private final Country2SearchRepository country2SearchRepository;

    public Country2Resource(Country2Repository country2Repository, Country2SearchRepository country2SearchRepository) {
        this.country2Repository = country2Repository;
        this.country2SearchRepository = country2SearchRepository;
    }

    /**
     * {@code POST  /country-2-s} : Create a new country2.
     *
     * @param country2 the country2 to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new country2, or with status {@code 400 (Bad Request)} if the country2 has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Country2> createCountry2(@Valid @RequestBody Country2 country2) throws URISyntaxException {
        log.debug("REST request to save Country2 : {}", country2);
        if (country2.getId() != null) {
            throw new BadRequestAlertException("A new country2 cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Country2 result = country2Repository.save(country2);
        country2SearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/country-2-s/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /country-2-s/:id} : Updates an existing country2.
     *
     * @param id the id of the country2 to save.
     * @param country2 the country2 to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated country2,
     * or with status {@code 400 (Bad Request)} if the country2 is not valid,
     * or with status {@code 500 (Internal Server Error)} if the country2 couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Country2> updateCountry2(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Country2 country2
    ) throws URISyntaxException {
        log.debug("REST request to update Country2 : {}, {}", id, country2);
        if (country2.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, country2.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!country2Repository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Country2 result = country2Repository.save(country2);
        country2SearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, country2.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /country-2-s/:id} : Partial updates given fields of an existing country2, field will ignore if it is null
     *
     * @param id the id of the country2 to save.
     * @param country2 the country2 to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated country2,
     * or with status {@code 400 (Bad Request)} if the country2 is not valid,
     * or with status {@code 404 (Not Found)} if the country2 is not found,
     * or with status {@code 500 (Internal Server Error)} if the country2 couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Country2> partialUpdateCountry2(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Country2 country2
    ) throws URISyntaxException {
        log.debug("REST request to partial update Country2 partially : {}, {}", id, country2);
        if (country2.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, country2.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!country2Repository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Country2> result = country2Repository
            .findById(country2.getId())
            .map(existingCountry2 -> {
                if (country2.getCountryName() != null) {
                    existingCountry2.setCountryName(country2.getCountryName());
                }

                return existingCountry2;
            })
            .map(country2Repository::save)
            .map(savedCountry2 -> {
                country2SearchRepository.index(savedCountry2);
                return savedCountry2;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, country2.getId().toString())
        );
    }

    /**
     * {@code GET  /country-2-s} : get all the country2s.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of country2s in body.
     */
    @GetMapping("")
    public List<Country2> getAllCountry2s() {
        log.debug("REST request to get all Country2s");
        return country2Repository.findAll();
    }

    /**
     * {@code GET  /country-2-s/:id} : get the "id" country2.
     *
     * @param id the id of the country2 to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the country2, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Country2> getCountry2(@PathVariable("id") Long id) {
        log.debug("REST request to get Country2 : {}", id);
        Optional<Country2> country2 = country2Repository.findById(id);
        return ResponseUtil.wrapOrNotFound(country2);
    }

    /**
     * {@code DELETE  /country-2-s/:id} : delete the "id" country2.
     *
     * @param id the id of the country2 to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry2(@PathVariable("id") Long id) {
        log.debug("REST request to delete Country2 : {}", id);
        country2Repository.deleteById(id);
        country2SearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /country-2-s/_search?query=:query} : search for the country2 corresponding
     * to the query.
     *
     * @param query the query of the country2 search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Country2> searchCountry2s(@RequestParam("query") String query) {
        log.debug("REST request to search Country2s for query {}", query);
        try {
            return StreamSupport.stream(country2SearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
