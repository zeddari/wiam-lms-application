import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TicketSubjectsComponent } from './list/ticket-subjects.component';
import { TicketSubjectsDetailComponent } from './detail/ticket-subjects-detail.component';
import { TicketSubjectsUpdateComponent } from './update/ticket-subjects-update.component';
import TicketSubjectsResolve from './route/ticket-subjects-routing-resolve.service';

const ticketSubjectsRoute: Routes = [
  {
    path: '',
    component: TicketSubjectsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TicketSubjectsDetailComponent,
    resolve: {
      ticketSubjects: TicketSubjectsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TicketSubjectsUpdateComponent,
    resolve: {
      ticketSubjects: TicketSubjectsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TicketSubjectsUpdateComponent,
    resolve: {
      ticketSubjects: TicketSubjectsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ticketSubjectsRoute;
