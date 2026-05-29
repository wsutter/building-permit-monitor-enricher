package ch.studior2.buildingpermitmonitor.enricher.config;

import ch.studior2.buildingpermitmonitor.contracts.geocoding.GeocodingProvider;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "building-permit.geocoding")
public record GeocodingProperties(
    GeocodingProvider provider,
    String baseUrl,
    String searchPath,
    Duration timeout,
    String type,
    String origins,
    Integer spatialReference,
    Integer limit) {}
