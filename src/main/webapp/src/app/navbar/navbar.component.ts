import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';

declare var gapi: any;
import swal from 'sweetalert2';

/**
 * Component for handling the navbar
 */
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  // get current tab from parent controller
  @Input() navSelection: string;

  TOMCAT_URL: string;
  http: XMLHttpRequest;

  loginUsername: string;
  userInfo: object;

  /**
   * Binds the Google SSO button and initializes the router
   * @param router router to be used to route between pages
   */
  constructor(private router: Router) {
    window['onSignIn'] = ((user) => {
      this.onSignIn(user);
    });
  }

  /**
   * Initialize resources on startup
   */
  ngOnInit() {
    this.http = new XMLHttpRequest;
    this.TOMCAT_URL = 'http://localhost:8080';
    this.userInfo = JSON.parse(localStorage.getItem('user'));
  }

  /**
   * Handle Google SSO
   * @param googleUser Google User from Google APIs
   */
  onSignIn(googleUser) {
    const profile = googleUser.getBasicProfile();
    const idToken = googleUser.getAuthResponse().id_token;

    this.http.open('POST', this.TOMCAT_URL + '/user?login=true', false);
    this.http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // package request
    const request = {'token': idToken};
    this.http.send(JSON.stringify(request));

    const resp = JSON.parse(this.http.response);
    // if the user successfully signed in, send visual success message
    if (resp.status === 'SUCCESS') {
      swal({
        title: 'Success',
        type: 'success',
        text: resp.message
      }).then(() => {
        // store user information in local storage
        localStorage.setItem('user', JSON.stringify({
          'userId': profile.getId(),
          'userName': profile.getName(),
          'userType': resp.userType,
          'createdOn': resp.createdOn,
          'profileImage': profile.getImageUrl()
        }));
        // go back to home and refresh
        this.router.navigateByUrl('/');
        location.reload();
      });
    } else {
      // if the user sign in was a failure, send visual failure message
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
   * Initates a user sign out request
   * @param userInitiated boolean representing whether it is user or system initiated
   */
  signOut(userInitiated) {
    if (userInitiated) {
      gapi.load('auth2', () => {
        gapi.auth2.init();
      });
      // send visual indicator if signout was user initiated
      swal({
        title: 'Success',
        type: 'success',
        text: 'Successfully logged out'
      }).then(() => {
        // redirect page to home and reload
        const auth2 = gapi.auth2.getAuthInstance();
        localStorage.clear();
        auth2.signOut();
        this.router.navigateByUrl('/');
        location.reload();
      });
    } else {
      // just perform signout if the signout was system initiated
      const auth2 = gapi.auth2.getAuthInstance();
      localStorage.clear();
      auth2.signOut();
    }
  }

  /**
   * Upload one or more files to the system
   * @param files FileList of files to be uploaded
   */
  upload(files: FileList) {
    const user = JSON.parse(localStorage.getItem('user'));
    const re = /(?:\.([^.]+))?$/;
    let validFiles = true;
    this.http.open('POST', this.TOMCAT_URL + '/upload?userId=' + user.userId, false);

    // package request
    const request = new FormData();
    Array.from(files).forEach(file => {
      if (re.exec(file.name)[1] !== 'txt' &&
        re.exec(file.name)[1] !== 'docx' && re.exec(file.name)[1] !== 'pdf' && re.exec(file.name)[1] !== 'html') {
        validFiles = false;
      }
      request.append(file.name, file);
    });

    if (!validFiles) {
      swal({
        title: 'Warning',
        type: 'warning',
        html: 'Invalid files detected. Please only upload <b>TXT</b>, <b>HTML</b>, <b>DOCX</b>, or <b>PDF</b> files'
      });
    } else {
      this.http.send(request);
      const resp = JSON.parse(this.http.response);

      // send proper messages on success or error
      if (resp.status === 'SUCCESS') {
        swal({
          title: 'Success',
          type: 'success',
          text: resp.message
        }).then(() => {
          location.reload();
        });
      } else {
        swal({
          title: 'Failure',
          type: 'error',
          text: resp.message
        });
      }
    }

  }

}
