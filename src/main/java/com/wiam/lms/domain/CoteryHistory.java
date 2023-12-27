package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wiam.lms.domain.enumeration.Attendance;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CoteryHistory.
 */
@Entity
@Table(name = "cotery_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "coteryhistory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CoteryHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "cotery_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String coteryName;

    @Column(name = "student_full_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String studentFullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Attendance attendanceStatus;

    @JsonIgnoreProperties(value = { "coteryHistory" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "coteryHistory")
    @org.springframework.data.annotation.Transient
    private FollowUp followUp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "coteryHistories", "certificates" }, allowSetters = true)
    private Cotery student2;

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

    public CoteryHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public CoteryHistory date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCoteryName() {
        return this.coteryName;
    }

    public CoteryHistory coteryName(String coteryName) {
        this.setCoteryName(coteryName);
        return this;
    }

    public void setCoteryName(String coteryName) {
        this.coteryName = coteryName;
    }

    public String getStudentFullName() {
        return this.studentFullName;
    }

    public CoteryHistory studentFullName(String studentFullName) {
        this.setStudentFullName(studentFullName);
        return this;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public Attendance getAttendanceStatus() {
        return this.attendanceStatus;
    }

    public CoteryHistory attendanceStatus(Attendance attendanceStatus) {
        this.setAttendanceStatus(attendanceStatus);
        return this;
    }

    public void setAttendanceStatus(Attendance attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public FollowUp getFollowUp() {
        return this.followUp;
    }

    public void setFollowUp(FollowUp followUp) {
        if (this.followUp != null) {
            this.followUp.setCoteryHistory(null);
        }
        if (followUp != null) {
            followUp.setCoteryHistory(this);
        }
        this.followUp = followUp;
    }

    public CoteryHistory followUp(FollowUp followUp) {
        this.setFollowUp(followUp);
        return this;
    }

    public Cotery getStudent2() {
        return this.student2;
    }

    public void setStudent2(Cotery cotery) {
        this.student2 = cotery;
    }

    public CoteryHistory student2(Cotery cotery) {
        this.setStudent2(cotery);
        return this;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CoteryHistory student(Student student) {
        this.setStudent(student);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoteryHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((CoteryHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CoteryHistory{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", coteryName='" + getCoteryName() + "'" +
            ", studentFullName='" + getStudentFullName() + "'" +
            ", attendanceStatus='" + getAttendanceStatus() + "'" +
            "}";
    }
}
