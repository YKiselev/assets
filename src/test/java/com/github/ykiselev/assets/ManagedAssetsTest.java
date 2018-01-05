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

import org.junit.Before;
import org.junit.Test;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class ManagedAssetsTest {

    private Assets delegate = mock(Assets.class);

    private ReadableResource readableResource = mock(ReadableResource.class);

    private final ManagedAssets assets = new ManagedAssets(
            delegate,
            new HashMap<>()
    );

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(assets.resolve(any(String.class), any(Class.class)))
                .thenReturn(readableResource);
    }

    @Test
    public void shouldLoadOnce() {
        when(readableResource.read(eq("a"), eq(assets)))
                .thenReturn(Optional.of("A"));
        assertSame(
                assets.load("a", String.class),
                assets.load("a", String.class)
        );
        verify(delegate, atLeast(1)).resolve(eq("a"), eq(String.class));
        verify(delegate, atMost(1)).resolve(eq("a"), eq(String.class));
        verify(delegate, atMost(1)).open(eq("a"));
    }

    @Test
    public void shouldLoadSubAssetOnce() {
        when(readableResource.read(eq("a"), eq(assets)))
                .then(invocation -> {
                            final Assets assets = invocation.getArgument(1);
                            assertEquals(assets.load("b", Double.class), Math.PI, 0.0001d);
                            return Optional.of("A");
                        }
                );
        when(readableResource.read(eq("b"), eq(assets)))
                .thenReturn(Optional.of(Math.PI));
        assertSame(
                assets.load("a", String.class),
                assets.load("a", String.class)
        );
        verify(delegate, atLeast(1)).resolve(eq("a"), eq(String.class));
        verify(delegate, atMost(1)).resolve(eq("a"), eq(String.class));
        verify(delegate, atMost(1)).open(eq("a"));
        verify(delegate, atLeast(1)).resolve(eq("b"), eq(Double.class));
        verify(delegate, atMost(1)).resolve(eq("b"), eq(Double.class));
        verify(delegate, atMost(1)).open(eq("b"));
    }

    @Test
    public void shouldCloseAutoCloseables() throws Exception {
        final AutoCloseable a = mock(AutoCloseable.class);
        when(readableResource.read(eq("ac"), eq(assets)))
                .thenReturn(Optional.of(a));
        assertSame(a, assets.load("ac", AutoCloseable.class));
        assets.close();
        verify(a, atLeast(1)).close();
    }

    @Test
    public void shouldCloseCloseables() throws Exception {
        final Closeable c = mock(Closeable.class);
        when(readableResource.read(eq("c"), eq(assets)))
                .thenReturn(Optional.of(c));
        assertSame(c, assets.load("c", Closeable.class));
        assets.close();
        verify(c, atLeast(1)).close();
    }

}