[![Build Status](https://travis-ci.org/YKiselev/lwjgl3-playground.svg?branch=master)](https://travis-ci.org/YKiselev/assets)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.ykiselev/assets.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.ykiselev%22%20AND%20a%3A%22assets%22)

# Assets
## Synopsis

Simple framework for asset management.

# How to use

Asset framework consists of three interfaces:

## Resources interface
Low-level api to resolve resource URI to java.io.InputStream
```java
interface Resources {

    InputStream open(URI resource) throws ResourceException;
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

    default T read(URI resource, Assets assets) throws ResourceException {
        try (InputStream is = assets.open(resource)) {
            return read(is, assets);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    T read(InputStream inputStream, Assets assets) throws ResourceException;
}
```

## Implementations
### com.github.ykiselev.assets.SimpleAssets 
This is a base implementation of Assets interface. Instance of this class will require implementation of com.github.ykiselev.assets.Resources (which will be 
used to resolve URI to InputStream) and two functions: Function<Class, ReadableResource> - should resolve ReadableResource by specified asset class 
and Function<String, ReadableResource> - should resolve ReadableResource by asset's URI path extension.

### com.github.ykiselev.assets.ManagedAssets 
This class is intended to be used as decoration for other implementations of Assets. To create instance of this class user will need to provide implementation 
of Assets (for example - com.github.ykiselev.assets.SimpleAssets) and an instance of class implementing java.util.Map which will be used as internal cache, not 
only to speed-up consecutive calls with the same asset URI but also to release any system resources held by asset (asset class should implement Closeable or 
AutoCloseable interface). This cleanup is performed when method com.github.ykiselev.assets.ManagedAssets.close is called.  

# License

This project is licensed under the Apache License, Version 2.0.