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

import java.net.URI;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

/**
 * Implementation of this interface should be able to read (de-serialize) object instance of supported class from supplied {@link ReadableByteChannel}.
 * <p>
 * Created by Y.Kiselev on 15.05.2016.
 */
public interface ReadableResource<T> {

    /**
     * Convenient method to read resource by {@link URI}
     *
     * @param resource the resource name.
     * @param assets   the instance of asset manager. At first glance {@link Resources} would suffice but {@link Assets} may be required for cases when we read compound asset consisting of different assets.
     * @return de-serialized resource or nothing.
     * @throws ResourceException if something goes wrong during de-serialization of resource.
     */
    default Optional<T> read(String resource, Assets assets) throws ResourceException {
        return assets.open(resource).map(ch -> read(ch, resource, assets));
    }

    /**
     * Reads resource from channel.
     *
     * @param channel  the binary stream to read resource from.
     * @param resource the resource name.
     * @param assets   the instance of asset manager. At first glance {@link Resources} would suffice but {@link Assets} may be required for cases when we read compound asset consisting of different assets.
     * @return de-serialized resource.
     * @throws ResourceException if something goes wrong during de-serialization of resource.
     */
    T read(ReadableByteChannel channel, String resource, Assets assets) throws ResourceException;
}
