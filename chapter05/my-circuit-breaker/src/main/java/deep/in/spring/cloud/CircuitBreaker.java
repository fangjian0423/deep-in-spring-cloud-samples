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

import java.util.function.Function;
import java.util.function.Supplier;

import static deep.in.spring.cloud.State.CLOSED;
import static deep.in.spring.cloud.State.HALF_OPEN;
import static deep.in.spring.cloud.State.OPEN;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class CircuitBreaker {

    private State state;

    private Config config;

    private Counter counter;

    private long lastOpenedTime;

    public CircuitBreaker(Config config) {
        this.counter = new Counter(config.getFailureCount(), config.getFailureTimeInterval());
        this.state = CLOSED;
        this.config = config;
    }

    public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
        try {
            if (state == OPEN) {
                // 判断 half-open 是否超时
                if (halfOpenTimeout()) {
                    return halfOpenHandle(toRun, fallback);
                }
                return fallback.apply(new DegradeException("degrade by circuit breaker"));
            } else if (state == CLOSED) {
                T result = toRun.get();
                closed();
                return result;
            } else {
                return halfOpenHandle(toRun, fallback);
            }
        } catch (Exception e) {
            counter.incrFailureCount();
            if (counter.failureThresholdReached()) { // 错误次数达到阀值，进入 open 状态
                open();
            }
            return fallback.apply(e);
        }
    }

    private <T> T halfOpenHandle(Supplier<T> toRun, Function<Throwable, T> fallback) {
        try {
            halfOpen(); // closed 状态超时进入 half-open 状态
            T result = toRun.get();
            int halfOpenSuccCount = counter.incrSuccessHalfOpenCount();
            if (halfOpenSuccCount >= this.config.getHalfOpenSuccessCount()) { // half-open 状态成功次数到达阀值，进入 closed 状态
                closed();
            }
            return result;
        } catch (Exception e) {
            // half-open 状态发生一次错误进入 open 状态
            open();
            return fallback.apply(new DegradeException("degrade by circuit breaker"));
        }
    }

    private boolean halfOpenTimeout() {
        return System.currentTimeMillis() - lastOpenedTime > config.getHalfOpenTimeout();
    }

    private void closed() {
        counter.reset();
        state = CLOSED;
    }

    private void open() {
        state = OPEN;
        lastOpenedTime = System.currentTimeMillis();
    }

    private void halfOpen() {
        state = HALF_OPEN;
    }

}
