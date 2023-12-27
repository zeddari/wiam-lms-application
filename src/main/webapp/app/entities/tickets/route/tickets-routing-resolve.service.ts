import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITickets } from '../tickets.model';
import { TicketsService } from '../service/tickets.service';

export const ticketsResolve = (route: ActivatedRouteSnapshot): Observable<null | ITickets> => {
  const id = route.params['id'];
  if (id) {
    return inject(TicketsService)
      .find(id)
      .pipe(
        mergeMap((tickets: HttpResponse<ITickets>) => {
          if (tickets.body) {
            return of(tickets.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default ticketsResolve;
