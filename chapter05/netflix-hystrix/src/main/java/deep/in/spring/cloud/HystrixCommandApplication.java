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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Profile("annotation")
@SpringBootApplication
@EnableCircuitBreaker
public class HystrixCommandApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(HystrixCommandApplication.class)
            .properties("spring.profiles.active=annotation").web(WebApplicationType.SERVLET)
            .run(args);
    }

    @RestController
    class HystrixController {

        @HystrixCommand(groupKey = "ControllerGroup", fallbackMethod = "fallback")
        @GetMapping("/exp")
        public String exp() {
            return new RestTemplate().getForObject("https://httpbin.org/status/500", String.class);
        }

        @HystrixCommand(commandKey = "hello", fallbackMethod = "fallback",
            commandProperties = {
                @HystrixProperty(name = "fallback.enabled", value = "false")
            })
        @GetMapping("/hello1")
        public String hello1() {
            return new RestTemplate().getForObject("https://httpbin.org/status/500", String.class);
        }

        public String fallback() {
            return "Hystrix fallback";
        }

    }

}
