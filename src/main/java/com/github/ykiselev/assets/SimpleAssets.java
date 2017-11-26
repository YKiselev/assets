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

import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;

/**
 * Created by Y.Kiselev on 15.05.2016.
 */
public final class SimpleAssets implements Assets {

    private final Resources resources;

    private final Function<Class, ReadableResource> byClass;

    private final Function<String, ReadableResource> byExtension;

    public SimpleAssets(Resources resources, Function<Class, ReadableResource> byClass, Function<String, ReadableResource> byExtension) {
        this.resources = resources;
        this.byClass = byClass;
        this.byExtension = byExtension;
    }

    private String format(URI resource) {
        final String path = resource.getPath();
        final int i = path.lastIndexOf('.');
        if (i == -1) {
            return null;
        }
        return path.substring(i + 1);
    }

    @Override
    public void close() throws Exception {
        // nothing to do
    }

    @Override
    public InputStream open(URI resource) throws ResourceException {
        return this.resources.open(resource);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T load(URI resource, Class<T> clazz) throws ResourceException {
        final ReadableResource readableResource;
        if (clazz == null) {
            readableResource = byExtension.apply(format(resource));
        } else {
            readableResource = byClass.apply(clazz);
        }
        return (T) readableResource.read(resource, this);
    }
}
