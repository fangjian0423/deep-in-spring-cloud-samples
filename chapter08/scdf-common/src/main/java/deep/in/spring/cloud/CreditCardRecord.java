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
import java.util.Map;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class CreditCardRecord {

    public static Map<String, String> cardTypes = new HashMap<>();
    public static Map<String, String> users = new HashMap<>();

    static {
        cardTypes.put("0", "CMB");  // 招行银行
        cardTypes.put("1", "ICBC"); // 工商银行
        cardTypes.put("2", "ABC");  // 农业银行
        cardTypes.put("3", "CCB");  // 建设银行
        cardTypes.put("4", "BCM");  // 交通银行
        cardTypes.put("5", "CMBC"); // 民生银行

        users.put("0", "jim");
        users.put("1", "jerry");
        users.put("2", "tom");
    }

    private String user;
    private BigDecimal cost;
    private String cardType;

    public CreditCardRecord() {
    }

    public CreditCardRecord(String user, BigDecimal cost, String cardType) {
        this.user = user;
        this.cost = cost;
        this.cardType = cardType;
    }

    public static Map<String, String> getCardTypes() {
        return cardTypes;
    }

    public static void setCardTypes(Map<String, String> cardTypes) {
        CreditCardRecord.cardTypes = cardTypes;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public String toString() {
        return "CreditCardRecord{" +
            "user='" + user + '\'' +
            ", cost=" + cost +
            ", cardType='" + cardType + '\'' +
            '}';
    }
}
