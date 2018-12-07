# =barkoder=
a minimalistic WMS system

[![Build Status](https://travis-ci.org/maslick/barkoder.svg?branch=master)](https://travis-ci.org/maslick/barkoder)


## Features
* simple REST API
* OAuth2.0 by Keycloak
* written in Kotlin
* leverages SpringBoot v2


## API
* List all Items: ``GET /items``
* Get an Item by its id: ``GET /item/{id}``
* Get an Item by its barcode: ``GET /barcode/{barcode}``
* Add new Item: ``POST /item``
* Update an Item: ``PUT /item``
* Delete an Item by its id: ``DELETE /item/{id}``
* Delete an Item by its barcode: ``DELETE /barcode/{barcode}``

## Entity
All requests shall use the ``application/json`` MIME type (UTF-8).

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
