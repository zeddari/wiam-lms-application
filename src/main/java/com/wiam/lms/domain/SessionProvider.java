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
 * A SessionProvider.
 */
@Entity
@Table(name = "session_provider")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "sessionprovider")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SessionProvider implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Size(max = 500)
    @Column(name = "website", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String website;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "provider")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "provider", "sessions" }, allowSetters = true)
    private Set<SessionLink> sessionLinks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SessionProvider id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SessionProvider name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public SessionProvider description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return this.website;
    }

    public SessionProvider website(String website) {
        this.setWebsite(website);
        return this;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Set<SessionLink> getSessionLinks() {
        return this.sessionLinks;
    }

    public void setSessionLinks(Set<SessionLink> sessionLinks) {
        if (this.sessionLinks != null) {
            this.sessionLinks.forEach(i -> i.setProvider(null));
        }
        if (sessionLinks != null) {
            sessionLinks.forEach(i -> i.setProvider(this));
        }
        this.sessionLinks = sessionLinks;
    }

    public SessionProvider sessionLinks(Set<SessionLink> sessionLinks) {
        this.setSessionLinks(sessionLinks);
        return this;
    }

    public SessionProvider addSessionLink(SessionLink sessionLink) {
        this.sessionLinks.add(sessionLink);
        sessionLink.setProvider(this);
        return this;
    }

    public SessionProvider removeSessionLink(SessionLink sessionLink) {
        this.sessionLinks.remove(sessionLink);
        sessionLink.setProvider(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionProvider)) {
            return false;
        }
        return getId() != null && getId().equals(((SessionProvider) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SessionProvider{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", website='" + getWebsite() + "'" +
            "}";
    }
}
