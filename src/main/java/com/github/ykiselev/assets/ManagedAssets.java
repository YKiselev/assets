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
import java.nio.channels.ReadableByteChannel;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Created by Y.Kiselev on 16.05.2016.
 */
public final class ManagedAssets implements Assets, AutoCloseable {

    private final Assets assets;

    private final Map<String, Object> cache;

    public ManagedAssets(Assets assets, Map<String, Object> cache) {
        this.assets = requireNonNull(assets);
        this.cache = requireNonNull(cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T load(String resource, Class<T> clazz) throws ResourceException {
        return (T) cache.computeIfAbsent(resource, r -> assets.load(r, clazz));
    }

    @Override
    public void close() {
        cache.forEach((key, value) -> close(value));
        cache.clear();
    }

    private void close(Object asset) throws IllegalStateException {
        try {
            if (asset instanceof Closeable) {
                ((Closeable) asset).close();
            } else if (asset instanceof AutoCloseable) {
                ((AutoCloseable) asset).close();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> ReadableResource<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return assets.resolve(resource, clazz);
    }

    @Override
    public ReadableByteChannel open(String resource) throws ResourceException {
        return assets.open(resource);
    }
}
