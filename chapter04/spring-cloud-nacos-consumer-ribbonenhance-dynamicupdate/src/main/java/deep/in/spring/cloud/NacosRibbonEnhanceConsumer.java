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

import javax.servlet.http.HttpServletRequest;

import com.netflix.loadbalancer.IRule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(TrafficRule.class)
public class NacosRibbonEnhanceConsumer {

    public static void main(String[] args) {
        SpringApplication.run(NacosRibbonEnhanceConsumer.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(GrayInterceptor grayInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(grayInterceptor);
        return restTemplate;
    }

    @Bean
    public GrayInterceptor grayInterceptor(TrafficRule rule) {
        return new GrayInterceptor(rule);
    }

    @Bean
    public GrayRequestInterceptor requestInterceptor(TrafficRule rule) {
        return new GrayRequestInterceptor(rule);
    }

    @Bean
    public IRule myRule() {
        return new GrayRule();
    }

    @RestController
    class HelloController {

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private EchoService echoService;

        private String serviceName = "nacos-traffic-service";

        @GetMapping("/echo")
        public String echo(HttpServletRequest request) {
            HttpHeaders headers = new HttpHeaders();
            if (StringUtils.isNotEmpty(request.getHeader("Gray"))) {
                headers.add("Gray", request.getHeader("Gray"));
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);
            return restTemplate.exchange("http://" + serviceName + "/", HttpMethod.GET, entity, String.class).getBody();
        }

        @GetMapping("/echoFeign")
        public String echoFeign(HttpServletRequest request) {
            if (StringUtils.isNotEmpty(request.getHeader("Gray"))) {
                return echoService.echo(request.getHeader("Gray"));
            }
            return echoService.echo("false");
        }

    }

    @FeignClient(name = "nacos-traffic-service")
    interface EchoService {

        @GetMapping("/")
        String echo(@RequestHeader("Gray") String gray);

    }

}
