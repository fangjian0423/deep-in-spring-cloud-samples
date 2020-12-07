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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bus.event.AckRemoteApplicationEvent;
import org.springframework.cloud.bus.event.SentApplicationEvent;
import org.springframework.cloud.bus.event.UnknownRemoteApplicationEvent;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@RemoteApplicationEventScan(basePackages = "deep.in.spring.cloud")
public class SCBNode3 {

    public static void main(String[] args) {
        SpringApplication.run(SCBNode3.class, args);
    }

    @Autowired
    ApplicationContext applicationContext;

    @Value("${spring.cloud.bus.id}")
    String originService;

    @RestController
    class BusController {

        @GetMapping("/event")
        String event(
            @RequestBody User user,
            @RequestParam(required = false) String destination) {
            applicationContext.publishEvent(new CustomEvent(this, user, originService, destination));
            return "ok";
        }

    }

    @Service
    class EventReceiver {

        @EventListener
        public void receive(CustomEvent event) {
            System.out.println("receive: " + event.getUser());
        }

        @EventListener
        public void receive(UnknownRemoteApplicationEvent event) {
            System.out.println(
                "receive UnknownRemoteApplicationEvent: " + event.getTypeInfo() + ", " + event.getPayloadAsString());
        }

        @EventListener
        public void receive(AckRemoteApplicationEvent event) {
            System.out.println(
                "receive AckRemoteApplicationEvent, origin: " + event.getOriginService() + ", dest: " + event
                    .getDestinationService() + ", ackDest: " + event.getAckDestinationService() + ", ackId: " + event
                    .getAckId());
        }

        @EventListener
        public void receive(SentApplicationEvent event) {
            System.out.println(
                "receive SentApplicationEvent, origin: " + event.getOriginService() + ", dest: " + event
                    .getDestinationService() + ", type: " + event.getType() + ", id: " + event
                    .getId());
        }

    }

}
