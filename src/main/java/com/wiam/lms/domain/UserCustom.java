package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wiam.lms.domain.enumeration.AccountStatus;
import com.wiam.lms.domain.enumeration.Role;
import com.wiam.lms.domain.enumeration.Sex;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserCustom.
 */
@Entity
@Table(name = "user_custom")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "usercustom")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserCustom implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "first_name", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String firstName;

    @NotNull
    @Size(max = 50)
    @Column(name = "last_name", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastName;

    @NotNull
    @Size(max = 50)
    @Column(name = "email", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @NotNull
    @Size(max = 50)
    @Column(name = "account_name", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String accountName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Role role;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private AccountStatus status;

    @NotNull
    @Size(max = 50)
    @Column(name = "password", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String password;

    @NotNull
    @Size(max = 50)
    @Column(name = "phone_number_1", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phoneNumber1;

    @Size(max = 50)
    @Column(name = "phone_numver_2", length = 50)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phoneNumver2;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Sex sex;

    @NotNull
    @Column(name = "country_internal_id", nullable = false)
    private Long countryInternalId;

    @NotNull
    @Column(name = "nationality_id", nullable = false)
    private Long nationalityId;

    @NotNull
    @Column(name = "birth_day", nullable = false)
    private LocalDate birthDay;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @Column(name = "address")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String address;

    @Column(name = "facebook")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String facebook;

    @Column(name = "telegram_user_custom_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String telegramUserCustomId;

    @Column(name = "telegram_user_custom_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String telegramUserCustomName;

    @Column(name = "biography")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String biography;

    @Column(name = "bank_account_details")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String bankAccountDetails;

    @Lob
    @Column(name = "certificate")
    private byte[] certificate;

    @Column(name = "certificate_content_type")
    private String certificateContentType;

    @Column(name = "job_internal_id")
    private Long jobInternalId;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private ZonedDateTime creationDate;

    @Column(name = "modification_date")
    private ZonedDateTime modificationDate;

    @Column(name = "deletion_date")
    private ZonedDateTime deletionDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userCustom")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "userCustom" }, allowSetters = true)
    private Set<Language> languages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "students", "userCustoms" }, allowSetters = true)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "userCustoms" }, allowSetters = true)
    private Job job;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_user_custom__exam",
        joinColumns = @JoinColumn(name = "user_custom_id"),
        inverseJoinColumns = @JoinColumn(name = "exam_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "userCustoms" }, allowSetters = true)
    private Set<Exam> exams = new HashSet<>();

    @JsonIgnoreProperties(value = { "userCustom", "sponsorings", "students" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userCustom")
    @org.springframework.data.annotation.Transient
    private Sponsor sponsor;

    @JsonIgnoreProperties(value = { "userCustom", "departement", "job", "sessions" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userCustom")
    @org.springframework.data.annotation.Transient
    private Employee employee;

    @JsonIgnoreProperties(value = { "userCustom", "courses", "sessions" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userCustom")
    @org.springframework.data.annotation.Transient
    private Professor professor;

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
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userCustom")
    @org.springframework.data.annotation.Transient
    private Student student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserCustom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public UserCustom firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public UserCustom lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public UserCustom email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public UserCustom accountName(String accountName) {
        this.setAccountName(accountName);
        return this;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Role getRole() {
        return this.role;
    }

    public UserCustom role(Role role) {
        this.setRole(role);
        return this;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return this.status;
    }

    public UserCustom status(AccountStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getPassword() {
        return this.password;
    }

    public UserCustom password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber1() {
        return this.phoneNumber1;
    }

    public UserCustom phoneNumber1(String phoneNumber1) {
        this.setPhoneNumber1(phoneNumber1);
        return this;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumver2() {
        return this.phoneNumver2;
    }

    public UserCustom phoneNumver2(String phoneNumver2) {
        this.setPhoneNumver2(phoneNumver2);
        return this;
    }

    public void setPhoneNumver2(String phoneNumver2) {
        this.phoneNumver2 = phoneNumver2;
    }

    public Sex getSex() {
        return this.sex;
    }

    public UserCustom sex(Sex sex) {
        this.setSex(sex);
        return this;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Long getCountryInternalId() {
        return this.countryInternalId;
    }

    public UserCustom countryInternalId(Long countryInternalId) {
        this.setCountryInternalId(countryInternalId);
        return this;
    }

    public void setCountryInternalId(Long countryInternalId) {
        this.countryInternalId = countryInternalId;
    }

    public Long getNationalityId() {
        return this.nationalityId;
    }

    public UserCustom nationalityId(Long nationalityId) {
        this.setNationalityId(nationalityId);
        return this;
    }

    public void setNationalityId(Long nationalityId) {
        this.nationalityId = nationalityId;
    }

    public LocalDate getBirthDay() {
        return this.birthDay;
    }

    public UserCustom birthDay(LocalDate birthDay) {
        this.setBirthDay(birthDay);
        return this;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public UserCustom photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public UserCustom photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getAddress() {
        return this.address;
    }

    public UserCustom address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFacebook() {
        return this.facebook;
    }

    public UserCustom facebook(String facebook) {
        this.setFacebook(facebook);
        return this;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTelegramUserCustomId() {
        return this.telegramUserCustomId;
    }

    public UserCustom telegramUserCustomId(String telegramUserCustomId) {
        this.setTelegramUserCustomId(telegramUserCustomId);
        return this;
    }

    public void setTelegramUserCustomId(String telegramUserCustomId) {
        this.telegramUserCustomId = telegramUserCustomId;
    }

    public String getTelegramUserCustomName() {
        return this.telegramUserCustomName;
    }

    public UserCustom telegramUserCustomName(String telegramUserCustomName) {
        this.setTelegramUserCustomName(telegramUserCustomName);
        return this;
    }

    public void setTelegramUserCustomName(String telegramUserCustomName) {
        this.telegramUserCustomName = telegramUserCustomName;
    }

    public String getBiography() {
        return this.biography;
    }

    public UserCustom biography(String biography) {
        this.setBiography(biography);
        return this;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBankAccountDetails() {
        return this.bankAccountDetails;
    }

    public UserCustom bankAccountDetails(String bankAccountDetails) {
        this.setBankAccountDetails(bankAccountDetails);
        return this;
    }

    public void setBankAccountDetails(String bankAccountDetails) {
        this.bankAccountDetails = bankAccountDetails;
    }

    public byte[] getCertificate() {
        return this.certificate;
    }

    public UserCustom certificate(byte[] certificate) {
        this.setCertificate(certificate);
        return this;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    public String getCertificateContentType() {
        return this.certificateContentType;
    }

    public UserCustom certificateContentType(String certificateContentType) {
        this.certificateContentType = certificateContentType;
        return this;
    }

    public void setCertificateContentType(String certificateContentType) {
        this.certificateContentType = certificateContentType;
    }

    public Long getJobInternalId() {
        return this.jobInternalId;
    }

    public UserCustom jobInternalId(Long jobInternalId) {
        this.setJobInternalId(jobInternalId);
        return this;
    }

    public void setJobInternalId(Long jobInternalId) {
        this.jobInternalId = jobInternalId;
    }

    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    public UserCustom creationDate(ZonedDateTime creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ZonedDateTime getModificationDate() {
        return this.modificationDate;
    }

    public UserCustom modificationDate(ZonedDateTime modificationDate) {
        this.setModificationDate(modificationDate);
        return this;
    }

    public void setModificationDate(ZonedDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public ZonedDateTime getDeletionDate() {
        return this.deletionDate;
    }

    public UserCustom deletionDate(ZonedDateTime deletionDate) {
        this.setDeletionDate(deletionDate);
        return this;
    }

    public void setDeletionDate(ZonedDateTime deletionDate) {
        this.deletionDate = deletionDate;
    }

    public Set<Language> getLanguages() {
        return this.languages;
    }

    public void setLanguages(Set<Language> languages) {
        if (this.languages != null) {
            this.languages.forEach(i -> i.setUserCustom(null));
        }
        if (languages != null) {
            languages.forEach(i -> i.setUserCustom(this));
        }
        this.languages = languages;
    }

    public UserCustom languages(Set<Language> languages) {
        this.setLanguages(languages);
        return this;
    }

    public UserCustom addLanguages(Language language) {
        this.languages.add(language);
        language.setUserCustom(this);
        return this;
    }

    public UserCustom removeLanguages(Language language) {
        this.languages.remove(language);
        language.setUserCustom(null);
        return this;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public UserCustom country(Country country) {
        this.setCountry(country);
        return this;
    }

    public Job getJob() {
        return this.job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public UserCustom job(Job job) {
        this.setJob(job);
        return this;
    }

    public Set<Exam> getExams() {
        return this.exams;
    }

    public void setExams(Set<Exam> exams) {
        this.exams = exams;
    }

    public UserCustom exams(Set<Exam> exams) {
        this.setExams(exams);
        return this;
    }

    public UserCustom addExam(Exam exam) {
        this.exams.add(exam);
        return this;
    }

    public UserCustom removeExam(Exam exam) {
        this.exams.remove(exam);
        return this;
    }

    public Sponsor getSponsor() {
        return this.sponsor;
    }

    public void setSponsor(Sponsor sponsor) {
        if (this.sponsor != null) {
            this.sponsor.setUserCustom(null);
        }
        if (sponsor != null) {
            sponsor.setUserCustom(this);
        }
        this.sponsor = sponsor;
    }

    public UserCustom sponsor(Sponsor sponsor) {
        this.setSponsor(sponsor);
        return this;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        if (this.employee != null) {
            this.employee.setUserCustom(null);
        }
        if (employee != null) {
            employee.setUserCustom(this);
        }
        this.employee = employee;
    }

    public UserCustom employee(Employee employee) {
        this.setEmployee(employee);
        return this;
    }

    public Professor getProfessor() {
        return this.professor;
    }

    public void setProfessor(Professor professor) {
        if (this.professor != null) {
            this.professor.setUserCustom(null);
        }
        if (professor != null) {
            professor.setUserCustom(this);
        }
        this.professor = professor;
    }

    public UserCustom professor(Professor professor) {
        this.setProfessor(professor);
        return this;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        if (this.student != null) {
            this.student.setUserCustom(null);
        }
        if (student != null) {
            student.setUserCustom(this);
        }
        this.student = student;
    }

    public UserCustom student(Student student) {
        this.setStudent(student);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserCustom)) {
            return false;
        }
        return getId() != null && getId().equals(((UserCustom) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserCustom{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", accountName='" + getAccountName() + "'" +
            ", role='" + getRole() + "'" +
            ", status='" + getStatus() + "'" +
            ", password='" + getPassword() + "'" +
            ", phoneNumber1='" + getPhoneNumber1() + "'" +
            ", phoneNumver2='" + getPhoneNumver2() + "'" +
            ", sex='" + getSex() + "'" +
            ", countryInternalId=" + getCountryInternalId() +
            ", nationalityId=" + getNationalityId() +
            ", birthDay='" + getBirthDay() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", address='" + getAddress() + "'" +
            ", facebook='" + getFacebook() + "'" +
            ", telegramUserCustomId='" + getTelegramUserCustomId() + "'" +
            ", telegramUserCustomName='" + getTelegramUserCustomName() + "'" +
            ", biography='" + getBiography() + "'" +
            ", bankAccountDetails='" + getBankAccountDetails() + "'" +
            ", certificate='" + getCertificate() + "'" +
            ", certificateContentType='" + getCertificateContentType() + "'" +
            ", jobInternalId=" + getJobInternalId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", modificationDate='" + getModificationDate() + "'" +
            ", deletionDate='" + getDeletionDate() + "'" +
            "}";
    }
}
