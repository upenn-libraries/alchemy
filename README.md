
# alchemy

A Java library and command-line tool for the
[Symplectic Elements](http://symplectic.co.uk/products/elements/) API and Reporting Database.

It's an extensible swiss army knife for working with Elements.

This project is not affiliated with or supported by Symplectic.

# Installation and Configuration

This should work on any platform that runs Java (Linux, Windows, Mac). Theoretically.

Install Java 1.8 if it isn't already installed.

Download the most recent distribution file from the "releases" page of
this repository. Unpack the .zip or .tar.gz file.

Copy `alchemy.sample.properties` to `alchemy.properties`
and edit it to suit your environment. See the comments in the file for
more information.

# Running the CLI App

The CLI app runs 'tasks', which are simply Java classes used by a
simple framework that facilitates access to the Elements API and the
reporting database. The app ships with several predefined tasks, and
you can also create your own.

To run a task:
```
java -jar target/alchemy-0.1.jar CategoryTypesReport publications pubtypes.csv
```

There is a convenience shell script for Linux and Mac:
```
./alchemy.sh CategoryTypesReport publications pubtypes.csv
```

To use a different configuration file:

```
./alchemy.sh -c production.properties CategoryTypesReport publications pubtypes.csv
```

To get help:

```
# shows a list of available tasks
./alchemy.sh -h

# shows help for this specific task
./alchemy.sh CategoryTypesReport -h
```

# Writing your own tasks

The library was designed to make writing your own tasks easy:

- create a subclass of Task
- make sure it's in the classpath when running the CLI
- use the full name of the class to refer to the task 

For example, if you create a class named edu.college.MyTask and
compile it into mycode.jar, run the CLI app as follows:

```
java -cp mycode.jar -jar target/alchemy-0.1.jar edu.college.MyTask
```

Things to know about the Task lifecycle:

- Do NOT write a constructor or do any initialization in a
  constructor. Instead, override and extend `init()`.
- Tasks should not retain any state. They may be reused at some point
  (although they aren't, currently).

# Using as a Library

To run tasks from your own Java code:

```java
Map<String, List<String>> options = ...
List<String> args = ...

TaskResolver taskResolver = App.createDefaultTaskResolver();
TaskRunner taskRunner = new TaskRunner(taskResolver);
taskRunner.run("CategoryTypesReport", options, args);
```

# How to Build from Source

Install maven and run this command:

```
mvn package
```

This creates a jar in the `target/` directory.

To see which API resources are covered:

```
egrep "Resource: .+" src -r -o --no-filename | sort
```

# Contributing

This project welcomes contributions, especially of tasks that are
broadly useful to the Elements user community at large.
