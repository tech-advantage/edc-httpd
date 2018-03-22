# edc httpd 

edc httpd is an embedded httpd server to serve the edc documentation in standalone java application.

## How can I get the latest release?

You can pull it from the central Maven repositories:

### Maven
```xml
<dependency>
  <groupId>fr.techad</groupId>
  <artifactId>edc-httpd-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle
```groovy
    compile group: 'fr.techad', name: 'edc-httpd-java', version: '1.0.0'
```
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