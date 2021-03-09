package com.zf1976.ant.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Objects;

@SpringBootTest
class AntGatewayApplicationTests {

    @Test
    void contextLoads() {
        HttpStatus httpStatus = Objects.requireNonNull(WebClient.create()
                                                                .get()
                                                                .uri("http://localhost:9000/oauth/check_token", uriBuilder -> uriBuilder.queryParam("token", "fucking")
                                                                                                                                        .build())
                                                                .exchange()
                                                                .block(Duration.ofSeconds(10)))
                                       .statusCode();
        if (!httpStatus.is2xxSuccessful()) {
            System.out.println("error");
        }


    }

}
