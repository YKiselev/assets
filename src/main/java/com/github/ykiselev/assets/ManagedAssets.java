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

import java.io.Closeable;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Created by Y.Kiselev on 16.05.2016.
 */
public final class ManagedAssets implements Assets, AutoCloseable {

    private final Assets assets;

    private final Map<URI, Object> cache;

    public ManagedAssets(Assets assets, Map<URI, Object> cache) {
        this.assets = requireNonNull(assets);
        this.cache = requireNonNull(cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T load(URI resource, Class<T> clazz) throws ResourceException {
        return (T) cache.computeIfAbsent(resource, r -> assets.load(r, clazz));
    }

    @Override
    public void close() throws Exception {
        for (Map.Entry<URI, Object> entry : this.cache.entrySet()) {
            if (entry.getValue() instanceof Closeable) {
                ((Closeable) entry.getValue()).close();
            } else if (entry.getValue() instanceof AutoCloseable) {
                ((AutoCloseable) entry.getValue()).close();
            }
        }
    }

    @Override
    public <T> ReadableResource<T> resolve(URI resource, Class<T> clazz) throws ResourceException {
        return assets.resolve(resource, clazz);
    }

    @Override
    public InputStream open(URI resource) throws ResourceException {
        return assets.open(resource);
    }
}
