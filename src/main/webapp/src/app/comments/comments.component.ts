import { Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute, Params} from '@angular/router';
import * as FileSaver from 'file-saver';
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
  comment_user: any;

  constructor(private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.results = [];
    this.comment_user = JSON.parse(localStorage.getItem('user'));
    this.activatedRoute.queryParams.subscribe((params: Params) => {
        this.docId = params['docId'];
      });
      console.log("docId:" + this.docId);
      console.log(this.comment_user)
    this.getComments();
  }

  getComments(){
  	let url = this.TOMCAT_URL + '/comments?docId=' + this.docId;
    
    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = this.http.response.split('"\n');
    this.results = [];
    resp.forEach(element => {
      if (element.length > 0) {
        this.results.push({
          'docId': element.split('~')[0],
          'comment': element.split('~')[1],
          'comment_user': element.split('~')[2],
          'date': element.split('~')[3]
        });
      }
    });
    console.log("getting comments");
  }

  insertComment(){
  	let url = this.TOMCAT_URL + '/commenting?docId=' + this.docId + "&comment=" + this.comment + "&comment_user=" + this.comment_user.userName;
  	this.http.open('POST', url, false);
    this.http.send(null);
    console.log("comment:" + this.comment);
  }
}
