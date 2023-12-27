import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ExamComponent } from './list/exam.component';
import { ExamDetailComponent } from './detail/exam-detail.component';
import { ExamUpdateComponent } from './update/exam-update.component';
import ExamResolve from './route/exam-routing-resolve.service';

const examRoute: Routes = [
  {
    path: '',
    component: ExamComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ExamDetailComponent,
    resolve: {
      exam: ExamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ExamUpdateComponent,
    resolve: {
      exam: ExamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ExamUpdateComponent,
    resolve: {
      exam: ExamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default examRoute;
