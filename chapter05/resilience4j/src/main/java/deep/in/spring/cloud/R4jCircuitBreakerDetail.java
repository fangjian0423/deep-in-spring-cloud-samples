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

import java.time.Duration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.control.Try;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class R4jCircuitBreakerDetail {

    public static void main(String[] args) {
        //testFailureRateThresholdInMinimumNumberOfCalls();
        //testIgnoreException();
        //testSlowCallRateThresholdInMinimumNumberOfCalls();
        waitDurationInOpenState();
    }

    private static void testFailureRateThresholdInMinimumNumberOfCalls() {
        CircuitBreaker circuitBreaker = CircuitBreaker.of("httpbin",
            CircuitBreakerConfig.custom().
                minimumNumberOfCalls(10).
                failureRateThreshold(20).
                build());

        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 10; i++) {
            String result = Try.ofSupplier(CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return restTemplate.
                        getForEntity("http://httpbin.org/status/500", String.class).
                        getStatusCode().toString();
                })).recover(throwable -> "fallback: " + throwable.getMessage()).get();
            System.out.println(result);
        }
    }

    private static void testIgnoreException() {
        CircuitBreaker circuitBreaker = CircuitBreaker.of("httpbin",
            CircuitBreakerConfig.custom().
                minimumNumberOfCalls(10).
                failureRateThreshold(20).
                //ignoreExceptions(HttpServerErrorException.InternalServerError.class).
                ignoreException( throwable -> {
                    if(throwable instanceof HttpServerErrorException.InternalServerError) {
                        return true;
                    }
                    return false;
                }).
                build());

        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 11; i++) {
            String result = Try.ofSupplier(CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return restTemplate.
                        getForEntity("http://httpbin.org/status/500", String.class).
                        getStatusCode().toString();
                })).recover(throwable -> "fallback: " + throwable.getMessage()).get();
            System.out.println(result);
        }
    }

    private static void testSlowCallRateThresholdInMinimumNumberOfCalls() {
        CircuitBreaker circuitBreaker = CircuitBreaker.of("httpbin",
            CircuitBreakerConfig.custom().
                minimumNumberOfCalls(2).
                slowCallRateThreshold(100).
                slowCallDurationThreshold(Duration.ofSeconds(2)).
                build());

        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 5; i++) {
            String result = Try.ofSupplier(CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return restTemplate.
                        getForEntity("http://httpbin.org/delay/3", String.class).
                        getStatusCode().toString();
                })).recover(throwable -> "fallback: " + throwable.getMessage()).get();
            System.out.println(result);
        }
    }

    private static void waitDurationInOpenState() {
        CircuitBreaker circuitBreaker = CircuitBreaker.of("httpbin",
            CircuitBreakerConfig.custom().
                minimumNumberOfCalls(4).
                failureRateThreshold(100).
                waitDurationInOpenState(Duration.ofSeconds(10)). // 10 秒后进入 half-open 状态
                build());

        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 6; i++) {
            String result = Try.ofSupplier(CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return restTemplate.
                        getForEntity("http://httpbin.org/status/500", String.class).
                        getStatusCode().toString();
                })).recover(throwable -> "fallback: " + throwable.getMessage()).get();
            System.out.println(result);
        }

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 3; i++) {
            String result = Try.ofSupplier(CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return restTemplate.
                        getForEntity("http://httpbin.org/status/500", String.class).
                        getStatusCode().toString();
                })).recover(throwable -> "fallback: " + throwable.getMessage()).get();
            System.out.println(result);
        }
    }

}
