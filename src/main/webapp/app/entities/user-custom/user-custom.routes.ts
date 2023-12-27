import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { UserCustomComponent } from './list/user-custom.component';
import { UserCustomDetailComponent } from './detail/user-custom-detail.component';
import { UserCustomUpdateComponent } from './update/user-custom-update.component';
import UserCustomResolve from './route/user-custom-routing-resolve.service';

const userCustomRoute: Routes = [
  {
    path: '',
    component: UserCustomComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserCustomDetailComponent,
    resolve: {
      userCustom: UserCustomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserCustomUpdateComponent,
    resolve: {
      userCustom: UserCustomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserCustomUpdateComponent,
    resolve: {
      userCustom: UserCustomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default userCustomRoute;
