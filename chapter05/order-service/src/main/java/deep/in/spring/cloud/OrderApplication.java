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

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @RestController
    class OrderController {

        @Autowired
        private DeliveryService deliveryService;

        @GetMapping("/order")
        public String order() {
            String orderId = UUID.randomUUID().toString();
            deliveryService.delivery(orderId);
            return "order " + orderId + " generate";
        }

    }

    @Bean
    public FallbackFactory fallbackFactory() {
        return new FallbackFactory();
    }

    @FeignClient(name = "delivery-service", fallbackFactory = FallbackFactory.class)
    public interface DeliveryService {

        @GetMapping("/delivery")
        String delivery(@RequestParam("orderId") String orderId);

    }

    class FallbackInventoryService implements DeliveryService {

        @Override
        public String delivery(String orderId) {
            return "delivery service has some problems";
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
