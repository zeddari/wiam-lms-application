package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wiam.lms.domain.enumeration.QuestionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Question.
 */
@Entity
@Table(name = "question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "question")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "question", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String question;

    @Size(max = 200)
    @Column(name = "note", length = 200)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String note;

    @NotNull
    @Size(max = 200)
    @Column(name = "a_1", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String a1;

    @NotNull
    @Column(name = "a_1_v", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean a1v;

    @NotNull
    @Size(max = 200)
    @Column(name = "a_2", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String a2;

    @NotNull
    @Column(name = "a_2_v", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean a2v;

    @Size(max = 200)
    @Column(name = "a_3", length = 200)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String a3;

    @Column(name = "a_3_v")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean a3v;

    @Size(max = 200)
    @Column(name = "a_4", length = 200)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String a4;

    @Column(name = "a_4_v")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean a4v;

    @NotNull
    @Column(name = "isactive", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isactive;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "question", "student" }, allowSetters = true)
    private Set<Answer> answers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "parts", "enrolements", "questions", "topic1", "level", "professor1" }, allowSetters = true)
    private Course course;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "questions")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "questions" }, allowSetters = true)
    private Set<Quiz> quizzes = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "questions")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "students", "questions", "part", "session", "type" }, allowSetters = true)
    private Set<QuizCertificate> quizCertificates = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }

    public Question question(String question) {
        this.setQuestion(question);
        return this;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getNote() {
        return this.note;
    }

    public Question note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String geta1() {
        return this.a1;
    }

    public Question a1(String a1) {
        this.seta1(a1);
        return this;
    }

    public void seta1(String a1) {
        this.a1 = a1;
    }

    public Boolean geta1v() {
        return this.a1v;
    }

    public Question a1v(Boolean a1v) {
        this.seta1v(a1v);
        return this;
    }

    public void seta1v(Boolean a1v) {
        this.a1v = a1v;
    }

    public String geta2() {
        return this.a2;
    }

    public Question a2(String a2) {
        this.seta2(a2);
        return this;
    }

    public void seta2(String a2) {
        this.a2 = a2;
    }

    public Boolean geta2v() {
        return this.a2v;
    }

    public Question a2v(Boolean a2v) {
        this.seta2v(a2v);
        return this;
    }

    public void seta2v(Boolean a2v) {
        this.a2v = a2v;
    }

    public String geta3() {
        return this.a3;
    }

    public Question a3(String a3) {
        this.seta3(a3);
        return this;
    }

    public void seta3(String a3) {
        this.a3 = a3;
    }

    public Boolean geta3v() {
        return this.a3v;
    }

    public Question a3v(Boolean a3v) {
        this.seta3v(a3v);
        return this;
    }

    public void seta3v(Boolean a3v) {
        this.a3v = a3v;
    }

    public String geta4() {
        return this.a4;
    }

    public Question a4(String a4) {
        this.seta4(a4);
        return this;
    }

    public void seta4(String a4) {
        this.a4 = a4;
    }

    public Boolean geta4v() {
        return this.a4v;
    }

    public Question a4v(Boolean a4v) {
        this.seta4v(a4v);
        return this;
    }

    public void seta4v(Boolean a4v) {
        this.a4v = a4v;
    }

    public Boolean getIsactive() {
        return this.isactive;
    }

    public Question isactive(Boolean isactive) {
        this.setIsactive(isactive);
        return this;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }

    public String getQuestionTitle() {
        return this.questionTitle;
    }

    public Question questionTitle(String questionTitle) {
        this.setQuestionTitle(questionTitle);
        return this;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public QuestionType getQuestionType() {
        return this.questionType;
    }

    public Question questionType(QuestionType questionType) {
        this.setQuestionType(questionType);
        return this;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionDescription() {
        return this.questionDescription;
    }

    public Question questionDescription(String questionDescription) {
        this.setQuestionDescription(questionDescription);
        return this;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public Integer getQuestionPoint() {
        return this.questionPoint;
    }

    public Question questionPoint(Integer questionPoint) {
        this.setQuestionPoint(questionPoint);
        return this;
    }

    public void setQuestionPoint(Integer questionPoint) {
        this.questionPoint = questionPoint;
    }

    public String getQuestionSubject() {
        return this.questionSubject;
    }

    public Question questionSubject(String questionSubject) {
        this.setQuestionSubject(questionSubject);
        return this;
    }

    public void setQuestionSubject(String questionSubject) {
        this.questionSubject = questionSubject;
    }

    public String getQuestionStatus() {
        return this.questionStatus;
    }

    public Question questionStatus(String questionStatus) {
        this.setQuestionStatus(questionStatus);
        return this;
    }

    public void setQuestionStatus(String questionStatus) {
        this.questionStatus = questionStatus;
    }

    public Set<Answer> getAnswers() {
        return this.answers;
    }

    public void setAnswers(Set<Answer> answers) {
        if (this.answers != null) {
            this.answers.forEach(i -> i.setQuestion(null));
        }
        if (answers != null) {
            answers.forEach(i -> i.setQuestion(this));
        }
        this.answers = answers;
    }

    public Question answers(Set<Answer> answers) {
        this.setAnswers(answers);
        return this;
    }

    public Question addAnswer(Answer answer) {
        this.answers.add(answer);
        answer.setQuestion(this);
        return this;
    }

    public Question removeAnswer(Answer answer) {
        this.answers.remove(answer);
        answer.setQuestion(null);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Question course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Set<Quiz> getQuizzes() {
        return this.quizzes;
    }

    public void setQuizzes(Set<Quiz> quizzes) {
        if (this.quizzes != null) {
            this.quizzes.forEach(i -> i.removeQuestion(this));
        }
        if (quizzes != null) {
            quizzes.forEach(i -> i.addQuestion(this));
        }
        this.quizzes = quizzes;
    }

    public Question quizzes(Set<Quiz> quizzes) {
        this.setQuizzes(quizzes);
        return this;
    }

    public Question addQuiz(Quiz quiz) {
        this.quizzes.add(quiz);
        quiz.getQuestions().add(this);
        return this;
    }

    public Question removeQuiz(Quiz quiz) {
        this.quizzes.remove(quiz);
        quiz.getQuestions().remove(this);
        return this;
    }

    public Set<QuizCertificate> getQuizCertificates() {
        return this.quizCertificates;
    }

    public void setQuizCertificates(Set<QuizCertificate> quizCertificates) {
        if (this.quizCertificates != null) {
            this.quizCertificates.forEach(i -> i.removeQuestions(this));
        }
        if (quizCertificates != null) {
            quizCertificates.forEach(i -> i.addQuestions(this));
        }
        this.quizCertificates = quizCertificates;
    }

    public Question quizCertificates(Set<QuizCertificate> quizCertificates) {
        this.setQuizCertificates(quizCertificates);
        return this;
    }

    public Question addQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.add(quizCertificate);
        quizCertificate.getQuestions().add(this);
        return this;
    }

    public Question removeQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.remove(quizCertificate);
        quizCertificate.getQuestions().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", question='" + getQuestion() + "'" +
            ", note='" + getNote() + "'" +
            ", a1='" + geta1() + "'" +
            ", a1v='" + geta1v() + "'" +
            ", a2='" + geta2() + "'" +
            ", a2v='" + geta2v() + "'" +
            ", a3='" + geta3() + "'" +
            ", a3v='" + geta3v() + "'" +
            ", a4='" + geta4() + "'" +
            ", a4v='" + geta4v() + "'" +
            ", isactive='" + getIsactive() + "'" +
            ", questionTitle='" + getQuestionTitle() + "'" +
            ", questionType='" + getQuestionType() + "'" +
            ", questionDescription='" + getQuestionDescription() + "'" +
            ", questionPoint=" + getQuestionPoint() +
            ", questionSubject='" + getQuestionSubject() + "'" +
            ", questionStatus='" + getQuestionStatus() + "'" +
            "}";
    }
}
