package ch.studior2.buildingpermitmonitor.enricher.geocoding;

import ch.studior2.buildingpermitmonitor.contracts.model.Coordinates;

public interface GeocodingClient {
  Coordinates findCoordinates(String address, String municipality);
}
