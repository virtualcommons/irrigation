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

Quickstart:

* [install and setup Java, Ant, and Maven](https://bitbucket.org/virtualcommons/csidex/wiki/Home)
* [download and unpack the foraging codebase](https://bitbucket.org/virtualcommons/irrigation/downloads)
* From the command-line (e.g., Windows PowerShell or Command Prompt, Mac OSX Terminal.app, or any Linux terminal program)
```
#!bash
% ant prepare-demo
% ant demo
```
For more detailed instructions, please see the [installation instructions on our wiki](https://bitbucket.org/virtualcommons/irrigation/wiki/Installation).

### how to contribute
This experiment is not under active development. If you'd like to use it to run experiments or want to extend the
software, [please let us know](http://vcweb.asu.edu/contact). 
