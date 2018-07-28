import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
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
  user: any;
  fileData: any;

  constructor(private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.results = [];
    this.user = JSON.parse(localStorage.getItem('user'));
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
            'userName': element.split('~')[2],
            'userType': element.split('~')[3],
            'profileImage': element.split('~')[4],
            'date': element.split('~')[5]
          });
        }
      });
    }
  }

  insertComment() {
    if (this.comment) {
      const url = this.TOMCAT_URL + '/comment?docId=' + this.docId + '&comment=' + this.comment +
        '&commentUser=' + this.user.userId;
      this.http.open('POST', url, false);
      this.http.send(null);
      swal({
        title: 'Success',
        type: 'success',
        text: 'Successfully added new comment'
      }).then(() => {
        this.getComments();
        this.comment = null;
      });
    } else {
      swal({
        title: 'No Comment',
        type: 'warning',
        text: 'No comment written'
      });
    }
  }

  downloadFile(fileName: string, uploadDate: string) {
    this.http.open('GET', this.TOMCAT_URL + '/download?fileName=' + this.fileData.fileName + '&uploadTime=' + this.fileData.uploadedOn,
     true);
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
}
