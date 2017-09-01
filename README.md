# Simple Client for CX Services

## Introduction
This is an simple Cytoscape App to call
Simple client for CX-based services

## How to Build
This app is not ready for the App Store, and you need to build it by yourself.  To build this Cytoscape app, you need the following:

- Latest version of JDK 8 (Oracle's one is recommended)
- Apache Maven 3

1. ```git clone https://github.com/idekerlab/cx-service-client.git```
1. ```cd cx-service-client```
1. ```mvn clean install```
1. Now you can install the jar file from Cytoscape's App menu (install from file)

## How to use

This app adds two menu items:

- Layout &rarr; Call remote layout...
- Tools &rarr; Call remote attribute service...

### Parameters

#### URL
In this version, you need to pass all of the parameters as an URL.  This means this URL parameter is the only path to send optional parameters to CI services.

##### Example
* ```http://localhost:3000?prog=dot```

(TBD)


----

&copy; UC, San Diego Dept of Medicine

Keiichiro Ono
