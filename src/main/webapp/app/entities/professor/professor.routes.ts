import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ProfessorComponent } from './list/professor.component';
import { ProfessorDetailComponent } from './detail/professor-detail.component';
import { ProfessorUpdateComponent } from './update/professor-update.component';
import ProfessorResolve from './route/professor-routing-resolve.service';

const professorRoute: Routes = [
  {
    path: '',
    component: ProfessorComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProfessorDetailComponent,
    resolve: {
      professor: ProfessorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProfessorUpdateComponent,
    resolve: {
      professor: ProfessorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProfessorUpdateComponent,
    resolve: {
      professor: ProfessorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default professorRoute;
