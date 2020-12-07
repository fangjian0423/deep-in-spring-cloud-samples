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

import java.util.function.Supplier;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.control.Try;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class R4jCircuitBreaker {

    public static void main(String[] args) {
        CircuitBreaker circuitBreaker = CircuitBreaker.of("httpbin",
            CircuitBreakerConfig.custom().failureRateThreshold(50).minimumNumberOfCalls(2).build());

        RestTemplate restTemplate = new RestTemplate();

        Supplier<String> decoratedSupplier1 = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> {
                return restTemplate.
                    getForEntity("http://httpbin.org/status/500", String.class).
                    getStatusCode().toString();
            });

        Supplier<String> decoratedSupplier2 = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> {
                return restTemplate.
                    getForEntity("http://httpbin.org/status/200", String.class).
                    getStatusCode().toString();
            });

        String result1 = Try.ofSupplier(decoratedSupplier1)
            .recover(throwable -> "Hello from Recovery1: " + throwable.getMessage()).get();

        System.out.println(result1);

        String result2 = Try.ofSupplier(decoratedSupplier2)
            .recover(throwable -> "Hello from Recovery2" + throwable.getMessage()).get();

        System.out.println(result2);

        String result3 = Try.ofSupplier(decoratedSupplier2)
            .recover(throwable -> "Hello from Recovery3" + throwable.getMessage()).get();

        System.out.println(result3);

    }

}
