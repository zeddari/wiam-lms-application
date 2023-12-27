package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Part;
import com.wiam.lms.repository.PartRepository;
import com.wiam.lms.repository.search.PartSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Part}.
 */
@RestController
@RequestMapping("/api/parts")
@Transactional
public class PartResource {

    private final Logger log = LoggerFactory.getLogger(PartResource.class);

    private static final String ENTITY_NAME = "part";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartRepository partRepository;

    private final PartSearchRepository partSearchRepository;

    public PartResource(PartRepository partRepository, PartSearchRepository partSearchRepository) {
        this.partRepository = partRepository;
        this.partSearchRepository = partSearchRepository;
    }

    /**
     * {@code POST  /parts} : Create a new part.
     *
     * @param part the part to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new part, or with status {@code 400 (Bad Request)} if the part has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Part> createPart(@Valid @RequestBody Part part) throws URISyntaxException {
        log.debug("REST request to save Part : {}", part);
        if (part.getId() != null) {
            throw new BadRequestAlertException("A new part cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Part result = partRepository.save(part);
        partSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/parts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /parts/:id} : Updates an existing part.
     *
     * @param id the id of the part to save.
     * @param part the part to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated part,
     * or with status {@code 400 (Bad Request)} if the part is not valid,
     * or with status {@code 500 (Internal Server Error)} if the part couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Part> updatePart(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Part part)
        throws URISyntaxException {
        log.debug("REST request to update Part : {}, {}", id, part);
        if (part.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, part.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Part result = partRepository.save(part);
        partSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, part.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /parts/:id} : Partial updates given fields of an existing part, field will ignore if it is null
     *
     * @param id the id of the part to save.
     * @param part the part to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated part,
     * or with status {@code 400 (Bad Request)} if the part is not valid,
     * or with status {@code 404 (Not Found)} if the part is not found,
     * or with status {@code 500 (Internal Server Error)} if the part couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Part> partialUpdatePart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Part part
    ) throws URISyntaxException {
        log.debug("REST request to partial update Part partially : {}, {}", id, part);
        if (part.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, part.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Part> result = partRepository
            .findById(part.getId())
            .map(existingPart -> {
                if (part.getTitleAr() != null) {
                    existingPart.setTitleAr(part.getTitleAr());
                }
                if (part.getTitleLat() != null) {
                    existingPart.setTitleLat(part.getTitleLat());
                }
                if (part.getDescription() != null) {
                    existingPart.setDescription(part.getDescription());
                }
                if (part.getDuration() != null) {
                    existingPart.setDuration(part.getDuration());
                }
                if (part.getImageLink() != null) {
                    existingPart.setImageLink(part.getImageLink());
                }
                if (part.getImageLinkContentType() != null) {
                    existingPart.setImageLinkContentType(part.getImageLinkContentType());
                }
                if (part.getVideoLink() != null) {
                    existingPart.setVideoLink(part.getVideoLink());
                }

                return existingPart;
            })
            .map(partRepository::save)
            .map(savedPart -> {
                partSearchRepository.index(savedPart);
                return savedPart;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, part.getId().toString())
        );
    }

    /**
     * {@code GET  /parts} : get all the parts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parts in body.
     */
    @GetMapping("")
    public List<Part> getAllParts() {
        log.debug("REST request to get all Parts");
        return partRepository.findAll();
    }

    /**
     * {@code GET  /parts/:id} : get the "id" part.
     *
     * @param id the id of the part to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the part, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Part> getPart(@PathVariable("id") Long id) {
        log.debug("REST request to get Part : {}", id);
        Optional<Part> part = partRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(part);
    }

    /**
     * {@code DELETE  /parts/:id} : delete the "id" part.
     *
     * @param id the id of the part to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable("id") Long id) {
        log.debug("REST request to delete Part : {}", id);
        partRepository.deleteById(id);
        partSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /parts/_search?query=:query} : search for the part corresponding
     * to the query.
     *
     * @param query the query of the part search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Part> searchParts(@RequestParam("query") String query) {
        log.debug("REST request to search Parts for query {}", query);
        try {
            return StreamSupport.stream(partSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
