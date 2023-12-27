package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProgressionMode.
 */
@Entity
@Table(name = "progression_mode")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "progressionmode")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProgressionMode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "title_ar", length = 20, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleAr;

    @NotNull
    @Size(max = 20)
    @Column(name = "title_lat", length = 20, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleLat;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mode")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "session", "student1", "mode" }, allowSetters = true)
    private Set<Progression> progressions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProgressionMode id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleAr() {
        return this.titleAr;
    }

    public ProgressionMode titleAr(String titleAr) {
        this.setTitleAr(titleAr);
        return this;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleLat() {
        return this.titleLat;
    }

    public ProgressionMode titleLat(String titleLat) {
        this.setTitleLat(titleLat);
        return this;
    }

    public void setTitleLat(String titleLat) {
        this.titleLat = titleLat;
    }

    public Set<Progression> getProgressions() {
        return this.progressions;
    }

    public void setProgressions(Set<Progression> progressions) {
        if (this.progressions != null) {
            this.progressions.forEach(i -> i.setMode(null));
        }
        if (progressions != null) {
            progressions.forEach(i -> i.setMode(this));
        }
        this.progressions = progressions;
    }

    public ProgressionMode progressions(Set<Progression> progressions) {
        this.setProgressions(progressions);
        return this;
    }

    public ProgressionMode addProgression(Progression progression) {
        this.progressions.add(progression);
        progression.setMode(this);
        return this;
    }

    public ProgressionMode removeProgression(Progression progression) {
        this.progressions.remove(progression);
        progression.setMode(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProgressionMode)) {
            return false;
        }
        return getId() != null && getId().equals(((ProgressionMode) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProgressionMode{" +
            "id=" + getId() +
            ", titleAr='" + getTitleAr() + "'" +
            ", titleLat='" + getTitleLat() + "'" +
            "}";
    }
}
