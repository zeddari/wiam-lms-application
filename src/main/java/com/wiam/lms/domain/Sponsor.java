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
 * A Sponsor.
 */
@Entity
@Table(name = "sponsor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "sponsor")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sponsor implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sponsor")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sponsor", "project", "currency" }, allowSetters = true)
    private Set<Sponsoring> sponsorings = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_sponsor__students",
        joinColumns = @JoinColumn(name = "sponsor_id"),
        inverseJoinColumns = @JoinColumn(name = "students_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<Student> students = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sponsor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Sponsor phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public Sponsor mobileNumber(String mobileNumber) {
        this.setMobileNumber(mobileNumber);
        return this;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Boolean getGender() {
        return this.gender;
    }

    public Sponsor gender(Boolean gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getAbout() {
        return this.about;
    }

    public Sponsor about(String about) {
        this.setAbout(about);
        return this;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public byte[] getImageLink() {
        return this.imageLink;
    }

    public Sponsor imageLink(byte[] imageLink) {
        this.setImageLink(imageLink);
        return this;
    }

    public void setImageLink(byte[] imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageLinkContentType() {
        return this.imageLinkContentType;
    }

    public Sponsor imageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
        return this;
    }

    public void setImageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
    }

    public String getCode() {
        return this.code;
    }

    public Sponsor code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    public Sponsor birthdate(LocalDate birthdate) {
        this.setBirthdate(birthdate);
        return this;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getLastDegree() {
        return this.lastDegree;
    }

    public Sponsor lastDegree(String lastDegree) {
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

    public Sponsor userCustom(UserCustom userCustom) {
        this.setUserCustom(userCustom);
        return this;
    }

    public Set<Sponsoring> getSponsorings() {
        return this.sponsorings;
    }

    public void setSponsorings(Set<Sponsoring> sponsorings) {
        if (this.sponsorings != null) {
            this.sponsorings.forEach(i -> i.setSponsor(null));
        }
        if (sponsorings != null) {
            sponsorings.forEach(i -> i.setSponsor(this));
        }
        this.sponsorings = sponsorings;
    }

    public Sponsor sponsorings(Set<Sponsoring> sponsorings) {
        this.setSponsorings(sponsorings);
        return this;
    }

    public Sponsor addSponsoring(Sponsoring sponsoring) {
        this.sponsorings.add(sponsoring);
        sponsoring.setSponsor(this);
        return this;
    }

    public Sponsor removeSponsoring(Sponsoring sponsoring) {
        this.sponsorings.remove(sponsoring);
        sponsoring.setSponsor(null);
        return this;
    }

    public Set<Student> getStudents() {
        return this.students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public Sponsor students(Set<Student> students) {
        this.setStudents(students);
        return this;
    }

    public Sponsor addStudents(Student student) {
        this.students.add(student);
        return this;
    }

    public Sponsor removeStudents(Student student) {
        this.students.remove(student);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sponsor)) {
            return false;
        }
        return getId() != null && getId().equals(((Sponsor) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sponsor{" +
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
