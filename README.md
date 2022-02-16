# NODE-RED Testcontainers

A [Testcontainers](https://www.testcontainers.org/) implementation for [NODE-RED](https://nodered.org/).

![](https://img.shields.io/github/license/jsoladur/node-red-testcontainers?label=License)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jsoladur/node-red-testcontainers.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.jsoladur/node-red-testcontainers)

## Getting Started

_The `@Container` annotation used here in the readme is from the JUnit 5 support of Testcontainers.
Please refer to the Testcontainers documentation for more information._

Simply spin up a default NODE-RED instance:

```java
@Container
static final NodeRedContainer nodeRedContainer = new NodeRedContainer();
```

Use another NODE-RED Docker image/version than used in this Testcontainers:

```java
@Container
static final  NodeRedContainer nodeRedContainer = new NodeRedContainer("nodered/node-red:2.1.0");
```

Power up a NODE-RED instance with existing `flows.json` and `flows_cred.json` file (from classpath):

```java
@Container
static final NodeRedContainer nodeRedContainer = new NodeRedContainer()
        .withFlowsJson("flows_jsonplaceholder_posts.json")
        .withFlowsCredJson("flows_cred.json")
        .withNodeRedCredentialSecret("MY_NODE_RED_CREDENTIAL_SECRET");
```

You can obtain several properties form the NODE-RED container:

```java
String endpoint = nodeRedContainer.getNodeRedUrl();
```
See also [`NodeRedContainerTest`](./src/test/java/io/github/jsoladur/nodered/NodeRedContainerTest.java) class and the other integration tests.

## Installation

The release versions of this project are available at [Maven Central](https://search.maven.org/artifact/io.github.jsoladur/node-red-testcontainers).
Simply put the dependency coordinates to your `pom.xml` (or something similar, if you use e.g. Gradle or something else):

```xml
<dependency>
  <groupId>io.github.jsoladur</groupId>
  <artifactId>node-red-testcontainers</artifactId>
  <version>${node-red-testcontainers.version}</version>
  <scope>test</scope>
</dependency>
```

## License

MIT License

Copyright (c) 2022 José María Sola Durán

See [LICENSE](LICENSE) file for details.

## Acknowledgments

Many thanks to the creators and maintainers of [NODE-RED](https://nodered.org/).
You do an awesome job!

Thanks a lot to [@dasniko](https://github.com/dasniko) for some inspiration for this project.