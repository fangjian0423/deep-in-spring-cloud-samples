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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class JavaFunctionApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext =
            SpringApplication.run(JavaFunctionApplication.class, args);
        System.out.println(applicationContext.getBean("upperCase", Function.class));
        System.out.println(applicationContext.getBean("upperCase", Function.class).apply("a"));
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
