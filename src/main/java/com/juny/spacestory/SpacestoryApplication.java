package com.juny.spacestory;

import com.juny.spacestory.global.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@PropertySource(
    value = {
      "classpath:env.yml",
    },
    factory = EnvConfig.class)
@EnableScheduling
public class SpacestoryApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpacestoryApplication.class, args);
  }
}
