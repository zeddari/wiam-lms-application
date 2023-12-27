import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISponsoring } from '../sponsoring.model';
import { SponsoringService } from '../service/sponsoring.service';

export const sponsoringResolve = (route: ActivatedRouteSnapshot): Observable<null | ISponsoring> => {
  const id = route.params['id'];
  if (id) {
    return inject(SponsoringService)
      .find(id)
      .pipe(
        mergeMap((sponsoring: HttpResponse<ISponsoring>) => {
          if (sponsoring.body) {
            return of(sponsoring.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default sponsoringResolve;
