import { Component, OnInit } from '@angular/core';

import swal from 'sweetalert2';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  TOMCAT_URL: string;
  http: XMLHttpRequest;

  searchQuery: string;
  results: any;
  user: any;

  constructor() { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.user = JSON.parse(localStorage.getItem('user'));
    this.results = [];
  }

  search() {
    if (!this.searchQuery) {
      swal({
        title: 'No query',
        type: 'warning',
        text: 'Please submit a query to use the search functionality'
      });
    } else {
      this.getSearchResults(this.searchQuery);
    }
  }

  getSearchResults(searchParam: string) {
    const url = this.TOMCAT_URL + '/userSearch?userName=' + searchParam;

    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    this.results = [];
    resp.forEach(element => {
      if (element.length > 0) {
        this.results.push({
          'userName': element.split('~')[0],
          'userType': element.split('~')[1],
          'userId': element.split('~')[2],
          'createdOn': +element.split('~')[3],
          'profileImage': element.split('~')[4]
        });
      }
    });

    if (this.results.length === 0) {
      swal({
        title: 'No results',
        type: 'warning',
        text: 'No results found'
      });
    }
  }

}
