package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Enrolement.
 */
@Entity
@Table(name = "enrolement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "enrolement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Enrolement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "is_active", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @Column(name = "activated_at")
    private ZonedDateTime activatedAt;

    @Column(name = "activated_by")
    private ZonedDateTime activatedBy;

    @NotNull
    @Column(name = "enrolment_start_time", nullable = false)
    private ZonedDateTime enrolmentStartTime;

    @NotNull
    @Column(name = "enrolemnt_end_time", nullable = false)
    private ZonedDateTime enrolemntEndTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "enrolment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "currency", "enrolment" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "parts", "enrolements", "questions", "topic1", "level", "professor1" }, allowSetters = true)
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Enrolement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Enrolement isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ZonedDateTime getActivatedAt() {
        return this.activatedAt;
    }

    public Enrolement activatedAt(ZonedDateTime activatedAt) {
        this.setActivatedAt(activatedAt);
        return this;
    }

    public void setActivatedAt(ZonedDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }

    public ZonedDateTime getActivatedBy() {
        return this.activatedBy;
    }

    public Enrolement activatedBy(ZonedDateTime activatedBy) {
        this.setActivatedBy(activatedBy);
        return this;
    }

    public void setActivatedBy(ZonedDateTime activatedBy) {
        this.activatedBy = activatedBy;
    }

    public ZonedDateTime getEnrolmentStartTime() {
        return this.enrolmentStartTime;
    }

    public Enrolement enrolmentStartTime(ZonedDateTime enrolmentStartTime) {
        this.setEnrolmentStartTime(enrolmentStartTime);
        return this;
    }

    public void setEnrolmentStartTime(ZonedDateTime enrolmentStartTime) {
        this.enrolmentStartTime = enrolmentStartTime;
    }

    public ZonedDateTime getEnrolemntEndTime() {
        return this.enrolemntEndTime;
    }

    public Enrolement enrolemntEndTime(ZonedDateTime enrolemntEndTime) {
        this.setEnrolemntEndTime(enrolemntEndTime);
        return this;
    }

    public void setEnrolemntEndTime(ZonedDateTime enrolemntEndTime) {
        this.enrolemntEndTime = enrolemntEndTime;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setEnrolment(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setEnrolment(this));
        }
        this.payments = payments;
    }

    public Enrolement payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public Enrolement addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setEnrolment(this);
        return this;
    }

    public Enrolement removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setEnrolment(null);
        return this;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Enrolement student(Student student) {
        this.setStudent(student);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Enrolement course(Course course) {
        this.setCourse(course);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enrolement)) {
            return false;
        }
        return getId() != null && getId().equals(((Enrolement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Enrolement{" +
            "id=" + getId() +
            ", isActive='" + getIsActive() + "'" +
            ", activatedAt='" + getActivatedAt() + "'" +
            ", activatedBy='" + getActivatedBy() + "'" +
            ", enrolmentStartTime='" + getEnrolmentStartTime() + "'" +
            ", enrolemntEndTime='" + getEnrolemntEndTime() + "'" +
            "}";
    }
}
