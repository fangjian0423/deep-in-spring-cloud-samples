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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class SpringCloudFunctionTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudFunctionTaskApplication.class, args);
    }

    @Bean
    public Supplier<List<String>> supplier() {
        return () -> Arrays.asList("200", "201", "202");
    }

    @Bean
    public Function<List<String>, List<String>> function() {
        return (list) ->
            list.stream().map( item -> "prefix-" + item).collect(Collectors.toList());
    }

    @Bean
    public Consumer<List<String>> consumer() {
        return (list) -> {
            list.stream().forEach(System.out::println);
        };
    }

}
