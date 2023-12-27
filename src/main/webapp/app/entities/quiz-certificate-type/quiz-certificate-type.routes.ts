import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { QuizCertificateTypeComponent } from './list/quiz-certificate-type.component';
import { QuizCertificateTypeDetailComponent } from './detail/quiz-certificate-type-detail.component';
import { QuizCertificateTypeUpdateComponent } from './update/quiz-certificate-type-update.component';
import QuizCertificateTypeResolve from './route/quiz-certificate-type-routing-resolve.service';

const quizCertificateTypeRoute: Routes = [
  {
    path: '',
    component: QuizCertificateTypeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QuizCertificateTypeDetailComponent,
    resolve: {
      quizCertificateType: QuizCertificateTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QuizCertificateTypeUpdateComponent,
    resolve: {
      quizCertificateType: QuizCertificateTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QuizCertificateTypeUpdateComponent,
    resolve: {
      quizCertificateType: QuizCertificateTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default quizCertificateTypeRoute;
