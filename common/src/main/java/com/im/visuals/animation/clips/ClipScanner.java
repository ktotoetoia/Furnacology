package com.im.visuals.animation.clips;

import com.im.core.tags.ITagLibrary;
import com.im.core.tags.Tag;
import com.im.core.tags.TagLibrary;
import com.im.core.values.random.WeightedRandom;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public final class ClipScanner
{
    private final Function<ResourceLocation, Integer> _frames;

    public ClipScanner(Function<ResourceLocation, Integer> frames) { _frames = frames; }

    public TagLibrary<Clip> library(ResourceLocation folder, List<Entry> entries, @Nullable ITagLibrary<Clip> parent)
    {
        TagLibrary<Clip> library = new TagLibrary<Clip>().withParent(parent);

        for (Entry entry : entries)
        {
            WeightedRandom<Clip> clips = scan(folder, entry.patterns());
            if (!clips.isEmpty())
                library.add(entry.tag(), clips);
        }

        return library;
    }

    private WeightedRandom<Clip> scan(ResourceLocation folder, List<ClipPattern> patterns)
    {
        WeightedRandom<Clip> variants = new WeightedRandom<>();

        for (ClipPattern pattern : patterns)
        {
            for (int variant = 1; ; variant++)
            {
                ResourceLocation texture = folder.withSuffix("/" + pattern.prefix() + variant);
                int count = _frames.apply(texture);

                if (count <= 0) break;

                variants.add(Clip.of(texture, count, pattern.ticksPerFrame().apply(count)), pattern.weight());
            }
        }

        return variants;
    }

    public record Entry(Tag<Clip> tag, List<ClipPattern> patterns)
    {
        public static Entry of(Tag<Clip> tag, ClipPattern... patterns) { return new Entry(tag, List.of(patterns)); }
    }
}