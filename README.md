[![Build Status](https://travis-ci.org/YKiselev/assets.svg?branch=master)](https://travis-ci.org/YKiselev/assets)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.ykiselev/assets.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.ykiselev%22%20AND%20a%3A%22assets%22)

# Assets
## Synopsis

Simple framework for asset management (asset here is any resource, for example image or sound).

# How to use

Asset framework consists of three interfaces:

## Resources interface
Low-level api to resolve resource URI to java.nio.channels.ReadableByteChannel
```java
interface Resources {

    ReadableByteChannel open(URI resource) throws ResourceException;
}
```
## Assets interface
Top-level interface which extends com.github.ykiselev.assets.Resources and adds methods to access registered com.github.ykiselev.assets.ReadableResource's or assets itself
```java
interface Assets extends Resources {

    /**
     * Resolves readable resource from supplied URI and class.
     */
    <T> ReadableResource<T> resolve(URI resource, Class<T> clazz) throws ResourceException;

    /**
     * Convenient method to resolve {@link ReadableResource} by asset class.
     */
    default <T> ReadableResource<T> resolve(Class<T> clazz) throws ResourceException {
        return resolve(null, clazz);
    }

    /**
     * Convenient method to resolve {@link ReadableResource} by asset class.
     */
    default <T> ReadableResource<T> resolve(URI resource) throws ResourceException {
        return resolve(resource, null);
    }

    /**
     * Loads asset using one of registered {@link ReadableResource}'s
     */
    default <T> T load(URI resource, Class<T> clazz) throws ResourceException {
        return resolve(resource, clazz).read(resource, this);
    }

    /**
     * Convenient method taking only one string argument as a resource name.
     */
    default <T> T load(String resource) throws ResourceException {
        return load(resource, null);
    }

    /**
     * Convenient method taking only one string argument as a resource name.
     */
    default <T> T load(String resource, Class<T> clazz) throws ResourceException {
        return load(URI.create(resource), clazz);
    }
}
```
## ReadableResource<T> interface
Api to be implemented by user for each supported asset class
```java
public interface ReadableResource<T> {

    /**
     * Convenient method to read resource by {@link URI}
     */
    default T read(URI resource, Assets assets) throws ResourceException {
        try (ReadableByteChannel channel = assets.open(resource)) {
            return read(channel, resource, assets);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * Reads resource from channel
     */
    T read(ReadableByteChannel channel, URI resource, Assets assets) throws ResourceException;
}
```

## Implementations
### SimpleAssets class 
This is a base implementation of Assets interface. Instance of this class will require implementation of com.github.ykiselev.assets.Resources (which will be 
used to resolve URI to ReadableByteChannel) and two functions: Function<Class, ReadableResource> - should resolve ReadableResource by specified asset class 
and Function<String, ReadableResource> - should resolve ReadableResource by asset's URI path extension.

### ManagedAssets class 
This class is intended to be used as decoration for other implementations of Assets. To create instance of this class user will need to provide implementation 
of Assets (for example - com.github.ykiselev.assets.SimpleAssets) and an instance of class implementing java.util.Map which will be used as internal cache, not 
only to speed-up consecutive calls with the same asset URI but also to release any system resources held by asset (asset class should implement Closeable or 
AutoCloseable interface). This cleanup is performed when method com.github.ykiselev.assets.ManagedAssets.close is called.  

## Usage
So user may use composition of provided classes plus implementations of three simple interfaces, like this:
```java
class Example {

    public static void main(String[] args) {
        // 1
        Resources resources = resource -> Channels.newChannel(
                        Example.class.getResourceAsStream(resource.toString())
                );
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

    // other methods skipped...

}
```
Full source code of this example can be found in src/test/java/com/github/ykiselev/assets/Example.java.

# License

This project is licensed under the Apache License, Version 2.0.