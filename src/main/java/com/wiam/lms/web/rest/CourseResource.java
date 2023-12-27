package com.wiam.lms.web.rest;

import com.wiam.lms.domain.Course;
import com.wiam.lms.repository.CourseRepository;
import com.wiam.lms.repository.search.CourseSearchRepository;
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
 * REST controller for managing {@link com.wiam.lms.domain.Course}.
 */
@RestController
@RequestMapping("/api/courses")
@Transactional
public class CourseResource {

    private final Logger log = LoggerFactory.getLogger(CourseResource.class);

    private static final String ENTITY_NAME = "course";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CourseRepository courseRepository;

    private final CourseSearchRepository courseSearchRepository;

    public CourseResource(CourseRepository courseRepository, CourseSearchRepository courseSearchRepository) {
        this.courseRepository = courseRepository;
        this.courseSearchRepository = courseSearchRepository;
    }

    /**
     * {@code POST  /courses} : Create a new course.
     *
     * @param course the course to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new course, or with status {@code 400 (Bad Request)} if the course has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) throws URISyntaxException {
        log.debug("REST request to save Course : {}", course);
        if (course.getId() != null) {
            throw new BadRequestAlertException("A new course cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Course result = courseRepository.save(course);
        courseSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/courses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /courses/:id} : Updates an existing course.
     *
     * @param id the id of the course to save.
     * @param course the course to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated course,
     * or with status {@code 400 (Bad Request)} if the course is not valid,
     * or with status {@code 500 (Internal Server Error)} if the course couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Course course
    ) throws URISyntaxException {
        log.debug("REST request to update Course : {}, {}", id, course);
        if (course.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, course.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Course result = courseRepository.save(course);
        courseSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, course.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /courses/:id} : Partial updates given fields of an existing course, field will ignore if it is null
     *
     * @param id the id of the course to save.
     * @param course the course to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated course,
     * or with status {@code 400 (Bad Request)} if the course is not valid,
     * or with status {@code 404 (Not Found)} if the course is not found,
     * or with status {@code 500 (Internal Server Error)} if the course couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Course> partialUpdateCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Course course
    ) throws URISyntaxException {
        log.debug("REST request to partial update Course partially : {}, {}", id, course);
        if (course.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, course.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Course> result = courseRepository
            .findById(course.getId())
            .map(existingCourse -> {
                if (course.getTitleAr() != null) {
                    existingCourse.setTitleAr(course.getTitleAr());
                }
                if (course.getTitleLat() != null) {
                    existingCourse.setTitleLat(course.getTitleLat());
                }
                if (course.getDescription() != null) {
                    existingCourse.setDescription(course.getDescription());
                }
                if (course.getSubTitles() != null) {
                    existingCourse.setSubTitles(course.getSubTitles());
                }
                if (course.getRequirement() != null) {
                    existingCourse.setRequirement(course.getRequirement());
                }
                if (course.getDuration() != null) {
                    existingCourse.setDuration(course.getDuration());
                }
                if (course.getOption() != null) {
                    existingCourse.setOption(course.getOption());
                }
                if (course.getType() != null) {
                    existingCourse.setType(course.getType());
                }
                if (course.getImageLink() != null) {
                    existingCourse.setImageLink(course.getImageLink());
                }
                if (course.getImageLinkContentType() != null) {
                    existingCourse.setImageLinkContentType(course.getImageLinkContentType());
                }
                if (course.getVideoLink() != null) {
                    existingCourse.setVideoLink(course.getVideoLink());
                }
                if (course.getPrice() != null) {
                    existingCourse.setPrice(course.getPrice());
                }
                if (course.getIsActive() != null) {
                    existingCourse.setIsActive(course.getIsActive());
                }
                if (course.getActivateAt() != null) {
                    existingCourse.setActivateAt(course.getActivateAt());
                }
                if (course.getIsConfirmed() != null) {
                    existingCourse.setIsConfirmed(course.getIsConfirmed());
                }
                if (course.getConfirmedAt() != null) {
                    existingCourse.setConfirmedAt(course.getConfirmedAt());
                }

                return existingCourse;
            })
            .map(courseRepository::save)
            .map(savedCourse -> {
                courseSearchRepository.index(savedCourse);
                return savedCourse;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, course.getId().toString())
        );
    }

    /**
     * {@code GET  /courses} : get all the courses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courses in body.
     */
    @GetMapping("")
    public List<Course> getAllCourses() {
        log.debug("REST request to get all Courses");
        return courseRepository.findAll();
    }

    /**
     * {@code GET  /courses/:id} : get the "id" course.
     *
     * @param id the id of the course to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the course, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable("id") Long id) {
        log.debug("REST request to get Course : {}", id);
        Optional<Course> course = courseRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(course);
    }

    /**
     * {@code DELETE  /courses/:id} : delete the "id" course.
     *
     * @param id the id of the course to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") Long id) {
        log.debug("REST request to delete Course : {}", id);
        courseRepository.deleteById(id);
        courseSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /courses/_search?query=:query} : search for the course corresponding
     * to the query.
     *
     * @param query the query of the course search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Course> searchCourses(@RequestParam("query") String query) {
        log.debug("REST request to search Courses for query {}", query);
        try {
            return StreamSupport.stream(courseSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
