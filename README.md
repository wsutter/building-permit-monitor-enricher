# Enricher Module

## Purpose

The Enricher enhances normalized permits with additional information obtained from external services.

Currently, the service performs geocoding using the Swiss GeoAdmin API.

## Responsibilities

### Event Consumption

Consumes:

```text
building-permit.normalized
```

using:

```java
KafkaGroupIDs.ENRICHER
```

### Geocoding

The service queries the Swiss GeoAdmin API to determine geographic coordinates.

Returned coordinates:

* WGS84 Latitude
* WGS84 Longitude

These coordinates can be directly used by:

* Leaflet
* OpenStreetMap
* PostGIS

### Event Publishing

Produces:

```text
building-permit.enriched
```

using:

```java
BuildingPermitEnrichedEvent
```

### Error Handling

Failed messages are routed to:

```text
building-permit.normalized.dlq
```

## Event Flow

```text
building-permit.normalized
            ↓
         Enricher
            ↓
building-permit.enriched
```

## Technologies / Frameworks

* Java 25
* Spring Boot 4
* Spring Kafka
* Spring WebFlux
* Reactor

## Startup

```bash
mvn spring-boot:run -pl enricher
```
