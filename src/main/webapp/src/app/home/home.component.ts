import { Component, OnInit } from '@angular/core';

/**
 * Component for handling home page
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  backgroundUrl: string;
  backgroundNum: number;

  constructor() { }

  /**
   * Randomize the background image on initialization
   */
  ngOnInit() {
    this.backgroundNum = Math.floor(Math.random() * (4 - 1)) + 1;
    this.backgroundUrl = 'assets/background' + this.backgroundNum + '.jpg';
  }

}
