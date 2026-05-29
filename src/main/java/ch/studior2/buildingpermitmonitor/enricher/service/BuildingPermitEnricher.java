package ch.studior2.buildingpermitmonitor.enricher.service;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitNormalizedEvent;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingQuality;
import ch.studior2.buildingpermitmonitor.contracts.group.KafkaGroupIDs;
import ch.studior2.buildingpermitmonitor.contracts.model.Coordinates;
import ch.studior2.buildingpermitmonitor.contracts.topic.KafkaTopics;
import ch.studior2.buildingpermitmonitor.enricher.config.GeocodingProperties;
import ch.studior2.buildingpermitmonitor.enricher.geocoding.GeocodingClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BuildingPermitEnricher {

  private final GeocodingProperties geocodingProperties;
  private final GeocodingClient geocodingClient;
  private final KafkaTemplate<String, BuildingPermitEnrichedEvent> kafkaTemplate;

  public BuildingPermitEnricher(
      GeocodingProperties geocodingProperties,
      GeocodingClient geocodingClient,
      KafkaTemplate<String, BuildingPermitEnrichedEvent> kafkaTemplate) {
    this.geocodingProperties = geocodingProperties;
    this.geocodingClient = geocodingClient;
    this.kafkaTemplate = kafkaTemplate;
  }

  @KafkaListener(topics = KafkaTopics.NORMALIZED, groupId = KafkaGroupIDs.ENRICHER)
  public void enrich(BuildingPermitNormalizedEvent event) {
    Coordinates coordinates =
        geocodingClient.findCoordinates(event.address(), event.municipality());

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
            coordinates.longitude(),
            geocodingProperties.provider(),
            coordinates.latitude() == null || coordinates.longitude() == null
                ? GeocodingQuality.NOT_FOUND
                : GeocodingQuality.ADDRESS);

    kafkaTemplate.send(KafkaTopics.ENRICHED, event.permitId(), enrichedEvent);
  }
}
