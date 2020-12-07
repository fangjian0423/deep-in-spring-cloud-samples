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

import java.util.Collections;
import java.util.Map;

import com.alibaba.cloud.circuitbreaker.sentinel.SentinelCircuitBreakerFactory;
import com.alibaba.cloud.circuitbreaker.sentinel.SentinelConfigBuilder;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Profile("sccb")
@SpringBootApplication
public class SentinelSpringCloudCircuitBreakerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SentinelSpringCloudCircuitBreakerApplication.class)
            .properties("spring.profiles.active=sccb").web(WebApplicationType.SERVLET)
            .run(args);
    }

    @RestController
    class SentinelController {
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
                    if (throwable instanceof DegradeException) {
                        return "degrade by sentinel";
                    }
                    return "exception occurs with url: " + url;
                });
                sb.append(httpResult).append("<br/>");
            }
            return sb.toString();
        }

        @GetMapping("/rt")
        public String rt() {
            StringBuilder sb = new StringBuilder();
            CircuitBreaker cb = circuitBreakerFactory.create("rt");
            String url = "https://httpbin.org/delay/3";
            for (int index = 0; index < 10; index++) {
                String httpResult = cb.run(() -> {
                    Map<String, Object> map = restTemplate.getForObject(url, Map.class);
                    return (String)map.get("origin");
                }, throwable -> {
                    if (throwable instanceof DegradeException) {
                        return "degrade by sentinel";
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
    public Customizer<SentinelCircuitBreakerFactory> customizer() {
        return factory -> {
            factory.configureDefault(id -> new SentinelConfigBuilder()
                .resourceName(id)
                .rules(Collections.singletonList(new DegradeRule(id)
                    .setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT)
                    .setCount(3).setTimeWindow(10)))
                .build());
            factory.configure(builder -> {
                builder
                    .rules(Collections.singletonList(new DegradeRule("slow")
                        .setGrade(RuleConstant.DEGRADE_GRADE_RT).setCount(100)
                        .setTimeWindow(5)));
            }, "rt");
        };
    }

}
