import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import * as FileSaver from 'file-saver';
import swal from 'sweetalert2';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  TOMCAT_URL: string;

  searchQuery: string;
  fileTypeQuery: string;
  userNameQuery: string;
  userTypeQuery: string;

  option: string;
  results: any;
  http: XMLHttpRequest;
  selectedSearchOption: any;
  user: any;

  bookmarkedFiles: any;

  constructor(private router: Router) { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.selectedSearchOption = 1;
    this.results = [];
    this.bookmarkedFiles = [];
    this.user = JSON.parse(localStorage.getItem('user'));

    if (this.user) {
      this.getBookmarks(this.user.userId);
    }
  }

  search() {
    if (!this.searchQuery && !this.fileTypeQuery && !this.userNameQuery && !this.userTypeQuery) {
      swal({
        title: 'No query',
        type: 'warning',
        text: 'Please submit a query to use the search functionality'
      });
    } else {
      this.getSearchResults(this.searchQuery, this.fileTypeQuery, this.userNameQuery, this.userTypeQuery);
    }
  }

  getSearchResults(searchParam, fileType, userName, userType) {
    let url = this.TOMCAT_URL + '/search';

    if (searchParam) {
      if (url.indexOf('?') === -1) {
        url += '?fileName=' + searchParam + '&contents=' + searchParam;
      } else {
        url += '&fileName=' + searchParam + '&contents=' + searchParam;
      }
    }
    if (fileType) {
      if (url.indexOf('?') === -1) {
        url += '?fileType=' + fileType;
      } else {
        url += '&fileType=' + fileType;
      }
    }
    if (userName) {
      if (url.indexOf('?') === -1) {
        url += '?userName=' + userName;
      } else {
        url += '&userName=' + userName;
      }
    }
    if (userType) {
      if (url.indexOf('?') === -1) {
        url += '?userType=' + userType;
      } else {
        url += '&userType=' + userType;
      }
    }

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

  viewProfile(userId: string) {
    if (this.user.userId !== userId) {
      this.router.navigateByUrl('/user?userId=' + userId);
    } else {
      this.router.navigateByUrl('/profile');
    }
  }

  getBookmarks(userId: string) {
    this.http.open('GET', this.TOMCAT_URL + '/bookmark?userId=' + userId, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    this.bookmarkedFiles = [];
    resp.forEach(element => {
      if (element.length > 0) {
        this.bookmarkedFiles.push(element.split('~')[6]);
      }
    });
  }

  bookmarkFile(userId: string, fileId: string) {
    this.http.open('POST', this.TOMCAT_URL + '/bookmark?userId=' + userId + '&fileId=' + fileId, false);
    this.http.send(null);
    const resp = JSON.parse(this.http.response);
    if (resp.status === 'SUCCESS') {
      swal({
        title: 'Success',
        type: 'success',
        text: resp.message
      }).then(() => {
        this.getBookmarks(userId);
      });
    }
  }

}
