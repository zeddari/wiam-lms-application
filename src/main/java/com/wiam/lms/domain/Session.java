package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * A Session.
 */
@Entity
@Table(name = "session")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "session")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "title", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String title;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @Column(name = "session_start_time", nullable = false)
    private ZonedDateTime sessionStartTime;

    @NotNull
    @Column(name = "session_end_time", nullable = false)
    private ZonedDateTime sessionEndTime;

    @NotNull
    @Column(name = "is_active", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @NotNull
    @Min(value = 0)
    @Max(value = 100)
    @Column(name = "session_size", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer sessionSize;

    @DecimalMin(value = "0")
    @Column(name = "price")
    private Double price;

    @NotNull
    @Column(name = "currency", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String currency;

    @NotNull
    @Column(name = "targeted_age", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String targetedAge;

    @NotNull
    @Column(name = "targeted_gender", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean targetedGender;

    @Lob
    @Column(name = "thumbnail")
    private byte[] thumbnail;

    @Column(name = "thumbnail_content_type")
    private String thumbnailContentType;

    @NotNull
    @Column(name = "planning_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String planningType;

    @Column(name = "once_date")
    private ZonedDateTime onceDate;

    @Column(name = "monday")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean monday;

    @Column(name = "tuesday")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean tuesday;

    @Column(name = "wednesday")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean wednesday;

    @Column(name = "thursday")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean thursday;

    @Column(name = "friday")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean friday;

    @Column(name = "saturday")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean saturday;

    @Column(name = "sanday")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean sanday;

    @Column(name = "period_start_date")
    private LocalDate periodStartDate;

    @Column(name = "periode_end_date")
    private LocalDate periodeEndDate;

    @Column(name = "no_periode_end_date")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean noPeriodeEndDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "session", "student1", "mode" }, allowSetters = true)
    private Set<Progression> progression1s = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "students", "questions", "part", "session", "type" }, allowSetters = true)
    private Set<QuizCertificate> quizCertificates = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_session__professors",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "professors_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "userCustom", "courses", "sessions" }, allowSetters = true)
    private Set<Professor> professors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_session__employees",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "employees_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "userCustom", "departement", "job", "sessions" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_session__links",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "links_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "provider", "sessions" }, allowSetters = true)
    private Set<SessionLink> links = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sessions", "site" }, allowSetters = true)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sessions" }, allowSetters = true)
    private SessionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sessions" }, allowSetters = true)
    private SessionMode mode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sessions", "reviews", "parts", "quizCertificates", "course", "part1" }, allowSetters = true)
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sessions" }, allowSetters = true)
    private SessionJoinMode jmode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sessions", "students", "groups", "group1" }, allowSetters = true)
    private Group group;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Session id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Session title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Session description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getSessionStartTime() {
        return this.sessionStartTime;
    }

    public Session sessionStartTime(ZonedDateTime sessionStartTime) {
        this.setSessionStartTime(sessionStartTime);
        return this;
    }

    public void setSessionStartTime(ZonedDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public ZonedDateTime getSessionEndTime() {
        return this.sessionEndTime;
    }

    public Session sessionEndTime(ZonedDateTime sessionEndTime) {
        this.setSessionEndTime(sessionEndTime);
        return this;
    }

    public void setSessionEndTime(ZonedDateTime sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Session isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSessionSize() {
        return this.sessionSize;
    }

    public Session sessionSize(Integer sessionSize) {
        this.setSessionSize(sessionSize);
        return this;
    }

    public void setSessionSize(Integer sessionSize) {
        this.sessionSize = sessionSize;
    }

    public Double getPrice() {
        return this.price;
    }

    public Session price(Double price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Session currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTargetedAge() {
        return this.targetedAge;
    }

    public Session targetedAge(String targetedAge) {
        this.setTargetedAge(targetedAge);
        return this;
    }

    public void setTargetedAge(String targetedAge) {
        this.targetedAge = targetedAge;
    }

    public Boolean getTargetedGender() {
        return this.targetedGender;
    }

    public Session targetedGender(Boolean targetedGender) {
        this.setTargetedGender(targetedGender);
        return this;
    }

    public void setTargetedGender(Boolean targetedGender) {
        this.targetedGender = targetedGender;
    }

    public byte[] getThumbnail() {
        return this.thumbnail;
    }

    public Session thumbnail(byte[] thumbnail) {
        this.setThumbnail(thumbnail);
        return this;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailContentType() {
        return this.thumbnailContentType;
    }

    public Session thumbnailContentType(String thumbnailContentType) {
        this.thumbnailContentType = thumbnailContentType;
        return this;
    }

    public void setThumbnailContentType(String thumbnailContentType) {
        this.thumbnailContentType = thumbnailContentType;
    }

    public String getPlanningType() {
        return this.planningType;
    }

    public Session planningType(String planningType) {
        this.setPlanningType(planningType);
        return this;
    }

    public void setPlanningType(String planningType) {
        this.planningType = planningType;
    }

    public ZonedDateTime getOnceDate() {
        return this.onceDate;
    }

    public Session onceDate(ZonedDateTime onceDate) {
        this.setOnceDate(onceDate);
        return this;
    }

    public void setOnceDate(ZonedDateTime onceDate) {
        this.onceDate = onceDate;
    }

    public Boolean getMonday() {
        return this.monday;
    }

    public Session monday(Boolean monday) {
        this.setMonday(monday);
        return this;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return this.tuesday;
    }

    public Session tuesday(Boolean tuesday) {
        this.setTuesday(tuesday);
        return this;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return this.wednesday;
    }

    public Session wednesday(Boolean wednesday) {
        this.setWednesday(wednesday);
        return this;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return this.thursday;
    }

    public Session thursday(Boolean thursday) {
        this.setThursday(thursday);
        return this;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return this.friday;
    }

    public Session friday(Boolean friday) {
        this.setFriday(friday);
        return this;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Boolean getSaturday() {
        return this.saturday;
    }

    public Session saturday(Boolean saturday) {
        this.setSaturday(saturday);
        return this;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Boolean getSanday() {
        return this.sanday;
    }

    public Session sanday(Boolean sanday) {
        this.setSanday(sanday);
        return this;
    }

    public void setSanday(Boolean sanday) {
        this.sanday = sanday;
    }

    public LocalDate getPeriodStartDate() {
        return this.periodStartDate;
    }

    public Session periodStartDate(LocalDate periodStartDate) {
        this.setPeriodStartDate(periodStartDate);
        return this;
    }

    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodeEndDate() {
        return this.periodeEndDate;
    }

    public Session periodeEndDate(LocalDate periodeEndDate) {
        this.setPeriodeEndDate(periodeEndDate);
        return this;
    }

    public void setPeriodeEndDate(LocalDate periodeEndDate) {
        this.periodeEndDate = periodeEndDate;
    }

    public Boolean getNoPeriodeEndDate() {
        return this.noPeriodeEndDate;
    }

    public Session noPeriodeEndDate(Boolean noPeriodeEndDate) {
        this.setNoPeriodeEndDate(noPeriodeEndDate);
        return this;
    }

    public void setNoPeriodeEndDate(Boolean noPeriodeEndDate) {
        this.noPeriodeEndDate = noPeriodeEndDate;
    }

    public Set<Progression> getProgression1s() {
        return this.progression1s;
    }

    public void setProgression1s(Set<Progression> progressions) {
        if (this.progression1s != null) {
            this.progression1s.forEach(i -> i.setSession(null));
        }
        if (progressions != null) {
            progressions.forEach(i -> i.setSession(this));
        }
        this.progression1s = progressions;
    }

    public Session progression1s(Set<Progression> progressions) {
        this.setProgression1s(progressions);
        return this;
    }

    public Session addProgression1(Progression progression) {
        this.progression1s.add(progression);
        progression.setSession(this);
        return this;
    }

    public Session removeProgression1(Progression progression) {
        this.progression1s.remove(progression);
        progression.setSession(null);
        return this;
    }

    public Set<QuizCertificate> getQuizCertificates() {
        return this.quizCertificates;
    }

    public void setQuizCertificates(Set<QuizCertificate> quizCertificates) {
        if (this.quizCertificates != null) {
            this.quizCertificates.forEach(i -> i.setSession(null));
        }
        if (quizCertificates != null) {
            quizCertificates.forEach(i -> i.setSession(this));
        }
        this.quizCertificates = quizCertificates;
    }

    public Session quizCertificates(Set<QuizCertificate> quizCertificates) {
        this.setQuizCertificates(quizCertificates);
        return this;
    }

    public Session addQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.add(quizCertificate);
        quizCertificate.setSession(this);
        return this;
    }

    public Session removeQuizCertificate(QuizCertificate quizCertificate) {
        this.quizCertificates.remove(quizCertificate);
        quizCertificate.setSession(null);
        return this;
    }

    public Set<Professor> getProfessors() {
        return this.professors;
    }

    public void setProfessors(Set<Professor> professors) {
        this.professors = professors;
    }

    public Session professors(Set<Professor> professors) {
        this.setProfessors(professors);
        return this;
    }

    public Session addProfessors(Professor professor) {
        this.professors.add(professor);
        return this;
    }

    public Session removeProfessors(Professor professor) {
        this.professors.remove(professor);
        return this;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public Session employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Session addEmployees(Employee employee) {
        this.employees.add(employee);
        return this;
    }

    public Session removeEmployees(Employee employee) {
        this.employees.remove(employee);
        return this;
    }

    public Set<SessionLink> getLinks() {
        return this.links;
    }

    public void setLinks(Set<SessionLink> sessionLinks) {
        this.links = sessionLinks;
    }

    public Session links(Set<SessionLink> sessionLinks) {
        this.setLinks(sessionLinks);
        return this;
    }

    public Session addLinks(SessionLink sessionLink) {
        this.links.add(sessionLink);
        return this;
    }

    public Session removeLinks(SessionLink sessionLink) {
        this.links.remove(sessionLink);
        return this;
    }

    public Classroom getClassroom() {
        return this.classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Session classroom(Classroom classroom) {
        this.setClassroom(classroom);
        return this;
    }

    public SessionType getType() {
        return this.type;
    }

    public void setType(SessionType sessionType) {
        this.type = sessionType;
    }

    public Session type(SessionType sessionType) {
        this.setType(sessionType);
        return this;
    }

    public SessionMode getMode() {
        return this.mode;
    }

    public void setMode(SessionMode sessionMode) {
        this.mode = sessionMode;
    }

    public Session mode(SessionMode sessionMode) {
        this.setMode(sessionMode);
        return this;
    }

    public Part getPart() {
        return this.part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Session part(Part part) {
        this.setPart(part);
        return this;
    }

    public SessionJoinMode getJmode() {
        return this.jmode;
    }

    public void setJmode(SessionJoinMode sessionJoinMode) {
        this.jmode = sessionJoinMode;
    }

    public Session jmode(SessionJoinMode sessionJoinMode) {
        this.setJmode(sessionJoinMode);
        return this;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Session group(Group group) {
        this.setGroup(group);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Session)) {
            return false;
        }
        return getId() != null && getId().equals(((Session) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Session{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", sessionStartTime='" + getSessionStartTime() + "'" +
            ", sessionEndTime='" + getSessionEndTime() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", sessionSize=" + getSessionSize() +
            ", price=" + getPrice() +
            ", currency='" + getCurrency() + "'" +
            ", targetedAge='" + getTargetedAge() + "'" +
            ", targetedGender='" + getTargetedGender() + "'" +
            ", thumbnail='" + getThumbnail() + "'" +
            ", thumbnailContentType='" + getThumbnailContentType() + "'" +
            ", planningType='" + getPlanningType() + "'" +
            ", onceDate='" + getOnceDate() + "'" +
            ", monday='" + getMonday() + "'" +
            ", tuesday='" + getTuesday() + "'" +
            ", wednesday='" + getWednesday() + "'" +
            ", thursday='" + getThursday() + "'" +
            ", friday='" + getFriday() + "'" +
            ", saturday='" + getSaturday() + "'" +
            ", sanday='" + getSanday() + "'" +
            ", periodStartDate='" + getPeriodStartDate() + "'" +
            ", periodeEndDate='" + getPeriodeEndDate() + "'" +
            ", noPeriodeEndDate='" + getNoPeriodeEndDate() + "'" +
            "}";
    }
}
