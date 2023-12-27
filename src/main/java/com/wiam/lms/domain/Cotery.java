package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wiam.lms.domain.enumeration.Attendance;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Cotery.
 */
@Entity
@Table(name = "cotery")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cotery")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cotery implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student2")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "followUp", "student2", "student" }, allowSetters = true)
    private Set<CoteryHistory> coteryHistories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cotery")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "student", "cotery" }, allowSetters = true)
    private Set<Certificate> certificates = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cotery id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Cotery date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCoteryName() {
        return this.coteryName;
    }

    public Cotery coteryName(String coteryName) {
        this.setCoteryName(coteryName);
        return this;
    }

    public void setCoteryName(String coteryName) {
        this.coteryName = coteryName;
    }

    public String getStudentFullName() {
        return this.studentFullName;
    }

    public Cotery studentFullName(String studentFullName) {
        this.setStudentFullName(studentFullName);
        return this;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public Attendance getAttendanceStatus() {
        return this.attendanceStatus;
    }

    public Cotery attendanceStatus(Attendance attendanceStatus) {
        this.setAttendanceStatus(attendanceStatus);
        return this;
    }

    public void setAttendanceStatus(Attendance attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public Set<CoteryHistory> getCoteryHistories() {
        return this.coteryHistories;
    }

    public void setCoteryHistories(Set<CoteryHistory> coteryHistories) {
        if (this.coteryHistories != null) {
            this.coteryHistories.forEach(i -> i.setStudent2(null));
        }
        if (coteryHistories != null) {
            coteryHistories.forEach(i -> i.setStudent2(this));
        }
        this.coteryHistories = coteryHistories;
    }

    public Cotery coteryHistories(Set<CoteryHistory> coteryHistories) {
        this.setCoteryHistories(coteryHistories);
        return this;
    }

    public Cotery addCoteryHistory(CoteryHistory coteryHistory) {
        this.coteryHistories.add(coteryHistory);
        coteryHistory.setStudent2(this);
        return this;
    }

    public Cotery removeCoteryHistory(CoteryHistory coteryHistory) {
        this.coteryHistories.remove(coteryHistory);
        coteryHistory.setStudent2(null);
        return this;
    }

    public Set<Certificate> getCertificates() {
        return this.certificates;
    }

    public void setCertificates(Set<Certificate> certificates) {
        if (this.certificates != null) {
            this.certificates.forEach(i -> i.setCotery(null));
        }
        if (certificates != null) {
            certificates.forEach(i -> i.setCotery(this));
        }
        this.certificates = certificates;
    }

    public Cotery certificates(Set<Certificate> certificates) {
        this.setCertificates(certificates);
        return this;
    }

    public Cotery addCertificate(Certificate certificate) {
        this.certificates.add(certificate);
        certificate.setCotery(this);
        return this;
    }

    public Cotery removeCertificate(Certificate certificate) {
        this.certificates.remove(certificate);
        certificate.setCotery(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cotery)) {
            return false;
        }
        return getId() != null && getId().equals(((Cotery) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cotery{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", coteryName='" + getCoteryName() + "'" +
            ", studentFullName='" + getStudentFullName() + "'" +
            ", attendanceStatus='" + getAttendanceStatus() + "'" +
            "}";
    }
}
