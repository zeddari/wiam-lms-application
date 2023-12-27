import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISponsor } from '../sponsor.model';
import { SponsorService } from '../service/sponsor.service';

export const sponsorResolve = (route: ActivatedRouteSnapshot): Observable<null | ISponsor> => {
  const id = route.params['id'];
  if (id) {
    return inject(SponsorService)
      .find(id)
      .pipe(
        mergeMap((sponsor: HttpResponse<ISponsor>) => {
          if (sponsor.body) {
            return of(sponsor.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default sponsorResolve;
