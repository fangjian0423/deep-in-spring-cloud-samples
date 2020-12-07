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

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class RibbonRequestContext {

    private final Map<String, String> attr = new HashMap<>();

    public String put(String key, String value) {
        return attr.put(key, value);
    }

    public String remove(String key) {
        return attr.remove(key);
    }

    public String get(String key) {
        return attr.get(key);
    }

}
