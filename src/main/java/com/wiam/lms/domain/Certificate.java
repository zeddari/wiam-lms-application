package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wiam.lms.domain.enumeration.CertificateType;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Certificate.
 */
@Entity
@Table(name = "certificate")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "certificate")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cotery_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String coteryName;

    @Column(name = "student_full_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String studentFullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private CertificateType certificateType;

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
    @JsonIgnoreProperties(value = { "coteryHistories", "certificates" }, allowSetters = true)
    private Cotery cotery;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Certificate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoteryName() {
        return this.coteryName;
    }

    public Certificate coteryName(String coteryName) {
        this.setCoteryName(coteryName);
        return this;
    }

    public void setCoteryName(String coteryName) {
        this.coteryName = coteryName;
    }

    public String getStudentFullName() {
        return this.studentFullName;
    }

    public Certificate studentFullName(String studentFullName) {
        this.setStudentFullName(studentFullName);
        return this;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public CertificateType getCertificateType() {
        return this.certificateType;
    }

    public Certificate certificateType(CertificateType certificateType) {
        this.setCertificateType(certificateType);
        return this;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Certificate student(Student student) {
        this.setStudent(student);
        return this;
    }

    public Cotery getCotery() {
        return this.cotery;
    }

    public void setCotery(Cotery cotery) {
        this.cotery = cotery;
    }

    public Certificate cotery(Cotery cotery) {
        this.setCotery(cotery);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Certificate)) {
            return false;
        }
        return getId() != null && getId().equals(((Certificate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Certificate{" +
            "id=" + getId() +
            ", coteryName='" + getCoteryName() + "'" +
            ", studentFullName='" + getStudentFullName() + "'" +
            ", certificateType='" + getCertificateType() + "'" +
            "}";
    }
}
