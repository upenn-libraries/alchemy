
# elements-tools

A Java library and command-line interface (CLI) for working with the
[Symplectic Elements](http://symplectic.co.uk/products/elements/) API.

This project is a work in progress, and is not affiliated with or
supported by Symplectic. Please read the license.

# Installation and Configuration

This should work on any platform that runs Java (Linux, Windows, Mac). Theoretically.

Install Java 1.8 if it isn't already installed.

Download the most recent .zip distribution file. (TODO: create this!)

Copy `elements-tools.sample.properties` to `elements-tools.properties`
and edit it to suit your environment. See the comments in the file for
more information.

# Running the CLI App

The CLI app runs 'tasks', which are simply Java classes that a simple
framework that facilitates interaction with the Elements API. The app
ships with several predefined tasks; you can also create your own.

To run a task:
```
java -jar target/elements-tools-0.1-SNAPSHOT.jar CategoryTypesReport publications pubtypes.csv
```

There is a convenience shell script for Linux and Mac:
```
./elements-tools.sh CategoryTypesReport publications pubtypes.csv
```

To get help:

```
# shows a list of available tasks
./elements-tools.sh -h

# shows help for this specific task
./elements-tools.sh CategoryTypesReport -h
```

# Writing your own tasks

The library was designed to make writing your own tasks easy:

- create a subclass of Task
- make sure it's in the classpath when running the CLI
- use the full name of the class to refer to the task 

For example, if you create a class named edu.college.MyTask and
compile it into mycode.jar, run the CLI app as follows:

```
java -cp mycode.jar -jar target/elements-tools-0.1-SNAPSHOT.jar edu.college.MyTask
```

# How to Build from Source

Install maven and run this command:

```
mvn package
```

# Contributing

Please contribute!

# License

TODO
