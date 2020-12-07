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

import java.util.Iterator;
import java.util.Random;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.util.CollectionUtils;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class MySubscribableChannel extends AbstractSubscribableChannel {

    private Random random = new Random();

    @Override
    protected boolean sendInternal(Message<?> message, long timeout) {
        if (message == null || CollectionUtils.isEmpty(getSubscribers())) {
            return false;
        }
        Iterator<MessageHandler> iter = getSubscribers().iterator();
        int index = 0, targetIndex = random.nextInt(getSubscribers().size());
        while(iter.hasNext()) {
            MessageHandler handler = iter.next();
            if(index == targetIndex) {
                handler.handleMessage(message);
                return true;
            }
            index ++;
        }
        return false;
    }

}
