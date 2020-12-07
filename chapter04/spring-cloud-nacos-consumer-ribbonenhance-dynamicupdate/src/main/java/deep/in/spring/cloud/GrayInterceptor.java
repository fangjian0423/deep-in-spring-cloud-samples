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

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class GrayInterceptor implements ClientHttpRequestInterceptor {

    private TrafficRule rule;

    public GrayInterceptor(TrafficRule rule) {
        this.rule = rule;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        if (rule.getType().equalsIgnoreCase("header")) {
            if (request.getHeaders().containsKey(rule.getName())) {
                String value = request.getHeaders().get(rule.getName()).iterator().next();
                if (value.equals(rule.getValue())) {
                    RibbonRequestContextHolder.getCurrentContext().put("Gray", Boolean.TRUE.toString());
                }
            }
        } else if (rule.getType().equalsIgnoreCase("param")) {
            String query = request.getURI().getQuery();
            String[] queryKV = query.split("&");
            for(String queryItem : queryKV) {
                String[] queryInfo = queryItem.split("=");
                if (queryInfo[0].equalsIgnoreCase(rule.getName()) && queryInfo[1].equals(rule.getValue())) {
                    RibbonRequestContextHolder.getCurrentContext().put("Gray", Boolean.TRUE.toString());
                }
            }
        }
        return execution.execute(request, body);
    }

}
