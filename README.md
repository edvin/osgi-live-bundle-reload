# OSGi Live Bundle Reloader

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/no.tornado/osgi-live-bundle-reload/badge.svg)](https://search.maven.org/#search|ga|1|no.tornado.osgi-live-bundle-reload)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

**Automatically reload your bundles when they are changed.**

This tool is primarily used to support reloading in development. I made it
because IntelliJ IDEA doesn't support reloading bundles in OSGi containers.

Specify a comma separated list of directories to watch with the system property 
 `bundle.reload.dirs` and drop [this jar](http://repo1.maven.org/maven2/no/tornado/osgi-live-bundle-reload/1.0/osgi-live-bundle-reload-1.0.jar) into your OSGi container bundle directory.
 
Typically you would watch the bundle-directory of your container, or the output directory of the bundles you're corrently working on in the IDE.
 
## Reload delay on Mac
 
This bundle uses the [Java Watch Service](https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchService.html) which
unfortunately uses polling to watch for changes on Mac. This leads to a couple of seconds delay after the bundle is changed.
  
This could be alliviated to some degree by specifying the `SensitivityWatchEventModifier.HIGH` parameter to
`Path.register`, but `SensitivityWatchEventModifier` is in the `com.sun.nio.file` package, which comes with it's
own set of problems for OSGi environments.
 
 
