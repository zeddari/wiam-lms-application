package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Employee.
 */
@Entity
@Table(name = "employee")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "employee")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "phone_number")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phoneNumber;

    @Column(name = "mobile_number")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String mobileNumber;

    @Column(name = "gender")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean gender;

    @Size(max = 500)
    @Column(name = "about", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String about;

    @Lob
    @Column(name = "image_link")
    private byte[] imageLink;

    @Column(name = "image_link_content_type")
    private String imageLinkContentType;

    @Size(max = 100)
    @Column(name = "code", length = 100, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Size(max = 500)
    @Column(name = "last_degree", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastDegree;

    @JsonIgnoreProperties(
        value = { "languages", "country", "job", "exams", "sponsor", "employee", "professor", "student" },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private UserCustom userCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employees", "departements", "departement1" }, allowSetters = true)
    private Departement departement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employees" }, allowSetters = true)
    private JobTitle job;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "employees")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = {
            "progression1s", "quizCertificates", "professors", "employees", "links", "classroom", "type", "mode", "part", "jmode", "group",
        },
        allowSetters = true
    )
    private Set<Session> sessions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Employee id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Employee phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public Employee mobileNumber(String mobileNumber) {
        this.setMobileNumber(mobileNumber);
        return this;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Boolean getGender() {
        return this.gender;
    }

    public Employee gender(Boolean gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getAbout() {
        return this.about;
    }

    public Employee about(String about) {
        this.setAbout(about);
        return this;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public byte[] getImageLink() {
        return this.imageLink;
    }

    public Employee imageLink(byte[] imageLink) {
        this.setImageLink(imageLink);
        return this;
    }

    public void setImageLink(byte[] imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageLinkContentType() {
        return this.imageLinkContentType;
    }

    public Employee imageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
        return this;
    }

    public void setImageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
    }

    public String getCode() {
        return this.code;
    }

    public Employee code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    public Employee birthdate(LocalDate birthdate) {
        this.setBirthdate(birthdate);
        return this;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getLastDegree() {
        return this.lastDegree;
    }

    public Employee lastDegree(String lastDegree) {
        this.setLastDegree(lastDegree);
        return this;
    }

    public void setLastDegree(String lastDegree) {
        this.lastDegree = lastDegree;
    }

    public UserCustom getUserCustom() {
        return this.userCustom;
    }

    public void setUserCustom(UserCustom userCustom) {
        this.userCustom = userCustom;
    }

    public Employee userCustom(UserCustom userCustom) {
        this.setUserCustom(userCustom);
        return this;
    }

    public Departement getDepartement() {
        return this.departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Employee departement(Departement departement) {
        this.setDepartement(departement);
        return this;
    }

    public JobTitle getJob() {
        return this.job;
    }

    public void setJob(JobTitle jobTitle) {
        this.job = jobTitle;
    }

    public Employee job(JobTitle jobTitle) {
        this.setJob(jobTitle);
        return this;
    }

    public Set<Session> getSessions() {
        return this.sessions;
    }

    public void setSessions(Set<Session> sessions) {
        if (this.sessions != null) {
            this.sessions.forEach(i -> i.removeEmployees(this));
        }
        if (sessions != null) {
            sessions.forEach(i -> i.addEmployees(this));
        }
        this.sessions = sessions;
    }

    public Employee sessions(Set<Session> sessions) {
        this.setSessions(sessions);
        return this;
    }

    public Employee addSession(Session session) {
        this.sessions.add(session);
        session.getEmployees().add(this);
        return this;
    }

    public Employee removeSession(Session session) {
        this.sessions.remove(session);
        session.getEmployees().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        return getId() != null && getId().equals(((Employee) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Employee{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", mobileNumber='" + getMobileNumber() + "'" +
            ", gender='" + getGender() + "'" +
            ", about='" + getAbout() + "'" +
            ", imageLink='" + getImageLink() + "'" +
            ", imageLinkContentType='" + getImageLinkContentType() + "'" +
            ", code='" + getCode() + "'" +
            ", birthdate='" + getBirthdate() + "'" +
            ", lastDegree='" + getLastDegree() + "'" +
            "}";
    }
}
