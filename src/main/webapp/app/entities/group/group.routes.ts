import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { GroupComponent } from './list/group.component';
import { GroupDetailComponent } from './detail/group-detail.component';
import { GroupUpdateComponent } from './update/group-update.component';
import GroupResolve from './route/group-routing-resolve.service';

const groupRoute: Routes = [
  {
    path: '',
    component: GroupComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GroupDetailComponent,
    resolve: {
      group: GroupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GroupUpdateComponent,
    resolve: {
      group: GroupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GroupUpdateComponent,
    resolve: {
      group: GroupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default groupRoute;
