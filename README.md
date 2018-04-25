# edc httpd 

edc httpd is an embedded httpd server to serve the edc documentation in standalone java application.

The server expose a web service to search in the content. The search is based on a query. The result is sorted as following : 

1. All documents which match the query in the label
1. Then all documents which match the query in the content

The search is done only in the document and chapter. The bricks are ignored.

## How can I get the latest release?

You can pull it from the central Maven repositories:

### Maven
```xml
<dependency>
  <groupId>fr.techad</groupId>
  <artifactId>edc-httpd-java</artifactId>
  <version>1.1.0</version>
</dependency>
```

### Gradle
```groovy
    compile group: 'fr.techad', name: 'edc-httpd-java', version: '1.1.0'
```

## How can I search keyword in the content?

Use the web service : `/httpd/api/search?query=YourQuery`.

The query is based on [Lucene](https://lucene.apache.org/). So you can create complex query with the lucene syntax query.

The wildcard is supported.

*Example*

The query: `/httpd/api/search?query=httpd` returns help documentations which contain the word `httpd`.

The query `/httpd/api/search?query=httpd AND server` returns help documentations which contain the words `httpd` *AND* `server`.

The query `/httpd/api/search?query=http*` returns help documentations which contain the words which start with `http`.

## Configuration

### Define the path

In the `src/main/java/resources` folder, create the file: `webserver.yml`.
Define the `base`variable to define the path to the published documentation.

```yaml
base: /local/edc-httpd/html
```

By default, the indexed content is stored in the folder `.edc/index` in the home user. It is possible to override this value with the variable `indexPath`.

*Example*

```yaml
base: /local/edc-httpd/html
indexPath: /local/edc-httpd/.edc
```

*Optional*

It is possible to override the default port (8088). You have to create a `server.yml` file and define its content like this:

```yaml
httpPort: 9000
ip: 0.0.0.0
enableHttp: true
```

Modify the `httpPort` to define the new port value.

### Start the server

To start the server, call `EdcWebServer.run();`  

*Example*

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