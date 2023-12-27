import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { Question2Component } from './list/question-2.component';
import { Question2DetailComponent } from './detail/question-2-detail.component';
import { Question2UpdateComponent } from './update/question-2-update.component';
import Question2Resolve from './route/question-2-routing-resolve.service';

const question2Route: Routes = [
  {
    path: '',
    component: Question2Component,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: Question2DetailComponent,
    resolve: {
      question2: Question2Resolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: Question2UpdateComponent,
    resolve: {
      question2: Question2Resolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: Question2UpdateComponent,
    resolve: {
      question2: Question2Resolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default question2Route;
