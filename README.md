# edc httpd 

edc httpd is an embedded httpd server to serve the edc documention in standalone java application.

## Configuration

### Define the path
In the `src/main/java/resources` folder, create the file: `webserver.yml`.
Define the `base`variable to define the path to the published documentation.

```yaml
base: /local/edc-httpd/html
```

*Optional*

It is possible to override the default port (8088). You have to create a `server.yml` file and define its content like this:

```yaml
httpPort: 9000
ip: 0.0.0.0
enableHttp: true
```

Modify the `httpPort` to define the nw port value.

### Add the dependency

**Maven**

Add the dependency in your `pom.xml` for maven

```xml
<dependency>
  <groupId>fr.techad.edc</groupId>
  <artifactId>httpd-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

**Gradle**

```groovy
compile group: 'fr.techad.edc', name: 'httpd-java', version: '1.0.0'
```

### Start the server

To start the start, add 

```java
package fr.techad.edc.demo;

import fr.techad.edc.httpd.EdcWebServer;

public class Main {
    public static void main(String[] args) {
        EdcWebServer.run();
        /* ... */
    }
}
```

## License

MIT [TECH'advantage](mailto:contact@tech-advantage.com)