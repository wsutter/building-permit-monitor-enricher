package ch.studior2.buildingpermitmonitor.enricher.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GeocodingProperties.class)
public class GeocodingConfiguration {}
