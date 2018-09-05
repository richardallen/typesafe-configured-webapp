import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

/**
 * A service to get the Typesafe Configuration from the server.
 */
@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  configUrl = 'api/config';

  constructor(private http: HttpClient) {
  }

  getConfig() {
    return this.http.get(this.configUrl);
  }
}
