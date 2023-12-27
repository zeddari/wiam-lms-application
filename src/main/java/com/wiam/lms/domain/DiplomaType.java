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
 * A DiplomaType.
 */
@Entity
@Table(name = "diploma_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "diplomatype")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DiplomaType implements Serializable {

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

    @Size(max = 20)
    @Column(name = "title_lat", length = 20)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String titleLat;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "diplomaType")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "userCustom", "diplomaType" }, allowSetters = true)
    private Set<Diploma> diplomas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DiplomaType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleAr() {
        return this.titleAr;
    }

    public DiplomaType titleAr(String titleAr) {
        this.setTitleAr(titleAr);
        return this;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getTitleLat() {
        return this.titleLat;
    }

    public DiplomaType titleLat(String titleLat) {
        this.setTitleLat(titleLat);
        return this;
    }

    public void setTitleLat(String titleLat) {
        this.titleLat = titleLat;
    }

    public Set<Diploma> getDiplomas() {
        return this.diplomas;
    }

    public void setDiplomas(Set<Diploma> diplomas) {
        if (this.diplomas != null) {
            this.diplomas.forEach(i -> i.setDiplomaType(null));
        }
        if (diplomas != null) {
            diplomas.forEach(i -> i.setDiplomaType(this));
        }
        this.diplomas = diplomas;
    }

    public DiplomaType diplomas(Set<Diploma> diplomas) {
        this.setDiplomas(diplomas);
        return this;
    }

    public DiplomaType addDiploma(Diploma diploma) {
        this.diplomas.add(diploma);
        diploma.setDiplomaType(this);
        return this;
    }

    public DiplomaType removeDiploma(Diploma diploma) {
        this.diplomas.remove(diploma);
        diploma.setDiplomaType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiplomaType)) {
            return false;
        }
        return getId() != null && getId().equals(((DiplomaType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DiplomaType{" +
            "id=" + getId() +
            ", titleAr='" + getTitleAr() + "'" +
            ", titleLat='" + getTitleLat() + "'" +
            "}";
    }
}
