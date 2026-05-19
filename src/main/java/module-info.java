module ch.studior2.buildingpermitmonitor.enricher {
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.kafka;
  requires kafka.clients;
  requires ch.studior2.buildingpermitmonitor.contracts;

  opens ch.studior2.buildingpermitmonitor.enricher to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.enricher.geocoding to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.enricher.service to
      spring.core,
      spring.beans,
      spring.context;
}
