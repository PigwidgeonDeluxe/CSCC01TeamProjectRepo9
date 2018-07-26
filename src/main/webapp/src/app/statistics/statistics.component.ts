import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {

  TOMCAT_URL: string;
  http: XMLHttpRequest;

  fileTypeData: any;
  fileUploaderData: any;
  fileSizeData: any;

  topContributor: any;
  popularFileType: any;
  largestFile: number;
  newestUser: any;
  oldestUser: any;

  constructor() { }

  ngOnInit() {
    this.http = new XMLHttpRequest();
    this.TOMCAT_URL = 'http://localhost:8080';

    this.fileTypeData = {
      chartType: 'PieChart',
      dataTable: [
        ['File Type', 'Quantity']
      ],
      options: {
        width: 500
      },
    };

    this.fileUploaderData = {
      chartType: 'ColumnChart',
      dataTable: [
        ['Uploader', 'Files']
      ],
      options: {
        width: 500,
        legend: 'none',
        animation: {
          duration: 1000,
          easing: 'out',
          startup: true
        }
      }
    };

    this.fileSizeData = {
      chartType: 'Histogram',
      dataTable: [
        ['Name', 'Size (B)']
      ],
      options: {
        width: 500,
        legend: 'none',
        histogram: { bucketSize: 80000 },
        colors: ['green']
      }
    };

    this.getStatistics();
  }

  getStatistics() {
    const url = this.TOMCAT_URL + '/statistics';

    this.http.open('GET', url, false);
    this.http.send(null);
    const resp = JSON.parse(this.http.response);

    for (const key in resp.fileType) {
      if (resp.fileType.hasOwnProperty(key)) {
        this.fileTypeData.dataTable.push([key, resp.fileType[key]]);
        if (this.popularFileType) {
          if (resp.fileType[key] > this.popularFileType.files) {
            this.popularFileType = {'fileType': key, 'files': resp.fileType[key]};
          }
        } else {
          this.popularFileType = {'fileType': key, 'files': resp.fileType[key]};
        }
      }
    }

    for (const key in resp.uploaderStats) {
      if (resp.uploaderStats.hasOwnProperty(key)) {
        this.fileUploaderData.dataTable.push([key, resp.uploaderStats[key]]);
        if (this.topContributor) {
          if (resp.uploaderStats[key] > this.topContributor.files) {
            this.topContributor = {'name': key, 'files': resp.uploaderStats[key]};
          }
        } else {
          this.topContributor = {'name': key, 'files': resp.uploaderStats[key]};
        }
      }
    }

    for (const key in resp.fileSize) {
      if (resp.fileSize.hasOwnProperty(key)) {
        this.fileSizeData.dataTable.push([key, resp.fileSize[key]]);
        if (this.largestFile) {
          if (resp.fileSize[key] > this.largestFile) {
            this.largestFile = resp.fileSize[key];
          }
        } else {
          this.largestFile = resp.fileSize[key];
        }
      }
    }

    this.largestFile = Math.round(this.largestFile / 1000) / 100;

  }

}
