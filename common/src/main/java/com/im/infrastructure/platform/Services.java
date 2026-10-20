package com.im.infrastructure.platform;

import java.util.ServiceLoader;

public final class Services
{
    public static final IPlatformHelper Platform = load(IPlatformHelper.class);

    public static <T> T load(Class<T> type)
    {
        return ServiceLoader.load(type).findFirst().orElseThrow(() -> new IllegalStateException("No implementation found for " + type.getName()));
    }
}