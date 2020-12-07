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

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableBinding({Source.class, Sink.class})
public class SCSRetryApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(SCSRetryApplication.class)
            .web(WebApplicationType.NONE).run(args);
    }

    @Service
    class SCSRetryReceiveService {

        private AtomicInteger count = new AtomicInteger(1);

        @StreamListener(Sink.INPUT)
        public void receiveOrderlyMsg(String receiveMsg) {
            System.out.println("invoke: " + count.get());
            if (count.getAndIncrement() <= 6) {
                throw new RuntimeException("Oops: " + receiveMsg);
            } else {
                System.out.println("receiveOrderlyMsg: " + receiveMsg);
            }
        }

    }

    @Autowired
    private Source source;

    @Bean
    public CommandLineRunner runner() {
        return (args) -> {
            int count = 1;
            for (int index = 1; index <= count; index++) {
                source.output().send(MessageBuilder.withPayload("msg-" + index)
                    .setHeader("index", index).build());
            }
        };
    }

}
