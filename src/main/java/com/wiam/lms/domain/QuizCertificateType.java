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
 * A QuizCertificateType.
 */
@Entity
@Table(name = "quiz_certificate_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "quizcertificatetype")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuizCertificateType implements Serializable {

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

    @Size(max = 20)
    @Column(name = "title_lat", length = 20)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleLat;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "type")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "students", "questions", "part", "session", "type" }, allowSetters = true)
    private Set<QuizCertificate> quizCertificates = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public QuizCertificateType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleAr() {
        return this.titleAr;
    }

    public QuizCertificateType titleAr(String titleAr) {
        this.setTitleAr(titleAr);
        return this;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleLat() {
        return this.titleLat;
    }

    public QuizCertificateType titleLat(String titleLat) {
        this.setTitleLat(titleLat);
        return this;
    }

    public void setTitleLat(String titleLat) {
        this.titleLat = titleLat;
    }

    public Set<QuizCertificate> getQuizCertificates() {
        return this.quizCertificates;
    }

    public void setQuizCertificates(Set<QuizCertificate> quizCertificates) {
        if (this.quizCertificates != null) {
            this.quizCertificates.forEach(i -> i.setType(null));
        }
        if (quizCertificates != null) {
            quizCertificates.forEach(i -> i.setType(this));
        }
        this.quizCertificates = quizCertificates;
    }

    public QuizCertificateType quizCertificates(Set<QuizCertificate> quizCertificates) {
        this.setQuizCertificates(quizCertificates);
        return this;
    }

    public QuizCertificateType addQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.add(quizCertificate);
        quizCertificate.setType(this);
        return this;
    }

    public QuizCertificateType removeQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.remove(quizCertificate);
        quizCertificate.setType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuizCertificateType)) {
            return false;
        }
        return getId() != null && getId().equals(((QuizCertificateType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuizCertificateType{" +
            "id=" + getId() +
            ", titleAr='" + getTitleAr() + "'" +
            ", titleLat='" + getTitleLat() + "'" +
            "}";
    }
}
