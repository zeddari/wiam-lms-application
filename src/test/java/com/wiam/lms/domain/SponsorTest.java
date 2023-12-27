package com.wiam.lms.domain;

import static com.wiam.lms.domain.SponsorTestSamples.*;
import static com.wiam.lms.domain.SponsoringTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SponsorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sponsor.class);
        Sponsor sponsor1 = getSponsorSample1();
        Sponsor sponsor2 = new Sponsor();
        assertThat(sponsor1).isNotEqualTo(sponsor2);

        sponsor2.setId(sponsor1.getId());
        assertThat(sponsor1).isEqualTo(sponsor2);

        sponsor2 = getSponsorSample2();
        assertThat(sponsor1).isNotEqualTo(sponsor2);
    }

    @Test
    void userCustomTest() throws Exception {
        Sponsor sponsor = getSponsorRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        sponsor.setUserCustom(userCustomBack);
        assertThat(sponsor.getUserCustom()).isEqualTo(userCustomBack);

        sponsor.userCustom(null);
        assertThat(sponsor.getUserCustom()).isNull();
    }

    @Test
    void sponsoringTest() throws Exception {
        Sponsor sponsor = getSponsorRandomSampleGenerator();
        Sponsoring sponsoringBack = getSponsoringRandomSampleGenerator();

        sponsor.addSponsoring(sponsoringBack);
        assertThat(sponsor.getSponsorings()).containsOnly(sponsoringBack);
        assertThat(sponsoringBack.getSponsor()).isEqualTo(sponsor);

        sponsor.removeSponsoring(sponsoringBack);
        assertThat(sponsor.getSponsorings()).doesNotContain(sponsoringBack);
        assertThat(sponsoringBack.getSponsor()).isNull();

        sponsor.sponsorings(new HashSet<>(Set.of(sponsoringBack)));
        assertThat(sponsor.getSponsorings()).containsOnly(sponsoringBack);
        assertThat(sponsoringBack.getSponsor()).isEqualTo(sponsor);

        sponsor.setSponsorings(new HashSet<>());
        assertThat(sponsor.getSponsorings()).doesNotContain(sponsoringBack);
        assertThat(sponsoringBack.getSponsor()).isNull();
    }

    @Test
    void studentsTest() throws Exception {
        Sponsor sponsor = getSponsorRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        sponsor.addStudents(studentBack);
        assertThat(sponsor.getStudents()).containsOnly(studentBack);

        sponsor.removeStudents(studentBack);
        assertThat(sponsor.getStudents()).doesNotContain(studentBack);

        sponsor.students(new HashSet<>(Set.of(studentBack)));
        assertThat(sponsor.getStudents()).containsOnly(studentBack);

        sponsor.setStudents(new HashSet<>());
        assertThat(sponsor.getStudents()).doesNotContain(studentBack);
    }
}
