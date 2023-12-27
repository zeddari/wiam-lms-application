import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SessionTypeComponent } from './list/session-type.component';
import { SessionTypeDetailComponent } from './detail/session-type-detail.component';
import { SessionTypeUpdateComponent } from './update/session-type-update.component';
import SessionTypeResolve from './route/session-type-routing-resolve.service';

const sessionTypeRoute: Routes = [
  {
    path: '',
    component: SessionTypeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SessionTypeDetailComponent,
    resolve: {
      sessionType: SessionTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SessionTypeUpdateComponent,
    resolve: {
      sessionType: SessionTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SessionTypeUpdateComponent,
    resolve: {
      sessionType: SessionTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sessionTypeRoute;
