import { Component, OnInit } from '@angular/core';

import swal from 'sweetalert2';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  TOMCAT_URL: string;

  user: any;
  http: XMLHttpRequest;
  results: any;

  constructor() { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.user = JSON.parse(localStorage.getItem('user'));
    this.results = [];

    this.getUserFiles();
  }

  getUserFollow(){
    const url = this.TOMCAT_URL + '/follow?userId=' + this.user.userId;

    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    this.results = [];
    resp.forEach(element => {
      if (element.length > 0) {
        this.results.push({
          'fileName': element.split('~')[0],
          'fileType': element.split('~')[1],
          'userType': element.split('~')[2],
          'userName': element.split('~')[3],
          'fileSize': Math.round(+element.split('~')[4] / 1000) / 100,
          'uploadDate': +element.split('~')[5],
          'fileContent': element.split('~')[6]
        });
      }
    });
  }

  getUserFiles() {
    const url = this.TOMCAT_URL + '/search?userName=' + this.user.userName;

    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('"\n');
    this.results = [];
    resp.forEach(element => {
      if (element.length > 0) {
        this.results.push({
          'fileName': element.split('~')[0],
          'fileType': element.split('~')[1],
          'userType': element.split('~')[2],
          'userName': element.split('~')[3],
          'fileSize': Math.round(+element.split('~')[4] / 1000) / 100,
          'uploadDate': +element.split('~')[5],
          'fileContent': element.split('~')[6]
        });
      }
    });

    if (this.results.length === 0) {
      swal({
        title: 'No Results',
        type: 'warning',
        text: 'No results found'
      });
    }
  }

}
