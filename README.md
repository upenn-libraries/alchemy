
# elements-tools

A Java library and command-line interface (CLI) for working with the
Symplectic Elements API.

This project is a work in progress, and is not affiliated with or
supported by Symplectic. Please read the license.

# Installation and Configuration

Install Java 1.8 on your system if it isn't already installed. Any
platform that runs Java (Linux, Windows, Mac) should
work. Theoretically.

Download the most recent .jar file.

Create a file named `elements-tools.properties` with the following:

```
elements_tools.api.url=https://localhost:8090/elements-api/v4.9
elements_tools.api.ignore_cert_mismatch=false
elements_tools.api.username=someuser
elements_tools.api.password=somepassword
```

# How to Run the CLI App

To run a task:
```
java -jar target/elements-tools-0.1-SNAPSHOT.jar Report
```

There is a convenience shell script for Linux and Mac:
```
./elements-tools.sh Report
```

# Existing Tasks

The app comes with several tasks.

# Writing your own tasks

The library was designed to make writing your own tasks easy. In an
nutshell, all you need to do is create a subclass of Task, make sure
it's in the classpath when running Java, and use the full name of the
task when running the CLI program.

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
