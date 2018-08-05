import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import * as FileSaver from 'file-saver';

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

  fileTypeData: any;
  fileSizeData: any;

  popularFileType: any;
  largestFile: any;

  bookmarkedFiles: any;

  constructor(private router: Router) { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.user = JSON.parse(localStorage.getItem('user'));
    this.results = [];
    this.following = [];
    this.bookmarkedFiles = [];

    this.fileTypeData = {
      chartType: 'PieChart',
      dataTable: [
        ['File Type', 'Quantity']
      ],
      options: {
        width: 500
      }
    };

    this.fileSizeData = {
      chartType: 'Histogram',
      dataTable: [
        ['Name', 'Size (B)']
      ],
      options: {
        width: 500,
        legend: 'none',
        histogram: { bucketSize: 80000 },
        colors: ['green']
      }
    };

    this.getUserFiles();
    this.getStatistics();
    this.getFollowing();
    this.getBookmarks();
  }

  getStatistics() {
    const user = JSON.parse(localStorage.getItem('user'));

    const url = this.TOMCAT_URL + '/statistics?userName=' + user.userName;

    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = JSON.parse(this.http.response);

    for (const key in resp.fileType) {
      if (resp.fileType.hasOwnProperty(key)) {
        this.fileTypeData.dataTable.push([key, resp.fileType[key]]);
        if (this.popularFileType) {
          if (resp.fileType[key] > this.popularFileType.files) {
            this.popularFileType = {'fileType': key, 'files': resp.fileType[key]};
          }
        } else {
          this.popularFileType = {'fileType': key, 'files': resp.fileType[key]};
        }
      }
    }

    for (const key in resp.fileSize) {
      if (resp.fileSize.hasOwnProperty(key)) {
        this.fileSizeData.dataTable.push([key, resp.fileSize[key]]);
        if (this.largestFile) {
          if (resp.fileSize[key] > this.largestFile) {
            this.largestFile = resp.fileSize[key];
          }
        } else {
          this.largestFile = resp.fileSize[key];
        }
      }
    }

    this.largestFile = Math.round(this.largestFile / 1000) / 100;
  }

  getUserFiles() {
    const url = this.TOMCAT_URL + '/userFiles?userId=' + this.user.userId;

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
          'docId': element.split('~')[6]
        });
      }
    });
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

  getFollowing() {
    const url = this.TOMCAT_URL + '/follow?userId=' + this.user.userId;
    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    resp.forEach(element => {
      if (element.length > 0) {
        this.following.push({
          'userId': element.split('~')[0],
          'userName': element.split('~')[1],
          'userType': element.split('~')[2],
          'profileImage': element.split('~')[3],
          'createdOn': +element.split('~')[4]
        });
      }
    });
  }

  getBookmarks() {
    const url = this.TOMCAT_URL + '/bookmark?userId=' + this.user.userId;
    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    this.bookmarkedFiles = [];
    resp.forEach(element => {
      if (element.length > 0) {
        this.bookmarkedFiles.push({
          'fileName': element.split('~')[0],
          'fileType': element.split('~')[1],
          'fileSize': Math.round(+element.split('~')[2] / 1000) / 100,
          'userName': element.split('~')[3],
          'userType': element.split('~')[4],
          'uploadedOn': +element.split('~')[5],
          'docId': element.split('~')[6]
        });
      }
    });
  }

  viewComments(docId: string) {
    this.router.navigateByUrl('/comments?docId=' + docId);
  }

  viewProfile(userId: string) {
    this.router.navigateByUrl('/user?userId=' + userId);
  }

}
