import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  backgroundUrl: string;
  backgroundNum: number;

  constructor() { }

  ngOnInit() {
    this.backgroundNum = Math.floor(Math.random() * (4 - 1)) + 1;
    this.backgroundUrl = 'assets/background' + this.backgroundNum + '.jpg';
  }

}
