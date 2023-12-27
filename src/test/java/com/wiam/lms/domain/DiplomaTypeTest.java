package com.wiam.lms.domain;

import static com.wiam.lms.domain.DiplomaTestSamples.*;
import static com.wiam.lms.domain.DiplomaTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DiplomaTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DiplomaType.class);
        DiplomaType diplomaType1 = getDiplomaTypeSample1();
        DiplomaType diplomaType2 = new DiplomaType();
        assertThat(diplomaType1).isNotEqualTo(diplomaType2);

        diplomaType2.setId(diplomaType1.getId());
        assertThat(diplomaType1).isEqualTo(diplomaType2);

        diplomaType2 = getDiplomaTypeSample2();
        assertThat(diplomaType1).isNotEqualTo(diplomaType2);
    }

    @Test
    void diplomaTest() throws Exception {
        DiplomaType diplomaType = getDiplomaTypeRandomSampleGenerator();
        Diploma diplomaBack = getDiplomaRandomSampleGenerator();

        diplomaType.addDiploma(diplomaBack);
        assertThat(diplomaType.getDiplomas()).containsOnly(diplomaBack);
        assertThat(diplomaBack.getDiplomaType()).isEqualTo(diplomaType);

        diplomaType.removeDiploma(diplomaBack);
        assertThat(diplomaType.getDiplomas()).doesNotContain(diplomaBack);
        assertThat(diplomaBack.getDiplomaType()).isNull();

        diplomaType.diplomas(new HashSet<>(Set.of(diplomaBack)));
        assertThat(diplomaType.getDiplomas()).containsOnly(diplomaBack);
        assertThat(diplomaBack.getDiplomaType()).isEqualTo(diplomaType);

        diplomaType.setDiplomas(new HashSet<>());
        assertThat(diplomaType.getDiplomas()).doesNotContain(diplomaBack);
        assertThat(diplomaBack.getDiplomaType()).isNull();
    }
}
