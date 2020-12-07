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
import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionRegistry;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.config.RoutingFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class SpringCloudFunctionApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext =
            SpringApplication.run(SpringCloudFunctionApplication.class, args);
        applicationContext.getBean("upperCase", Function.class).apply("a"); // A
        FunctionRegistry registry = applicationContext.getBean(FunctionRegistry.class);
        Function<String, String> upperCaseFunc = registry.lookup(Function.class, "upperCase");
        upperCaseFunc.apply("b"); // B
        Function<String, String> lowerCaseFunc = registry.lookup(Function.class, "lowerCase");
        System.out.println(lowerCaseFunc.apply("B")); // b
        System.out.println(registry.getNames(Function.class));
        System.out.println(registry.getNames(Consumer.class));
        Consumer<String> consumer = code -> {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("http://httpbin.org/status/" + code, String.class);
            System.out.println(responseEntity.getStatusCode());
        };
        registry.register(new FunctionRegistration(consumer, "httpFunc").type(FunctionType.of(Consumer.class)));
        System.out.println(applicationContext.getBeansOfType(Function.class));
        Consumer<String> httpFunc = registry.lookup(Consumer.class, "httpFunc");
        httpFunc.accept("200");
        Function routingFunction = registry.lookup(Function.class, RoutingFunction.FUNCTION_NAME);
        routingFunction.apply("routing"); // ROUTING

        Function composite = registry.lookup("upperCase|print");
        composite.apply("hello function"); // HELLO FUNCTION
    }

    @Bean
    public Function<String, String> upperCase() {
        return s -> s.toUpperCase();
    }

    @Bean
    public Function<String, String> lowerCase() {
        return s -> s.toLowerCase();
    }

    @Bean
    public Consumer<String> print() {
        return s -> System.out.println(s);
    }

}
