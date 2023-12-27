package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wiam.lms.domain.enumeration.Sourate;
import com.wiam.lms.domain.enumeration.Tilawa;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FollowUp.
 */
@Entity
@Table(name = "follow_up")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "followup")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FollowUp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_sourate")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Sourate fromSourate;

    @Column(name = "from_aya")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer fromAya;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_sourate")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Sourate toSourate;

    @Column(name = "to_aya")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer toAya;

    @Enumerated(EnumType.STRING)
    @Column(name = "tilawa_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Tilawa tilawaType;

    @Column(name = "notation")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String notation;

    @Column(name = "remarks")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String remarks;

    @JsonIgnoreProperties(value = { "followUp", "student2", "student" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private CoteryHistory coteryHistory;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FollowUp id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sourate getFromSourate() {
        return this.fromSourate;
    }

    public FollowUp fromSourate(Sourate fromSourate) {
        this.setFromSourate(fromSourate);
        return this;
    }

    public void setFromSourate(Sourate fromSourate) {
        this.fromSourate = fromSourate;
    }

    public Integer getFromAya() {
        return this.fromAya;
    }

    public FollowUp fromAya(Integer fromAya) {
        this.setFromAya(fromAya);
        return this;
    }

    public void setFromAya(Integer fromAya) {
        this.fromAya = fromAya;
    }

    public Sourate getToSourate() {
        return this.toSourate;
    }

    public FollowUp toSourate(Sourate toSourate) {
        this.setToSourate(toSourate);
        return this;
    }

    public void setToSourate(Sourate toSourate) {
        this.toSourate = toSourate;
    }

    public Integer getToAya() {
        return this.toAya;
    }

    public FollowUp toAya(Integer toAya) {
        this.setToAya(toAya);
        return this;
    }

    public void setToAya(Integer toAya) {
        this.toAya = toAya;
    }

    public Tilawa getTilawaType() {
        return this.tilawaType;
    }

    public FollowUp tilawaType(Tilawa tilawaType) {
        this.setTilawaType(tilawaType);
        return this;
    }

    public void setTilawaType(Tilawa tilawaType) {
        this.tilawaType = tilawaType;
    }

    public String getNotation() {
        return this.notation;
    }

    public FollowUp notation(String notation) {
        this.setNotation(notation);
        return this;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public FollowUp remarks(String remarks) {
        this.setRemarks(remarks);
        return this;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public CoteryHistory getCoteryHistory() {
        return this.coteryHistory;
    }

    public void setCoteryHistory(CoteryHistory coteryHistory) {
        this.coteryHistory = coteryHistory;
    }

    public FollowUp coteryHistory(CoteryHistory coteryHistory) {
        this.setCoteryHistory(coteryHistory);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FollowUp)) {
            return false;
        }
        return getId() != null && getId().equals(((FollowUp) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FollowUp{" +
            "id=" + getId() +
            ", fromSourate='" + getFromSourate() + "'" +
            ", fromAya=" + getFromAya() +
            ", toSourate='" + getToSourate() + "'" +
            ", toAya=" + getToAya() +
            ", tilawaType='" + getTilawaType() + "'" +
            ", notation='" + getNotation() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}
