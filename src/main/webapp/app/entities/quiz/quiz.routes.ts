import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { QuizComponent } from './list/quiz.component';
import { QuizDetailComponent } from './detail/quiz-detail.component';
import { QuizUpdateComponent } from './update/quiz-update.component';
import QuizResolve from './route/quiz-routing-resolve.service';

const quizRoute: Routes = [
  {
    path: '',
    component: QuizComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QuizDetailComponent,
    resolve: {
      quiz: QuizResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QuizUpdateComponent,
    resolve: {
      quiz: QuizResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QuizUpdateComponent,
    resolve: {
      quiz: QuizResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default quizRoute;
