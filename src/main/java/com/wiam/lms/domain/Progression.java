package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Progression.
 */
@Entity
@Table(name = "progression")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "progression")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Progression implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean status;

    @Column(name = "is_justified")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isJustified;

    @Column(name = "justif_ref")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String justifRef;

    @Min(value = 0)
    @Column(name = "late_arrival")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer lateArrival;

    @Min(value = 0)
    @Column(name = "early_departure")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer earlyDeparture;

    @NotNull
    @Column(name = "task_done", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean taskDone;

    @Column(name = "grade_1")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String grade1;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Min(value = 1)
    @Max(value = 480)
    @Column(name = "task_start")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer taskStart;

    @Min(value = 1)
    @Max(value = 480)
    @Column(name = "task_end")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer taskEnd;

    @Column(name = "task_step")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer taskStep;

    @NotNull
    @Column(name = "progression_date", nullable = false)
    private LocalDate progressionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "progression1s", "quizCertificates", "professors", "employees", "links", "classroom", "type", "mode", "part", "jmode", "group",
        },
        allowSetters = true
    )
    private Session session;

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
    private Student student1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "progressions" }, allowSetters = true)
    private ProgressionMode mode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Progression id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public Progression status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getIsJustified() {
        return this.isJustified;
    }

    public Progression isJustified(Boolean isJustified) {
        this.setIsJustified(isJustified);
        return this;
    }

    public void setIsJustified(Boolean isJustified) {
        this.isJustified = isJustified;
    }

    public String getJustifRef() {
        return this.justifRef;
    }

    public Progression justifRef(String justifRef) {
        this.setJustifRef(justifRef);
        return this;
    }

    public void setJustifRef(String justifRef) {
        this.justifRef = justifRef;
    }

    public Integer getLateArrival() {
        return this.lateArrival;
    }

    public Progression lateArrival(Integer lateArrival) {
        this.setLateArrival(lateArrival);
        return this;
    }

    public void setLateArrival(Integer lateArrival) {
        this.lateArrival = lateArrival;
    }

    public Integer getEarlyDeparture() {
        return this.earlyDeparture;
    }

    public Progression earlyDeparture(Integer earlyDeparture) {
        this.setEarlyDeparture(earlyDeparture);
        return this;
    }

    public void setEarlyDeparture(Integer earlyDeparture) {
        this.earlyDeparture = earlyDeparture;
    }

    public Boolean getTaskDone() {
        return this.taskDone;
    }

    public Progression taskDone(Boolean taskDone) {
        this.setTaskDone(taskDone);
        return this;
    }

    public void setTaskDone(Boolean taskDone) {
        this.taskDone = taskDone;
    }

    public String getGrade1() {
        return this.grade1;
    }

    public Progression grade1(String grade1) {
        this.setGrade1(grade1);
        return this;
    }

    public void setGrade1(String grade1) {
        this.grade1 = grade1;
    }

    public String getDescription() {
        return this.description;
    }

    public Progression description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTaskStart() {
        return this.taskStart;
    }

    public Progression taskStart(Integer taskStart) {
        this.setTaskStart(taskStart);
        return this;
    }

    public void setTaskStart(Integer taskStart) {
        this.taskStart = taskStart;
    }

    public Integer getTaskEnd() {
        return this.taskEnd;
    }

    public Progression taskEnd(Integer taskEnd) {
        this.setTaskEnd(taskEnd);
        return this;
    }

    public void setTaskEnd(Integer taskEnd) {
        this.taskEnd = taskEnd;
    }

    public Integer getTaskStep() {
        return this.taskStep;
    }

    public Progression taskStep(Integer taskStep) {
        this.setTaskStep(taskStep);
        return this;
    }

    public void setTaskStep(Integer taskStep) {
        this.taskStep = taskStep;
    }

    public LocalDate getProgressionDate() {
        return this.progressionDate;
    }

    public Progression progressionDate(LocalDate progressionDate) {
        this.setProgressionDate(progressionDate);
        return this;
    }

    public void setProgressionDate(LocalDate progressionDate) {
        this.progressionDate = progressionDate;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Progression session(Session session) {
        this.setSession(session);
        return this;
    }

    public Student getStudent1() {
        return this.student1;
    }

    public void setStudent1(Student student) {
        this.student1 = student;
    }

    public Progression student1(Student student) {
        this.setStudent1(student);
        return this;
    }

    public ProgressionMode getMode() {
        return this.mode;
    }

    public void setMode(ProgressionMode progressionMode) {
        this.mode = progressionMode;
    }

    public Progression mode(ProgressionMode progressionMode) {
        this.setMode(progressionMode);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Progression)) {
            return false;
        }
        return getId() != null && getId().equals(((Progression) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Progression{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", isJustified='" + getIsJustified() + "'" +
            ", justifRef='" + getJustifRef() + "'" +
            ", lateArrival=" + getLateArrival() +
            ", earlyDeparture=" + getEarlyDeparture() +
            ", taskDone='" + getTaskDone() + "'" +
            ", grade1='" + getGrade1() + "'" +
            ", description='" + getDescription() + "'" +
            ", taskStart=" + getTaskStart() +
            ", taskEnd=" + getTaskEnd() +
            ", taskStep=" + getTaskStep() +
            ", progressionDate='" + getProgressionDate() + "'" +
            "}";
    }
}
