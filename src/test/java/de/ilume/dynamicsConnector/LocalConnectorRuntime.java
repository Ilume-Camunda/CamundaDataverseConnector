package de.ilume.dynamicsConnector;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class LocalConnectorRuntime {

    public static void main(String[] args) {
        SpringApplication.run(LocalConnectorRuntime.class, args);
    }
}
