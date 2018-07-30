import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';
import { Router } from '@angular/router';

declare var gapi: any;
import swal from 'sweetalert2';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  TOMCAT_URL: string;
  http: XMLHttpRequest;

  loginUsername: string;
  modalRef: BsModalRef;
  userInfo: object;

  constructor(private router: Router) {
    window['onSignIn'] = ((user) => {
      this.onSignIn(user);
    });
  }

  ngOnInit() {
    this.http = new XMLHttpRequest;
    this.TOMCAT_URL = 'http://localhost:8080';
    this.userInfo = JSON.parse(localStorage.getItem('user'));
  }

  onSignIn(googleUser) {
    const profile = googleUser.getBasicProfile();
    const idToken = googleUser.getAuthResponse().id_token;

    this.http.open('POST', this.TOMCAT_URL + '/user?login=true', false);
    this.http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // package request
    const request = {'token': idToken};
    this.http.send(JSON.stringify(request));

    const resp = JSON.parse(this.http.response);
    if (resp.status === 'SUCCESS') {
      swal({
        title: 'Success',
        type: 'success',
        text: resp.message
      }).then(() => {
        localStorage.setItem('user', JSON.stringify({
          'userId': profile.getId(),
          'userName': profile.getName(),
          'userType': resp.userType,
          'createdOn': resp.createdOn,
          'profileImage': profile.getImageUrl()
        }));
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

  upload(files: FileList) {
    const user = JSON.parse(localStorage.getItem('user'));
    this.http.open('POST', this.TOMCAT_URL + '/upload?userId=' + user.userId, false);

    const request = new FormData();
    Array.from(files).forEach(file => {
      request.append(file.name, file);
    });

    this.http.send(request);
    const resp = JSON.parse(this.http.response);

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
