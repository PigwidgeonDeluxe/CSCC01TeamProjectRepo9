import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import * as FileSaver from 'file-saver';
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
  following: any;

  constructor(private router: Router) { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.user = JSON.parse(localStorage.getItem('user'));
    this.results = [];
    this.following = [];

    this.getUserFiles();
    this.getUserFollowing();
  }

  getUserFollowing(){
    const url = this.TOMCAT_URL + '/follow?userId=' + this.user.userId;

    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    this.following = [];
    resp.forEach(element => {
      if (element.length > 0) {
        this.following.push({
          'userName': element.split('~')[0],
          'profileImage': element.split('~')[1],
          'fileName': element.split('~')[2]
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
          'docId': element.split('~')[6],
          'fileContent': element.split('~')[7]
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

  downloadFile(fileName: string, uploadDate: string) {
    this.http.open('GET', this.TOMCAT_URL + '/download?fileName=' + fileName + '&uploadTime=' + uploadDate, true);
    this.http.responseType = 'arraybuffer';
    this.http.send(null);

    this.http.onload = () => {
      const data = this.http.response;
      const extension = fileName.split('.').pop();
      let contentType;
      if (extension === 'pdf') {
        contentType = {type: 'application/pdf'};
      } else if (extension === 'doc' || extension === 'docx') {
        contentType = {type: 'application/msword'};
      } else if (extension === 'html') {
        contentType = {type: 'text/html'};
      } else {
        contentType = {type: 'text/plain'};
      }
      const blob = new Blob([data], contentType);
      FileSaver.saveAs(blob, fileName);
    };
  }

  viewComments(docId: string) {
    this.router.navigateByUrl('/comments?docId=' + docId);
  }

}
