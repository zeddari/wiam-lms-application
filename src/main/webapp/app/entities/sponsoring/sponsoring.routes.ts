import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SponsoringComponent } from './list/sponsoring.component';
import { SponsoringDetailComponent } from './detail/sponsoring-detail.component';
import { SponsoringUpdateComponent } from './update/sponsoring-update.component';
import SponsoringResolve from './route/sponsoring-routing-resolve.service';

const sponsoringRoute: Routes = [
  {
    path: '',
    component: SponsoringComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SponsoringDetailComponent,
    resolve: {
      sponsoring: SponsoringResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SponsoringUpdateComponent,
    resolve: {
      sponsoring: SponsoringResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SponsoringUpdateComponent,
    resolve: {
      sponsoring: SponsoringResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sponsoringRoute;
