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

package deep.in.spring.cloud.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class CircuitBreakerRestCommand extends HystrixCommand<String> {

    private final RestTemplate restTemplate = new RestTemplate();

    private String code;

    public CircuitBreakerRestCommand(String code) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CBRestExample"))
            .andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter()
                    .withCircuitBreakerErrorThresholdPercentage(10)
                    .withCircuitBreakerRequestVolumeThreshold(10)
                    .withCircuitBreakerSleepWindowInMilliseconds(3000)
                    //.withMetricsRollingStatisticalWindowInMilliseconds(1000)
            )
        );
        this.code = code;
    }

    @Override
    protected String run() {
        String url = "http://httpbin.org/status/" + code;
        System.out.println("start to curl: " + url);
        restTemplate.getForObject(url, String.class);
        return "Request success";
    }

    @Override
    protected String getFallback() {
        return "Request failed";
    }
}
