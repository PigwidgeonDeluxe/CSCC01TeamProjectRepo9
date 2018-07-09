import { Component, OnInit, TemplateRef } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';

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
  loginPassword: string;

  registerUsername: string;
  registerPassword: string;
  registerUserType: string;

  modalRef: BsModalRef;
  userInfo: object;

  constructor(private modalService: BsModalService) { }

  ngOnInit() {
    this.http = new XMLHttpRequest;
    this.TOMCAT_URL = 'http://localhost:8080';
    this.userInfo = JSON.parse(localStorage.getItem('user'));
  }

  openModal(template: TemplateRef<any>) {
    this.modalRef = this.modalService.show(template);
  }

  loginUser() {
    this.http.open('POST', this.TOMCAT_URL + '/user?login=true', false);
    this.http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    const request = {
      'userName': this.loginUsername,
      'password': this.loginPassword
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
          'userName': this.loginUsername,
          'userType': resp.userType
        }));
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

  registerUser() {
    this.http.open('POST', this.TOMCAT_URL + '/user?create=true', false);
    this.http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    const request = {
      'userName': this.registerUsername,
      'password': this.registerPassword,
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
          'userName': this.registerUsername,
          'userType': this.registerUserType === '1' ? 'student' : 'instructor'
        }));
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

  logout() {
    localStorage.removeItem('user');
    swal({
      title: 'Logged Out',
      type: 'success',
      text: 'Successfully logged out!'
    }).then(() => {
      location.reload();
    });

  }

}
