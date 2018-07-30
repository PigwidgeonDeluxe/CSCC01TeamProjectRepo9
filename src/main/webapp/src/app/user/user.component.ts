import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';

import swal from 'sweetalert2';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  TOMCAT_URL: string;
  http: XMLHttpRequest;

  user: any;
  userId: string;
  userInfo: any;
  results: any;
  following: any;
  followingUser: boolean;

  fileTypeData: any;
  fileSizeData: any;

  popularFileType: any;
  largestFile: any;

  constructor(private router: Router, private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.TOMCAT_URL = 'http://localhost:8080';
    this.http = new XMLHttpRequest();
    this.user = JSON.parse(localStorage.getItem('user'));
    this.results = [];
    this.following = [];
    this.followingUser = false;
    this.activatedRoute.queryParams.subscribe((params: Params) => {
      this.userId = params['userId'];
    });

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

    this.getUserInfo();
    this.getUserFiles();
    this.getStatistics();
    this.getFollowing();
    this.isFollowing();
  }

  getStatistics() {
    const url = this.TOMCAT_URL + '/statistics?userName=' + this.userInfo.userName;

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

  getUserInfo() {
    const url = this.TOMCAT_URL + '/user?userId=' + this.userId;
    this.http.open('GET', url, false);
    this.http.send(null);
    this.userInfo = JSON.parse(this.http.response);
  }

  getUserFiles() {
    const url = this.TOMCAT_URL + '/search?userName=' + this.userInfo.userName;
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
          'userId': element.split('~')[4],
          'fileSize': Math.round(+element.split('~')[5] / 1000) / 100,
          'uploadDate': +element.split('~')[6],
          'docId': element.split('~')[7],
          'fileContent': element.split('~')[8]
        });
      }
    });
  }

  follow() {
    const url = this.TOMCAT_URL + '/follow?userId=' + this.user.userId + '&followUserId=' + this.userId;
    this.http.open('POST', url, false);
    this.http.send(null);
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
        title: 'Error',
        type: 'error',
        text: resp.message
      }).then(() => {
        location.reload();
      });
    }
  }

  isFollowing() {
    const url = this.TOMCAT_URL + '/follow?userId=' + this.user.userId;
    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    resp.forEach(element => {
      if (element.length > 0) {
        if (element.split('~')[0] === this.userId) {
          this.followingUser = true;
        }
      }
    });
  }

  getFollowing() {
    const url = this.TOMCAT_URL + '/follow?userId=' + this.userId;
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

  viewProfile(userId: string) {
    this.router.navigateByUrl('/user?userId=' + userId);
  }

}
