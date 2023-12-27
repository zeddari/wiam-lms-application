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
 * A Topic.
 */
@Entity
@Table(name = "topic")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "topic")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Topic implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "title_ar", length = 100, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleAr;

    @NotNull
    @Size(max = 100)
    @Column(name = "title_lat", length = 100, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleLat;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "topic1")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "parts", "enrolements", "questions", "topic1", "level", "professor1" }, allowSetters = true)
    private Set<Course> courses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "topic2")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "courses", "topics", "topic2" }, allowSetters = true)
    private Set<Topic> topics = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "courses", "topics", "topic2" }, allowSetters = true)
    private Topic topic2;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Topic id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleAr() {
        return this.titleAr;
    }

    public Topic titleAr(String titleAr) {
        this.setTitleAr(titleAr);
        return this;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleLat() {
        return this.titleLat;
    }

    public Topic titleLat(String titleLat) {
        this.setTitleLat(titleLat);
        return this;
    }

    public void setTitleLat(String titleLat) {
        this.titleLat = titleLat;
    }

    public String getDescription() {
        return this.description;
    }

    public Topic description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Course> getCourses() {
        return this.courses;
    }

    public void setCourses(Set<Course> courses) {
        if (this.courses != null) {
            this.courses.forEach(i -> i.setTopic1(null));
        }
        if (courses != null) {
            courses.forEach(i -> i.setTopic1(this));
        }
        this.courses = courses;
    }

    public Topic courses(Set<Course> courses) {
        this.setCourses(courses);
        return this;
    }

    public Topic addCourse(Course course) {
        this.courses.add(course);
        course.setTopic1(this);
        return this;
    }

    public Topic removeCourse(Course course) {
        this.courses.remove(course);
        course.setTopic1(null);
        return this;
    }

    public Set<Topic> getTopics() {
        return this.topics;
    }

    public void setTopics(Set<Topic> topics) {
        if (this.topics != null) {
            this.topics.forEach(i -> i.setTopic2(null));
        }
        if (topics != null) {
            topics.forEach(i -> i.setTopic2(this));
        }
        this.topics = topics;
    }

    public Topic topics(Set<Topic> topics) {
        this.setTopics(topics);
        return this;
    }

    public Topic addTopic(Topic topic) {
        this.topics.add(topic);
        topic.setTopic2(this);
        return this;
    }

    public Topic removeTopic(Topic topic) {
        this.topics.remove(topic);
        topic.setTopic2(null);
        return this;
    }

    public Topic getTopic2() {
        return this.topic2;
    }

    public void setTopic2(Topic topic) {
        this.topic2 = topic;
    }

    public Topic topic2(Topic topic) {
        this.setTopic2(topic);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Topic)) {
            return false;
        }
        return getId() != null && getId().equals(((Topic) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Topic{" +
            "id=" + getId() +
            ", titleAr='" + getTitleAr() + "'" +
            ", titleLat='" + getTitleLat() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
