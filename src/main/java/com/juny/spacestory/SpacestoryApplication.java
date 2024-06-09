package com.juny.spacestory;

import com.juny.spacestory.global.config.EnvConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
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
@OpenAPIDefinition(
    servers = {
      @Server(url = "https://spacestory.duckdns.org", description = "For JINBOKKK"),
      @Server(url = "http://localhost:8080", description = "For JUNYYY")
    })
public class SpacestoryApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpacestoryApplication.class, args);
  }
}
