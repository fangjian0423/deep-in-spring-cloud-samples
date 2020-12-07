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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableDiscoveryClient(autoRegister = false)
//@LoadBalancerClients(defaultConfiguration = MyLoadBalancerConfiguration.class)
public class NacosConsumer4SCLB {

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumer4SCLB.class, args);
    }

    @Configuration
    @LoadBalancerClient(name = "nacos-provider-lb", configuration = MyLoadBalancerConfiguration.class)
    class LoadBalanceConfiguration {

    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate normalRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RandomServiceInstanceChooser randomServiceInstanceChooser(DiscoveryClient discoveryClient) {
        return new RandomServiceInstanceChooser(discoveryClient);
    }

    @RestController
    class HelloController {

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private RestTemplate normalRestTemplate;

        @Autowired
        private RandomServiceInstanceChooser randomServiceInstanceChooser;

        private String serviceName = "nacos-provider-lb";

        @GetMapping("/echo")
        public String echo() {
            return restTemplate.getForObject("http://" + serviceName + "/", String.class);
        }

        @GetMapping("/customChooser")
        public String customChooser() {
            ServiceInstance serviceInstance = randomServiceInstanceChooser.choose(serviceName);
            return normalRestTemplate.getForObject(
                "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/", String.class);
        }

    }

}
