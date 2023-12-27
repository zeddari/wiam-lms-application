import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgression } from '../progression.model';
import { ProgressionService } from '../service/progression.service';

export const progressionResolve = (route: ActivatedRouteSnapshot): Observable<null | IProgression> => {
  const id = route.params['id'];
  if (id) {
    return inject(ProgressionService)
      .find(id)
      .pipe(
        mergeMap((progression: HttpResponse<IProgression>) => {
          if (progression.body) {
            return of(progression.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default progressionResolve;
