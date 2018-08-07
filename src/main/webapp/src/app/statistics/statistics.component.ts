import { Component, OnInit } from '@angular/core';

/**
 * Comopnent for handling system statistics
 */
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

  stats: any;
  fileTypeEmpty: boolean;
  fileUploaderEmpty: boolean;
  fileSizeEmpty: boolean;

  topContributor: any;
  popularFileType: any;
  largestFile: number;

  constructor() { }

  /**
   * Initialize resources and charts
   */
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
        histogram: { bucketSize: 10000 },
        colors: ['green']
      }
    };

    this.getStatistics();
  }

  /**
   * Get statistics from the backend
   */
  getStatistics() {
    const url = this.TOMCAT_URL + '/statistics';

    this.http.open('GET', url, false);
    this.http.send(null);
    this.stats = JSON.parse(this.http.response);

    for (const key in this.stats.fileType) {
      if (this.stats.fileType.hasOwnProperty(key)) {
        // add data to chart
        this.fileTypeData.dataTable.push([key, this.stats.fileType[key]]);
        // get the most popular file type
        if (this.popularFileType) {
          if (this.stats.fileType[key] > this.popularFileType.files) {
            this.popularFileType = {'fileType': key, 'files': this.stats.fileType[key]};
          }
        } else {
          this.popularFileType = {'fileType': key, 'files': this.stats.fileType[key]};
        }
      }
    }

    for (const key in this.stats.uploaderStats) {
      if (this.stats.uploaderStats.hasOwnProperty(key)) {
        // add data to chart
        this.fileUploaderData.dataTable.push([key, this.stats.uploaderStats[key]]);
        // get the top contributor
        if (this.topContributor) {
          if (this.stats.uploaderStats[key] > this.topContributor.files) {
            this.topContributor = {'name': key, 'files': this.stats.uploaderStats[key]};
          }
        } else {
          this.topContributor = {'name': key, 'files': this.stats.uploaderStats[key]};
        }
      }
    }

    for (const key in this.stats.fileSize) {
      if (this.stats.fileSize.hasOwnProperty(key)) {
        // add data to the chart
        this.fileSizeData.dataTable.push([key, this.stats.fileSize[key]]);
        if (this.largestFile) {
          // get the largest file
          if (this.stats.fileSize[key] > this.largestFile) {
            this.largestFile = this.stats.fileSize[key];
          }
        } else {
          this.largestFile = this.stats.fileSize[key];
        }
      }
    }

    // cast largest file to a digestable format
    this.largestFile = Math.round(this.largestFile / 1000) / 100;

    // handling if there are no files in the system
    if (Object.keys(this.stats.fileType).length === 0) {
      this.fileTypeEmpty = true;
    }

    if (Object.keys(this.stats.uploaderStats).length === 0) {
      this.fileUploaderEmpty = true;
    }

    if (Object.keys(this.stats.fileSize).length === 0) {
      this.fileSizeEmpty = true;
    }

  }

}
