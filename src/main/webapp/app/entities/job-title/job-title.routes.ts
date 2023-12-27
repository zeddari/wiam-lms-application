import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { JobTitleComponent } from './list/job-title.component';
import { JobTitleDetailComponent } from './detail/job-title-detail.component';
import { JobTitleUpdateComponent } from './update/job-title-update.component';
import JobTitleResolve from './route/job-title-routing-resolve.service';

const jobTitleRoute: Routes = [
  {
    path: '',
    component: JobTitleComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: JobTitleDetailComponent,
    resolve: {
      jobTitle: JobTitleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: JobTitleUpdateComponent,
    resolve: {
      jobTitle: JobTitleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: JobTitleUpdateComponent,
    resolve: {
      jobTitle: JobTitleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default jobTitleRoute;
