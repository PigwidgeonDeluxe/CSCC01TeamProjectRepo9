import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import * as FileSaver from 'file-saver';
import swal from 'sweetalert2';

/**
 * Component for handling searching
 */
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

  /**
   * Initialize router
   * @param router router to route between pages
   */
  constructor(private router: Router) { }

  /**
   * Initialize resources
   */
  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.selectedSearchOption = 1;
    this.results = [];
    this.bookmarkedFiles = [];
    this.user = JSON.parse(localStorage.getItem('user'));

    // if a user is logged in, get their bookmarks
    if (this.user) {
      this.getBookmarks(this.user.userId);
    }
  }

  /**
   * Search for a given file
   */
  search() {
    // if all forms are empty, do not initiate a request
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

  /**
   * Iniitates a search request
   * @param searchParam form for searching by file name
   * @param fileType form for searching by file type
   * @param userName form for searching by user name
   * @param userType form for searching by user type
   */
  getSearchResults(searchParam, fileType, userName, userType) {
    let url = this.TOMCAT_URL + '/search';

    // handle URL sequencing
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
    // package response
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

    // if there were no results found, send a display to the user
    if (this.results.length === 0) {
      swal({
        title: 'No Results',
        type: 'warning',
        text: 'No results found'
      });
    }
  }

  /**
   * Download a given file
   * @param fileName name of the file
   * @param uploadDate date the file was uploaded
   */
  downloadFile(fileName: string, uploadDate: string) {
    this.http.open('GET', this.TOMCAT_URL + '/download?fileName=' + fileName + '&uploadTime=' + uploadDate, true);
    this.http.responseType = 'arraybuffer';
    this.http.send(null);

    this.http.onload = () => {
      const data = this.http.response;
      const extension = fileName.split('.').pop();
      let contentType;
      // handle file content types
      if (extension === 'pdf') {
        contentType = {type: 'application/pdf'};
      } else if (extension === 'doc' || extension === 'docx') {
        contentType = {type: 'application/msword'};
      } else if (extension === 'html') {
        contentType = {type: 'text/html'};
      } else {
        contentType = {type: 'text/plain'};
      }
      // package blob and initiate browser download request
      const blob = new Blob([data], contentType);
      FileSaver.saveAs(blob, fileName);
    };
  }

  /**
   * Route to view the comments of a given file
   * @param docId ID of the given file
   */
  viewComments(docId: string) {
    this.router.navigateByUrl('/comments?docId=' + docId);
  }

  /**
   * Route to view the profile of a given user
   * @param userId ID of the given user
   */
  viewProfile(userId: string) {
    if (this.user.userId !== userId) {
      this.router.navigateByUrl('/user?userId=' + userId);
    } else {
      this.router.navigateByUrl('/profile');
    }
  }

  /**
   * Get the bookmarks of a given user
   * @param userId ID of the given user
   */
  getBookmarks(userId: string) {
    this.http.open('GET', this.TOMCAT_URL + '/bookmark?userId=' + userId, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    this.bookmarkedFiles = [];
    // package response
    resp.forEach(element => {
      if (element.length > 0) {
        this.bookmarkedFiles.push(element.split('~')[6]);
      }
    });
  }

  /**
   * Bookmark a given file
   * @param userId ID of the user who wishes to bookmark a file
   * @param fileId ID of the file to be bookmarked by the given user
   */
  bookmarkFile(userId: string, fileId: string) {
    this.http.open('POST', this.TOMCAT_URL + '/bookmark?userId=' + userId + '&fileId=' + fileId, false);
    this.http.send(null);
    const resp = JSON.parse(this.http.response);
    if (resp.status === 'SUCCESS') {
      // send response to user
      swal({
        title: 'Success',
        type: 'success',
        text: resp.message
      }).then(() => {
        // create the bookmark link
        this.getBookmarks(userId);
      });
    }
  }

}
