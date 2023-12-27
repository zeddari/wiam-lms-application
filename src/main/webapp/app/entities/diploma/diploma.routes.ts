import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { DiplomaComponent } from './list/diploma.component';
import { DiplomaDetailComponent } from './detail/diploma-detail.component';
import { DiplomaUpdateComponent } from './update/diploma-update.component';
import DiplomaResolve from './route/diploma-routing-resolve.service';

const diplomaRoute: Routes = [
  {
    path: '',
    component: DiplomaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DiplomaDetailComponent,
    resolve: {
      diploma: DiplomaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DiplomaUpdateComponent,
    resolve: {
      diploma: DiplomaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DiplomaUpdateComponent,
    resolve: {
      diploma: DiplomaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default diplomaRoute;
