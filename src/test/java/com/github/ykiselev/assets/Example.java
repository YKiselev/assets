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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Example {

    public static void main(String[] args) {
        // 1
        Resources resources = resource -> Example.class.getResourceAsStream(resource.toString());
        // 2
        Function<Class, ReadableResource> byClass = clazz -> {
            if (String.class.isAssignableFrom(clazz)) {
                return (stream, resource, assets) -> readText(stream);
            } else {
                throw new IllegalArgumentException("Unsupported resource class:" + clazz);
            }
        };
        // 3
        Function<String, ReadableResource> byExtension = ext -> {
            if ("text".equals(ext)) {
                return (stream, resource, assets) -> readText(stream);
            } else {
                throw new IllegalArgumentException("Unsupported resource extension:" + ext);
            }
        };
        // Create instance of ManagedAssets which will delegate real work to SimpleAssets
        ManagedAssets managedAssets = new ManagedAssets(
                new SimpleAssets(resources, byClass, byExtension),
                new HashMap<>()
        );
        // Now we can load assets
        String AssetByClass = managedAssets.load("/sample.txt", String.class);
        String AssetByExtension = managedAssets.load("/sample.txt", null);
        assertEquals("Hello, World!", AssetByClass);
        assertSame(
                AssetByClass,
                AssetByExtension
        );
    }

    /**
     * Please note: this method replaces line endings with '\n' which is Ok here but please don't use it in production.
     */
    private static String readText(InputStream stream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}