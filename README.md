### Irrigation experiment
A collective action / [public goods experiment](http://en.wikipedia.org/wiki/Public_goods_game) implemented in Java
Swing and the [sesef](http://bitbucket.org/virtualcommons/sesef) framework. The irrigation experiment places
participants in an upstream-downstream scenario where participants are randomly partitioned into groups and can choose when
to open or close their irrigation gates in real-time and have dedicated communication and investment rounds where they can coordinate on how much to invest in their common infrastructure. 

### publications
This software was used to generate the data published in

```
John M. Anderies, Marco A. Janssen, Allen Lee, Hannah Wasserman, Environmental variability and collective action: Experimental insights from an irrigation game, Ecological Economics, Volume 93, September 2013, Pages 166-176, ISSN 0921-8009, http://dx.doi.org/10.1016/j.ecolecon.2013.04.010.
(http://www.sciencedirect.com/science/article/pii/S0921800913001390)
```

Data from this publication is currently archived at http://dev.commons.asu.edu/data/irrigation/all-data.zip

### features

* participants are placed in randomized ordered groups (A -> E where A is upstream and E is downstream) and go through a series of chat -> investment -> real-time decision
  making rounds
* includes comprehension quizzes with detailed feedback, dynamic graph and chart visualizations via the excellent [JFreeChart](http://www.jfree.org/jfreechart) library
* round and experiment parameterization including:
    * chat and round duration
    * infrastructure decay and shocks to the infrastructure and water supply
    * initial endowment and payment exchange rate
    * limited information where participants can only communicate with and view information from their immediate neighbors

### how to run the software

* [install and setup Java, Ant, and Maven](https://github.com/virtualcommons/sesef/wiki/)
* clone or download this codebase
* copy `build.properties.example` to `build.properties` and customize as needed, in particular `server.address`, `web.dir`, and `codebase.url`
* run `ant deploy`
* For a quick start demo, run the following on the command-line 
```
% ant prepare-demo
% ant demo # this will start a server, a facilitator, and 5 client windows to form a full group
```

### how to contribute
[Please let us know](http://vcweb.asu.edu/contact) if you are interested in extending or running this software. 
