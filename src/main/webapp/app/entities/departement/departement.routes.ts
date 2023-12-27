import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { DepartementComponent } from './list/departement.component';
import { DepartementDetailComponent } from './detail/departement-detail.component';
import { DepartementUpdateComponent } from './update/departement-update.component';
import DepartementResolve from './route/departement-routing-resolve.service';

const departementRoute: Routes = [
  {
    path: '',
    component: DepartementComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DepartementDetailComponent,
    resolve: {
      departement: DepartementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DepartementUpdateComponent,
    resolve: {
      departement: DepartementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DepartementUpdateComponent,
    resolve: {
      departement: DepartementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default departementRoute;
