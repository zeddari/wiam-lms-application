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
 * A Currency.
 */
@Entity
@Table(name = "currency")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "currency")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Currency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name_ar", length = 50, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nameAr;

    @NotNull
    @Size(max = 500)
    @Column(name = "name_lat", length = 500, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nameLat;

    @NotNull
    @Size(max = 10)
    @Column(name = "code", length = 10, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "currency")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sponsor", "project", "currency" }, allowSetters = true)
    private Set<Sponsoring> sponsorings = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "currency")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "currency", "enrolment" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Currency id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameAr() {
        return this.nameAr;
    }

    public Currency nameAr(String nameAr) {
        this.setNameAr(nameAr);
        return this;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getNameLat() {
        return this.nameLat;
    }

    public Currency nameLat(String nameLat) {
        this.setNameLat(nameLat);
        return this;
    }

    public void setNameLat(String nameLat) {
        this.nameLat = nameLat;
    }

    public String getCode() {
        return this.code;
    }

    public Currency code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<Sponsoring> getSponsorings() {
        return this.sponsorings;
    }

    public void setSponsorings(Set<Sponsoring> sponsorings) {
        if (this.sponsorings != null) {
            this.sponsorings.forEach(i -> i.setCurrency(null));
        }
        if (sponsorings != null) {
            sponsorings.forEach(i -> i.setCurrency(this));
        }
        this.sponsorings = sponsorings;
    }

    public Currency sponsorings(Set<Sponsoring> sponsorings) {
        this.setSponsorings(sponsorings);
        return this;
    }

    public Currency addSponsoring(Sponsoring sponsoring) {
        this.sponsorings.add(sponsoring);
        sponsoring.setCurrency(this);
        return this;
    }

    public Currency removeSponsoring(Sponsoring sponsoring) {
        this.sponsorings.remove(sponsoring);
        sponsoring.setCurrency(null);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setCurrency(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setCurrency(this));
        }
        this.payments = payments;
    }

    public Currency payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public Currency addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setCurrency(this);
        return this;
    }

    public Currency removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setCurrency(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Currency)) {
            return false;
        }
        return getId() != null && getId().equals(((Currency) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Currency{" +
            "id=" + getId() +
            ", nameAr='" + getNameAr() + "'" +
            ", nameLat='" + getNameLat() + "'" +
            ", code='" + getCode() + "'" +
            "}";
    }
}
