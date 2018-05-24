package eu.nimble.service.datachannel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableCircuitBreaker
@EnableAutoConfiguration
@EnableEurekaClient
@EnableFeignClients
@RestController
@SpringBootApplication
@EnableSwagger2
public class DataChannelServiceApplication {
    public static void main(String[] args) {
        new SpringApplication(DataChannelServiceApplication.class).run(args);
    }
}
