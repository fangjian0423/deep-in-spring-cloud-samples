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
import java.util.function.Supplier;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class SupplierApplication {

    public static void main(String[] args) {

        Random random = new Random();

        Supplier supplier100 = () -> random.nextInt(100);
        Supplier supplier1000 = () -> random.nextInt(1000);

        System.out.println(supplier100.get());
        System.out.println(supplier1000.get());

    }

}
