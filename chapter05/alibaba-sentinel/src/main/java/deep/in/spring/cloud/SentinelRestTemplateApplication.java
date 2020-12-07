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

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Profile("resttemplate")
@SpringBootApplication
public class SentinelRestTemplateApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SentinelRestTemplateApplication.class)
            .properties("spring.profiles.active=resttemplate").web(WebApplicationType.SERVLET)
            .run(args);
    }

    //@Bean
    //@SentinelRestTemplate
    //public RestTemplate restTemplate() {
    //    return new RestTemplate();
    //}

    @Bean
    @SentinelRestTemplate
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        template.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                throw new IllegalStateException("illegal status code");
            }
        });
        return template;
    }

    @RestController
    class SentinelController {

        @Autowired
        RestTemplate restTemplate;

        @GetMapping("/exp")
        public String exp() {
            return restTemplate.getForObject("https://httpbin.org/status/500", String.class);
        }

        @GetMapping("/rt")
        public String rt() {
            return restTemplate.getForObject("https://httpbin.org/delay/3", String.class);
        }

        @GetMapping("/404")
        public String f04() {
            return restTemplate.getForObject("https://httpbin.org/status/404", String.class);
        }

    }

}
