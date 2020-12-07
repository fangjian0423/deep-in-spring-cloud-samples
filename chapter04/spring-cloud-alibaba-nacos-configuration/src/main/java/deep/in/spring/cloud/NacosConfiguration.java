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

import java.io.StringReader;

import org.apache.tomcat.util.http.parser.Host;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableConfigurationProperties(BookProperties.class)
public class NacosConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(NacosConfiguration.class, args);
    }

    @RestController
    @RefreshScope
    class ConfigurationController {

        @Autowired
        ApplicationContext applicationContext;

        @Value("${book.author:unknown}")
        String bookAuthor;

        @Autowired
        BookProperties bookProperties;

        @GetMapping("/config")
        public String config() {
            StringBuilder sb = new StringBuilder();
            sb.append("env.get('book.category')=" + applicationContext.getEnvironment()
                .getProperty("book.category", "unknown"))
                .append("<br/>env.get('book.author')=" + applicationContext.getEnvironment()
                    .getProperty("book.author", "unknown"))
                .append("<br/>bookAuthor=" + bookAuthor)
                .append("<br/>bookProperties=" + bookProperties);
            return sb.toString();
        }

    }

    @RestController
    class EventController {

        @Autowired
        ApplicationContext applicationContext;

        @GetMapping("/event")
        public String event() {
            applicationContext.publishEvent(new RefreshEvent(this, null, "just for test"));
            return "send RefreshEvent";
        }

    }

    @Component
    class EventReceiver implements ApplicationListener<ApplicationEvent> {

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            if (event instanceof EnvironmentChangeEvent) {
                System.out.println(((EnvironmentChangeEvent)event).getKeys());
            } else if (event instanceof RefreshScopeRefreshedEvent) {
                System.out.println(((RefreshScopeRefreshedEvent)event).getName());
            }
        }
    }

}
