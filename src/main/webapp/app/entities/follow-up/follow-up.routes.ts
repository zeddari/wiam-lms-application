import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { FollowUpComponent } from './list/follow-up.component';
import { FollowUpDetailComponent } from './detail/follow-up-detail.component';
import { FollowUpUpdateComponent } from './update/follow-up-update.component';
import FollowUpResolve from './route/follow-up-routing-resolve.service';

const followUpRoute: Routes = [
  {
    path: '',
    component: FollowUpComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FollowUpDetailComponent,
    resolve: {
      followUp: FollowUpResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FollowUpUpdateComponent,
    resolve: {
      followUp: FollowUpResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FollowUpUpdateComponent,
    resolve: {
      followUp: FollowUpResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default followUpRoute;
