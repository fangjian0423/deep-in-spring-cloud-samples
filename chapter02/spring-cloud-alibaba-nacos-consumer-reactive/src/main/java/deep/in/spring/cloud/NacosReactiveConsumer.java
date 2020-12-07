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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient(autoRegister = false)
public class NacosReactiveConsumer {

    public static void main(String[] args) {
        SpringApplication.run(NacosReactiveConsumer.class, args);
    }

    @RestController
    class HelloController {

        @Autowired
        private ReactiveDiscoveryClient reactiveDiscoveryClient;

        @Autowired
        private DiscoveryClient discoveryClient;

        private String serviceName = "my-provider";

        @GetMapping("/services")
        public Flux<String> info() {
            return reactiveDiscoveryClient.getServices();
        }

        @GetMapping("/instances")
        public Flux<String> instance() {
            return reactiveDiscoveryClient.getInstances(serviceName).map(instance ->
                "[ serviceId: " + instance.getServiceId() +
                    ", host: " + instance.getHost() +
                    ", port: " + instance.getPort() + " ]");
        }

        @GetMapping("/hello")
        public Mono<String> hello() {
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
            ServiceInstance serviceInstance = serviceInstances.stream()
                .findAny().orElseThrow(() ->
                    new IllegalStateException("no " + serviceName + " instance available"));
            return WebClient.create("http://" +
                serviceInstance.getHost() + ":" + serviceInstance.getPort())
                .get().uri("/echo?name=nacos").retrieve().bodyToMono(String.class);
        }

    }

}
