import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';

import * as FileSaver from 'file-saver';
import swal from 'sweetalert2';

/**
 * Component for handling comments
 */
@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {

  TOMCAT_URL: string;
  http: XMLHttpRequest;

  results: any;
  loading: boolean;

  docId: string;
  comment: string;
  user: any;
  fileData: any;

  constructor(private activatedRoute: ActivatedRoute) { }

  /**
   * Initialize resources on initialization
   */
  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';
    this.results = [];
    this.user = JSON.parse(localStorage.getItem('user'));
    // get docId from URL
    this.activatedRoute.queryParams.subscribe((params: Params) => {
        this.docId = params['docId'];
      });
    this.getComments();
  }

  /**
   * Gets comments attributed to the current file
   */
  getComments() {
    const url = this.TOMCAT_URL + '/comment?docId=' + this.docId;
    if (this.docId !== undefined) {
      this.http.open('GET', url, false);
      this.http.send(null);

      // package backend response
      this.fileData = JSON.parse(this.http.response);
      // filter file size
      this.fileData.fileSize = Math.round(this.fileData.fileSize / 1000) / 100;
      const resp = JSON.parse(this.http.response).comments.split('\n');
      this.results = [];

      // reorganize backend response for HTML
      resp.forEach(element => {
        if (element.length > 0) {
          this.results.push({
            'docId': element.split('~')[0],
            'comment': element.split('~')[1],
            'userName': element.split('~')[2],
            'userType': element.split('~')[3],
            'userId': element.split('~')[4],
            'profileImage': element.split('~')[5],
            'date': element.split('~')[6]
          });
        }
      });
    }
  }

  /**
   * Send comment creation request to backend
   */
  insertComment() {
    // if there is a comment written, initiate a POST request
    if (this.comment) {
      const url = this.TOMCAT_URL + '/comment?docId=' + this.docId + '&comment=' + this.comment +
        '&commentUser=' + this.user.userId;
      this.http.open('POST', url, false);
      this.http.send(null);
      // visual response back to user
      swal({
        title: 'Success',
        type: 'success',
        text: 'Successfully added new comment'
      }).then(() => {
        // refresh comments and comment field after user acknowledges response
        this.getComments();
        this.comment = null;
      });
    // otherwise throw an error
    } else {
      swal({
        title: 'No Comment',
        type: 'warning',
        text: 'No comment written'
      });
    }
  }

  /**
   * Downloads a given file given its file name
   * @param fileName name of the given file
   */
  downloadFile(fileName: string) {
    this.http.open('GET', this.TOMCAT_URL + '/download?fileName=' + this.fileData.fileName + '&uploadTime=' + this.fileData.uploadedOn,
     true);
    // set response type (for PDF byte buffers)
    this.http.responseType = 'arraybuffer';
    this.http.send(null);

    this.http.onload = () => {
      const data = this.http.response;
      const extension = fileName.split('.').pop();
      let contentType;
      // handle content type handling
      if (extension === 'pdf') {
        contentType = {type: 'application/pdf'};
      } else if (extension === 'doc' || extension === 'docx') {
        contentType = {type: 'application/msword'};
      } else if (extension === 'html') {
        contentType = {type: 'text/html'};
      } else {
        contentType = {type: 'text/plain'};
      }
      // package blob and initiate a file save from the browser
      const blob = new Blob([data], contentType);
      FileSaver.saveAs(blob, fileName);
    };
  }
}
