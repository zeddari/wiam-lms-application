package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Course.
 */
@Entity
@Table(name = "course")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "course")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "title_ar", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleAr;

    @NotNull
    @Size(max = 100)
    @Column(name = "title_lat", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleLat;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Size(max = 500)
    @Column(name = "sub_titles", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String subTitles;

    @Size(max = 500)
    @Column(name = "requirement", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String requirement;

    @Column(name = "duration")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer duration;

    @Size(max = 500)
    @Column(name = "jhi_option", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String option;

    @Column(name = "type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean type;

    @Lob
    @Column(name = "image_link")
    private byte[] imageLink;

    @Column(name = "image_link_content_type")
    private String imageLinkContentType;

    @Column(name = "video_link")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String videoLink;

    @DecimalMin(value = "0")
    @Column(name = "price")
    private Double price;

    @Column(name = "is_active")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @Column(name = "activate_at")
    private LocalDate activateAt;

    @Column(name = "is_confirmed")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isConfirmed;

    @Column(name = "confirmed_at")
    private LocalDate confirmedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sessions", "reviews", "parts", "quizCertificates", "course", "part1" }, allowSetters = true)
    private Set<Part> parts = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "payments", "student", "course" }, allowSetters = true)
    private Set<Enrolement> enrolements = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "answers", "course", "quizzes", "quizCertificates" }, allowSetters = true)
    private Set<Question> questions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "courses", "topics", "topic2" }, allowSetters = true)
    private Topic topic1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "courses" }, allowSetters = true)
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "userCustom", "courses", "sessions" }, allowSetters = true)
    private Professor professor1;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Course id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleAr() {
        return this.titleAr;
    }

    public Course titleAr(String titleAr) {
        this.setTitleAr(titleAr);
        return this;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleLat() {
        return this.titleLat;
    }

    public Course titleLat(String titleLat) {
        this.setTitleLat(titleLat);
        return this;
    }

    public void setTitleLat(String titleLat) {
        this.titleLat = titleLat;
    }

    public String getDescription() {
        return this.description;
    }

    public Course description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubTitles() {
        return this.subTitles;
    }

    public Course subTitles(String subTitles) {
        this.setSubTitles(subTitles);
        return this;
    }

    public void setSubTitles(String subTitles) {
        this.subTitles = subTitles;
    }

    public String getRequirement() {
        return this.requirement;
    }

    public Course requirement(String requirement) {
        this.setRequirement(requirement);
        return this;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public Course duration(Integer duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getOption() {
        return this.option;
    }

    public Course option(String option) {
        this.setOption(option);
        return this;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Boolean getType() {
        return this.type;
    }

    public Course type(Boolean type) {
        this.setType(type);
        return this;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public byte[] getImageLink() {
        return this.imageLink;
    }

    public Course imageLink(byte[] imageLink) {
        this.setImageLink(imageLink);
        return this;
    }

    public void setImageLink(byte[] imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageLinkContentType() {
        return this.imageLinkContentType;
    }

    public Course imageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
        return this;
    }

    public void setImageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
    }

    public String getVideoLink() {
        return this.videoLink;
    }

    public Course videoLink(String videoLink) {
        this.setVideoLink(videoLink);
        return this;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public Double getPrice() {
        return this.price;
    }

    public Course price(Double price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Course isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getActivateAt() {
        return this.activateAt;
    }

    public Course activateAt(LocalDate activateAt) {
        this.setActivateAt(activateAt);
        return this;
    }

    public void setActivateAt(LocalDate activateAt) {
        this.activateAt = activateAt;
    }

    public Boolean getIsConfirmed() {
        return this.isConfirmed;
    }

    public Course isConfirmed(Boolean isConfirmed) {
        this.setIsConfirmed(isConfirmed);
        return this;
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public LocalDate getConfirmedAt() {
        return this.confirmedAt;
    }

    public Course confirmedAt(LocalDate confirmedAt) {
        this.setConfirmedAt(confirmedAt);
        return this;
    }

    public void setConfirmedAt(LocalDate confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Set<Part> getParts() {
        return this.parts;
    }

    public void setParts(Set<Part> parts) {
        if (this.parts != null) {
            this.parts.forEach(i -> i.setCourse(null));
        }
        if (parts != null) {
            parts.forEach(i -> i.setCourse(this));
        }
        this.parts = parts;
    }

    public Course parts(Set<Part> parts) {
        this.setParts(parts);
        return this;
    }

    public Course addPart(Part part) {
        this.parts.add(part);
        part.setCourse(this);
        return this;
    }

    public Course removePart(Part part) {
        this.parts.remove(part);
        part.setCourse(null);
        return this;
    }

    public Set<Enrolement> getEnrolements() {
        return this.enrolements;
    }

    public void setEnrolements(Set<Enrolement> enrolements) {
        if (this.enrolements != null) {
            this.enrolements.forEach(i -> i.setCourse(null));
        }
        if (enrolements != null) {
            enrolements.forEach(i -> i.setCourse(this));
        }
        this.enrolements = enrolements;
    }

    public Course enrolements(Set<Enrolement> enrolements) {
        this.setEnrolements(enrolements);
        return this;
    }

    public Course addEnrolement(Enrolement enrolement) {
        this.enrolements.add(enrolement);
        enrolement.setCourse(this);
        return this;
    }

    public Course removeEnrolement(Enrolement enrolement) {
        this.enrolements.remove(enrolement);
        enrolement.setCourse(null);
        return this;
    }

    public Set<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Set<Question> questions) {
        if (this.questions != null) {
            this.questions.forEach(i -> i.setCourse(null));
        }
        if (questions != null) {
            questions.forEach(i -> i.setCourse(this));
        }
        this.questions = questions;
    }

    public Course questions(Set<Question> questions) {
        this.setQuestions(questions);
        return this;
    }

    public Course addQuestion(Question question) {
        this.questions.add(question);
        question.setCourse(this);
        return this;
    }

    public Course removeQuestion(Question question) {
        this.questions.remove(question);
        question.setCourse(null);
        return this;
    }

    public Topic getTopic1() {
        return this.topic1;
    }

    public void setTopic1(Topic topic) {
        this.topic1 = topic;
    }

    public Course topic1(Topic topic) {
        this.setTopic1(topic);
        return this;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Course level(Level level) {
        this.setLevel(level);
        return this;
    }

    public Professor getProfessor1() {
        return this.professor1;
    }

    public void setProfessor1(Professor professor) {
        this.professor1 = professor;
    }

    public Course professor1(Professor professor) {
        this.setProfessor1(professor);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        return getId() != null && getId().equals(((Course) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Course{" +
            "id=" + getId() +
            ", titleAr='" + getTitleAr() + "'" +
            ", titleLat='" + getTitleLat() + "'" +
            ", description='" + getDescription() + "'" +
            ", subTitles='" + getSubTitles() + "'" +
            ", requirement='" + getRequirement() + "'" +
            ", duration=" + getDuration() +
            ", option='" + getOption() + "'" +
            ", type='" + getType() + "'" +
            ", imageLink='" + getImageLink() + "'" +
            ", imageLinkContentType='" + getImageLinkContentType() + "'" +
            ", videoLink='" + getVideoLink() + "'" +
            ", price=" + getPrice() +
            ", isActive='" + getIsActive() + "'" +
            ", activateAt='" + getActivateAt() + "'" +
            ", isConfirmed='" + getIsConfirmed() + "'" +
            ", confirmedAt='" + getConfirmedAt() + "'" +
            "}";
    }
}
