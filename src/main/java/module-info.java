module ch.studior2.buildingpermitmonitor.enricher {
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.kafka;
  requires kafka.clients;
  requires ch.studior2.buildingpermitmonitor.contracts;
  requires spring.webflux;
  requires reactor.core;
  requires spring.web;

  opens ch.studior2.buildingpermitmonitor.enricher to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.enricher.config to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.enricher.geocoding to
      spring.core,
      spring.beans,
      spring.context,
      com.fasterxml.jackson.databind,
      tools.jackson.databind;
  opens ch.studior2.buildingpermitmonitor.enricher.service to
      spring.core,
      spring.beans,
      spring.context;
}
