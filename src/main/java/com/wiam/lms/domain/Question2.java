package com.wiam.lms.domain;

import com.wiam.lms.domain.enumeration.QuestionType;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Question2.
 */
@Entity
@Table(name = "question_2")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "question2")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "question_title")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String questionTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QuestionType questionType;

    @Column(name = "question_description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String questionDescription;

    @Column(name = "question_point")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer questionPoint;

    @Column(name = "question_subject")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String questionSubject;

    @Column(name = "question_status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String questionStatus;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question2 id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionTitle() {
        return this.questionTitle;
    }

    public Question2 questionTitle(String questionTitle) {
        this.setQuestionTitle(questionTitle);
        return this;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public QuestionType getQuestionType() {
        return this.questionType;
    }

    public Question2 questionType(QuestionType questionType) {
        this.setQuestionType(questionType);
        return this;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionDescription() {
        return this.questionDescription;
    }

    public Question2 questionDescription(String questionDescription) {
        this.setQuestionDescription(questionDescription);
        return this;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public Integer getQuestionPoint() {
        return this.questionPoint;
    }

    public Question2 questionPoint(Integer questionPoint) {
        this.setQuestionPoint(questionPoint);
        return this;
    }

    public void setQuestionPoint(Integer questionPoint) {
        this.questionPoint = questionPoint;
    }

    public String getQuestionSubject() {
        return this.questionSubject;
    }

    public Question2 questionSubject(String questionSubject) {
        this.setQuestionSubject(questionSubject);
        return this;
    }

    public void setQuestionSubject(String questionSubject) {
        this.questionSubject = questionSubject;
    }

    public String getQuestionStatus() {
        return this.questionStatus;
    }

    public Question2 questionStatus(String questionStatus) {
        this.setQuestionStatus(questionStatus);
        return this;
    }

    public void setQuestionStatus(String questionStatus) {
        this.questionStatus = questionStatus;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question2)) {
            return false;
        }
        return getId() != null && getId().equals(((Question2) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question2{" +
            "id=" + getId() +
            ", questionTitle='" + getQuestionTitle() + "'" +
            ", questionType='" + getQuestionType() + "'" +
            ", questionDescription='" + getQuestionDescription() + "'" +
            ", questionPoint=" + getQuestionPoint() +
            ", questionSubject='" + getQuestionSubject() + "'" +
            ", questionStatus='" + getQuestionStatus() + "'" +
            "}";
    }
}
