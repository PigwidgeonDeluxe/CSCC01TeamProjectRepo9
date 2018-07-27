import { Component, OnInit } from '@angular/core';
import * as FileSaver from 'file-saver';
import swal from 'sweetalert2';
import {Router} from '@angular/router';

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
  loading: boolean;
  user: any;

  constructor(private router: Router) { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.selectedSearchOption = 1;
    this.results = [];
    this.user = JSON.parse(localStorage.getItem('user'));
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
