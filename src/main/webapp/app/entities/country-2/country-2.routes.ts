import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { Country2Component } from './list/country-2.component';
import { Country2DetailComponent } from './detail/country-2-detail.component';
import { Country2UpdateComponent } from './update/country-2-update.component';
import Country2Resolve from './route/country-2-routing-resolve.service';

const country2Route: Routes = [
  {
    path: '',
    component: Country2Component,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: Country2DetailComponent,
    resolve: {
      country2: Country2Resolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: Country2UpdateComponent,
    resolve: {
      country2: Country2Resolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: Country2UpdateComponent,
    resolve: {
      country2: Country2Resolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default country2Route;
