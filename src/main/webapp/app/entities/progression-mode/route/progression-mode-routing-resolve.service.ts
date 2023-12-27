import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgressionMode } from '../progression-mode.model';
import { ProgressionModeService } from '../service/progression-mode.service';

export const progressionModeResolve = (route: ActivatedRouteSnapshot): Observable<null | IProgressionMode> => {
  const id = route.params['id'];
  if (id) {
    return inject(ProgressionModeService)
      .find(id)
      .pipe(
        mergeMap((progressionMode: HttpResponse<IProgressionMode>) => {
          if (progressionMode.body) {
            return of(progressionMode.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default progressionModeResolve;
