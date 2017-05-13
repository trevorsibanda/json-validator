# json-validator

[![CircleCI](https://circleci.com/gh/trevorsibanda/json-validator.svg?style=svg)](https://circleci.com/gh/trevorsibanda/json-validator)

A simple REST service in Scala for validating JSON documents against JSON schema

# Endpoints
```
POST    /schema/SCHEMAID        - Upload JSON Schema with unique `SCHEMAID`
GET     /schema/SCHEMAID        - Download JSON Schema with unique `SCHEMAID`

POST    /validate/SCHEMAID      - Validate JSON document against the JSON Schema identified by `SCHEMAID`
```

# Setup


```sh 
git clone https://github.com/trevorsibanda/json-validator
cd json-validator
sbt run
```
