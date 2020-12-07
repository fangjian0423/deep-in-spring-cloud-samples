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

import deep.in.spring.cloud.command.CircuitBreakerRestCommand;
import deep.in.spring.cloud.command.HelloWorldCommand;
import deep.in.spring.cloud.command.TimeoutRestCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Profile("raw")
@SpringBootApplication
public class HystrixCircuitBreakerDemo {

    public static void main(String[] args) {
        new SpringApplicationBuilder(HystrixCircuitBreakerDemo.class)
            .properties("spring.profiles.active=raw").web(WebApplicationType.NONE)
            .run(args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                HelloWorldCommand helloWorld1 = new HelloWorldCommand("200");
                HelloWorldCommand helloWorld2 = new HelloWorldCommand("500");
                System.err.println(
                    helloWorld1.execute() + " and Circuit Breaker is " + (helloWorld1.isCircuitBreakerOpen() ? "open"
                        : "closed"));
                System.err.println(
                    helloWorld2.execute() + " and Circuit Breaker is " + (helloWorld2.isCircuitBreakerOpen() ? "open"
                        : "closed"));
                System.err.println("================");
                int num = 1;
                while (num <= 10) {
                    TimeoutRestCommand command = new TimeoutRestCommand(num);
                    System.err.println("Execute " + num + ": " + command.execute() + " and Circuit Breaker is " + (
                        command.isCircuitBreakerOpen() ? "open" : "closed"));
                    num++;
                }
                System.err.println("================");
                num = 1;
                while (num <= 15) {
                    CircuitBreakerRestCommand command = new CircuitBreakerRestCommand("500");
                    System.err.println("Execute " + num + ": " + command.execute() + " and Circuit Breaker is " + (
                        command.isCircuitBreakerOpen() ? "open" : "closed"));
                    num++;
                }
                Thread.sleep(3000L);
                CircuitBreakerRestCommand command = new CircuitBreakerRestCommand("200");
                System.err.println("Execute " + num + ": " + command.execute() + " and Circuit Breaker is " + (
                    command.isCircuitBreakerOpen() ? "open" : "closed"));
            }
        };
    }

}
