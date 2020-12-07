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
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableBinding(Source.class)
@EnableScheduling
public class CreditCardSourceApplication {

    private final Logger logger = LoggerFactory.getLogger(CreditCardSourceApplication.class);

    public static Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(CreditCardSourceApplication.class, args);
    }

    @Autowired
    private Source source;

    @Scheduled(fixedDelay = 2000)
    public void sendMsg() {
        int cost = random.nextInt(2000);
        String cardType = CreditCardRecord.cardTypes.get(
            String.valueOf(random.nextInt(CreditCardRecord.cardTypes.size())));
        String user = CreditCardRecord.users.get(String.valueOf(random.nextInt(CreditCardRecord.users.size())));
        CreditCardRecord record = new CreditCardRecord(user, new BigDecimal(cost), cardType);
        logger.info("credit card cost record: " + record);
        source.output().send(MessageBuilder.withPayload(record).build());
    }

}
