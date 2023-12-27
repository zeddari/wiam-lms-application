package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Level;
import com.wiam.lms.repository.LevelRepository;
import com.wiam.lms.repository.search.LevelSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Level}.
 */
@RestController
@RequestMapping("/api/levels")
@Transactional
public class LevelResource {

    private final Logger log = LoggerFactory.getLogger(LevelResource.class);

    private static final String ENTITY_NAME = "level";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LevelRepository levelRepository;

    private final LevelSearchRepository levelSearchRepository;

    public LevelResource(LevelRepository levelRepository, LevelSearchRepository levelSearchRepository) {
        this.levelRepository = levelRepository;
        this.levelSearchRepository = levelSearchRepository;
    }

    /**
     * {@code POST  /levels} : Create a new level.
     *
     * @param level the level to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new level, or with status {@code 400 (Bad Request)} if the level has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Level> createLevel(@Valid @RequestBody Level level) throws URISyntaxException {
        log.debug("REST request to save Level : {}", level);
        if (level.getId() != null) {
            throw new BadRequestAlertException("A new level cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Level result = levelRepository.save(level);
        levelSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/levels/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /levels/:id} : Updates an existing level.
     *
     * @param id the id of the level to save.
     * @param level the level to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated level,
     * or with status {@code 400 (Bad Request)} if the level is not valid,
     * or with status {@code 500 (Internal Server Error)} if the level couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Level> updateLevel(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Level level)
        throws URISyntaxException {
        log.debug("REST request to update Level : {}, {}", id, level);
        if (level.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, level.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!levelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Level result = levelRepository.save(level);
        levelSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, level.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /levels/:id} : Partial updates given fields of an existing level, field will ignore if it is null
     *
     * @param id the id of the level to save.
     * @param level the level to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated level,
     * or with status {@code 400 (Bad Request)} if the level is not valid,
     * or with status {@code 404 (Not Found)} if the level is not found,
     * or with status {@code 500 (Internal Server Error)} if the level couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Level> partialUpdateLevel(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Level level
    ) throws URISyntaxException {
        log.debug("REST request to partial update Level partially : {}, {}", id, level);
        if (level.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, level.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!levelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Level> result = levelRepository
            .findById(level.getId())
            .map(existingLevel -> {
                if (level.getTitleAr() != null) {
                    existingLevel.setTitleAr(level.getTitleAr());
                }
                if (level.getTitleLat() != null) {
                    existingLevel.setTitleLat(level.getTitleLat());
                }

                return existingLevel;
            })
            .map(levelRepository::save)
            .map(savedLevel -> {
                levelSearchRepository.index(savedLevel);
                return savedLevel;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, level.getId().toString())
        );
    }

    /**
     * {@code GET  /levels} : get all the levels.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of levels in body.
     */
    @GetMapping("")
    public List<Level> getAllLevels() {
        log.debug("REST request to get all Levels");
        return levelRepository.findAll();
    }

    /**
     * {@code GET  /levels/:id} : get the "id" level.
     *
     * @param id the id of the level to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the level, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Level> getLevel(@PathVariable("id") Long id) {
        log.debug("REST request to get Level : {}", id);
        Optional<Level> level = levelRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(level);
    }

    /**
     * {@code DELETE  /levels/:id} : delete the "id" level.
     *
     * @param id the id of the level to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLevel(@PathVariable("id") Long id) {
        log.debug("REST request to delete Level : {}", id);
        levelRepository.deleteById(id);
        levelSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /levels/_search?query=:query} : search for the level corresponding
     * to the query.
     *
     * @param query the query of the level search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Level> searchLevels(@RequestParam("query") String query) {
        log.debug("REST request to search Levels for query {}", query);
        try {
            return StreamSupport.stream(levelSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
