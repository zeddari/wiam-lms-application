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
 * A Project.
 */
@Entity
@Table(name = "project")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "project")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "title_ar", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleAr;

    @NotNull
    @Size(max = 100)
    @Column(name = "title_lat", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleLat;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Size(max = 500)
    @Column(name = "goals", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String goals;

    @Size(max = 500)
    @Column(name = "requirement", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String requirement;

    @Lob
    @Column(name = "image_link")
    private byte[] imageLink;

    @Column(name = "image_link_content_type")
    private String imageLinkContentType;

    @Column(name = "video_link")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String videoLink;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "budget", nullable = false)
    private Double budget;

    @Column(name = "is_active")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @Column(name = "activate_at")
    private LocalDate activateAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sponsor", "project", "currency" }, allowSetters = true)
    private Set<Sponsoring> sponsorings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Project id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleAr() {
        return this.titleAr;
    }

    public Project titleAr(String titleAr) {
        this.setTitleAr(titleAr);
        return this;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleLat() {
        return this.titleLat;
    }

    public Project titleLat(String titleLat) {
        this.setTitleLat(titleLat);
        return this;
    }

    public void setTitleLat(String titleLat) {
        this.titleLat = titleLat;
    }

    public String getDescription() {
        return this.description;
    }

    public Project description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGoals() {
        return this.goals;
    }

    public Project goals(String goals) {
        this.setGoals(goals);
        return this;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getRequirement() {
        return this.requirement;
    }

    public Project requirement(String requirement) {
        this.setRequirement(requirement);
        return this;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public byte[] getImageLink() {
        return this.imageLink;
    }

    public Project imageLink(byte[] imageLink) {
        this.setImageLink(imageLink);
        return this;
    }

    public void setImageLink(byte[] imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageLinkContentType() {
        return this.imageLinkContentType;
    }

    public Project imageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
        return this;
    }

    public void setImageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
    }

    public String getVideoLink() {
        return this.videoLink;
    }

    public Project videoLink(String videoLink) {
        this.setVideoLink(videoLink);
        return this;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public Double getBudget() {
        return this.budget;
    }

    public Project budget(Double budget) {
        this.setBudget(budget);
        return this;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Project isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getActivateAt() {
        return this.activateAt;
    }

    public Project activateAt(LocalDate activateAt) {
        this.setActivateAt(activateAt);
        return this;
    }

    public void setActivateAt(LocalDate activateAt) {
        this.activateAt = activateAt;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Project startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Project endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<Sponsoring> getSponsorings() {
        return this.sponsorings;
    }

    public void setSponsorings(Set<Sponsoring> sponsorings) {
        if (this.sponsorings != null) {
            this.sponsorings.forEach(i -> i.setProject(null));
        }
        if (sponsorings != null) {
            sponsorings.forEach(i -> i.setProject(this));
        }
        this.sponsorings = sponsorings;
    }

    public Project sponsorings(Set<Sponsoring> sponsorings) {
        this.setSponsorings(sponsorings);
        return this;
    }

    public Project addSponsoring(Sponsoring sponsoring) {
        this.sponsorings.add(sponsoring);
        sponsoring.setProject(this);
        return this;
    }

    public Project removeSponsoring(Sponsoring sponsoring) {
        this.sponsorings.remove(sponsoring);
        sponsoring.setProject(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return getId() != null && getId().equals(((Project) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Project{" +
            "id=" + getId() +
            ", titleAr='" + getTitleAr() + "'" +
            ", titleLat='" + getTitleLat() + "'" +
            ", description='" + getDescription() + "'" +
            ", goals='" + getGoals() + "'" +
            ", requirement='" + getRequirement() + "'" +
            ", imageLink='" + getImageLink() + "'" +
            ", imageLinkContentType='" + getImageLinkContentType() + "'" +
            ", videoLink='" + getVideoLink() + "'" +
            ", budget=" + getBudget() +
            ", isActive='" + getIsActive() + "'" +
            ", activateAt='" + getActivateAt() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
