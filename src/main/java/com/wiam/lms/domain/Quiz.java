package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wiam.lms.domain.enumeration.QuizType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Quiz.
 */
@Entity
@Table(name = "quiz")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "quiz")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Quiz implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quiz_title")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String quizTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QuizType quizType;

    @Column(name = "quiz_description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String quizDescription;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_quiz__question",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "answers", "course", "quizzes", "quizCertificates" }, allowSetters = true)
    private Set<Question> questions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Quiz id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuizTitle() {
        return this.quizTitle;
    }

    public Quiz quizTitle(String quizTitle) {
        this.setQuizTitle(quizTitle);
        return this;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public QuizType getQuizType() {
        return this.quizType;
    }

    public Quiz quizType(QuizType quizType) {
        this.setQuizType(quizType);
        return this;
    }

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }

    public String getQuizDescription() {
        return this.quizDescription;
    }

    public Quiz quizDescription(String quizDescription) {
        this.setQuizDescription(quizDescription);
        return this;
    }

    public void setQuizDescription(String quizDescription) {
        this.quizDescription = quizDescription;
    }

    public Set<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Quiz questions(Set<Question> questions) {
        this.setQuestions(questions);
        return this;
    }

    public Quiz addQuestion(Question question) {
        this.questions.add(question);
        return this;
    }

    public Quiz removeQuestion(Question question) {
        this.questions.remove(question);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quiz)) {
            return false;
        }
        return getId() != null && getId().equals(((Quiz) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Quiz{" +
            "id=" + getId() +
            ", quizTitle='" + getQuizTitle() + "'" +
            ", quizType='" + getQuizType() + "'" +
            ", quizDescription='" + getQuizDescription() + "'" +
            "}";
    }
}
