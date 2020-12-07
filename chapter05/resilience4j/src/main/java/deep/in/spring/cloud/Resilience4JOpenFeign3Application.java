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
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Profile("openfeign3r4j")
@SpringBootApplication
@EnableFeignClients
public class Resilience4JOpenFeign3Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Resilience4JOpenFeign3Application.class)
            .properties("spring.profiles.active=openfeign3r4j").web(WebApplicationType.SERVLET)
            .run(args);
    }

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @Bean
    CommandLineRunner runner() {
        return args -> {
            circuitBreakerRegistry.circuitBreaker("inventory", CircuitBreakerConfig.custom().minimumNumberOfCalls(3)
                .failureRateThreshold(100).build());
        };
    }

    @Profile("openfeign3r4j")
    @FeignClient(name = "inventory-provider")
    public interface InventoryService {

        @GetMapping("/save")
        @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackSave")
        String save();

        default String fallbackSave(Throwable cause) {
            if (cause instanceof CallNotPermittedException) {
                return "fallback by r4j";
            }
            return "biz error: " + cause.getMessage();
        }

    }

    @RestController
    class Resilience4JController {

        @Autowired
        InventoryService inventoryService;

        @GetMapping("/save")
        public String save() {
            return inventoryService.save();
        }

        @GetMapping("/updateConfig")
        public String updateConfig() {
            circuitBreakerRegistry.remove("inventory");
            circuitBreakerRegistry.circuitBreaker("inventory", CircuitBreakerConfig.custom().minimumNumberOfCalls(4)
                .failureRateThreshold(100).build());
            return "ok";
        }

    }

}
