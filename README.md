# =barkoder=
a minimalistic WMS system

[![Build Status](https://travis-ci.org/maslick/barkoder.svg?branch=master)](https://travis-ci.org/maslick/barkoder)
[![Maintainability](https://api.codeclimate.com/v1/badges/22cf9e7940d43e7e8f16/maintainability)](https://codeclimate.com/github/maslick/barkoder/maintainability)
[![codecov](https://codecov.io/gh/maslick/barkoder/branch/master/graph/badge.svg)](https://codecov.io/gh/maslick/barkoder)
[ ![Download](https://api.bintray.com/packages/maslick/maven/barkoder/images/download.svg) ](https://bintray.com/maslick/maven/barkoder/_latestVersion)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)



![barkoder architecture](barkoder.png)

## Features
* simple REST API
* OAuth2.0 by Keycloak (optional)
* written in Kotlin
* leverages SpringBoot v2
* Android native [client](https://github.com/maslick/kodermobilj)
* Web [client](https://github.com/maslick/barkoder-ui)

## Installation
```
$ git clone https://github.com/maslick/barkoder.git
$ ./gradlew clean build
```

or simply download the artifact from ``bintray``:
```
$ wget -O barkoder-0.3.jar https://bintray.com/maslick/maven/download_file?file_path=io/maslick/barkoder/0.3/barkoder-0.3.jar
```

## Usage
Create the file ``application.properties`` and put it in the same directory as the jar:
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
keycloak.auth-server-url=${KCHOST}/auth
keycloak.resource=${CLIENT}
keycloak.realm=${REALM}
keycloak.bearer-only=true
keycloak.ssl-required=external
keycloak.credentials.secret=${CLIENT_SECRET}
keycloak.cors=true
keycloak.enabled=${KC_ENABLED:false}

keycloak.securityConstraints[0].securityCollections[0].name=secured stuff
keycloak.securityConstraints[0].authRoles[0]=${CLIENT_ROLE}
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
* Add multiple Items: ``POST /items``
* Update an Item: ``PUT /item``
* Delete an Item by its id: ``DELETE /item/{id}``
* Delete an Item by its barcode: ``DELETE /barcode/{barcode}``

For the payload use the ``application/json`` MIME type (UTF-8).

If the REST API is secured with ``Keycloak``, an ``Authorization`` header should be present in each request:
```
"Authorization" : "bearer ${KEYCLOAK_TOKEN}"
```

## Entity
``Item``:
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

``Response``:
```
{
  "status": "OK",
  "errorMessage": null
}
```

## Docker-compose
This [configuration](deployment/docker-compose.yml) contains three containers: frontend, backend, database. Edit environment variables according to your setup and then run: 

```bash
cd deployment
docker-compose up -d 
```

