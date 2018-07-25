import { Component, OnInit } from '@angular/core';
import { Router } from '../../../node_modules/@angular/router';

declare var gapi: any;
import swal from 'sweetalert2';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  TOMCAT_URL: string;
  http: XMLHttpRequest;

  registerUsername: string;
  registerUserType: string;

  constructor(private router: Router) {
    window['onRegister'] = ((user) => {
      this.onRegister(user);
    });
  }

  ngOnInit() {
    this.http = new XMLHttpRequest;
    this.TOMCAT_URL = 'http://localhost:8080';
    this.registerUserType = '1';
  }

  onRegister(googleUser) {
    const profile = googleUser.getBasicProfile();
    const idToken = googleUser.getAuthResponse().id_token;

    this.http.open('POST', this.TOMCAT_URL + '/user?create=true', false);
    this.http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // package request
    const request = {
      'token': idToken,
      'userType': this.registerUserType === '1' ? 'student' : 'instructor'
    };
    this.http.send(JSON.stringify(request));

    const resp = JSON.parse(this.http.response);
    if (resp.status === 'SUCCESS') {
      swal({
        title: 'Success',
        type: 'success',
        text: resp.message
      }).then(() => {
        localStorage.setItem('user', JSON.stringify({
          'userName': profile.getName(),
          'userType': this.registerUserType === '1' ? 'student' : 'instructor',
          'createdOn': resp.createdOn,
          'profileImage': profile.getImageUrl()
        }));
        this.router.navigateByUrl('/');
        location.reload();
      });
    } else {
      swal({
        title: 'Failure',
        type: 'error',
        text: resp.message
      }).then(() => {
        this.signOut(false);
      });
    }
  }

  signOut(userInitiated) {
    if (userInitiated) {
      gapi.load('auth2', () => {
        gapi.auth2.init();
      });
      swal({
        title: 'Success',
        type: 'success',
        text: 'Successfully logged out'
      }).then(() => {
        const auth2 = gapi.auth2.getAuthInstance();
        localStorage.clear();
        auth2.signOut();
        this.router.navigateByUrl('/');
        location.reload();
      });
    } else {
      const auth2 = gapi.auth2.getAuthInstance();
      localStorage.clear();
      auth2.signOut();
    }
  }

}
