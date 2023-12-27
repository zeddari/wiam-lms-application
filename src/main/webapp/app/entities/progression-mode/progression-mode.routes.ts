import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ProgressionModeComponent } from './list/progression-mode.component';
import { ProgressionModeDetailComponent } from './detail/progression-mode-detail.component';
import { ProgressionModeUpdateComponent } from './update/progression-mode-update.component';
import ProgressionModeResolve from './route/progression-mode-routing-resolve.service';

const progressionModeRoute: Routes = [
  {
    path: '',
    component: ProgressionModeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgressionModeDetailComponent,
    resolve: {
      progressionMode: ProgressionModeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgressionModeUpdateComponent,
    resolve: {
      progressionMode: ProgressionModeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgressionModeUpdateComponent,
    resolve: {
      progressionMode: ProgressionModeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default progressionModeRoute;
