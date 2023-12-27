import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ClassroomComponent } from './list/classroom.component';
import { ClassroomDetailComponent } from './detail/classroom-detail.component';
import { ClassroomUpdateComponent } from './update/classroom-update.component';
import ClassroomResolve from './route/classroom-routing-resolve.service';

const classroomRoute: Routes = [
  {
    path: '',
    component: ClassroomComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ClassroomDetailComponent,
    resolve: {
      classroom: ClassroomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ClassroomUpdateComponent,
    resolve: {
      classroom: ClassroomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ClassroomUpdateComponent,
    resolve: {
      classroom: ClassroomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default classroomRoute;
