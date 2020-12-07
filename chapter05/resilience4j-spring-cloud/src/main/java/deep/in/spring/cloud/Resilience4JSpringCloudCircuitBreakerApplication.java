/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package deep.in.spring.cloud;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class Resilience4JSpringCloudCircuitBreakerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Resilience4JSpringCloudCircuitBreakerApplication.class, args);
    }

    @RestController
    class Resilience4JController {
        @Autowired
        CircuitBreakerFactory circuitBreakerFactory;

        @Autowired
        RestTemplate restTemplate;

        @GetMapping("/exp")
        public String exp() {
            StringBuilder sb = new StringBuilder();
            CircuitBreaker cb = circuitBreakerFactory.create("temp");
            String url = "https://httpbin.org/status/500";
            for (int index = 0; index < 10; index++) {
                String httpResult = cb.run(() -> {
                    return restTemplate.getForObject(url, String.class);
                }, throwable -> {
                    if (throwable instanceof CallNotPermittedException) {
                        return "circuit break by resilience4j";
                    }
                    return "exception occurs with url: " + url;
                });
                sb.append(httpResult).append("<br/>");
            }
            return sb.toString();
        }

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> customizer() {
        return factory -> {
            factory.configureDefault(id -> {
                    Resilience4JCircuitBreakerConfiguration configuration
                        = new Resilience4JCircuitBreakerConfiguration();
                    configuration.setCircuitBreakerConfig(
                        CircuitBreakerConfig.custom().minimumNumberOfCalls(3).failureRateThreshold(100).build()
                    );
                    configuration.setTimeLimiterConfig(TimeLimiterConfig.ofDefaults());
                    return configuration;
                }
            );
        };
    }

}
