import { Component, OnInit } from '@angular/core';
import { Router } from '../../../node_modules/@angular/router';

declare var gapi: any;
import swal from 'sweetalert2';

/**
 * Component for handling user registration
 */
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

  /**
   * Initialize router and bind Google SSO button
   * @param router router to route between pages
   */
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

  /**
   * Handle creation of new user
   * @param googleUser Google User from Google APIs
   */
  onRegister(googleUser) {
    const profile = googleUser.getBasicProfile();
    const idToken = googleUser.getAuthResponse().id_token;

    this.http.open('POST', this.TOMCAT_URL + '/user?create=true', false);
    this.http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // package request
    const request = {
      'token': idToken,
      'userType': this.registerUserType === '1' ? 'student' : 'instructor',
      'userName': profile.getName(),
      'profileImage': profile.getImageUrl()
    };
    this.http.send(JSON.stringify(request));

    const resp = JSON.parse(this.http.response);
    // on successful user creation, send message to user
    if (resp.status === 'SUCCESS') {
      swal({
        title: 'Success',
        type: 'success',
        text: resp.message
      }).then(() => {
        // store newly created user information in local storage
        localStorage.setItem('user', JSON.stringify({
          'userId': profile.getId(),
          'userName': profile.getName(),
          'userType': this.registerUserType === '1' ? 'student' : 'instructor',
          'createdOn': resp.createdOn,
          'profileImage': profile.getImageUrl()
        }));
        // reroute to home page and reload
        this.router.navigateByUrl('/');
        location.reload();
      });
    // on failure, send an error message to the user
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

  /**
   * Handle sign out request
   * @param userInitiated boolean representing if sign out was initiated by the system or the user
   */
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
        // if the sign out was user initiated, reroute to the home page
        const auth2 = gapi.auth2.getAuthInstance();
        localStorage.clear();
        auth2.signOut();
        this.router.navigateByUrl('/');
        location.reload();
      });
    } else {
      // if the sign out was system initiated, just signout and do not reroute
      const auth2 = gapi.auth2.getAuthInstance();
      localStorage.clear();
      auth2.signOut();
    }
  }

}
