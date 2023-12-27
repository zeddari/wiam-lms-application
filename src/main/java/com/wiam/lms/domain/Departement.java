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
 * A Departement.
 */
@Entity
@Table(name = "departement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "departement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Departement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name_ar", length = 100, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nameAr;

    @NotNull
    @Size(max = 100)
    @Column(name = "name_lat", length = 100, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nameLat;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "departement")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "userCustom", "departement", "job", "sessions" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "departement1")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "employees", "departements", "departement1" }, allowSetters = true)
    private Set<Departement> departements = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employees", "departements", "departement1" }, allowSetters = true)
    private Departement departement1;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Departement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameAr() {
        return this.nameAr;
    }

    public Departement nameAr(String nameAr) {
        this.setNameAr(nameAr);
        return this;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getNameLat() {
        return this.nameLat;
    }

    public Departement nameLat(String nameLat) {
        this.setNameLat(nameLat);
        return this;
    }

    public void setNameLat(String nameLat) {
        this.nameLat = nameLat;
    }

    public String getDescription() {
        return this.description;
    }

    public Departement description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setDepartement(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setDepartement(this));
        }
        this.employees = employees;
    }

    public Departement employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Departement addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.setDepartement(this);
        return this;
    }

    public Departement removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.setDepartement(null);
        return this;
    }

    public Set<Departement> getDepartements() {
        return this.departements;
    }

    public void setDepartements(Set<Departement> departements) {
        if (this.departements != null) {
            this.departements.forEach(i -> i.setDepartement1(null));
        }
        if (departements != null) {
            departements.forEach(i -> i.setDepartement1(this));
        }
        this.departements = departements;
    }

    public Departement departements(Set<Departement> departements) {
        this.setDepartements(departements);
        return this;
    }

    public Departement addDepartement(Departement departement) {
        this.departements.add(departement);
        departement.setDepartement1(this);
        return this;
    }

    public Departement removeDepartement(Departement departement) {
        this.departements.remove(departement);
        departement.setDepartement1(null);
        return this;
    }

    public Departement getDepartement1() {
        return this.departement1;
    }

    public void setDepartement1(Departement departement) {
        this.departement1 = departement;
    }

    public Departement departement1(Departement departement) {
        this.setDepartement1(departement);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Departement)) {
            return false;
        }
        return getId() != null && getId().equals(((Departement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Departement{" +
            "id=" + getId() +
            ", nameAr='" + getNameAr() + "'" +
            ", nameLat='" + getNameLat() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
