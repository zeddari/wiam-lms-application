import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CoteryComponent } from './list/cotery.component';
import { CoteryDetailComponent } from './detail/cotery-detail.component';
import { CoteryUpdateComponent } from './update/cotery-update.component';
import CoteryResolve from './route/cotery-routing-resolve.service';

const coteryRoute: Routes = [
  {
    path: '',
    component: CoteryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CoteryDetailComponent,
    resolve: {
      cotery: CoteryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CoteryUpdateComponent,
    resolve: {
      cotery: CoteryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CoteryUpdateComponent,
    resolve: {
      cotery: CoteryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default coteryRoute;
