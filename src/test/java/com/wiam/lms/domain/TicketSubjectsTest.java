package com.wiam.lms.domain;

import static com.wiam.lms.domain.TicketSubjectsTestSamples.*;
import static com.wiam.lms.domain.TicketsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TicketSubjectsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketSubjects.class);
        TicketSubjects ticketSubjects1 = getTicketSubjectsSample1();
        TicketSubjects ticketSubjects2 = new TicketSubjects();
        assertThat(ticketSubjects1).isNotEqualTo(ticketSubjects2);

        ticketSubjects2.setId(ticketSubjects1.getId());
        assertThat(ticketSubjects1).isEqualTo(ticketSubjects2);

        ticketSubjects2 = getTicketSubjectsSample2();
        assertThat(ticketSubjects1).isNotEqualTo(ticketSubjects2);
    }

    @Test
    void ticketsTest() throws Exception {
        TicketSubjects ticketSubjects = getTicketSubjectsRandomSampleGenerator();
        Tickets ticketsBack = getTicketsRandomSampleGenerator();

        ticketSubjects.addTickets(ticketsBack);
        assertThat(ticketSubjects.getTickets()).containsOnly(ticketsBack);
        assertThat(ticketsBack.getSubject()).isEqualTo(ticketSubjects);

        ticketSubjects.removeTickets(ticketsBack);
        assertThat(ticketSubjects.getTickets()).doesNotContain(ticketsBack);
        assertThat(ticketsBack.getSubject()).isNull();

        ticketSubjects.tickets(new HashSet<>(Set.of(ticketsBack)));
        assertThat(ticketSubjects.getTickets()).containsOnly(ticketsBack);
        assertThat(ticketsBack.getSubject()).isEqualTo(ticketSubjects);

        ticketSubjects.setTickets(new HashSet<>());
        assertThat(ticketSubjects.getTickets()).doesNotContain(ticketsBack);
        assertThat(ticketsBack.getSubject()).isNull();
    }
}
