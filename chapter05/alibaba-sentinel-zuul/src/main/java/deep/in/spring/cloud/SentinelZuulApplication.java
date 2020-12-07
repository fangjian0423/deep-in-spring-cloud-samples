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

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableZuulProxy
public class SentinelZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelZuulApplication.class, args);
    }

    @Bean
    public ZuulBlockFallbackProvider zuulBlockFallbackProvider1() {
        return new ZuulBlockFallbackProvider() {
            @Override
            public String getRoute() {
                return "*";
            }

            @Override
            public BlockResponse fallbackResponse(String route, Throwable cause) {
                if (route.equals("my-provider1")) {
                    return new BlockResponse(403, "Provider1 Block", route);
                } else if (route.equals("my-provider2")) {
                    return new BlockResponse(403, "Provider2 Block", route);
                } else {
                    return new BlockResponse(403, "Sentinel Block", route);
                }
            }
        };
    }

}
