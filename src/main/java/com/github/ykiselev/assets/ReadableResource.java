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

import java.io.IOException;
import java.net.URI;
import java.nio.channels.ReadableByteChannel;

/**
 * Implementation of this interface should be able to read (de-serialize) object instance of supported class from supplied {@link ReadableByteChannel}.
 * <p>
 * Created by Y.Kiselev on 15.05.2016.
 */
public interface ReadableResource<T> {

    /**
     * Convenient method to read resource by {@link URI}
     *
     * @param resource the resource {@link URI}.
     * @param assets   the instance of asset manager. At first glance {@link Resources} would suffice but {@link Assets} may be required for cases when we read compound asset consisting of different assets.
     * @return de-serialized resource.
     * @throws ResourceException if something goes wrong during de-serialization of resource.
     */
    default T read(URI resource, Assets assets) throws ResourceException {
        try (ReadableByteChannel channel = assets.open(resource)) {
            return read(channel, resource, assets);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * Reads resource from channel.
     *
     * @param channel  the binary stream to read resource from.
     * @param resource the resource {@link URI}.
     * @param assets   the instance of asset manager. At first glance {@link Resources} would suffice but {@link Assets} may be required for cases when we read compound asset consisting of different assets.
     * @return de-serialized resource.
     * @throws ResourceException if something goes wrong during de-serialization of resource.
     */
    T read(ReadableByteChannel channel, URI resource, Assets assets) throws ResourceException;
}
