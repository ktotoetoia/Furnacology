package com.im.visuals.animation.clips;

import com.im.infrastructure.assets.ClasspathAssets;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class TextureFrames
{
    public static int count(ResourceLocation texture)
    {
        return ClasspathAssets.read(texture.withPath(path -> "textures/" + path + ".png"), TextureFrames::count, 0);
    }

    private static int count(InputStream stream) throws IOException
    {
        BufferedImage image = ImageIO.read(stream);

        if (image == null) return 0;

        return image.getWidth() > 0 ? image.getHeight() / image.getWidth() : 0;
    }
}