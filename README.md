# =barkoder=
a minimalistic WMS system

[![Build Status](https://travis-ci.org/maslick/barkoder.svg?branch=master)](https://travis-ci.org/maslick/barkoder)
[ ![Download](https://api.bintray.com/packages/maslick/maven/barkoder/images/download.svg) ](https://bintray.com/maslick/maven/barkoder/_latestVersion)

![barkoder architecture](barkoder.png)

## Features
* simple REST API
* OAuth2.0 by Keycloak
* written in Kotlin
* leverages SpringBoot v2

## Installation
```
$ git clone https://github.com/maslick/barkoder.git
$ ./gradlew clean build
```

or simply download the artifact from ``bintray``:
```
$ wget -O barkoder-0.1.jar https://bintray.com/maslick/maven/download_file?file_path=io/maslick/barkoder/0.1/barkoder-0.1.jar
```

## Usage
Create the file ``application.properties`` and put in the same directory as the jar:
```
# Db
spring.datasource.platform=postgres
spring.datasource.url=jdbc:postgresql://${PGHOST}:5432/${PGDATABASE}?sslmode=disable
spring.datasource.username=${PGUSER}
spring.datasource.password=${PGPASSWORD}

spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults=false
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL9Dialect
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update

# OAuth2.0
keycloak.auth-server-url=${KEYCLOAK_URL}:${KEYCLOAK_PORT}/auth
keycloak.resource=${CLIENT_ID}
keycloak.realm=${REALM_NAME}
keycloak.bearer-only=true
keycloak.ssl-required=external
keycloak.credentials.secret=${KEYCLOAK_CLIENT_SERCET}
keycloak.cors=true
keycloak.enabled=true

keycloak.securityConstraints[0].securityCollections[0].name=secured resource
keycloak.securityConstraints[0].authRoles[0]=${ROLE}
keycloak.securityConstraints[0].securityCollections[0].patterns[0]=/*
```
Run the service:
```
$ java -jar barkoder-0.1.jar
```

## API
* List all Items: ``GET /items``
* Get an Item by its id: ``GET /item/{id}``
* Get an Item by its barcode: ``GET /barcode/{barcode}``
* Add new Item: ``POST /item``
* Update an Item: ``PUT /item``
* Delete an Item by its id: ``DELETE /item/{id}``
* Delete an Item by its barcode: ``DELETE /barcode/{barcode}``

For the payload use the ``application/json`` MIME type (UTF-8).

If the REST API is secured with ``Keycloak``, an ``Authorization`` header should be present in each request:
```
"Authorization" : "bearer ${KEYCLOAK_TOKEN}"
```

## Entity
```
{
  "id": 1
  "title": "Union Lager",
  "category": "beer",
  "description": "Full flavour, medium bitterness",
  "barcode": "5901234123457",
  "quantity": 1
}
```
