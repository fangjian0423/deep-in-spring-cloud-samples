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
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class SpringCloudFunctionStreamMultiFuncsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudFunctionStreamMultiFuncsApplication.class, args);
    }

    private final EmitterProcessor<Message<?>> processor = EmitterProcessor.create();

    @RestController
    class MyController {
        @GetMapping("/go")
        public String go(@RequestParam("func") String func, @RequestParam("payload") String payload) {
            processor.onNext(MessageBuilder.withPayload(payload).
                setHeader("spring.cloud.function.definition", func)
                .setHeader("spring.cloud.stream.sendto.destination", "test-input").build());
            return "ok";
        }
    }

    @Bean
    public Supplier<Flux<Message<?>>> supplier() {
        return () -> processor;
    }

    @Bean
    public Consumer<String> consume1() {
        return x -> System.out.println("consume1: " + x);
    }

    @Bean
    public Consumer<String> consume2() {
        return x -> System.out.println("consume2: " + x);
    }

    @Bean
    public Consumer<String> consume3() {
        return x -> System.out.println("consume3: " + x);
    }

}
