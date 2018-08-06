import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from '../home/home.component';
import { RegisterComponent } from '../register/register.component';
import { ProfileComponent } from '../profile/profile.component';
import { StatisticsComponent } from '../statistics/statistics.component';
import { CommentsComponent } from '../comments/comments.component';
import { UsersComponent } from '../users/users.component';
import { UserComponent } from '../user/user.component';

/**
 * Module for handling route changing
 */

// collection of valid routes
const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'profile',
    component: ProfileComponent
  },
  {
    path: 'users',
    component: UsersComponent
  },
  {
    path: 'user',
    component: UserComponent
  },
  {
    path: 'statistics',
    component: StatisticsComponent
  },
  {
    path: 'comments',
    component: CommentsComponent
  },
  // route to home given any other route
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  },

];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ],
  declarations: []
})
export class AppRoutingModule { }
