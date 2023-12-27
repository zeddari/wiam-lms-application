package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Answer.
 */
@Entity
@Table(name = "answer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "answer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "a_1_v", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean a1v;

    @NotNull
    @Column(name = "a_2_v", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean a2v;

    @Column(name = "a_3_v")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean a3v;

    @Column(name = "a_4_v")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean a4v;

    @NotNull
    @Column(name = "result", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "answers", "course", "quizzes", "quizCertificates" }, allowSetters = true)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "userCustom",
            "coteryHistories",
            "certificates",
            "enrolements",
            "answers",
            "progressions",
            "group2",
            "country",
            "sponsors",
            "quizCertificates",
        },
        allowSetters = true
    )
    private Student student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Answer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean geta1v() {
        return this.a1v;
    }

    public Answer a1v(Boolean a1v) {
        this.seta1v(a1v);
        return this;
    }

    public void seta1v(Boolean a1v) {
        this.a1v = a1v;
    }

    public Boolean geta2v() {
        return this.a2v;
    }

    public Answer a2v(Boolean a2v) {
        this.seta2v(a2v);
        return this;
    }

    public void seta2v(Boolean a2v) {
        this.a2v = a2v;
    }

    public Boolean geta3v() {
        return this.a3v;
    }

    public Answer a3v(Boolean a3v) {
        this.seta3v(a3v);
        return this;
    }

    public void seta3v(Boolean a3v) {
        this.a3v = a3v;
    }

    public Boolean geta4v() {
        return this.a4v;
    }

    public Answer a4v(Boolean a4v) {
        this.seta4v(a4v);
        return this;
    }

    public void seta4v(Boolean a4v) {
        this.a4v = a4v;
    }

    public Boolean getResult() {
        return this.result;
    }

    public Answer result(Boolean result) {
        this.setResult(result);
        return this;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Answer question(Question question) {
        this.setQuestion(question);
        return this;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Answer student(Student student) {
        this.setStudent(student);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Answer)) {
            return false;
        }
        return getId() != null && getId().equals(((Answer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Answer{" +
            "id=" + getId() +
            ", a1v='" + geta1v() + "'" +
            ", a2v='" + geta2v() + "'" +
            ", a3v='" + geta3v() + "'" +
            ", a4v='" + geta4v() + "'" +
            ", result='" + getResult() + "'" +
            "}";
    }
}
