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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.cloud.task.listener.annotation.BeforeTask;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableTask
public class MyTaskApplication {

    private final Logger logger = LoggerFactory.getLogger(MyTaskApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MyTaskApplication.class, args);
    }

    @BeforeTask
    public void beforeTask(TaskExecution taskExecution) {
        logger.info("BeforeTask Trigger");
    }

    @Bean
    public CommandLineRunner runner1() {
        return (args) -> {
            Thread.sleep(60000L);
            System.out.println("Hello Spring Cloud Task runner1.");
        };
    }

    @Bean
    public CommandLineRunner runner2() {
        return (args) -> {
            System.out.println("Hello Spring Cloud Task runner2.");
        };
    }

    @Bean
    public ApplicationRunner runner3() {
        return (args) -> {
            System.out.println("Hello Spring Cloud Task runner3.");
        };
    }

}
