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

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class PollableChannelApp {

    public static void main(String[] args) {
        MyPollableChannel channel = new MyPollableChannel();
        channel.send(MessageBuilder.withPayload("custom payload1").setHeader("k1", "v1").build());
        channel.send(MessageBuilder.withPayload("custom payload2").setHeader("k2", "v2").build());
        channel.send(MessageBuilder
            .createMessage("custom payload3", new MessageHeaders(Collections.singletonMap("ignore", true))));

        System.out.println(channel.receive());
        System.out.println(channel.receive());
        System.out.println(channel.receive());
        System.out.println(channel.receive());
    }

}
