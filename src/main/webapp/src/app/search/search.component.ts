import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  TOMCAT_URL: string;
  searchQuery: string;
  option: string;
  results: any;
  http: XMLHttpRequest;
  selectedSearchOption: any;

  constructor() { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.selectedSearchOption = 1;
    this.results = [];
  }

  search() {
    if (this.searchQuery && this.selectedSearchOption === 1) {
      this.getSearchResults('fileName');
    } else if (this.searchQuery && this.selectedSearchOption === 2) {
      this.getSearchResults('fileType');
    } else if (this.searchQuery && this.selectedSearchOption === 3) {
      this.getSearchResults('userName');
    } else if (this.searchQuery && this.selectedSearchOption === 4) {
      this.getSearchResults('userType');
    }
  }

  getSearchResults(queryParam) {
    this.http.open('GET', this.TOMCAT_URL + '/search?' + queryParam + '=' + this.searchQuery, false);
    this.http.send(null);
    const resp = this.http.response.split('\n');
    this.results = [];
    resp.forEach(element => {
      if (element.length > 0) {
        this.results.push({
          'fileName': element.split('-')[0],
          'fileType': element.split('-')[1],
          'userType': element.split('-')[2],
          'userName': element.split('-')[3]
        });
      }
    });
  }

}
