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

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class ProfilePropertiesApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
            .web(WebApplicationType.NONE)
            .sources(ProfilePropertiesApplication.class)
            .run(args);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ApplicationRunner runner() {
        return (args) -> {
            ((AbstractEnvironment)applicationContext.getEnvironment()).getPropertySources().addFirst(new RandomValuePropertySource());
            ((AbstractEnvironment)applicationContext.getEnvironment()).getPropertySources().addFirst(new RandomValuePropertySource());
            System.out.println(applicationContext.getBean(UserService.class).findAll());
            System.out.println(applicationContext.getEnvironment().getProperty("custom.welcome"));
            System.out.println(Arrays.toString(applicationContext.getEnvironment().getActiveProfiles()));
            System.out.println(Arrays.toString(applicationContext.getEnvironment().getDefaultProfiles()));
        };
    }

}
