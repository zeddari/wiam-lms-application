import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SessionProviderComponent } from './list/session-provider.component';
import { SessionProviderDetailComponent } from './detail/session-provider-detail.component';
import { SessionProviderUpdateComponent } from './update/session-provider-update.component';
import SessionProviderResolve from './route/session-provider-routing-resolve.service';

const sessionProviderRoute: Routes = [
  {
    path: '',
    component: SessionProviderComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SessionProviderDetailComponent,
    resolve: {
      sessionProvider: SessionProviderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SessionProviderUpdateComponent,
    resolve: {
      sessionProvider: SessionProviderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SessionProviderUpdateComponent,
    resolve: {
      sessionProvider: SessionProviderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sessionProviderRoute;
