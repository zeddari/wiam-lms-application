package com.wiam.lms.domain;

import static com.wiam.lms.domain.DiplomaTestSamples.*;
import static com.wiam.lms.domain.DiplomaTypeTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiplomaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Diploma.class);
        Diploma diploma1 = getDiplomaSample1();
        Diploma diploma2 = new Diploma();
        assertThat(diploma1).isNotEqualTo(diploma2);

        diploma2.setId(diploma1.getId());
        assertThat(diploma1).isEqualTo(diploma2);

        diploma2 = getDiplomaSample2();
        assertThat(diploma1).isNotEqualTo(diploma2);
    }

    @Test
    void userCustomTest() throws Exception {
        Diploma diploma = getDiplomaRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        diploma.setUserCustom(userCustomBack);
        assertThat(diploma.getUserCustom()).isEqualTo(userCustomBack);

        diploma.userCustom(null);
        assertThat(diploma.getUserCustom()).isNull();
    }

    @Test
    void diplomaTypeTest() throws Exception {
        Diploma diploma = getDiplomaRandomSampleGenerator();
        DiplomaType diplomaTypeBack = getDiplomaTypeRandomSampleGenerator();

        diploma.setDiplomaType(diplomaTypeBack);
        assertThat(diploma.getDiplomaType()).isEqualTo(diplomaTypeBack);

        diploma.diplomaType(null);
        assertThat(diploma.getDiplomaType()).isNull();
    }
}
