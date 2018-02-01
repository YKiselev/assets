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

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * This implementation uses supplied instance of {@link ReadableResources} to resolve {@link ReadableResource}.
 * <p>
 * Created by Y.Kiselev on 15.05.2016.
 */
public final class SimpleAssets implements Assets {

    private final Resources resources;

    private final ReadableResources readableResources;

    public SimpleAssets(Resources resources, ReadableResources readableResources) {
        this.resources = requireNonNull(resources);
        this.readableResources = requireNonNull(readableResources);
    }

    @Override
    public <T> Optional<T> tryLoad(String resource, Class<T> clazz) throws ResourceException {
        return resources.open(resource)
                .map(channel ->
                        readableResources.resolve(resource, clazz)
                                .read(channel, resource, this)
                );
    }

    @Override
    public <T> ReadableResource<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return readableResources.resolve(resource, clazz);
    }
}
