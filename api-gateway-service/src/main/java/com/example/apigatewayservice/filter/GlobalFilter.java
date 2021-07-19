package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    @Data
    public static class Config {
        // Put the Configuration properties
        private String baseMessage;
        private boolean PreLogger;
        private boolean PostLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Global Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest serverHttpRequest = exchange.getRequest();
            ServerHttpResponse serverHttpResponse = exchange.getResponse();

            log.info("Global Filter baseMessage: {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("Global Filter start: request id -> {}", serverHttpRequest.getId());
            }
            // Global Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Global Filter end: response code -> {}", serverHttpResponse.getStatusCode());
                }
            }));
        };
    }

}