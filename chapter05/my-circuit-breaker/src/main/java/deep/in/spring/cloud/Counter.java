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

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class Counter {

    // Closed 状态进入 Open 状态的错误个数阀值
    private final int failureCount;

    // failureCount 统计时间窗口
    private final long failureTimeInterval;

    // 当前错误次数
    private final AtomicInteger currentCount;

    // 上一次调用失败的时间戳
    private long lastTime;

    // Half-Open 状态下成功次数
    private final AtomicInteger halfOpenSuccessCount;

    public Counter(int failureCount, long failureTimeInterval) {
        this.failureCount = failureCount;
        this.failureTimeInterval = failureTimeInterval;
        this.currentCount = new AtomicInteger(0);
        this.halfOpenSuccessCount = new AtomicInteger(0);
        this.lastTime = System.currentTimeMillis();
    }

    public synchronized int incrFailureCount() {
        long current = System.currentTimeMillis();
        if (current - lastTime > failureTimeInterval) { // 超过时间窗口，当前失败次数重置为 0
            lastTime = current;
            currentCount.set(0);
        }
        return currentCount.getAndIncrement();
    }

    public int incrSuccessHalfOpenCount() {
        return this.halfOpenSuccessCount.incrementAndGet();
    }

    public boolean failureThresholdReached() {
        return getCurCount() >= failureCount;
    }

    public int getCurCount() {
        return currentCount.get();
    }

    public synchronized void reset() {
        halfOpenSuccessCount.set(0);
        currentCount.set(0);
    }

}
