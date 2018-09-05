import { Component, OnInit } from '@angular/core';
import { Title } from "@angular/platform-browser";

import { ConfigService } from './config/config.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  /**
   * Holds the Typesafe Configuration loaded from the server.
   */
  appConfig = {};

  constructor(
    private configService: ConfigService,
    private titleService: Title
  ) {
  }

  ngOnInit() {
    // Load the Typesafe Configuration from the server.
    this.configService.getConfig().subscribe(
      data => {
        this.appConfig = data;
        this.titleService.setTitle(this.appConfig['appName'])
      },
      error => console.error(error)
    );
  }
}
