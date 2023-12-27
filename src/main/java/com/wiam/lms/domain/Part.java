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
 * A Part.
 */
@Entity
@Table(name = "part")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "part")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Part implements Serializable {

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

    @Column(name = "duration")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer duration;

    @Lob
    @Column(name = "image_link")
    private byte[] imageLink;

    @Column(name = "image_link_content_type")
    private String imageLinkContentType;

    @Column(name = "video_link")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String videoLink;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "part")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = {
            "progression1s", "quizCertificates", "professors", "employees", "links", "classroom", "type", "mode", "part", "jmode", "group",
        },
        allowSetters = true
    )
    private Set<Session> sessions = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "userCustom", "course" }, allowSetters = true)
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "part1")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sessions", "reviews", "parts", "quizCertificates", "course", "part1" }, allowSetters = true)
    private Set<Part> parts = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "part")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "students", "questions", "part", "session", "type" }, allowSetters = true)
    private Set<QuizCertificate> quizCertificates = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "parts", "enrolements", "questions", "topic1", "level", "professor1" }, allowSetters = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sessions", "reviews", "parts", "quizCertificates", "course", "part1" }, allowSetters = true)
    private Part part1;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Part id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleAr() {
        return this.titleAr;
    }

    public Part titleAr(String titleAr) {
        this.setTitleAr(titleAr);
        return this;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleLat() {
        return this.titleLat;
    }

    public Part titleLat(String titleLat) {
        this.setTitleLat(titleLat);
        return this;
    }

    public void setTitleLat(String titleLat) {
        this.titleLat = titleLat;
    }

    public String getDescription() {
        return this.description;
    }

    public Part description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public Part duration(Integer duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public byte[] getImageLink() {
        return this.imageLink;
    }

    public Part imageLink(byte[] imageLink) {
        this.setImageLink(imageLink);
        return this;
    }

    public void setImageLink(byte[] imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageLinkContentType() {
        return this.imageLinkContentType;
    }

    public Part imageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
        return this;
    }

    public void setImageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
    }

    public String getVideoLink() {
        return this.videoLink;
    }

    public Part videoLink(String videoLink) {
        this.setVideoLink(videoLink);
        return this;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public Set<Session> getSessions() {
        return this.sessions;
    }

    public void setSessions(Set<Session> sessions) {
        if (this.sessions != null) {
            this.sessions.forEach(i -> i.setPart(null));
        }
        if (sessions != null) {
            sessions.forEach(i -> i.setPart(this));
        }
        this.sessions = sessions;
    }

    public Part sessions(Set<Session> sessions) {
        this.setSessions(sessions);
        return this;
    }

    public Part addSession(Session session) {
        this.sessions.add(session);
        session.setPart(this);
        return this;
    }

    public Part removeSession(Session session) {
        this.sessions.remove(session);
        session.setPart(null);
        return this;
    }

    public Set<Review> getReviews() {
        return this.reviews;
    }

    public void setReviews(Set<Review> reviews) {
        if (this.reviews != null) {
            this.reviews.forEach(i -> i.setCourse(null));
        }
        if (reviews != null) {
            reviews.forEach(i -> i.setCourse(this));
        }
        this.reviews = reviews;
    }

    public Part reviews(Set<Review> reviews) {
        this.setReviews(reviews);
        return this;
    }

    public Part addReview(Review review) {
        this.reviews.add(review);
        review.setCourse(this);
        return this;
    }

    public Part removeReview(Review review) {
        this.reviews.remove(review);
        review.setCourse(null);
        return this;
    }

    public Set<Part> getParts() {
        return this.parts;
    }

    public void setParts(Set<Part> parts) {
        if (this.parts != null) {
            this.parts.forEach(i -> i.setPart1(null));
        }
        if (parts != null) {
            parts.forEach(i -> i.setPart1(this));
        }
        this.parts = parts;
    }

    public Part parts(Set<Part> parts) {
        this.setParts(parts);
        return this;
    }

    public Part addPart(Part part) {
        this.parts.add(part);
        part.setPart1(this);
        return this;
    }

    public Part removePart(Part part) {
        this.parts.remove(part);
        part.setPart1(null);
        return this;
    }

    public Set<QuizCertificate> getQuizCertificates() {
        return this.quizCertificates;
    }

    public void setQuizCertificates(Set<QuizCertificate> quizCertificates) {
        if (this.quizCertificates != null) {
            this.quizCertificates.forEach(i -> i.setPart(null));
        }
        if (quizCertificates != null) {
            quizCertificates.forEach(i -> i.setPart(this));
        }
        this.quizCertificates = quizCertificates;
    }

    public Part quizCertificates(Set<QuizCertificate> quizCertificates) {
        this.setQuizCertificates(quizCertificates);
        return this;
    }

    public Part addQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.add(quizCertificate);
        quizCertificate.setPart(this);
        return this;
    }

    public Part removeQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.remove(quizCertificate);
        quizCertificate.setPart(null);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Part course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Part getPart1() {
        return this.part1;
    }

    public void setPart1(Part part) {
        this.part1 = part;
    }

    public Part part1(Part part) {
        this.setPart1(part);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Part)) {
            return false;
        }
        return getId() != null && getId().equals(((Part) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Part{" +
            "id=" + getId() +
            ", titleAr='" + getTitleAr() + "'" +
            ", titleLat='" + getTitleLat() + "'" +
            ", description='" + getDescription() + "'" +
            ", duration=" + getDuration() +
            ", imageLink='" + getImageLink() + "'" +
            ", imageLinkContentType='" + getImageLinkContentType() + "'" +
            ", videoLink='" + getVideoLink() + "'" +
            "}";
    }
}
