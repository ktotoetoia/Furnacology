package com.im.visuals.audio;

import com.google.gson.JsonParser;
import com.im.core.tags.ITagLibrary;
import com.im.core.tags.Tag;
import com.im.core.tags.TagLibrary;
import com.im.infrastructure.assets.ClasspathAssets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class SoundScanner
{
    private final String _namespace;
    private final Set<String> _events;

    public SoundScanner(String namespace)
    {
        _namespace = namespace;
        _events = ClasspathAssets.read(ResourceLocation.fromNamespaceAndPath(namespace, "sounds.json"),
                stream -> Set.copyOf(JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject().keySet()), Set.of());
    }

    public TagLibrary<SoundEvent> library(String group, List<Entry> entries, @Nullable ITagLibrary<SoundEvent> parent)
    {
        TagLibrary<SoundEvent> library=new TagLibrary<SoundEvent>().withParent(parent);

        for (Entry entry : entries)
        {
            String event = group + "." + entry.tag().name();

            if (_events.contains(event)) library.add(entry.tag(), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(_namespace, event)));
            else if (parent == null && entry.fallback() != null)  library.add(entry.tag(), entry.fallback().get());
        }

        return library;
    }

    public record Entry(Tag<SoundEvent> tag, @Nullable Supplier<SoundEvent> fallback)
    {
        public static Entry of(Tag<SoundEvent> tag) { return new Entry(tag, null); }
        public static Entry of(Tag<SoundEvent> tag, Supplier<SoundEvent> fallback) { return new Entry(tag, fallback); }
    }
}