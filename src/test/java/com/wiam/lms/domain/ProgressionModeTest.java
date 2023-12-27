package com.wiam.lms.domain;

import static com.wiam.lms.domain.ProgressionModeTestSamples.*;
import static com.wiam.lms.domain.ProgressionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProgressionModeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProgressionMode.class);
        ProgressionMode progressionMode1 = getProgressionModeSample1();
        ProgressionMode progressionMode2 = new ProgressionMode();
        assertThat(progressionMode1).isNotEqualTo(progressionMode2);

        progressionMode2.setId(progressionMode1.getId());
        assertThat(progressionMode1).isEqualTo(progressionMode2);

        progressionMode2 = getProgressionModeSample2();
        assertThat(progressionMode1).isNotEqualTo(progressionMode2);
    }

    @Test
    void progressionTest() throws Exception {
        ProgressionMode progressionMode = getProgressionModeRandomSampleGenerator();
        Progression progressionBack = getProgressionRandomSampleGenerator();

        progressionMode.addProgression(progressionBack);
        assertThat(progressionMode.getProgressions()).containsOnly(progressionBack);
        assertThat(progressionBack.getMode()).isEqualTo(progressionMode);

        progressionMode.removeProgression(progressionBack);
        assertThat(progressionMode.getProgressions()).doesNotContain(progressionBack);
        assertThat(progressionBack.getMode()).isNull();

        progressionMode.progressions(new HashSet<>(Set.of(progressionBack)));
        assertThat(progressionMode.getProgressions()).containsOnly(progressionBack);
        assertThat(progressionBack.getMode()).isEqualTo(progressionMode);

        progressionMode.setProgressions(new HashSet<>());
        assertThat(progressionMode.getProgressions()).doesNotContain(progressionBack);
        assertThat(progressionBack.getMode()).isNull();
    }
}
