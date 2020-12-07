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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.vavr.control.Try;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class R4jBulkhead {

    public static void main(String[] args) {
        BulkheadConfig config = BulkheadConfig.custom()
            .maxConcurrentCalls(1)
            .maxWaitDuration(Duration.ofMillis(200))
            .build();

        Bulkhead bulkhead = Bulkhead.of("httpbin", config);

        RestTemplate restTemplate = new RestTemplate();

        Supplier<String> decoratedSupplier = Bulkhead
            .decorateSupplier(bulkhead, () -> {
                return restTemplate.
                    getForEntity("http://httpbin.org/delay/2", String.class).
                    getStatusCode().toString();
            });

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Try<String> execute = Try.ofSupplier(decoratedSupplier);
                System.out.println(execute.isSuccess() + ": " + (execute.isSuccess() ? execute.get() : execute.getCause()));
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Try<String> execute = Try.ofSupplier(decoratedSupplier);
                System.out.println(execute.isSuccess() + ": " + (execute.isSuccess() ? execute.get() : execute.getCause()));
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Try<String> execute = Try.ofSupplier(decoratedSupplier);
                System.out.println(execute.isSuccess() + ": " + (execute.isSuccess() ? execute.get() : execute.getCause()));
            }
        });

        executorService.shutdown();

    }

}
