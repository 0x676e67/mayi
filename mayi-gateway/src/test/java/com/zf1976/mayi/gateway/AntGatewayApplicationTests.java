package com.zf1976.mayi.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
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

        ClientResponse block = WebClient.create("http://localhost:9000/oauth/check_token")
                                        .get()
                                        .uri(uriBuilder -> {
                                            return uriBuilder.queryParam("token", "hanbi")
                                                             .build();
                                        })
                                        .exchange()
                                        .doOnSuccess(clientResponse -> {
                                            if (clientResponse.statusCode()
                                                              .is2xxSuccessful()) {

                                            }
                                        })
                                        .block(Duration.ofSeconds(5));
        if (block != null && block.statusCode().is2xxSuccessful()) {

        }
    }

}
