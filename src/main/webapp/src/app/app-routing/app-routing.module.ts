import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from '../home/home.component';
import { RegisterComponent } from '../register/register.component';
import { ProfileComponent } from '../profile/profile.component';
import { StatisticsComponent } from '../statistics/statistics.component';
import { CommentsComponent } from '../comments/comments.component';

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
    path: 'comments',
    component: CommentsComponent
  },
  {
    path: 'statistics',
    component: StatisticsComponent
  },
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
