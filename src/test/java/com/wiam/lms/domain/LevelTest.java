package com.wiam.lms.domain;

import static com.wiam.lms.domain.CourseTestSamples.*;
import static com.wiam.lms.domain.LevelTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LevelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Level.class);
        Level level1 = getLevelSample1();
        Level level2 = new Level();
        assertThat(level1).isNotEqualTo(level2);

        level2.setId(level1.getId());
        assertThat(level1).isEqualTo(level2);

        level2 = getLevelSample2();
        assertThat(level1).isNotEqualTo(level2);
    }

    @Test
    void courseTest() throws Exception {
        Level level = getLevelRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        level.addCourse(courseBack);
        assertThat(level.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getLevel()).isEqualTo(level);

        level.removeCourse(courseBack);
        assertThat(level.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getLevel()).isNull();

        level.courses(new HashSet<>(Set.of(courseBack)));
        assertThat(level.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getLevel()).isEqualTo(level);

        level.setCourses(new HashSet<>());
        assertThat(level.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getLevel()).isNull();
    }
}
