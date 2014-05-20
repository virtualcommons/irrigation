### irrigation
A collective action / [public goods experiment](http://en.wikipedia.org/wiki/Public_goods_game) implemented using Java
Swing and the [csidex](http://bitbucket.org/virtualcommons/csidex) framework that places
participants in an upstream-downstream scenario. Participants are randomly partitioned into groups and can choose when
to open or close their irrigation gates and how much to invest in their common infrastructure.

### features

* participants are placed in randomized groups and go through a series of chat -> investment -> real-time decision
  making rounds
* includes comprehension quizzes with detailed feedback, dynamic graph and chart visualizations via the excellent [JFreeChart](http://www.jfree.org/jfreechart) library
* round and experiment parameterization support for variable shocks to the infrastructure and water supply and limited
  information where participants can only communicate and see what is going on with their immediate neighbors

### how to run the software

* [install and setup Java, Ant, and Maven](https://bitbucket.org/virtualcommons/csidex/wiki/Home)
* [download and unpack the foraging codebase](https://bitbucket.org/virtualcommons/irrigation/downloads)
* For a quick start demo, run the following from the command-line (e.g., Windows PowerShell or Command Prompt, Mac OSX Terminal.app, Linux terminal)
```
#!bash
% ant prepare-demo
% ant demo # this will start a server, a facilitator, and 5 client windows to form a full group
```

For more detailed instructions, please refer to the [installation instructions on our wiki](https://bitbucket.org/virtualcommons/irrigation/wiki/Installation).

### how to contribute
This experiment is not under active development. If you'd like to use it to run experiments or want to extend the
software, [please let us know](http://vcweb.asu.edu/contact). 
