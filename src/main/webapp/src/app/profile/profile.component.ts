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

  fileTypeData: any;
  fileSizeData: any;

  popularFileType: any;
  largestFile: any;

  constructor() { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.user = JSON.parse(localStorage.getItem('user'));
    this.results = [];

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
