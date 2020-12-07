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

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Profile("flowcontrol")
@SpringBootApplication
@EnableFeignClients
public class SentinelFlowControlApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SentinelFlowControlApplication.class)
            .properties("spring.profiles.active=flowcontrol").web(WebApplicationType.SERVLET)
            .run(args);
    }

    @RestController
    class SentinelController {

        @GetMapping("/springcloud")
        public String springcloud() {
            return "Spring Cloud";
        }

        @GetMapping("/hello")
        public String hello() {
            return "Hello World";
        }

        @GetMapping("/hello/{name}")
        public String get(@PathVariable String name) {
            return "Hello: " + name;
        }

    }

}
