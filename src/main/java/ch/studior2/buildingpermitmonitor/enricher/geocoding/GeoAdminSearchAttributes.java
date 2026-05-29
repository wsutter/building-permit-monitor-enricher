package ch.studior2.buildingpermitmonitor.enricher.geocoding;

import java.util.Map;

public record GeoAdminSearchAttributes(
    Double lat,
    Double lon,
    String origin,
    Integer rank,
    String label,
    Map<String, Object> detail) {}
