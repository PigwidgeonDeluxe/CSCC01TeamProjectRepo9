import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import swal from 'sweetalert2';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {

  TOMCAT_URL: string;

  results: any;
  http: XMLHttpRequest;
  loading: boolean;

  docId: string;
  comment: string;
  commentUser: any;
  fileData: any;

  constructor(private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.results = [];
    this.commentUser = JSON.parse(localStorage.getItem('user'));
    this.activatedRoute.queryParams.subscribe((params: Params) => {
        this.docId = params['docId'];
      });
    this.getComments();
  }

  getComments() {
    const url = this.TOMCAT_URL + '/comment?docId=' + this.docId;
    if (this.docId !== undefined) {
      this.http.open('GET', url, false);
      this.http.send(null);
      this.fileData = JSON.parse(this.http.response);
      this.fileData.fileSize = Math.round(this.fileData.fileSize / 1000) / 100;
      const resp = JSON.parse(this.http.response).comments.split('\n');
      this.results = [];
      resp.forEach(element => {
        if (element.length > 0) {
          this.results.push({
            'docId': element.split('~')[0],
            'comment': element.split('~')[1],
            'commentUser': element.split('~')[2],
            'date': element.split('~')[3]
          });
        }
      });
    }
  }

  insertComment() {
    if (this.comment) {
      const url = this.TOMCAT_URL + '/comment?docId=' + this.docId + '&comment=' + this.comment +
        '&commentUser=' + this.commentUser.userName;
      this.http.open('POST', url, false);
      this.http.send(null);
    } else {
      swal({
        title: 'No Comment',
        type: 'warning',
        text: 'No comment written'
      });
    }
  }

}
