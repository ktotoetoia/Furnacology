package com.im.infrastructure.assets;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public final class ClasspathAssets
{
    public static <T> T read(ResourceLocation asset, IAssetReader<T> reader, T missing)
    {
        String path = "/assets/" + asset.getNamespace() + "/" + asset.getPath();

        try (InputStream stream = ClasspathAssets.class.getResourceAsStream(path))
        {
            return stream == null ? missing : reader.read(stream);
        }
        catch (IOException | RuntimeException exception)
        {
            return missing;
        }
    }

    public interface IAssetReader<T>
    {
        T read(InputStream stream) throws IOException;
    }

    private ClasspathAssets() { }
}