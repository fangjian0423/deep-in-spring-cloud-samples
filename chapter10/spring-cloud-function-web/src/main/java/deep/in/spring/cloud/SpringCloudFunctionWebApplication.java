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

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class SpringCloudFunctionWebApplication {

    private Random random = new Random();

    private RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudFunctionWebApplication.class, args);
    }

    @Bean
    public Function<String, String> upperCase() {
        return s -> s.toUpperCase();
    }

    @Bean
    public Function<String, String> lowerCase() {
        return s -> s.toLowerCase();
    }

    @Bean
    public Supplier<Integer> random() {
        return () -> random.nextInt(1000);
    }

    @Bean
    public Consumer<String> consumer() {
        return code -> {
            ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("http://httpbin.org/status/" + code, String.class);
            System.out.println(responseEntity.getStatusCode().toString());
        };
    }

    @Bean
    public Function<String, String> getFunc() {
        return s -> s.toUpperCase();
    }

    @Bean
    public Function<User, String> user() {
        return s -> s.toString();
    }

    public static class User {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
        }
    }

}
