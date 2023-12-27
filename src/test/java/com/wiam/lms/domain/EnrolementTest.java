package com.wiam.lms.domain;

import static com.wiam.lms.domain.CourseTestSamples.*;
import static com.wiam.lms.domain.EnrolementTestSamples.*;
import static com.wiam.lms.domain.PaymentTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EnrolementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Enrolement.class);
        Enrolement enrolement1 = getEnrolementSample1();
        Enrolement enrolement2 = new Enrolement();
        assertThat(enrolement1).isNotEqualTo(enrolement2);

        enrolement2.setId(enrolement1.getId());
        assertThat(enrolement1).isEqualTo(enrolement2);

        enrolement2 = getEnrolementSample2();
        assertThat(enrolement1).isNotEqualTo(enrolement2);
    }

    @Test
    void paymentTest() throws Exception {
        Enrolement enrolement = getEnrolementRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        enrolement.addPayment(paymentBack);
        assertThat(enrolement.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getEnrolment()).isEqualTo(enrolement);

        enrolement.removePayment(paymentBack);
        assertThat(enrolement.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getEnrolment()).isNull();

        enrolement.payments(new HashSet<>(Set.of(paymentBack)));
        assertThat(enrolement.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getEnrolment()).isEqualTo(enrolement);

        enrolement.setPayments(new HashSet<>());
        assertThat(enrolement.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getEnrolment()).isNull();
    }

    @Test
    void studentTest() throws Exception {
        Enrolement enrolement = getEnrolementRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        enrolement.setStudent(studentBack);
        assertThat(enrolement.getStudent()).isEqualTo(studentBack);

        enrolement.student(null);
        assertThat(enrolement.getStudent()).isNull();
    }

    @Test
    void courseTest() throws Exception {
        Enrolement enrolement = getEnrolementRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        enrolement.setCourse(courseBack);
        assertThat(enrolement.getCourse()).isEqualTo(courseBack);

        enrolement.course(null);
        assertThat(enrolement.getCourse()).isNull();
    }
}
