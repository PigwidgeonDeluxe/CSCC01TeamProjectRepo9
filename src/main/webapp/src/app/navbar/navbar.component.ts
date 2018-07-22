import { Component, OnInit, TemplateRef } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';

declare var gapi: any;
import swal from 'sweetalert2';
import $ from 'jquery';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  TOMCAT_URL: string;
  http: XMLHttpRequest;

  loginUsername: string;
  loginPassword: string;

  registerUsername: string;
  registerPassword: string;
  registerUserType: string;

  modalRef: BsModalRef;
  userInfo: object;
  registerUser: boolean;

  constructor(private modalService: BsModalService) {
    window['onSignIn'] = ((user) => {
      this.onSignIn(user);
    });

    window['onRegister'] = ((user) => {
      this.onRegister(user);
    });
  }

  ngOnInit() {
    this.http = new XMLHttpRequest;
    this.TOMCAT_URL = 'http://localhost:8080';
    this.userInfo = JSON.parse(localStorage.getItem('user'));
    this.registerUser = true;
  }

  openModal(template: TemplateRef<any>) {
    this.modalRef = this.modalService.show(template);
  }

  toggleRegister() {
    this.registerUser = !this.registerUser;
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
          'userName': profile.getName(),
          'userType': resp.userType
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
          'userType': this.registerUserType === '1' ? 'student' : 'instructor'
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
    this.http.open('POST', this.TOMCAT_URL + '/upload?userName=' + user.userName + '&userType=' + user.userType, false);

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
