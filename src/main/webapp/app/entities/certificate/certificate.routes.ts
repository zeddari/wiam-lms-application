import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CertificateComponent } from './list/certificate.component';
import { CertificateDetailComponent } from './detail/certificate-detail.component';
import { CertificateUpdateComponent } from './update/certificate-update.component';
import CertificateResolve from './route/certificate-routing-resolve.service';

const certificateRoute: Routes = [
  {
    path: '',
    component: CertificateComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CertificateDetailComponent,
    resolve: {
      certificate: CertificateResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CertificateUpdateComponent,
    resolve: {
      certificate: CertificateResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CertificateUpdateComponent,
    resolve: {
      certificate: CertificateResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default certificateRoute;
