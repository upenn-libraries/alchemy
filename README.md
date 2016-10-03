
# alchemy

A Java library and command-line tool for the
[Symplectic Elements](http://symplectic.co.uk/products/elements/) API and Reporting Database.

This project is not affiliated with or supported by Symplectic.

# Features and Uses

alchemy is meant to be an extensible swiss army knife for working with
Elements. It aspires to provide:

- A collection of pre-packaged, ready-to-run tasks broadly useful to
  Elements users
- A simple unified platform for scripts and sundry (vs scattered ad
  hoc scripts), eliminating a lot of boilerplate
- A way to do application configuration management (export system state to files)
- A way to do testing and verification

# Status

This is early 'alpha' code. What exists should mostly work. Holes need
to be filled in. Expect things to change.

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

Examples of how to run the CLI:

```
# run using Java
java -cp "lib/*" edu.upenn.library.elements.App CategoryTypesReport publications pubtypes.csv

# use the convenience script for Linux or Mac
./alchemy.sh CategoryTypesReport publications pubtypes.csv

# shows a list of program arguments and available tasks
./alchemy.sh -h

# shows help for a specific task
./alchemy.sh CategoryTypesReport -h

# use different config file
./alchemy.sh -c production.properties CategoryTypesReport publications pubtypes.csv

# run the task using the 'prod' api (as defined in alchemy.properties) instead of default 'dev'
./alchemy.sh -e prod CategoryTypesReport publications pubtypes.csv
```

# Writing your own tasks

Steps:

- create a subclass of Task
- make sure it's in the classpath when running the CLI
- use the full name of the class to refer to the task 

For example, you might create a class named edu.college.MyTask,
compile it into mycode.jar, copy it into the `lib/` directory of
alchemy, then run the CLI app as follows:

```
# java invocation
java -cp "lib/*" edu.upenn.library.elements.App edu.college.MyTask

# or, using the script
./alchemy edu.college.MyTask
```

Making API calls in Tasks looks like this:

```java
Resource apiResource = new Categories(Category.PUBLICATION);
apiResource.setParam("groups", "12");
Feed feed = getApi().getFeed(apiResource);
for(FeedEntry entry : feed.getEntries()) {
    // ...
}
```

Things to know about the Task lifecycle:

- Do NOT do any initialization in a constructor. Instead, 
  override and extend `init()`.
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
