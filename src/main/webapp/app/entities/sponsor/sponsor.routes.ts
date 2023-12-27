import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SponsorComponent } from './list/sponsor.component';
import { SponsorDetailComponent } from './detail/sponsor-detail.component';
import { SponsorUpdateComponent } from './update/sponsor-update.component';
import SponsorResolve from './route/sponsor-routing-resolve.service';

const sponsorRoute: Routes = [
  {
    path: '',
    component: SponsorComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SponsorDetailComponent,
    resolve: {
      sponsor: SponsorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SponsorUpdateComponent,
    resolve: {
      sponsor: SponsorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SponsorUpdateComponent,
    resolve: {
      sponsor: SponsorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sponsorRoute;
