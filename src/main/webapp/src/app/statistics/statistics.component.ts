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

    const fileTypeStats = JSON.parse(resp.fileType);
    const fileUploaderStats = JSON.parse(resp.uploaderStats);
    const fileSizeStats = JSON.parse(resp.fileSize);

    for (const key in fileTypeStats) {
      if (fileTypeStats.hasOwnProperty(key)) {
        this.fileTypeData.dataTable.push([key, fileTypeStats[key]]);
        if (this.popularFileType) {
          if (fileTypeStats[key] > this.popularFileType.files) {
            this.popularFileType = {'fileType': key, 'files': fileTypeStats[key]};
          }
        } else {
          this.popularFileType = {'fileType': key, 'files': fileTypeStats[key]};
        }
      }
    }

    for (const key in fileUploaderStats) {
      if (fileUploaderStats.hasOwnProperty(key)) {
        this.fileUploaderData.dataTable.push([key, fileUploaderStats[key]]);
        if (this.topContributor) {
          if (fileUploaderStats[key] > this.topContributor.files) {
            this.topContributor = {'name': key, 'files': fileUploaderStats[key]};
          }
        } else {
          this.topContributor = {'name': key, 'files': fileUploaderStats[key]};
        }
      }
    }

    for (const key in fileSizeStats) {
      if (fileSizeStats.hasOwnProperty(key)) {
        this.fileSizeData.dataTable.push([key, fileSizeStats[key]]);
        if (this.largestFile) {
          if (fileSizeStats[key] > this.largestFile) {
            this.largestFile = fileSizeStats[key];
          }
        } else {
          this.largestFile = fileSizeStats[key];
        }
      }
    }

    this.largestFile = Math.round(this.largestFile / 1000) / 100;

  }

}
