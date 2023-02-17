package dev.nincodedo.nincord;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "dev.nincodedo.nincord")
@EntityScan(basePackages = "dev.nincodedo.nincord")
public class NincordConfiguration {
}
