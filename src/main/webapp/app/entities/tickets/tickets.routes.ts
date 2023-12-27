import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TicketsComponent } from './list/tickets.component';
import { TicketsDetailComponent } from './detail/tickets-detail.component';
import { TicketsUpdateComponent } from './update/tickets-update.component';
import TicketsResolve from './route/tickets-routing-resolve.service';

const ticketsRoute: Routes = [
  {
    path: '',
    component: TicketsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TicketsDetailComponent,
    resolve: {
      tickets: TicketsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TicketsUpdateComponent,
    resolve: {
      tickets: TicketsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TicketsUpdateComponent,
    resolve: {
      tickets: TicketsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ticketsRoute;
