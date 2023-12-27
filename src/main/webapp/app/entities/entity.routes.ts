import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'user-custom',
    data: { pageTitle: 'wiamLmsApplicationApp.userCustom.home.title' },
    loadChildren: () => import('./user-custom/user-custom.routes'),
  },
  {
    path: 'job',
    data: { pageTitle: 'wiamLmsApplicationApp.job.home.title' },
    loadChildren: () => import('./job/job.routes'),
  },
  {
    path: 'country-2',
    data: { pageTitle: 'wiamLmsApplicationApp.country2.home.title' },
    loadChildren: () => import('./country-2/country-2.routes'),
  },
  {
    path: 'language',
    data: { pageTitle: 'wiamLmsApplicationApp.language.home.title' },
    loadChildren: () => import('./language/language.routes'),
  },
  {
    path: 'departement',
    data: { pageTitle: 'wiamLmsApplicationApp.departement.home.title' },
    loadChildren: () => import('./departement/departement.routes'),
  },
  {
    path: 'level',
    data: { pageTitle: 'wiamLmsApplicationApp.level.home.title' },
    loadChildren: () => import('./level/level.routes'),
  },
  {
    path: 'topic',
    data: { pageTitle: 'wiamLmsApplicationApp.topic.home.title' },
    loadChildren: () => import('./topic/topic.routes'),
  },
  {
    path: 'site',
    data: { pageTitle: 'wiamLmsApplicationApp.site.home.title' },
    loadChildren: () => import('./site/site.routes'),
  },
  {
    path: 'city',
    data: { pageTitle: 'wiamLmsApplicationApp.city.home.title' },
    loadChildren: () => import('./city/city.routes'),
  },
  {
    path: 'country',
    data: { pageTitle: 'wiamLmsApplicationApp.country.home.title' },
    loadChildren: () => import('./country/country.routes'),
  },
  {
    path: 'classroom',
    data: { pageTitle: 'wiamLmsApplicationApp.classroom.home.title' },
    loadChildren: () => import('./classroom/classroom.routes'),
  },
  {
    path: 'group',
    data: { pageTitle: 'wiamLmsApplicationApp.group.home.title' },
    loadChildren: () => import('./group/group.routes'),
  },
  {
    path: 'job-title',
    data: { pageTitle: 'wiamLmsApplicationApp.jobTitle.home.title' },
    loadChildren: () => import('./job-title/job-title.routes'),
  },
  {
    path: 'sponsor',
    data: { pageTitle: 'wiamLmsApplicationApp.sponsor.home.title' },
    loadChildren: () => import('./sponsor/sponsor.routes'),
  },
  {
    path: 'currency',
    data: { pageTitle: 'wiamLmsApplicationApp.currency.home.title' },
    loadChildren: () => import('./currency/currency.routes'),
  },
  {
    path: 'employee',
    data: { pageTitle: 'wiamLmsApplicationApp.employee.home.title' },
    loadChildren: () => import('./employee/employee.routes'),
  },
  {
    path: 'professor',
    data: { pageTitle: 'wiamLmsApplicationApp.professor.home.title' },
    loadChildren: () => import('./professor/professor.routes'),
  },
  {
    path: 'student',
    data: { pageTitle: 'wiamLmsApplicationApp.student.home.title' },
    loadChildren: () => import('./student/student.routes'),
  },
  {
    path: 'diploma-type',
    data: { pageTitle: 'wiamLmsApplicationApp.diplomaType.home.title' },
    loadChildren: () => import('./diploma-type/diploma-type.routes'),
  },
  {
    path: 'diploma',
    data: { pageTitle: 'wiamLmsApplicationApp.diploma.home.title' },
    loadChildren: () => import('./diploma/diploma.routes'),
  },
  {
    path: 'enrolement',
    data: { pageTitle: 'wiamLmsApplicationApp.enrolement.home.title' },
    loadChildren: () => import('./enrolement/enrolement.routes'),
  },
  {
    path: 'course',
    data: { pageTitle: 'wiamLmsApplicationApp.course.home.title' },
    loadChildren: () => import('./course/course.routes'),
  },
  {
    path: 'part',
    data: { pageTitle: 'wiamLmsApplicationApp.part.home.title' },
    loadChildren: () => import('./part/part.routes'),
  },
  {
    path: 'review',
    data: { pageTitle: 'wiamLmsApplicationApp.review.home.title' },
    loadChildren: () => import('./review/review.routes'),
  },
  {
    path: 'quiz-certificate',
    data: { pageTitle: 'wiamLmsApplicationApp.quizCertificate.home.title' },
    loadChildren: () => import('./quiz-certificate/quiz-certificate.routes'),
  },
  {
    path: 'quiz-certificate-type',
    data: { pageTitle: 'wiamLmsApplicationApp.quizCertificateType.home.title' },
    loadChildren: () => import('./quiz-certificate-type/quiz-certificate-type.routes'),
  },
  {
    path: 'question',
    data: { pageTitle: 'wiamLmsApplicationApp.question.home.title' },
    loadChildren: () => import('./question/question.routes'),
  },
  {
    path: 'answer',
    data: { pageTitle: 'wiamLmsApplicationApp.answer.home.title' },
    loadChildren: () => import('./answer/answer.routes'),
  },
  {
    path: 'progression',
    data: { pageTitle: 'wiamLmsApplicationApp.progression.home.title' },
    loadChildren: () => import('./progression/progression.routes'),
  },
  {
    path: 'progression-mode',
    data: { pageTitle: 'wiamLmsApplicationApp.progressionMode.home.title' },
    loadChildren: () => import('./progression-mode/progression-mode.routes'),
  },
  {
    path: 'payment',
    data: { pageTitle: 'wiamLmsApplicationApp.payment.home.title' },
    loadChildren: () => import('./payment/payment.routes'),
  },
  {
    path: 'session-type',
    data: { pageTitle: 'wiamLmsApplicationApp.sessionType.home.title' },
    loadChildren: () => import('./session-type/session-type.routes'),
  },
  {
    path: 'session-mode',
    data: { pageTitle: 'wiamLmsApplicationApp.sessionMode.home.title' },
    loadChildren: () => import('./session-mode/session-mode.routes'),
  },
  {
    path: 'session-join-mode',
    data: { pageTitle: 'wiamLmsApplicationApp.sessionJoinMode.home.title' },
    loadChildren: () => import('./session-join-mode/session-join-mode.routes'),
  },
  {
    path: 'session-provider',
    data: { pageTitle: 'wiamLmsApplicationApp.sessionProvider.home.title' },
    loadChildren: () => import('./session-provider/session-provider.routes'),
  },
  {
    path: 'session-link',
    data: { pageTitle: 'wiamLmsApplicationApp.sessionLink.home.title' },
    loadChildren: () => import('./session-link/session-link.routes'),
  },
  {
    path: 'session',
    data: { pageTitle: 'wiamLmsApplicationApp.session.home.title' },
    loadChildren: () => import('./session/session.routes'),
  },
  {
    path: 'tickets',
    data: { pageTitle: 'wiamLmsApplicationApp.tickets.home.title' },
    loadChildren: () => import('./tickets/tickets.routes'),
  },
  {
    path: 'ticket-subjects',
    data: { pageTitle: 'wiamLmsApplicationApp.ticketSubjects.home.title' },
    loadChildren: () => import('./ticket-subjects/ticket-subjects.routes'),
  },
  {
    path: 'project',
    data: { pageTitle: 'wiamLmsApplicationApp.project.home.title' },
    loadChildren: () => import('./project/project.routes'),
  },
  {
    path: 'sponsoring',
    data: { pageTitle: 'wiamLmsApplicationApp.sponsoring.home.title' },
    loadChildren: () => import('./sponsoring/sponsoring.routes'),
  },
  {
    path: 'quiz',
    data: { pageTitle: 'wiamLmsApplicationApp.quiz.home.title' },
    loadChildren: () => import('./quiz/quiz.routes'),
  },
  {
    path: 'question-2',
    data: { pageTitle: 'wiamLmsApplicationApp.question2.home.title' },
    loadChildren: () => import('./question-2/question-2.routes'),
  },
  {
    path: 'certificate',
    data: { pageTitle: 'wiamLmsApplicationApp.certificate.home.title' },
    loadChildren: () => import('./certificate/certificate.routes'),
  },
  {
    path: 'cotery-history',
    data: { pageTitle: 'wiamLmsApplicationApp.coteryHistory.home.title' },
    loadChildren: () => import('./cotery-history/cotery-history.routes'),
  },
  {
    path: 'cotery',
    data: { pageTitle: 'wiamLmsApplicationApp.cotery.home.title' },
    loadChildren: () => import('./cotery/cotery.routes'),
  },
  {
    path: 'follow-up',
    data: { pageTitle: 'wiamLmsApplicationApp.followUp.home.title' },
    loadChildren: () => import('./follow-up/follow-up.routes'),
  },
  {
    path: 'leave-request',
    data: { pageTitle: 'wiamLmsApplicationApp.leaveRequest.home.title' },
    loadChildren: () => import('./leave-request/leave-request.routes'),
  },
  {
    path: 'exam',
    data: { pageTitle: 'wiamLmsApplicationApp.exam.home.title' },
    loadChildren: () => import('./exam/exam.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
