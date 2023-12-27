import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITicketSubjects } from '../ticket-subjects.model';
import { TicketSubjectsService } from '../service/ticket-subjects.service';

export const ticketSubjectsResolve = (route: ActivatedRouteSnapshot): Observable<null | ITicketSubjects> => {
  const id = route.params['id'];
  if (id) {
    return inject(TicketSubjectsService)
      .find(id)
      .pipe(
        mergeMap((ticketSubjects: HttpResponse<ITicketSubjects>) => {
          if (ticketSubjects.body) {
            return of(ticketSubjects.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default ticketSubjectsResolve;
