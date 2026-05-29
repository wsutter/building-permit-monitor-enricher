package ch.studior2.buildingpermitmonitor.enricher.geocoding;

import static org.assertj.core.api.Assertions.assertThat;

import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingProvider;
import ch.studior2.buildingpermitmonitor.contracts.model.Coordinates;
import ch.studior2.buildingpermitmonitor.enricher.config.GeocodingProperties;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class GeoAdminGeocodingClientTest {

  @Test
  void findCoordinatesReturnsWgs84CoordinatesFromBestResult() {
    AtomicReference<URI> capturedUri = new AtomicReference<>();

    WebClient.Builder webClientBuilder =
        WebClient.builder()
            .exchangeFunction(
                request -> {
                  capturedUri.set(request.url());

                  return Mono.just(
                      ClientResponse.create(HttpStatus.OK)
                          .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                          .body(successResponse())
                          .build());
                });

    GeoAdminGeocodingClient client = new GeoAdminGeocodingClient(webClientBuilder, properties());

    Coordinates coordinates = client.findCoordinates("Alte Landstrasse 1, 8800 Thalwil", "Thalwil");

    assertThat(coordinates.latitude()).isEqualTo(47.2918);
    assertThat(coordinates.longitude()).isEqualTo(8.5631);

    URI uri = capturedUri.get();

    assertThat(uri).isNotNull();
    assertThat(uri.getPath()).isEqualTo("/rest/services/api/SearchServer");
    assertThat(uri.getQuery())
        .contains("type=locations")
        .contains("sr=4326")
        .contains("origins=address,parcel")
        .contains("limit=1")
        .contains("searchText=");
  }

  @Test
  void findCoordinatesReturnsNullCoordinatesWhenAddressIsBlank() {
    GeoAdminGeocodingClient client = new GeoAdminGeocodingClient(WebClient.builder(), properties());

    Coordinates coordinates = client.findCoordinates(" ", "Thalwil");

    assertThat(coordinates.latitude()).isNull();
    assertThat(coordinates.longitude()).isNull();
  }

  @Test
  void findCoordinatesReturnsNullCoordinatesWhenNoResultExists() {
    WebClient.Builder webClientBuilder =
        WebClient.builder()
            .exchangeFunction(
                _ ->
                    Mono.just(
                        ClientResponse.create(HttpStatus.OK)
                            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .body("{\"results\":[]}")
                            .build()));

    GeoAdminGeocodingClient client = new GeoAdminGeocodingClient(webClientBuilder, properties());

    Coordinates coordinates = client.findCoordinates("Unbekannte Adresse", "Thalwil");

    assertThat(coordinates.latitude()).isNull();
    assertThat(coordinates.longitude()).isNull();
  }

  private static GeocodingProperties properties() {
    return new GeocodingProperties(
        GeocodingProvider.GEO_ADMIN,
        "https://api3.geo.admin.ch",
        "/rest/services/api/SearchServer",
        Duration.ofSeconds(5),
        "locations",
        "address,parcel",
        4326,
        1);
  }

  private static String successResponse() {
    return """
    {
      "results": [
        {
          "attrs": {
            "lat": 47.2918,
            "lon": 8.5631
          }
        }
      ]
    }
    """;
  }
}
