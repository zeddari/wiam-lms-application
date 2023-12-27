package com.wiam.lms.domain;

import static com.wiam.lms.domain.TicketSubjectsTestSamples.*;
import static com.wiam.lms.domain.TicketsTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tickets.class);
        Tickets tickets1 = getTicketsSample1();
        Tickets tickets2 = new Tickets();
        assertThat(tickets1).isNotEqualTo(tickets2);

        tickets2.setId(tickets1.getId());
        assertThat(tickets1).isEqualTo(tickets2);

        tickets2 = getTicketsSample2();
        assertThat(tickets1).isNotEqualTo(tickets2);
    }

    @Test
    void userCustomTest() throws Exception {
        Tickets tickets = getTicketsRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        tickets.setUserCustom(userCustomBack);
        assertThat(tickets.getUserCustom()).isEqualTo(userCustomBack);

        tickets.userCustom(null);
        assertThat(tickets.getUserCustom()).isNull();
    }

    @Test
    void subjectTest() throws Exception {
        Tickets tickets = getTicketsRandomSampleGenerator();
        TicketSubjects ticketSubjectsBack = getTicketSubjectsRandomSampleGenerator();

        tickets.setSubject(ticketSubjectsBack);
        assertThat(tickets.getSubject()).isEqualTo(ticketSubjectsBack);

        tickets.subject(null);
        assertThat(tickets.getSubject()).isNull();
    }
}
