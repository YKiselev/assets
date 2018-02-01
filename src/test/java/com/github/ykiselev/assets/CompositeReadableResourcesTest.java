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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class CompositeReadableResourcesTest {

    @Test
    public void shouldResolve() {
        final ReadableResource<String> rr = (stream, res, assets) -> null;
        final ReadableResources delegate1 = mock(ReadableResources.class);
        final ReadableResources delegate2 = mock(ReadableResources.class);
        when(delegate1.resolve(eq("a"), eq(String.class)))
                .thenReturn(null);
        when(delegate2.resolve(eq("a"), eq(String.class)))
                .thenReturn(rr);
        final ReadableResources readableResources = new CompositeReadableResources(
                delegate1,
                delegate2
        );
        assertEquals(
                rr,
                readableResources.resolve("a", String.class)
        );
    }
}