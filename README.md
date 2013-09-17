# RXInvoice

A sample for restx.io featuring jongo integration, security best practices, spec tests, validation, ...

Source code should be pretty easy to read, check out the src/main/java directory.

## Running

Pre requisites:

- Java 7
- Git (or download via zip)
- Restx shell with core plugin installed

Here is the most simple way to try this app:
```
git clone git@github.com:xhanin/rxinvoice.git && cd rxinvoice && restx deps install + app run
```

If you downloaded an unzipped the sources:
```
restx deps install + app run
```

When dependencies are already installed:
```
restx app run
```

Production mode:
```
restx app run --mode=prod --quiet
```

## Accessing

open http://localhost:8080/api/@/ui/api-docs/#/

and play with REST endpoints.

## Build and tests

Test & Packaging with [Easyant](http://ant.apache.org/easyant/):
```
easyant test package
```

Test & Packaging with [Maven](http://maven.apache.org/):
```
mvn package
```

## Open in IDE

Simply import as Maven project.

With IntellJ command line installed, you can just do `idea pom.xml` from the command line.

Check [restx IDE support doc](http://restx.io/docs/ide.html) for details.
