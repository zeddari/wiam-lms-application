import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CoteryHistoryComponent } from './list/cotery-history.component';
import { CoteryHistoryDetailComponent } from './detail/cotery-history-detail.component';
import { CoteryHistoryUpdateComponent } from './update/cotery-history-update.component';
import CoteryHistoryResolve from './route/cotery-history-routing-resolve.service';

const coteryHistoryRoute: Routes = [
  {
    path: '',
    component: CoteryHistoryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CoteryHistoryDetailComponent,
    resolve: {
      coteryHistory: CoteryHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CoteryHistoryUpdateComponent,
    resolve: {
      coteryHistory: CoteryHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CoteryHistoryUpdateComponent,
    resolve: {
      coteryHistory: CoteryHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default coteryHistoryRoute;
