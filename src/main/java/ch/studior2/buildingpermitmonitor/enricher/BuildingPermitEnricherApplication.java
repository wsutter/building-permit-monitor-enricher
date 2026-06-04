package ch.studior2.buildingpermitmonitor.enricher;

import ch.studior2.buildingpermitmonitor.contracts.config.KafkaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(KafkaConfig.class)
@SpringBootApplication
public class BuildingPermitEnricherApplication {

  public static void main(String[] args) {
    SpringApplication.run(BuildingPermitEnricherApplication.class, args);
  }
}
