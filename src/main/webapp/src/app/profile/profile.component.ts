import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import * as FileSaver from 'file-saver';

/**
 * Component handling profile interactions
 */
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

  stats: any;
  fileTypeData: any;
  fileSizeData: any;

  fileTypeEmpty: boolean;
  fileSizeEmpty: boolean;

  popularFileType: any;
  largestFile: any;

  bookmarkedFiles: any;

  /**
   * Initialize router
   * @param router router to route between pages
   */
  constructor(private router: Router) { }

  /**
   * Initialize resources and charts on page load
   */
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

  /**
   * Get user statistics
   */
  getStatistics() {
    const user = JSON.parse(localStorage.getItem('user'));

    const url = this.TOMCAT_URL + '/statistics?userName=' + user.userName;

    this.http.open('GET', url, false);
    this.http.send(null);
    this.stats = JSON.parse(this.http.response);

    for (const key in this.stats.fileType) {
      if (this.stats.fileType.hasOwnProperty(key)) {
        // add file type data to file type chart
        this.fileTypeData.dataTable.push([key, this.stats.fileType[key]]);
        // get the most popular file type
        if (this.popularFileType) {
          if (this.stats.fileType[key] > this.popularFileType.files) {
            this.popularFileType = {'fileType': key, 'files': this.stats.fileType[key]};
          }
        } else {
          this.popularFileType = {'fileType': key, 'files': this.stats.fileType[key]};
        }
      }
    }

    for (const key in this.stats.fileSize) {
      if (this.stats.fileSize.hasOwnProperty(key)) {
        // add file size data to file size chart
        this.fileSizeData.dataTable.push([key, this.stats.fileSize[key]]);
        // get the largest file
        if (this.largestFile) {
          if (this.stats.fileSize[key] > this.largestFile) {
            this.largestFile = this.stats.fileSize[key];
          }
        } else {
          this.largestFile = this.stats.fileSize[key];
        }
      }
    }

    // cast largest file to digestable type
    this.largestFile = Math.round(this.largestFile / 1000) / 100;

    // handling if there are no files in the system
    if (Object.keys(this.stats.fileType).length === 0) {
      this.fileTypeEmpty = true;
    }

    if (Object.keys(this.stats.fileSize).length === 0) {
      this.fileSizeEmpty = true;
    }
  }

  /**
   * Get all the files uploaded by the user
   */
  getUserFiles() {
    const url = this.TOMCAT_URL + '/userFiles?userId=' + this.user.userId;

    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    this.results = [];
    // package response
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

  /**
   * Download a given file
   * @param fileName name of the given file
   * @param uploadDate the date the file was uploaded
   */
  downloadFile(fileName: string, uploadDate: string) {
    this.http.open('GET', this.TOMCAT_URL + '/download?fileName=' + fileName + '&uploadTime=' + uploadDate, true);
    this.http.responseType = 'arraybuffer';
    this.http.send(null);

    this.http.onload = () => {
      const data = this.http.response;
      const extension = fileName.split('.').pop();
      let contentType;
      // content type handling
      if (extension === 'pdf') {
        contentType = {type: 'application/pdf'};
      } else if (extension === 'doc' || extension === 'docx') {
        contentType = {type: 'application/msword'};
      } else if (extension === 'html') {
        contentType = {type: 'text/html'};
      } else {
        contentType = {type: 'text/plain'};
      }
      // package blob and initialte download request from browser
      const blob = new Blob([data], contentType);
      FileSaver.saveAs(blob, fileName);
    };
  }

  /**
   * Get all users being followed
   */
  getFollowing() {
    const url = this.TOMCAT_URL + '/follow?userId=' + this.user.userId;
    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    // package response
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

  /**
   * Get all files bookmarked
   */
  getBookmarks() {
    const url = this.TOMCAT_URL + '/bookmark?userId=' + this.user.userId;
    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    this.bookmarkedFiles = [];
    // package response
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

  /**
   * View a given comment
   * @param docId ID of the given comment
   */
  viewComments(docId: string) {
    this.router.navigateByUrl('/comments?docId=' + docId);
  }

  /**
   * View a given user's profile
   * @param userId ID of the given user
   */
  viewProfile(userId: string) {
    this.router.navigateByUrl('/user?userId=' + userId);
  }

}
