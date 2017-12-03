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

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class SimpleAssetsTest {

    private final Resources resources = mock(Resources.class);

    @SuppressWarnings("unchecked")
    private final Function<Class, ReadableResource> byClass = mock(Function.class);

    @SuppressWarnings("unchecked")
    private final Function<String, ReadableResource> byExtension = mock(Function.class);

    private final Assets assets = new SimpleAssets(
            resources,
            byClass,
            byExtension
    );

    @Test
    public void shouldLoadByClass() {
        when(byClass.apply(eq(Double.class)))
                .thenReturn((is, resource, a) -> Math.PI);
        assertEquals(Math.PI, assets.load("x.double", Double.class), 0.00001);
        assertEquals(Math.PI, assets.load(Double.class), 0.00001);
        verify(byExtension, never()).apply(any(String.class));
    }

    @Test
    public void shouldLoadByExtension() {
        when(byExtension.apply(eq("double")))
                .thenReturn((is, resource, a) -> Math.PI);
        assertEquals(Math.PI, assets.load("y.double"), 0.00001);
        verify(byClass, never()).apply(any(Class.class));
    }
}