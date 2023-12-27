import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SiteComponent } from './list/site.component';
import { SiteDetailComponent } from './detail/site-detail.component';
import { SiteUpdateComponent } from './update/site-update.component';
import SiteResolve from './route/site-routing-resolve.service';

const siteRoute: Routes = [
  {
    path: '',
    component: SiteComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SiteDetailComponent,
    resolve: {
      site: SiteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SiteUpdateComponent,
    resolve: {
      site: SiteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SiteUpdateComponent,
    resolve: {
      site: SiteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default siteRoute;
