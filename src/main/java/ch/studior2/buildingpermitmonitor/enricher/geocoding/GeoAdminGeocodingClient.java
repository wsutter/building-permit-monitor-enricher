package ch.studior2.buildingpermitmonitor.enricher.geocoding;

import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeoAdminQueryParameters;
import ch.studior2.buildingpermitmonitor.contracts.model.Coordinates;
import ch.studior2.buildingpermitmonitor.enricher.config.GeocodingProperties;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GeoAdminGeocodingClient implements GeocodingClient {

  private final GeocodingProperties properties;
  private final WebClient webClient;

  public GeoAdminGeocodingClient(
      WebClient.Builder webClientBuilder, GeocodingProperties properties) {
    this.properties = properties;
    this.webClient = webClientBuilder.baseUrl(properties.baseUrl()).build();
  }

  @Override
  public Coordinates findCoordinates(String address, String municipality) {
    if (address == null || address.isBlank()) {
      return new Coordinates(null, null);
    }

    String searchText =
        municipality == null || municipality.isBlank()
            ? address.trim()
            : address.trim() + ", " + municipality.trim();

    GeoAdminSearchResponse response =
        webClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(properties.searchPath())
                        .queryParam(GeoAdminQueryParameters.TYPE, properties.type())
                        .queryParam(GeoAdminQueryParameters.SEARCH_TEXT, searchText)
                        .queryParam(
                            GeoAdminQueryParameters.SPATIAL_REFERENCE,
                            properties.spatialReference())
                        .queryParam(GeoAdminQueryParameters.ORIGINS, properties.origins())
                        .queryParam(GeoAdminQueryParameters.LIMIT, properties.limit())
                        .build())
            .retrieve()
            .bodyToMono(GeoAdminSearchResponse.class)
            .timeout(properties.timeout())
            .onErrorReturn(new GeoAdminSearchResponse(List.of()))
            .block();

    if (response == null || response.results() == null || response.results().isEmpty()) {
      return new Coordinates(null, null);
    }

    GeoAdminSearchAttributes attrs = response.results().getFirst().attrs();

    if (attrs == null || attrs.lat() == null || attrs.lon() == null) {
      return new Coordinates(null, null);
    }

    return new Coordinates(attrs.lat(), attrs.lon());
  }
}
