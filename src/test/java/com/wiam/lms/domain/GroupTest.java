package com.wiam.lms.domain;

import static com.wiam.lms.domain.GroupTestSamples.*;
import static com.wiam.lms.domain.GroupTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Group.class);
        Group group1 = getGroupSample1();
        Group group2 = new Group();
        assertThat(group1).isNotEqualTo(group2);

        group2.setId(group1.getId());
        assertThat(group1).isEqualTo(group2);

        group2 = getGroupSample2();
        assertThat(group1).isNotEqualTo(group2);
    }

    @Test
    void sessionTest() throws Exception {
        Group group = getGroupRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        group.addSession(sessionBack);
        assertThat(group.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getGroup()).isEqualTo(group);

        group.removeSession(sessionBack);
        assertThat(group.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getGroup()).isNull();

        group.sessions(new HashSet<>(Set.of(sessionBack)));
        assertThat(group.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getGroup()).isEqualTo(group);

        group.setSessions(new HashSet<>());
        assertThat(group.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getGroup()).isNull();
    }

    @Test
    void studentTest() throws Exception {
        Group group = getGroupRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        group.addStudent(studentBack);
        assertThat(group.getStudents()).containsOnly(studentBack);
        assertThat(studentBack.getGroup2()).isEqualTo(group);

        group.removeStudent(studentBack);
        assertThat(group.getStudents()).doesNotContain(studentBack);
        assertThat(studentBack.getGroup2()).isNull();

        group.students(new HashSet<>(Set.of(studentBack)));
        assertThat(group.getStudents()).containsOnly(studentBack);
        assertThat(studentBack.getGroup2()).isEqualTo(group);

        group.setStudents(new HashSet<>());
        assertThat(group.getStudents()).doesNotContain(studentBack);
        assertThat(studentBack.getGroup2()).isNull();
    }

    @Test
    void groupTest() throws Exception {
        Group group = getGroupRandomSampleGenerator();
        Group groupBack = getGroupRandomSampleGenerator();

        group.addGroup(groupBack);
        assertThat(group.getGroups()).containsOnly(groupBack);
        assertThat(groupBack.getGroup1()).isEqualTo(group);

        group.removeGroup(groupBack);
        assertThat(group.getGroups()).doesNotContain(groupBack);
        assertThat(groupBack.getGroup1()).isNull();

        group.groups(new HashSet<>(Set.of(groupBack)));
        assertThat(group.getGroups()).containsOnly(groupBack);
        assertThat(groupBack.getGroup1()).isEqualTo(group);

        group.setGroups(new HashSet<>());
        assertThat(group.getGroups()).doesNotContain(groupBack);
        assertThat(groupBack.getGroup1()).isNull();
    }

    @Test
    void group1Test() throws Exception {
        Group group = getGroupRandomSampleGenerator();
        Group groupBack = getGroupRandomSampleGenerator();

        group.setGroup1(groupBack);
        assertThat(group.getGroup1()).isEqualTo(groupBack);

        group.group1(null);
        assertThat(group.getGroup1()).isNull();
    }
}
