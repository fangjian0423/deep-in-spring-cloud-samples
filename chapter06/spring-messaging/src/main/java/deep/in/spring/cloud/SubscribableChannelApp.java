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

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class SubscribableChannelApp {

    public static void main(String[] args) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        MySubscribableChannel channel = new MySubscribableChannel();

        channel.addInterceptor(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                String ignoreKey = "ignore";
                if (message.getHeaders().containsKey(ignoreKey) &&
                    message.getHeaders().get(ignoreKey, Boolean.class)) {
                    return null;
                }
                return message;
            }

            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                if (sent) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            }
        });

        channel.send(MessageBuilder.withPayload("custom payload1").setHeader("k1", "v1").build());
        channel.subscribe(msg -> {
            System.out.println("[" + Thread.currentThread().getName() + "] handler1 receive: " + msg);
        });
        channel.subscribe(msg -> {
            System.out.println("[" + Thread.currentThread().getName() + "] handler2 receive: " + msg);
        });
        channel.subscribe(msg -> {
            System.out.println("[" + Thread.currentThread().getName() + "] handler3 receive: " + msg);
        });
        channel.send(MessageBuilder.withPayload("custom payload2").setHeader("k2", "v2").build());
        channel.send(MessageBuilder
            .createMessage("custom payload3", new MessageHeaders(Collections.singletonMap("ignore", true))));
        System.out.println("successCount: " + successCount.get() + ", failCount: " + failCount.get());
    }

}
