import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICountry2 } from '../country-2.model';
import { Country2Service } from '../service/country-2.service';

export const country2Resolve = (route: ActivatedRouteSnapshot): Observable<null | ICountry2> => {
  const id = route.params['id'];
  if (id) {
    return inject(Country2Service)
      .find(id)
      .pipe(
        mergeMap((country2: HttpResponse<ICountry2>) => {
          if (country2.body) {
            return of(country2.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default country2Resolve;
