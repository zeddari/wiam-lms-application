import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SessionModeComponent } from './list/session-mode.component';
import { SessionModeDetailComponent } from './detail/session-mode-detail.component';
import { SessionModeUpdateComponent } from './update/session-mode-update.component';
import SessionModeResolve from './route/session-mode-routing-resolve.service';

const sessionModeRoute: Routes = [
  {
    path: '',
    component: SessionModeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SessionModeDetailComponent,
    resolve: {
      sessionMode: SessionModeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SessionModeUpdateComponent,
    resolve: {
      sessionMode: SessionModeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SessionModeUpdateComponent,
    resolve: {
      sessionMode: SessionModeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sessionModeRoute;
