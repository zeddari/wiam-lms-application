import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { EnrolementComponent } from './list/enrolement.component';
import { EnrolementDetailComponent } from './detail/enrolement-detail.component';
import { EnrolementUpdateComponent } from './update/enrolement-update.component';
import EnrolementResolve from './route/enrolement-routing-resolve.service';

const enrolementRoute: Routes = [
  {
    path: '',
    component: EnrolementComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnrolementDetailComponent,
    resolve: {
      enrolement: EnrolementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnrolementUpdateComponent,
    resolve: {
      enrolement: EnrolementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EnrolementUpdateComponent,
    resolve: {
      enrolement: EnrolementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default enrolementRoute;
