import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

// This isn't actually used, but is here as a simple example of how to
// load Typesafe Configuration outside of Angular components.
fetch('api/config')
  .then(response => response.json())
  .then(appConfig => {
    console.log('Hello from ' + appConfig['appName']);
  })
  .catch(error => console.error('Failed to load Typesafe Configuration', error));

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.log(err));
