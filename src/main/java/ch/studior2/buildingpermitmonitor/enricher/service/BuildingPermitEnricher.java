package ch.studior2.buildingpermitmonitor.enricher.service;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitNormalizedEvent;
import ch.studior2.buildingpermitmonitor.contracts.topic.KafkaTopics;
import ch.studior2.buildingpermitmonitor.enricher.geocoding.Coordinates;
import ch.studior2.buildingpermitmonitor.enricher.geocoding.MunicipalityCentroidLookup;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BuildingPermitEnricher {

  private final MunicipalityCentroidLookup centroidLookup;
  private final KafkaTemplate<String, BuildingPermitEnrichedEvent> kafkaTemplate;

  public BuildingPermitEnricher(
      MunicipalityCentroidLookup centroidLookup,
      KafkaTemplate<String, BuildingPermitEnrichedEvent> kafkaTemplate) {
    this.centroidLookup = centroidLookup;
    this.kafkaTemplate = kafkaTemplate;
  }

  @KafkaListener(topics = KafkaTopics.NORMALIZED, groupId = "enricher")
  public void enrich(BuildingPermitNormalizedEvent event) {
    Coordinates coordinates = centroidLookup.findApproximateCoordinates(event.municipality());

    BuildingPermitEnrichedEvent enrichedEvent =
        new BuildingPermitEnrichedEvent(
            event.permitId(),
            event.source(),
            event.externalId(),
            event.title(),
            event.description(),
            event.category(),
            event.status(),
            event.municipality(),
            event.publishedDate(),
            event.address(),
            coordinates.latitude(),
            coordinates.longitude());

    kafkaTemplate.send(KafkaTopics.ENRICHED, event.permitId(), enrichedEvent);
  }
}
