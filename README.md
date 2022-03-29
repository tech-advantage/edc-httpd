# edc httpd 

edc httpd is an embedded httpd server to serve the edc documentation in standalone java application.

The server expose a web service to search in the content. The search is based on a query. The result is sorted as following : 

1. All documents which match the query in the label
1. Then all documents which match the query in the content

The search is done only in the document and chapter. The bricks are ignored.

## edc Version

Current release is compatible with edc v3.2+ and it's built with Java 11

## How can I get the latest release?

You can pull it from the central [Maven repositories](https://mvnrepository.com/artifact/fr.techad/edc-httpd) (without dependencies included):
### Maven
```xml
<dependency>
  <groupId>fr.techad</groupId>
  <artifactId>edc-httpd</artifactId>
  <version>2.0.1</version>
</dependency>
```

### Gradle
```groovy
    implementation 'fr.techad:edc-httpd:2.0.1'
```
Also you can get it on releases page on [releases page](https://github.com/tech-advantage/edc-httpd/releases) (with dependencies included)

## How can I create and run a docker image?
You have just to use this two commands in the repository, -v parameter is optional for the second command
```Shell
docker build -t edc .
docker run -p 8088:8088 -v [hostPath]:/home edc
```

## How can I search keyword in the content?
The query is based on [Lucene](https://lucene.apache.org/). So you can create complex query with the lucene syntax query.


Use the web service : `/httpd/api/search?query=YourQuery`.
There are other optional parameters for this request :
- _lang_ to search the results match with specified lang
- _exact-match_ to specify if search is an exact word search
- _limit_ to set a limit of search results\

You can see an example with those parameters below.

The wildcard ( `*` ) is supported.

*Example*

The query: `/httpd/api/search?query=httpd` returns help documentations which contain the words which start by `httpd`.

The query `/httpd/api/search?query=httpd AND server` returns help documentations which contain the words which start by `httpd` *AND* `server`.

The query `/httpd/api/search?query=http*` returns help documentations which contain the words which start with `http`.

The query  `http://localhost:8088/httpd/api/search?query=read&lang=en&exact-match=true&limit=20` returns all results for the exact search read in the language "en" with a limit of 20 results.

Those others parameters are independant you can use one of them only :\
`http://localhost:8088/httpd/api/search?query=read&lang=en` (Only results in en language)\
`http://localhost:8088/httpd/api/search?query=read&exact-match=true` (Exact search of `read`)\
`http://localhost:8088/httpd/api/search?query=rea&exact-match=false` (Search of all words begin by `rea`)\
`http://localhost:8088/httpd/api/search?query=read&limit=20` (results limited by 20)
## How can I reindex the content?

Use the web service : `/httpd/api/reindex`.

You have to put a token in the headers with the key "Edc-Token", the value can be find in `./token.info`
Otherwise the request can't be done, and the server will return a unauthorized status code.

**Example**
```Shell
curl -H "Edc-Token: [token]" http://localhost:8088/httpd/api/reindex
```
## How can I upload a new doc?
Use the web service : `/httpd/api/upload`.

You have to put a token in the headers with the key "Edc-Token", the value can be find in `./token.info`
Otherwise the request can't be done, and the server will return a unauthorized status code.
Then you put your file like the example below.

**Example**
```Shell
   curl -H "Edc-Token: [token]" -v -F data='@/[filepath]' http://localhost:8088/httpd/api/upload
```

By default this upload will not override the i18n folder. To override it you have to put the parameter Overridei18n=true after the URL like this :
```Shell
   curl -H "Edc-Token: [token]" -v -F data='@/[filepath]' http://localhost:8088/httpd/api/upload?Overridei18n=true
```
Be careful if your client keep the cache, upload modifications will not appear on your client. To resolve this problem just clear your client cache.
Using cURL is only possible in a UNIX terminal!
## Configuration

### Define the path

In the `src/main/java/resources` folder, create the file: `webserver.yml`.
Define the `base` variable to define the path to the published documentation.

```yaml
base: /local/edc-httpd/html
```

By default, the indexed content is stored in the folder `.edc/index` in the home user. It is possible to override this value with the variable `indexPath`.
It is possible to activate an extra url to reindex the content on demand with the variable `indexUrlEnabled`. By default, this url is disabled. To reindex the content, call the url: `/httpd/api/reindex`.

Furthermore for upload operations it's recommanded to set a `requestMaxSize` to avoid some upload errors when uploading large files.

*Example*

```yaml
base: /local/edc-httpd/html
indexPath: /local/edc-httpd/.edc
requestMaxSize: 1GB
indexUrlEnabled: true
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

#### Based on the configuration file

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
#### With port scan

To start the server with a port scan, call `EdcWebServer.run("slf4j", minPort, maxPort);`.
The server will start with the first free found port and return it.

*Example*

```java
package fr.techad.edc.demo;

import fr.techad.edc.httpd.EdcWebServer;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Scan port between 8080 and 9080
            int port = EdcWebServer.run("slf4j", 8080, 9080);
            /* ... */
        }
        catch(IOExeption e) {
            /* ... */
        }
        /* ... */
    }
}
```
## License

MIT [TECH'advantage](mailto:contact@tech-advantage.com)