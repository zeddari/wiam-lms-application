import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IJobTitle } from '../job-title.model';
import { JobTitleService } from '../service/job-title.service';

export const jobTitleResolve = (route: ActivatedRouteSnapshot): Observable<null | IJobTitle> => {
  const id = route.params['id'];
  if (id) {
    return inject(JobTitleService)
      .find(id)
      .pipe(
        mergeMap((jobTitle: HttpResponse<IJobTitle>) => {
          if (jobTitle.body) {
            return of(jobTitle.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default jobTitleResolve;
