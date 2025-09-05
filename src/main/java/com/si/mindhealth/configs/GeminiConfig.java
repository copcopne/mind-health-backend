package com.si.mindhealth.configs;

import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(GeminiProperties.class)
public class GeminiConfig {

  @Bean
  WebClient geminiWebClient(GeminiProperties props) {

    var strategies = ExchangeStrategies.builder()
        .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
        .build();

    HttpClient httpClient = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(30));

    return WebClient.builder()
        .baseUrl(props.baseUrl())
        .exchangeStrategies(strategies)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
