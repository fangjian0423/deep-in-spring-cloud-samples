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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableIntegration
public class SpringIntegrationApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringIntegrationApplication.class)
            .web(WebApplicationType.SERVLET).run(args);
    }

    private static Random random = new Random();
    private static BigDecimal totalMoney = new BigDecimal(0);
    private static Map<String, Integer> goodsCount = new HashMap();
    private static Map<String, BigDecimal> goodsMoney = new HashMap();

    static {
        goodsCount.put("apple", 10);
        goodsCount.put("book", 100);
        goodsCount.put("clothes", 50);

        goodsMoney.put("apple", new BigDecimal(4000));
        goodsMoney.put("book", new BigDecimal(30));
        goodsMoney.put("clothes", new BigDecimal(500));
    }

    @RestController
    class MessagingController {
        @Autowired
        TestGateway testGateway;

        @GetMapping("/order")
        void order() {
            testGateway.echo(new OrderMsg("book", 10));
        }

        @GetMapping("/result")
        String result() {
            return "totalMoney: " + totalMoney + ", goods count info: " + goodsCount;
        }
    }

    @MessagingGateway(name = "testGateway", defaultRequestChannel = "order")
    public interface TestGateway {
        @Gateway(requestChannel = "order", replyTimeout = 2, requestTimeout = 200)
        String echo(OrderMsg orderMsg);
    }

    @Bean
    MessageChannel order() {
        return new DirectChannel();
    }

    @Bean
    MessagingTemplate messagingTemplate() {
        return new MessagingTemplate(order());
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            messagingTemplate().send(MessageBuilder.withPayload(
                new OrderMsg("apple", 5)).build());
            messagingTemplate().send(MessageBuilder.withPayload(
                new OrderMsg("book", 30)).build());
            messagingTemplate().send(MessageBuilder.withPayload(
                new OrderMsg("clothes", 1)).build());
        };
    }

    @Bean
    @InboundChannelAdapter(value = "order", poller = @Poller(fixedDelay = "10000", maxMessagesPerPoll = "1"))
    public MessageSource<OrderMsg> orderMessageSource() {
        return () -> {
            String randomGoods = new ArrayList<>(goodsCount.keySet()).get(random.nextInt(goodsCount.size()));
            return MessageBuilder.withPayload(new OrderMsg(randomGoods, random.nextInt(5))).build();
        };
    }

    @ServiceActivator(inputChannel = "order", outputChannel = "errorTopic")
    Message receive(@Payload OrderMsg order) {
        if (goodsCount.containsKey(order.getGoods()) && order.getCount() > 0) {
            int newCount = goodsCount.get(order.getGoods()) - order.getCount();
            if (newCount < 0) {
                return MessageBuilder.withPayload("goods: " + order.getGoods() + " count is not enough").build();
            } else {
                goodsCount.put(order.getGoods(), newCount);
                totalMoney = totalMoney.add(
                    goodsMoney.get(order.getGoods()).multiply(new BigDecimal(order.getCount())));
                return MessageBuilder.withPayload("goods: " + order.getGoods() + " handle successful").build();
            }
        } else {
            return MessageBuilder.withPayload("goods: " + order.getGoods() + " count is not enough or count is invalid")
                .build();
        }
    }

    @ServiceActivator(inputChannel = "errorTopic")
    void result(String msg) {
        System.out.println(msg);
    }

    static class OrderMsg {

        String goods;
        Integer count;

        public OrderMsg() {
        }

        public OrderMsg(String goods, Integer count) {
            this.goods = goods;
            this.count = count;
        }

        public String getGoods() {
            return goods;
        }

        public void setGoods(String goods) {
            this.goods = goods;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

}
