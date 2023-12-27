import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SessionLinkComponent } from './list/session-link.component';
import { SessionLinkDetailComponent } from './detail/session-link-detail.component';
import { SessionLinkUpdateComponent } from './update/session-link-update.component';
import SessionLinkResolve from './route/session-link-routing-resolve.service';

const sessionLinkRoute: Routes = [
  {
    path: '',
    component: SessionLinkComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SessionLinkDetailComponent,
    resolve: {
      sessionLink: SessionLinkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SessionLinkUpdateComponent,
    resolve: {
      sessionLink: SessionLinkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SessionLinkUpdateComponent,
    resolve: {
      sessionLink: SessionLinkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sessionLinkRoute;
