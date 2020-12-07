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

import deep.in.spring.cloud.SCSPollingConsumerApplication.MySink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.binder.PollableMessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static deep.in.spring.cloud.SCSPollingConsumerApplication.MySink.INPUT;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableBinding({MySink.class})
public class SCSPollingConsumerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(SCSPollingConsumerApplication.class)
            .web(WebApplicationType.NONE).run(args);
    }

    public interface MySink {

        String INPUT = "input";

        @Input(INPUT)
        PollableMessageSource input();
    }

    @Autowired
    MySink mySink;

    @Bean
    public CommandLineRunner runner() {
        return (args) -> {
            while (true) {
                mySink.input().poll(m -> {
                    System.out.println("poll: " + m.getPayload());
                });
                Thread.sleep(5000L);
            }
        };
    }

}
