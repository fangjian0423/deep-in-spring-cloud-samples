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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Service(version = "1.0.0")
@RestController
public class OrderServiceImpl implements OrderService {

    private static List<Order> orderList = new ArrayList<>();

    static {
        orderList.add(Order.generate("jim"));
        orderList.add(Order.generate("jim"));
        orderList.add(Order.generate("test"));
    }

    @GetMapping("/allOrders")
    @Override
    public List<Order> getAllOrders(@RequestParam("userId") final String userId) {
        return orderList.stream().filter(
            order -> order.getUserId().equals(userId)
        ).collect(Collectors.toList());
    }

    @GetMapping("/findOrder")
    @Override
    public Order findOrder(@RequestParam("orderId") String orderId) {
        return orderList.stream().filter(
            order -> order.getId().equals(orderId)
        ).findFirst().orElseGet(Order::error);
    }

}
