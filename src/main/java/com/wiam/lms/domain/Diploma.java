package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Diploma.
 */
@Entity
@Table(name = "diploma")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "diploma")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Diploma implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "title", length = 100, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String title;

    @Size(max = 100)
    @Column(name = "subject", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String subject;

    @Size(max = 500)
    @Column(name = "detail", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String detail;

    @Size(max = 500)
    @Column(name = "supervisor", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String supervisor;

    @Column(name = "grade")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String grade;

    @Column(name = "graduation_date")
    private LocalDate graduationDate;

    @Column(name = "school")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "languages", "country", "job", "exams", "sponsor", "employee", "professor", "student" },
        allowSetters = true
    )
    private UserCustom userCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "diplomas" }, allowSetters = true)
    private DiplomaType diplomaType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Diploma id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Diploma title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return this.subject;
    }

    public Diploma subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetail() {
        return this.detail;
    }

    public Diploma detail(String detail) {
        this.setDetail(detail);
        return this;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getSupervisor() {
        return this.supervisor;
    }

    public Diploma supervisor(String supervisor) {
        this.setSupervisor(supervisor);
        return this;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getGrade() {
        return this.grade;
    }

    public Diploma grade(String grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public LocalDate getGraduationDate() {
        return this.graduationDate;
    }

    public Diploma graduationDate(LocalDate graduationDate) {
        this.setGraduationDate(graduationDate);
        return this;
    }

    public void setGraduationDate(LocalDate graduationDate) {
        this.graduationDate = graduationDate;
    }

    public String getSchool() {
        return this.school;
    }

    public Diploma school(String school) {
        this.setSchool(school);
        return this;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public UserCustom getUserCustom() {
        return this.userCustom;
    }

    public void setUserCustom(UserCustom userCustom) {
        this.userCustom = userCustom;
    }

    public Diploma userCustom(UserCustom userCustom) {
        this.setUserCustom(userCustom);
        return this;
    }

    public DiplomaType getDiplomaType() {
        return this.diplomaType;
    }

    public void setDiplomaType(DiplomaType diplomaType) {
        this.diplomaType = diplomaType;
    }

    public Diploma diplomaType(DiplomaType diplomaType) {
        this.setDiplomaType(diplomaType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Diploma)) {
            return false;
        }
        return getId() != null && getId().equals(((Diploma) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Diploma{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", subject='" + getSubject() + "'" +
            ", detail='" + getDetail() + "'" +
            ", supervisor='" + getSupervisor() + "'" +
            ", grade='" + getGrade() + "'" +
            ", graduationDate='" + getGraduationDate() + "'" +
            ", school='" + getSchool() + "'" +
            "}";
    }
}
