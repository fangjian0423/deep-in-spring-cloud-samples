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

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class Config {

    // Closed 状态进入 Open 状态的错误个数阀值
    private int failureCount = 5;

    // failureCount 统计时间窗口
    private long failureTimeInterval = 2 * 1000;

    // Open 状态进入 Half-Open 状态的超时时间
    private int halfOpenTimeout = 5 * 1000;

    // Half-Open 状态进入 Open 状态的成功个数阀值
    private int halfOpenSuccessCount = 2;

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public long getFailureTimeInterval() {
        return failureTimeInterval;
    }

    public void setFailureTimeInterval(long failureTimeInterval) {
        this.failureTimeInterval = failureTimeInterval;
    }

    public int getHalfOpenTimeout() {
        return halfOpenTimeout;
    }

    public void setHalfOpenTimeout(int halfOpenTimeout) {
        this.halfOpenTimeout = halfOpenTimeout;
    }

    public int getHalfOpenSuccessCount() {
        return halfOpenSuccessCount;
    }

    public void setHalfOpenSuccessCount(int halfOpenSuccessCount) {
        this.halfOpenSuccessCount = halfOpenSuccessCount;
    }
}
