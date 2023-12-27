import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { QuizCertificateComponent } from './list/quiz-certificate.component';
import { QuizCertificateDetailComponent } from './detail/quiz-certificate-detail.component';
import { QuizCertificateUpdateComponent } from './update/quiz-certificate-update.component';
import QuizCertificateResolve from './route/quiz-certificate-routing-resolve.service';

const quizCertificateRoute: Routes = [
  {
    path: '',
    component: QuizCertificateComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QuizCertificateDetailComponent,
    resolve: {
      quizCertificate: QuizCertificateResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QuizCertificateUpdateComponent,
    resolve: {
      quizCertificate: QuizCertificateResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QuizCertificateUpdateComponent,
    resolve: {
      quizCertificate: QuizCertificateResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default quizCertificateRoute;
