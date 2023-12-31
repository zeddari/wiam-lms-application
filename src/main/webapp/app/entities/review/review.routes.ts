import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ReviewComponent } from './list/review.component';
import { ReviewDetailComponent } from './detail/review-detail.component';
import { ReviewUpdateComponent } from './update/review-update.component';
import ReviewResolve from './route/review-routing-resolve.service';

const reviewRoute: Routes = [
  {
    path: '',
    component: ReviewComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReviewDetailComponent,
    resolve: {
      review: ReviewResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReviewUpdateComponent,
    resolve: {
      review: ReviewResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReviewUpdateComponent,
    resolve: {
      review: ReviewResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default reviewRoute;
