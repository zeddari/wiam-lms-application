package com.wiam.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wiam.lms.domain.enumeration.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "payment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "payment_method", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String paymentMethod;

    @NotNull
    @Column(name = "paied_by", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String paiedBy;

    @NotNull
    @Column(name = "mode", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String mode;

    @Lob
    @Column(name = "poof")
    private byte[] poof;

    @Column(name = "poof_content_type")
    private String poofContentType;

    @NotNull
    @Column(name = "paid_at", nullable = false)
    private ZonedDateTime paidAt;

    @Column(name = "amount")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private PaymentType type;

    @NotNull
    @Column(name = "from_month", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer fromMonth;

    @NotNull
    @Column(name = "to_month", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer toMonth;

    @Lob
    @Column(name = "details")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sponsorings", "payments" }, allowSetters = true)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "payments", "student", "course" }, allowSetters = true)
    private Enrolement enrolment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public Payment paymentMethod(String paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaiedBy() {
        return this.paiedBy;
    }

    public Payment paiedBy(String paiedBy) {
        this.setPaiedBy(paiedBy);
        return this;
    }

    public void setPaiedBy(String paiedBy) {
        this.paiedBy = paiedBy;
    }

    public String getMode() {
        return this.mode;
    }

    public Payment mode(String mode) {
        this.setMode(mode);
        return this;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public byte[] getPoof() {
        return this.poof;
    }

    public Payment poof(byte[] poof) {
        this.setPoof(poof);
        return this;
    }

    public void setPoof(byte[] poof) {
        this.poof = poof;
    }

    public String getPoofContentType() {
        return this.poofContentType;
    }

    public Payment poofContentType(String poofContentType) {
        this.poofContentType = poofContentType;
        return this;
    }

    public void setPoofContentType(String poofContentType) {
        this.poofContentType = poofContentType;
    }

    public ZonedDateTime getPaidAt() {
        return this.paidAt;
    }

    public Payment paidAt(ZonedDateTime paidAt) {
        this.setPaidAt(paidAt);
        return this;
    }

    public void setPaidAt(ZonedDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getAmount() {
        return this.amount;
    }

    public Payment amount(String amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public PaymentType getType() {
        return this.type;
    }

    public Payment type(PaymentType type) {
        this.setType(type);
        return this;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public Integer getFromMonth() {
        return this.fromMonth;
    }

    public Payment fromMonth(Integer fromMonth) {
        this.setFromMonth(fromMonth);
        return this;
    }

    public void setFromMonth(Integer fromMonth) {
        this.fromMonth = fromMonth;
    }

    public Integer getToMonth() {
        return this.toMonth;
    }

    public Payment toMonth(Integer toMonth) {
        this.setToMonth(toMonth);
        return this;
    }

    public void setToMonth(Integer toMonth) {
        this.toMonth = toMonth;
    }

    public String getDetails() {
        return this.details;
    }

    public Payment details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Payment currency(Currency currency) {
        this.setCurrency(currency);
        return this;
    }

    public Enrolement getEnrolment() {
        return this.enrolment;
    }

    public void setEnrolment(Enrolement enrolement) {
        this.enrolment = enrolement;
    }

    public Payment enrolment(Enrolement enrolement) {
        this.setEnrolment(enrolement);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", paiedBy='" + getPaiedBy() + "'" +
            ", mode='" + getMode() + "'" +
            ", poof='" + getPoof() + "'" +
            ", poofContentType='" + getPoofContentType() + "'" +
            ", paidAt='" + getPaidAt() + "'" +
            ", amount='" + getAmount() + "'" +
            ", type='" + getType() + "'" +
            ", fromMonth=" + getFromMonth() +
            ", toMonth=" + getToMonth() +
            ", details='" + getDetails() + "'" +
            "}";
    }
}
