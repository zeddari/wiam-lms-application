import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ProgressionComponent } from './list/progression.component';
import { ProgressionDetailComponent } from './detail/progression-detail.component';
import { ProgressionUpdateComponent } from './update/progression-update.component';
import ProgressionResolve from './route/progression-routing-resolve.service';

const progressionRoute: Routes = [
  {
    path: '',
    component: ProgressionComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgressionDetailComponent,
    resolve: {
      progression: ProgressionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgressionUpdateComponent,
    resolve: {
      progression: ProgressionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgressionUpdateComponent,
    resolve: {
      progression: ProgressionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default progressionRoute;
