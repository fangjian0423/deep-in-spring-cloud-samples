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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class SentinelSpringCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelSpringCloudGatewayApplication.class, args);
    }

    //@Bean
    //public BlockRequestHandler blockRequestHandler() {
    //    return new BlockRequestHandler() {
    //        @Override
    //        public Mono<ServerResponse> handleRequest(ServerWebExchange exchange,
    //            Throwable t) {
    //            return ServerResponse.status(444).contentType(MediaType.APPLICATION_JSON)
    //                .body(fromValue("SCS Sentinel block"));
    //        }
    //    };
    //}

}
