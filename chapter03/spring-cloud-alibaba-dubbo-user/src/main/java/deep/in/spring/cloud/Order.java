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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class Order implements Serializable {

    private String id;

    private Timestamp createdTime;

    private String userId;

    public static Order generate(String userId) {
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        order.setUserId(userId);
        return order;
    }

    public static Order error() {
        Order order = new Order();
        order.setId("-1");
        order.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        order.setUserId("none");
        return order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
