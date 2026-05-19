package ch.studior2.buildingpermitmonitor.enricher.geocoding;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MunicipalityCentroidLookup {

  private static final Map<String, Coordinates> CENTROIDS =
      Map.of(
          "zürich", new Coordinates(47.3769, 8.5417),
          "zurich", new Coordinates(47.3769, 8.5417),
          "winterthur", new Coordinates(47.4988, 8.7237),
          "thalwil", new Coordinates(47.2918, 8.5631));

  public Coordinates findApproximateCoordinates(String municipality) {
    if (municipality == null || municipality.isBlank()) {
      return new Coordinates(null, null);
    }

    return CENTROIDS.getOrDefault(municipality.toLowerCase(), new Coordinates(null, null));
  }
}
