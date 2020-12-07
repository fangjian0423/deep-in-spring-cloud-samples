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

import deep.in.spring.cloud.SCSCustomBindingTargetFactoryApplication.MySink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.AbstractBindingTargetFactory;
import org.springframework.cloud.stream.binding.BindingTargetFactory;
import org.springframework.cloud.stream.binding.CompositeMessageChannelConfigurer;
import org.springframework.cloud.stream.binding.MessageChannelConfigurer;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableBinding({MySink.class})
public class SCSCustomBindingTargetFactoryApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(SCSCustomBindingTargetFactoryApplication.class)
            .web(WebApplicationType.NONE).run(args);
    }

    public interface MySink {

        String INPUT = "input";

        @Input(INPUT)
        PublishSubscribeChannel input();

    }

    class PublishSubscribeChannelBindingTargetFactory extends AbstractBindingTargetFactory<PublishSubscribeChannel> {

        private final MessageChannelConfigurer messageChannelConfigurer;

        @Autowired
        private GenericApplicationContext context;

        public PublishSubscribeChannelBindingTargetFactory(
            MessageChannelConfigurer messageChannelConfigurer) {
            super(PublishSubscribeChannel.class);
            this.messageChannelConfigurer = messageChannelConfigurer;
        }

        @Override
        public PublishSubscribeChannel createInput(String name) {
            PublishSubscribeChannel channel = new PublishSubscribeChannel();
            channel.setComponentName(name);
            this.messageChannelConfigurer.configureInputChannel(channel, name);
            if (context != null && !context.containsBean(name)) {
                context.registerBean(name, PublishSubscribeChannel.class, () -> channel);
            }
            return channel;
        }

        @Override
        public PublishSubscribeChannel createOutput(String name) {
            PublishSubscribeChannel channel = new PublishSubscribeChannel();
            channel.setComponentName(name);
            this.messageChannelConfigurer.configureOutputChannel(channel, name);
            if (context != null && !context.containsBean(name)) {
                context.registerBean(name, PublishSubscribeChannel.class, () -> channel);
            }
            return channel;
        }
    }

    @Service
    class MySinkService {

        @StreamListener(Sink.INPUT)
        public void receive1(String receiveMsg) {
            System.out.println("receive1: " + receiveMsg);
        }

        @StreamListener(Sink.INPUT)
        public void receive2(String receiveMsg) {
            System.out.println("receive2: " + receiveMsg);
        }

    }

    @Autowired
    private MySink sink;

    @Bean
    public CommandLineRunner runner() {
        return (args) -> {
            sink.input().send(MessageBuilder.withPayload("send by application with memory, not real MQ").build());
        };
    }

    @Bean
    @Primary
    BindingTargetFactory publishSubscribeChannelBindingTargetFactory(
        CompositeMessageChannelConfigurer compositeMessageChannelConfigurer) {
        return new PublishSubscribeChannelBindingTargetFactory(compositeMessageChannelConfigurer);
    }

}
