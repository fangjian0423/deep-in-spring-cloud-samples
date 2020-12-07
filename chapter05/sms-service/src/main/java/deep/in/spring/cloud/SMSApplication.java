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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class SMSApplication {

    public static void main(String[] args) {
        SpringApplication.run(SMSApplication.class, args);
    }

    @RestController
    class SMSController {

        private AtomicInteger count = new AtomicInteger();

        @GetMapping("/send")
        public String send(String orderId, int delaySecs) {
            int num = count.addAndGet(1);
            if(num >= 1000) {
                if(delaySecs > 0) {
                    try {
                        Thread.sleep(1000 * delaySecs);
                    } catch (InterruptedException e) {
                        return "Interrupted: " + e.getMessage();
                    }
                }
            }
            System.out.println(orderId + " send successfully");
            return "success";
        }

    }

}
