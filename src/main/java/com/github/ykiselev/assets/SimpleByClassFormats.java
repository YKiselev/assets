/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.assets;

import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Created by Y.Kiselev on 15.05.2016.
 */
public final class SimpleByClassFormats implements Function<Class, ReadableResource> {

    private final Map<Class, ReadableResource> map;

    public SimpleByClassFormats(Map<Class, ReadableResource> map) {
        this.map = map;
    }

    @Override
    public ReadableResource apply(Class clazz) {
        return requireNonNull(map.get(clazz), "Unknown class: " + clazz);
    }

}
