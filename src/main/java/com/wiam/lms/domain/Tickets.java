package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tickets.
 */
@Entity
@Table(name = "tickets")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "tickets")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tickets implements Serializable {

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

    @Lob
    @Column(name = "justif_doc")
    private byte[] justifDoc;

    @Column(name = "justif_doc_content_type")
    private String justifDocContentType;

    @NotNull
    @Column(name = "date_ticket", nullable = false)
    private ZonedDateTime dateTicket;

    @Column(name = "date_process")
    private ZonedDateTime dateProcess;

    @Column(name = "processed")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean processed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "languages", "country", "job", "exams", "sponsor", "employee", "professor", "student" },
        allowSetters = true
    )
    private UserCustom userCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tickets" }, allowSetters = true)
    private TicketSubjects subject;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tickets id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Tickets title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Tickets description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getJustifDoc() {
        return this.justifDoc;
    }

    public Tickets justifDoc(byte[] justifDoc) {
        this.setJustifDoc(justifDoc);
        return this;
    }

    public void setJustifDoc(byte[] justifDoc) {
        this.justifDoc = justifDoc;
    }

    public String getJustifDocContentType() {
        return this.justifDocContentType;
    }

    public Tickets justifDocContentType(String justifDocContentType) {
        this.justifDocContentType = justifDocContentType;
        return this;
    }

    public void setJustifDocContentType(String justifDocContentType) {
        this.justifDocContentType = justifDocContentType;
    }

    public ZonedDateTime getDateTicket() {
        return this.dateTicket;
    }

    public Tickets dateTicket(ZonedDateTime dateTicket) {
        this.setDateTicket(dateTicket);
        return this;
    }

    public void setDateTicket(ZonedDateTime dateTicket) {
        this.dateTicket = dateTicket;
    }

    public ZonedDateTime getDateProcess() {
        return this.dateProcess;
    }

    public Tickets dateProcess(ZonedDateTime dateProcess) {
        this.setDateProcess(dateProcess);
        return this;
    }

    public void setDateProcess(ZonedDateTime dateProcess) {
        this.dateProcess = dateProcess;
    }

    public Boolean getProcessed() {
        return this.processed;
    }

    public Tickets processed(Boolean processed) {
        this.setProcessed(processed);
        return this;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public UserCustom getUserCustom() {
        return this.userCustom;
    }

    public void setUserCustom(UserCustom userCustom) {
        this.userCustom = userCustom;
    }

    public Tickets userCustom(UserCustom userCustom) {
        this.setUserCustom(userCustom);
        return this;
    }

    public TicketSubjects getSubject() {
        return this.subject;
    }

    public void setSubject(TicketSubjects ticketSubjects) {
        this.subject = ticketSubjects;
    }

    public Tickets subject(TicketSubjects ticketSubjects) {
        this.setSubject(ticketSubjects);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tickets)) {
            return false;
        }
        return getId() != null && getId().equals(((Tickets) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tickets{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", justifDoc='" + getJustifDoc() + "'" +
            ", justifDocContentType='" + getJustifDocContentType() + "'" +
            ", dateTicket='" + getDateTicket() + "'" +
            ", dateProcess='" + getDateProcess() + "'" +
            ", processed='" + getProcessed() + "'" +
            "}";
    }
}
