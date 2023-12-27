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
 * A Student.
 */
@Entity
@Table(name = "student")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "student")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Student implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "followUp", "student2", "student" }, allowSetters = true)
    private Set<CoteryHistory> coteryHistories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "student", "cotery" }, allowSetters = true)
    private Set<Certificate> certificates = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "payments", "student", "course" }, allowSetters = true)
    private Set<Enrolement> enrolements = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "question", "student" }, allowSetters = true)
    private Set<Answer> answers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student1")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "session", "student1", "mode" }, allowSetters = true)
    private Set<Progression> progressions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sessions", "students", "groups", "group1" }, allowSetters = true)
    private Group group2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "students", "userCustoms" }, allowSetters = true)
    private Country country;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "students")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "userCustom", "sponsorings", "students" }, allowSetters = true)
    private Set<Sponsor> sponsors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "students")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "students", "questions", "part", "session", "type" }, allowSetters = true)
    private Set<QuizCertificate> quizCertificates = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Student id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Student phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public Student mobileNumber(String mobileNumber) {
        this.setMobileNumber(mobileNumber);
        return this;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Boolean getGender() {
        return this.gender;
    }

    public Student gender(Boolean gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getAbout() {
        return this.about;
    }

    public Student about(String about) {
        this.setAbout(about);
        return this;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public byte[] getImageLink() {
        return this.imageLink;
    }

    public Student imageLink(byte[] imageLink) {
        this.setImageLink(imageLink);
        return this;
    }

    public void setImageLink(byte[] imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageLinkContentType() {
        return this.imageLinkContentType;
    }

    public Student imageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
        return this;
    }

    public void setImageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
    }

    public String getCode() {
        return this.code;
    }

    public Student code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    public Student birthdate(LocalDate birthdate) {
        this.setBirthdate(birthdate);
        return this;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getLastDegree() {
        return this.lastDegree;
    }

    public Student lastDegree(String lastDegree) {
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

    public Student userCustom(UserCustom userCustom) {
        this.setUserCustom(userCustom);
        return this;
    }

    public Set<CoteryHistory> getCoteryHistories() {
        return this.coteryHistories;
    }

    public void setCoteryHistories(Set<CoteryHistory> coteryHistories) {
        if (this.coteryHistories != null) {
            this.coteryHistories.forEach(i -> i.setStudent(null));
        }
        if (coteryHistories != null) {
            coteryHistories.forEach(i -> i.setStudent(this));
        }
        this.coteryHistories = coteryHistories;
    }

    public Student coteryHistories(Set<CoteryHistory> coteryHistories) {
        this.setCoteryHistories(coteryHistories);
        return this;
    }

    public Student addCoteryHistory(CoteryHistory coteryHistory) {
        this.coteryHistories.add(coteryHistory);
        coteryHistory.setStudent(this);
        return this;
    }

    public Student removeCoteryHistory(CoteryHistory coteryHistory) {
        this.coteryHistories.remove(coteryHistory);
        coteryHistory.setStudent(null);
        return this;
    }

    public Set<Certificate> getCertificates() {
        return this.certificates;
    }

    public void setCertificates(Set<Certificate> certificates) {
        if (this.certificates != null) {
            this.certificates.forEach(i -> i.setStudent(null));
        }
        if (certificates != null) {
            certificates.forEach(i -> i.setStudent(this));
        }
        this.certificates = certificates;
    }

    public Student certificates(Set<Certificate> certificates) {
        this.setCertificates(certificates);
        return this;
    }

    public Student addCertificate(Certificate certificate) {
        this.certificates.add(certificate);
        certificate.setStudent(this);
        return this;
    }

    public Student removeCertificate(Certificate certificate) {
        this.certificates.remove(certificate);
        certificate.setStudent(null);
        return this;
    }

    public Set<Enrolement> getEnrolements() {
        return this.enrolements;
    }

    public void setEnrolements(Set<Enrolement> enrolements) {
        if (this.enrolements != null) {
            this.enrolements.forEach(i -> i.setStudent(null));
        }
        if (enrolements != null) {
            enrolements.forEach(i -> i.setStudent(this));
        }
        this.enrolements = enrolements;
    }

    public Student enrolements(Set<Enrolement> enrolements) {
        this.setEnrolements(enrolements);
        return this;
    }

    public Student addEnrolement(Enrolement enrolement) {
        this.enrolements.add(enrolement);
        enrolement.setStudent(this);
        return this;
    }

    public Student removeEnrolement(Enrolement enrolement) {
        this.enrolements.remove(enrolement);
        enrolement.setStudent(null);
        return this;
    }

    public Set<Answer> getAnswers() {
        return this.answers;
    }

    public void setAnswers(Set<Answer> answers) {
        if (this.answers != null) {
            this.answers.forEach(i -> i.setStudent(null));
        }
        if (answers != null) {
            answers.forEach(i -> i.setStudent(this));
        }
        this.answers = answers;
    }

    public Student answers(Set<Answer> answers) {
        this.setAnswers(answers);
        return this;
    }

    public Student addAnswer(Answer answer) {
        this.answers.add(answer);
        answer.setStudent(this);
        return this;
    }

    public Student removeAnswer(Answer answer) {
        this.answers.remove(answer);
        answer.setStudent(null);
        return this;
    }

    public Set<Progression> getProgressions() {
        return this.progressions;
    }

    public void setProgressions(Set<Progression> progressions) {
        if (this.progressions != null) {
            this.progressions.forEach(i -> i.setStudent1(null));
        }
        if (progressions != null) {
            progressions.forEach(i -> i.setStudent1(this));
        }
        this.progressions = progressions;
    }

    public Student progressions(Set<Progression> progressions) {
        this.setProgressions(progressions);
        return this;
    }

    public Student addProgression(Progression progression) {
        this.progressions.add(progression);
        progression.setStudent1(this);
        return this;
    }

    public Student removeProgression(Progression progression) {
        this.progressions.remove(progression);
        progression.setStudent1(null);
        return this;
    }

    public Group getGroup2() {
        return this.group2;
    }

    public void setGroup2(Group group) {
        this.group2 = group;
    }

    public Student group2(Group group) {
        this.setGroup2(group);
        return this;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Student country(Country country) {
        this.setCountry(country);
        return this;
    }

    public Set<Sponsor> getSponsors() {
        return this.sponsors;
    }

    public void setSponsors(Set<Sponsor> sponsors) {
        if (this.sponsors != null) {
            this.sponsors.forEach(i -> i.removeStudents(this));
        }
        if (sponsors != null) {
            sponsors.forEach(i -> i.addStudents(this));
        }
        this.sponsors = sponsors;
    }

    public Student sponsors(Set<Sponsor> sponsors) {
        this.setSponsors(sponsors);
        return this;
    }

    public Student addSponsor(Sponsor sponsor) {
        this.sponsors.add(sponsor);
        sponsor.getStudents().add(this);
        return this;
    }

    public Student removeSponsor(Sponsor sponsor) {
        this.sponsors.remove(sponsor);
        sponsor.getStudents().remove(this);
        return this;
    }

    public Set<QuizCertificate> getQuizCertificates() {
        return this.quizCertificates;
    }

    public void setQuizCertificates(Set<QuizCertificate> quizCertificates) {
        if (this.quizCertificates != null) {
            this.quizCertificates.forEach(i -> i.removeStudents(this));
        }
        if (quizCertificates != null) {
            quizCertificates.forEach(i -> i.addStudents(this));
        }
        this.quizCertificates = quizCertificates;
    }

    public Student quizCertificates(Set<QuizCertificate> quizCertificates) {
        this.setQuizCertificates(quizCertificates);
        return this;
    }

    public Student addQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.add(quizCertificate);
        quizCertificate.getStudents().add(this);
        return this;
    }

    public Student removeQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.remove(quizCertificate);
        quizCertificate.getStudents().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student)) {
            return false;
        }
        return getId() != null && getId().equals(((Student) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Student{" +
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
