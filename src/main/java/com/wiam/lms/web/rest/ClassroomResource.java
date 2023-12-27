package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Classroom;
import com.wiam.lms.repository.ClassroomRepository;
import com.wiam.lms.repository.search.ClassroomSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Classroom}.
 */
@RestController
@RequestMapping("/api/classrooms")
@Transactional
public class ClassroomResource {

    private final Logger log = LoggerFactory.getLogger(ClassroomResource.class);

    private static final String ENTITY_NAME = "classroom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClassroomRepository classroomRepository;

    private final ClassroomSearchRepository classroomSearchRepository;

    public ClassroomResource(ClassroomRepository classroomRepository, ClassroomSearchRepository classroomSearchRepository) {
        this.classroomRepository = classroomRepository;
        this.classroomSearchRepository = classroomSearchRepository;
    }

    /**
     * {@code POST  /classrooms} : Create a new classroom.
     *
     * @param classroom the classroom to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classroom, or with status {@code 400 (Bad Request)} if the classroom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Classroom> createClassroom(@Valid @RequestBody Classroom classroom) throws URISyntaxException {
        log.debug("REST request to save Classroom : {}", classroom);
        if (classroom.getId() != null) {
            throw new BadRequestAlertException("A new classroom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Classroom result = classroomRepository.save(classroom);
        classroomSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/classrooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /classrooms/:id} : Updates an existing classroom.
     *
     * @param id the id of the classroom to save.
     * @param classroom the classroom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classroom,
     * or with status {@code 400 (Bad Request)} if the classroom is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classroom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateClassroom(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Classroom classroom
    ) throws URISyntaxException {
        log.debug("REST request to update Classroom : {}, {}", id, classroom);
        if (classroom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classroom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classroomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Classroom result = classroomRepository.save(classroom);
        classroomSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classroom.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /classrooms/:id} : Partial updates given fields of an existing classroom, field will ignore if it is null
     *
     * @param id the id of the classroom to save.
     * @param classroom the classroom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classroom,
     * or with status {@code 400 (Bad Request)} if the classroom is not valid,
     * or with status {@code 404 (Not Found)} if the classroom is not found,
     * or with status {@code 500 (Internal Server Error)} if the classroom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Classroom> partialUpdateClassroom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Classroom classroom
    ) throws URISyntaxException {
        log.debug("REST request to partial update Classroom partially : {}, {}", id, classroom);
        if (classroom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classroom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classroomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Classroom> result = classroomRepository
            .findById(classroom.getId())
            .map(existingClassroom -> {
                if (classroom.getNameAr() != null) {
                    existingClassroom.setNameAr(classroom.getNameAr());
                }
                if (classroom.getNameLat() != null) {
                    existingClassroom.setNameLat(classroom.getNameLat());
                }
                if (classroom.getDescription() != null) {
                    existingClassroom.setDescription(classroom.getDescription());
                }

                return existingClassroom;
            })
            .map(classroomRepository::save)
            .map(savedClassroom -> {
                classroomSearchRepository.index(savedClassroom);
                return savedClassroom;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classroom.getId().toString())
        );
    }

    /**
     * {@code GET  /classrooms} : get all the classrooms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of classrooms in body.
     */
    @GetMapping("")
    public List<Classroom> getAllClassrooms() {
        log.debug("REST request to get all Classrooms");
        return classroomRepository.findAll();
    }

    /**
     * {@code GET  /classrooms/:id} : get the "id" classroom.
     *
     * @param id the id of the classroom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classroom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroom(@PathVariable("id") Long id) {
        log.debug("REST request to get Classroom : {}", id);
        Optional<Classroom> classroom = classroomRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(classroom);
    }

    /**
     * {@code DELETE  /classrooms/:id} : delete the "id" classroom.
     *
     * @param id the id of the classroom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable("id") Long id) {
        log.debug("REST request to delete Classroom : {}", id);
        classroomRepository.deleteById(id);
        classroomSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /classrooms/_search?query=:query} : search for the classroom corresponding
     * to the query.
     *
     * @param query the query of the classroom search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Classroom> searchClassrooms(@RequestParam("query") String query) {
        log.debug("REST request to search Classrooms for query {}", query);
        try {
            return StreamSupport.stream(classroomSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
