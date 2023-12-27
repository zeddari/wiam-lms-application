import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SessionJoinModeComponent } from './list/session-join-mode.component';
import { SessionJoinModeDetailComponent } from './detail/session-join-mode-detail.component';
import { SessionJoinModeUpdateComponent } from './update/session-join-mode-update.component';
import SessionJoinModeResolve from './route/session-join-mode-routing-resolve.service';

const sessionJoinModeRoute: Routes = [
  {
    path: '',
    component: SessionJoinModeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SessionJoinModeDetailComponent,
    resolve: {
      sessionJoinMode: SessionJoinModeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SessionJoinModeUpdateComponent,
    resolve: {
      sessionJoinMode: SessionJoinModeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SessionJoinModeUpdateComponent,
    resolve: {
      sessionJoinMode: SessionJoinModeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sessionJoinModeRoute;
