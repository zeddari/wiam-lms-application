import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CityComponent } from './list/city.component';
import { CityDetailComponent } from './detail/city-detail.component';
import { CityUpdateComponent } from './update/city-update.component';
import CityResolve from './route/city-routing-resolve.service';

const cityRoute: Routes = [
  {
    path: '',
    component: CityComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CityDetailComponent,
    resolve: {
      city: CityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CityUpdateComponent,
    resolve: {
      city: CityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CityUpdateComponent,
    resolve: {
      city: CityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cityRoute;
