# Typesafe Configured Webapp

I had a colleague ask about a specific use of Typesafe Config, so here it is.

This repo demonstrates a use of [Typesafe Config][] in a web application.
In particular, it demonstrates the use of an overriding configuration file,
and loading the configuration in an [Angular][] client.

This repo also happens to demonstrate building and running a [Jersey][]
backend app combined with an Angular client on [Tomcat][] using [Gradle][].

To run the application simply change to the root project directory
(where this file is located) and execute the following command.
```
./gradlew runWar
```

This will build the WAR and serve it up with Tomcat. You can stop
Tomcat with `CTRL+C`. When the app is running you can see the
result by navigating your browser to <http://localhost:8080/typesafe-configured-webapp>.
You should see "Go Jackets!".

Also, to see a message pulled from the Typesafe Config and served up from
a REST endpoint navigate to
<http://localhost:8080/typesafe-configured-webapp/api/welcome>.
Finally, to see the configuration that is loaded by the Angular client
navigate to <http://localhost:8080/typesafe-configured-webapp/api/config> --
it's pulled from the Typesafe Config and served up as JSON.

# Behind the scenes

While the Jackets may be loved by some and hated by others, as always,
what's happening behind the scenes is what really matters.

The word "Jackets", the colors, and the heading "Typesafe Configured Webapp"
are all loaded from Typesafe Config files. The class `TypesafeConfigFactory`
(an [HK2][] [`Factory`][Factory]) loads the configuration in the following priority,
meaning the configuration at the top of this list takes precedence.

- Java System properties
- Environment variables
- External overrides file defined by the Java System property `webapp.config.file`,\
  falling back to the file defined by the JNDI name `java:comp/env/webapp.config.file`\
  (declared in [`demo-webapp/src/main/webpp/META-INF/context.xml`][ctx] to point to
  the file at [`demo-webapp/overrides.conf`][EO])
- "overrides.conf" files found on the classpath\
  (found at [`demo-webapp/src/main/resources/overrides.conf`][overrides])
- "application.conf" files found on the classpath\
  (found at [`demo-webapp/src/main/resources/application.conf`][application])
- "reference.conf" files found on the classpath\
  (found at [`demo-webapp/src/main/resources/reference.conf`][reference])

Take a look at the source code for [`TypesafeConfigFactory.java`][TSCF]. It's simple.

# Jersey Resources

The app has two Jersey Resources, [`WelcomeResource`][WR] and [`ConfigResource`][CR].

- `WelcomeResource` pulls the config property `rich.server.welcome`. The config
  path `rich.server` is _not_ sent to the Angular client. Simply, everything
  _except_ `server` is sent to the Angular client.
- `ConfigResource` pulls all the configuration _except_ `server` and serves
  it up as JSON, which is accomplished using [`Config.withoutPath(...)`][WithoutPath]
  and [`ConfigObject.render(...)`][Render].
  The Angular client gets the configuration from this resource.

# Loading the config into the UI

The Angular client defines the TypeScript class [`ConfigService`][CS], which is
used to make a request to `ConfigResource` to load the configuration.
`ConfigService` is used by [`AppComponent`][AC] to display the configuration
properties `rich.appName`, `rich.team`, and the team colors (see [`app.component.html`][AH]).
The property `appName` is defined in [`demo-webapp/src/main/resources/application.conf`][application]
and the team properties are defined in [`demo-webapp/src/main/resources/overrides.conf`][overrides]
and overridden in the external file [`demo-webapp/overrides.conf`][EO].

There is also a simple example in [`main.ts`][main] of loading the config outside of
an Angular component.

# Not a Jackets fan?

Run `./gradlew runWar -Dno_external_override=true` then check out
<http://localhost:8080/typesafe-configured-webapp>. This removes the inclusion
of the external overrides file at `demo-webapp/overrides.conf`.

[Typesafe Config]: https://github.com/lightbend/config
[Angular]: https://angular.io/
[Jersey]: https://jersey.github.io/
[Tomcat]: http://tomcat.apache.org/
[Gradle]: https://gradle.org/
[HK2]: https://javaee.github.io/hk2/
[Factory]: https://javaee.github.io/hk2/apidocs/org/glassfish/hk2/api/Factory.html
[ctx]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-webapp/src/main/webapp/META-INF/context.xml
[EO]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-webapp/overrides.conf
[overrides]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-webapp/src/main/resources/overrides.conf
[application]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-webapp/src/main/resources/application.conf
[reference]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-webapp/src/main/resources/reference.conf
[TSCF]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-webapp/src/main/java/rich/TypesafeConfigFactory.java#35
[WR]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-webapp/src/main/java/rich/WelcomeResource.java
[CR]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-webapp/src/main/java/rich/ConfigResource.java
[WithoutPath]: https://lightbend.github.io/config/latest/api/com/typesafe/config/Config.html#withoutPath-java.lang.String-
[Render]: https://lightbend.github.io/config/latest/api/com/typesafe/config/ConfigValue.html#render-com.typesafe.config.ConfigRenderOptions-
[CS]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-ui/src/app/config/config.service.ts
[AC]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-ui/src/app/app.component.ts
[AH]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-ui/src/app/app.component.html
[main]: https://scm.ctisl.gtri.gatech.edu/git/users/ra95/repos/typesafe-configured-webapp/browse/demo-ui/src/main.ts
