package com.im.gameplay.items;

import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.regex.Pattern;

public final class ItemDescription
{
    private static final String Line = ".tooltip.";
    private static final String Bonus = "+";
    private static final Pattern Chance = Pattern.compile("^[0-9]+(\\.[0-9]+)?%");
    private static final int MaxLines = 8;

    public static void append(String descriptionId, List<Component> tooltip)
    {
        Language language = Language.getInstance();

        for (int line = 1; line <= MaxLines && language.has(descriptionId + Line + line); line++)
        {
            String key = descriptionId + Line + line;
            tooltip.add(Component.translatable(key).withStyle(getColorOf(language.getOrDefault(key))));
        }
    }

    private static ChatFormatting getColorOf(String text)
    {
        if (text.startsWith(Bonus)) return ChatFormatting.GREEN;
        if (Chance.matcher(text).find()) return ChatFormatting.YELLOW;

        return ChatFormatting.GRAY;
    }
}