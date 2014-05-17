### irrigation
A Java Swing collective action / [public goods experiment](http://en.wikipedia.org/wiki/Public_goods_game) that places
participants in an upstream-downstream scenario. Participants choose when to open or close their irrigation gates and
how much to invest in their common infrastructure.

It is dependent on the [csidex](http://bitbucket.org/virtualcommons/csidex) framework.

### installation requirements

- JDK 1.7
- [Apache Ant](http://ant.apache.org)

### deployment requirements

Pick one:

- a webserver to deliver JNLP files (can use Maven and its embedded Jetty webserver)
- networked filesystem where the codebase is installed mounted on each client.

### how to run

- download the software
- `cp build.properties.example build.properties` and then customize `build.properties`
- copy experiment configuration files to `src/main/resources/configuration`. For a quick demo, try `cp src/main/resources/configuration/demo/* src/main/resources/configuration`
- run `ant deploy`

After a successful deployment you can:

- start the server via `ant server` or `java -jar -server server.jar`.

- start a facilitator via `ant fac`, `java -jar facilitator.jar`, or visiting `${codebase.url}/facilitator.jnlp` in your
  browser for Java WebStart.

- start a client via `ant client`, `java -jar client.jar`, or visiting `${codebase.url}` in your browser to use Java
  WebStart.

NOTE: Java WebStart deployment requires security overrides, either trusted signed jars or adding an exception to the site list on each
client machine for the server hosting the webstart JNLP files and jars.

### how to contribute
This experiment is in dire need of a thorough UI refactor and is _not under active development_.

Past development has been supported by the [Center for the Study of Institutional Diversity](http://csid.asu.edu) and the [National Science Foundation](http://nsf.gov).
