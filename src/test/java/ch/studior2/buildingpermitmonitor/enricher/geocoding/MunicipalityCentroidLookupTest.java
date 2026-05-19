package ch.studior2.buildingpermitmonitor.enricher.geocoding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("MunicipalityCentroidLookup")
class MunicipalityCentroidLookupTest {

  private final MunicipalityCentroidLookup lookup = new MunicipalityCentroidLookup();

  @Nested
  @DisplayName("findApproximateCoordinates")
  class FindApproximateCoordinates {

    @ParameterizedTest(name = "{0}")
    @MethodSource("municipalities")
    @DisplayName("should return known coordinates")
    void shouldReturnKnownCoordinates(
        String municipality, Double expectedLatitude, Double expectedLongitude) {
      Coordinates coordinates = lookup.findApproximateCoordinates(municipality);

      assertThat(coordinates.latitude()).isEqualTo(expectedLatitude);
      assertThat(coordinates.longitude()).isEqualTo(expectedLongitude);
    }

    static Stream<Arguments> municipalities() {
      return Stream.of(
          arguments(named("Zürich", "Zürich"), 47.3769, 8.5417),
          arguments(named("Winterthur", "Winterthur"), 47.4988, 8.7237),
          arguments(named("Thalwil", "Thalwil"), 47.2918, 8.5631),
          arguments(named("unknown municipality", "Unknown"), null, null));
    }
  }
}
