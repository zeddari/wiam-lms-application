import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TopicComponent } from './list/topic.component';
import { TopicDetailComponent } from './detail/topic-detail.component';
import { TopicUpdateComponent } from './update/topic-update.component';
import TopicResolve from './route/topic-routing-resolve.service';

const topicRoute: Routes = [
  {
    path: '',
    component: TopicComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TopicDetailComponent,
    resolve: {
      topic: TopicResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TopicUpdateComponent,
    resolve: {
      topic: TopicResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TopicUpdateComponent,
    resolve: {
      topic: TopicResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default topicRoute;
