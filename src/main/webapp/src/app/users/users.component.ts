import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import swal from 'sweetalert2';

/**
 * Component handling searching of other users
 */
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
    this.user = JSON.parse(localStorage.getItem('user'));
    this.results = [];
  }

  /**
   * Search for a given user
   */
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

  /**
   * Initiate a search request for a given user
   * @param searchParam query containing name of user to search for
   */
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

  /**
   * Route to the profile of a given user
   * @param userId ID of the given user
   */
  viewProfile(userId: string) {
    // if the user is some other user, route to their profile
    if (userId !== this.user.userId) {
      this.router.navigateByUrl('/user?userId=' + userId);
    // if the user is the logged in user, route to profile
    } else {
      this.router.navigateByUrl('/profile');
    }
  }
}
