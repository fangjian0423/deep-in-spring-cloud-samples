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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableBinding(Sink.class)
public class CreditCardSinkApplication {

    private final Logger logger = LoggerFactory.getLogger(CreditCardSinkApplication.class);

    private Map<String, BigDecimal> lastCostInfo = new HashMap<>();
    private Set<String> blackList = new HashSet<>();

    private BigDecimal warningMoney = new BigDecimal(2000);

    public static void main(String[] args) {
        SpringApplication.run(CreditCardSinkApplication.class, args);
    }

    @StreamListener(Sink.INPUT)
    public void receive(CreditCardRecord record) {
        if (blackList.contains(record.getUser())) {
            logger.info(record.getUser() + " now is in black list");
            return;
        }
        logger.info(record.getUser() + " cost " + record.getCost() + " with " + record.getCardType());
        if (lastCostInfo.containsKey(record.getUser())) {
            BigDecimal recentlyCostMoney = lastCostInfo.get(record.getUser()).add(record.getCost());
            if (recentlyCostMoney.compareTo(warningMoney) >= 0) {
                logger.warn(record.getUser() + " recently cost " + recentlyCostMoney + ", go to black list");
                blackList.add(record.getUser()); // 超过 2000，加入黑名单
                return;
            }
        }
        lastCostInfo.put(record.getUser(), record.getCost());
    }

}
