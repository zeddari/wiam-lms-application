import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { DiplomaTypeComponent } from './list/diploma-type.component';
import { DiplomaTypeDetailComponent } from './detail/diploma-type-detail.component';
import { DiplomaTypeUpdateComponent } from './update/diploma-type-update.component';
import DiplomaTypeResolve from './route/diploma-type-routing-resolve.service';

const diplomaTypeRoute: Routes = [
  {
    path: '',
    component: DiplomaTypeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DiplomaTypeDetailComponent,
    resolve: {
      diplomaType: DiplomaTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DiplomaTypeUpdateComponent,
    resolve: {
      diplomaType: DiplomaTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DiplomaTypeUpdateComponent,
    resolve: {
      diplomaType: DiplomaTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default diplomaTypeRoute;
