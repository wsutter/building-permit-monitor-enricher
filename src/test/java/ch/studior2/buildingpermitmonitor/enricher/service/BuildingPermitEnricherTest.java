package ch.studior2.buildingpermitmonitor.enricher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitEnrichedEvent;
import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitNormalizedEvent;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingProvider;
import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingQuality;
import ch.studior2.buildingpermitmonitor.contracts.model.Coordinates;
import ch.studior2.buildingpermitmonitor.contracts.topic.KafkaTopics;
import ch.studior2.buildingpermitmonitor.enricher.config.GeocodingProperties;
import ch.studior2.buildingpermitmonitor.enricher.geocoding.GeocodingClient;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

@DisplayName("BuildingPermitEnricher")
class BuildingPermitEnricherTest {

  private final GeocodingProperties properties =
      new GeocodingProperties(
          GeocodingProvider.GEO_ADMIN,
          "https://api3.geo.admin.ch",
          "/rest/services/api/SearchServer",
          Duration.ofSeconds(5),
          "locations",
          "address,parcel",
          4326,
          1);

  private final GeocodingClient geocodingClient = Mockito.mock(GeocodingClient.class);
  private final KafkaTemplate kafkaTemplate = Mockito.mock(KafkaTemplate.class);

  private final BuildingPermitEnricher enricher =
      new BuildingPermitEnricher(properties, geocodingClient, kafkaTemplate);

  @Nested
  @DisplayName("enrich")
  class Enrich {

    @Test
    @DisplayName("should geocode address and publish enriched event")
    void shouldGeocodeAddressAndPublishEnrichedEvent() {
      BuildingPermitNormalizedEvent event = normalizedEvent();

      when(geocodingClient.findCoordinates("Eisenbahnstrasse 27, 8800 Thalwil", "Thalwil"))
          .thenReturn(new Coordinates(47.2918, 8.5631));

      enricher.enrich(event);

      ArgumentCaptor<BuildingPermitEnrichedEvent> eventCaptor =
          ArgumentCaptor.forClass(BuildingPermitEnrichedEvent.class);

      verify(kafkaTemplate).send(eq(KafkaTopics.ENRICHED), eq("kt-zh:123"), eventCaptor.capture());

      BuildingPermitEnrichedEvent enrichedEvent = eventCaptor.getValue();

      assertThat(enrichedEvent.latitude()).isEqualTo(47.2918);
      assertThat(enrichedEvent.longitude()).isEqualTo(8.5631);
      assertThat(enrichedEvent.geocodingProvider()).isEqualTo(GeocodingProvider.GEO_ADMIN);
      assertThat(enrichedEvent.geocodingQuality()).isEqualTo(GeocodingQuality.ADDRESS);
    }
  }

  private static BuildingPermitNormalizedEvent normalizedEvent() {
    return new BuildingPermitNormalizedEvent(
        "kt-zh:123",
        "kt-zh",
        "123",
        "Umbau Wohnung",
        "Umbau Wohnung",
        "RENOVATION",
        "SUBMITTED",
        "Thalwil",
        LocalDate.of(2026, 5, 19),
        "Eisenbahnstrasse 27, 8800 Thalwil");
  }
}
