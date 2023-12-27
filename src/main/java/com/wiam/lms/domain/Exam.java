package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wiam.lms.domain.enumeration.ExamType;
import com.wiam.lms.domain.enumeration.Riwayats;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Exam.
 */
@Entity
@Table(name = "exam")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "exam")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comite")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String comite;

    @Column(name = "student_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String studentName;

    @Column(name = "exam_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String examName;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_category")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Riwayats examCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private ExamType examType;

    @NotNull
    @Column(name = "tajweed_score", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer tajweedScore;

    @NotNull
    @Column(name = "hifd_score", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer hifdScore;

    @NotNull
    @Column(name = "adae_score", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer adaeScore;

    @Lob
    @Column(name = "observation")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String observation;

    @NotNull
    @Column(name = "decision", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer decision;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "exams")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = { "languages", "country", "job", "exams", "sponsor", "employee", "professor", "student" },
        allowSetters = true
    )
    private Set<UserCustom> userCustoms = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComite() {
        return this.comite;
    }

    public Exam comite(String comite) {
        this.setComite(comite);
        return this;
    }

    public void setComite(String comite) {
        this.comite = comite;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public Exam studentName(String studentName) {
        this.setStudentName(studentName);
        return this;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getExamName() {
        return this.examName;
    }

    public Exam examName(String examName) {
        this.setExamName(examName);
        return this;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Riwayats getExamCategory() {
        return this.examCategory;
    }

    public Exam examCategory(Riwayats examCategory) {
        this.setExamCategory(examCategory);
        return this;
    }

    public void setExamCategory(Riwayats examCategory) {
        this.examCategory = examCategory;
    }

    public ExamType getExamType() {
        return this.examType;
    }

    public Exam examType(ExamType examType) {
        this.setExamType(examType);
        return this;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    public Integer getTajweedScore() {
        return this.tajweedScore;
    }

    public Exam tajweedScore(Integer tajweedScore) {
        this.setTajweedScore(tajweedScore);
        return this;
    }

    public void setTajweedScore(Integer tajweedScore) {
        this.tajweedScore = tajweedScore;
    }

    public Integer getHifdScore() {
        return this.hifdScore;
    }

    public Exam hifdScore(Integer hifdScore) {
        this.setHifdScore(hifdScore);
        return this;
    }

    public void setHifdScore(Integer hifdScore) {
        this.hifdScore = hifdScore;
    }

    public Integer getAdaeScore() {
        return this.adaeScore;
    }

    public Exam adaeScore(Integer adaeScore) {
        this.setAdaeScore(adaeScore);
        return this;
    }

    public void setAdaeScore(Integer adaeScore) {
        this.adaeScore = adaeScore;
    }

    public String getObservation() {
        return this.observation;
    }

    public Exam observation(String observation) {
        this.setObservation(observation);
        return this;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Integer getDecision() {
        return this.decision;
    }

    public Exam decision(Integer decision) {
        this.setDecision(decision);
        return this;
    }

    public void setDecision(Integer decision) {
        this.decision = decision;
    }

    public Set<UserCustom> getUserCustoms() {
        return this.userCustoms;
    }

    public void setUserCustoms(Set<UserCustom> userCustoms) {
        if (this.userCustoms != null) {
            this.userCustoms.forEach(i -> i.removeExam(this));
        }
        if (userCustoms != null) {
            userCustoms.forEach(i -> i.addExam(this));
        }
        this.userCustoms = userCustoms;
    }

    public Exam userCustoms(Set<UserCustom> userCustoms) {
        this.setUserCustoms(userCustoms);
        return this;
    }

    public Exam addUserCustom(UserCustom userCustom) {
        this.userCustoms.add(userCustom);
        userCustom.getExams().add(this);
        return this;
    }

    public Exam removeUserCustom(UserCustom userCustom) {
        this.userCustoms.remove(userCustom);
        userCustom.getExams().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exam)) {
            return false;
        }
        return getId() != null && getId().equals(((Exam) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exam{" +
            "id=" + getId() +
            ", comite='" + getComite() + "'" +
            ", studentName='" + getStudentName() + "'" +
            ", examName='" + getExamName() + "'" +
            ", examCategory='" + getExamCategory() + "'" +
            ", examType='" + getExamType() + "'" +
            ", tajweedScore=" + getTajweedScore() +
            ", hifdScore=" + getHifdScore() +
            ", adaeScore=" + getAdaeScore() +
            ", observation='" + getObservation() + "'" +
            ", decision=" + getDecision() +
            "}";
    }
}
