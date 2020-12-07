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

import java.util.function.Consumer;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixObservableCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.gateway.filter.factory.HystrixGatewayFilterFactory.Config;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableCircuitBreaker
public class HystrixSpringCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixSpringCloudGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator hystrixRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(r -> r.path("/dubbo/**")
                .filters(f -> f.hystrix(new Consumer<Config>() {
                    @Override
                    public void accept(Config config) {
                        config.setSetter(HystrixObservableCommand.Setter
                            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SCGHystrix"))
                            .andCommandKey(HystrixCommandKey.Factory.asKey("my-provider1"))
                            .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter().withExecutionIsolationSemaphoreMaxConcurrentRequests(
                                    0).withExecutionIsolationStrategy(
                                    ExecutionIsolationStrategy.SEMAPHORE).withExecutionTimeoutEnabled(false)));
                    }
                }).stripPrefix(1))
                .uri("lb://my-provider1")
            )
            .route(r -> r.path("/springcloud/**")
                .filters(f -> f.hystrix(new Consumer<Config>() {
                    @Override
                    public void accept(Config config) {
                        config.setSetter(HystrixObservableCommand.Setter
                            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SCGHystrix"))
                            .andCommandKey(HystrixCommandKey.Factory.asKey("my-provider2"))
                            .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(5000)
                                .withMetricsRollingStatisticalWindowInMilliseconds(10000)
                                .withCircuitBreakerRequestVolumeThreshold(15)
                                .withCircuitBreakerErrorThresholdPercentage(50)
                                .withCircuitBreakerSleepWindowInMilliseconds(5000)));
                    }
                }).stripPrefix(1))
                .uri("lb://my-provider2")
            ).route(r -> r.path("/s-c-alibaba/**")
                .filters(f -> f.hystrix(new Consumer<Config>() {
                    @Override
                    public void accept(Config config) {
                        config.setSetter(HystrixObservableCommand.Setter
                            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("SCGHystrix"))
                            .andCommandKey(HystrixCommandKey.Factory.asKey("my-provider3"))
                            .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(5000)
                                    .withMetricsRollingStatisticalWindowInMilliseconds(10000)
                                    .withCircuitBreakerRequestVolumeThreshold(30)
                                    .withCircuitBreakerErrorThresholdPercentage(50)
                                    .withCircuitBreakerSleepWindowInMilliseconds(5000)));
                    }
                }).stripPrefix(1))
                .uri("lb://my-provider3")
            )
            .build();
    }

}
