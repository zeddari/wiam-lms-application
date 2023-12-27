import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { LanguageComponent } from './list/language.component';
import { LanguageDetailComponent } from './detail/language-detail.component';
import { LanguageUpdateComponent } from './update/language-update.component';
import LanguageResolve from './route/language-routing-resolve.service';

const languageRoute: Routes = [
  {
    path: '',
    component: LanguageComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LanguageDetailComponent,
    resolve: {
      language: LanguageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LanguageUpdateComponent,
    resolve: {
      language: LanguageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LanguageUpdateComponent,
    resolve: {
      language: LanguageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default languageRoute;
