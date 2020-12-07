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
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableBinding({Source.class})
public class SCSProducerPartitionApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(SCSProducerPartitionApplication.class)
            .web(WebApplicationType.NONE).run(args);
    }

    @Bean
    MyPartitionSelector mySelector() {
        return new MyPartitionSelector();
    }

    @Bean
    MyPartitionKeyExtractor myKeyExtractor() {
        return new MyPartitionKeyExtractor();
    }

    @Autowired
    private Source source;

    @Bean
    public CommandLineRunner runner() {
        return (args) -> {
            //source.output().send(MessageBuilder.withPayload("msg1")
            //    .setHeader("partitionKey", "test")
            //    .build());
            source.output().send(MessageBuilder.withPayload("msg1")
                .setHeader("partitionKey", new User(1, "deep in spring cloud"))
                .build());
        };
    }

    static class User {
        int id;
        String name;

        public User() {
        }

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
