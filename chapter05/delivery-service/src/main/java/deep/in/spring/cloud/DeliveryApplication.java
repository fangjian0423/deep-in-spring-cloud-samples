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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableFeignClients
public class DeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }

    @RestController
    class DeliveryController {

        @Autowired
        private SMSService smsService;

        @GetMapping("/delivery")
        public String delivery(String orderId) {
            String smsResult = smsService.send(orderId, 5);
            if(smsResult.equalsIgnoreCase("sms service has some problems")) {
                return "delivery " + orderId + " failed: " + smsResult;
            } else {
                return "delivery " + orderId + " success";
            }
        }

    }

    @Bean
    public FallbackFactory fallbackFactory() {
        return new FallbackFactory();
    }

    @FeignClient(name = "sms-service", fallbackFactory = FallbackFactory.class)
    public interface SMSService {

        @GetMapping("/send")
        String send(@RequestParam("orderId") String orderId, @RequestParam("delaySecs") int delaySecs);

    }

    class FallbackInventoryService implements SMSService {

        @Override
        public String send(String orderId, int delaySecs) {
            return "sms service has some problems";
        }
    }

    class FallbackFactory implements feign.hystrix.FallbackFactory {

        private FallbackInventoryService fallbackService = new FallbackInventoryService();

        @Override
        public Object create(Throwable cause) {
            return fallbackService;
        }
    }

}
