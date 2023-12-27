package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Level.
 */
@Entity
@Table(name = "level")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "level")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Level implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "title_ar", length = 20, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleAr;

    @NotNull
    @Size(max = 20)
    @Column(name = "title_lat", length = 20, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleLat;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "level")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "parts", "enrolements", "questions", "topic1", "level", "professor1" }, allowSetters = true)
    private Set<Course> courses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Level id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleAr() {
        return this.titleAr;
    }

    public Level titleAr(String titleAr) {
        this.setTitleAr(titleAr);
        return this;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleLat() {
        return this.titleLat;
    }

    public Level titleLat(String titleLat) {
        this.setTitleLat(titleLat);
        return this;
    }

    public void setTitleLat(String titleLat) {
        this.titleLat = titleLat;
    }

    public Set<Course> getCourses() {
        return this.courses;
    }

    public void setCourses(Set<Course> courses) {
        if (this.courses != null) {
            this.courses.forEach(i -> i.setLevel(null));
        }
        if (courses != null) {
            courses.forEach(i -> i.setLevel(this));
        }
        this.courses = courses;
    }

    public Level courses(Set<Course> courses) {
        this.setCourses(courses);
        return this;
    }

    public Level addCourse(Course course) {
        this.courses.add(course);
        course.setLevel(this);
        return this;
    }

    public Level removeCourse(Course course) {
        this.courses.remove(course);
        course.setLevel(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Level)) {
            return false;
        }
        return getId() != null && getId().equals(((Level) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Level{" +
            "id=" + getId() +
            ", titleAr='" + getTitleAr() + "'" +
            ", titleLat='" + getTitleLat() + "'" +
            "}";
    }
}
