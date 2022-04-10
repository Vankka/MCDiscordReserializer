/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2018-2022 Vankka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.vankka.mcdiscordreserializer.renderer.implementation;

import dev.vankka.mcdiscordreserializer.renderer.MinecraftRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * The default implementation for the {@link MinecraftRenderer}.
 */
public class DefaultMinecraftRenderer implements MinecraftRenderer {

    /**
     * The instance of {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer}.
     */
    public static final DefaultMinecraftRenderer INSTANCE = new DefaultMinecraftRenderer();

    private static final Pattern PATTERN_NEWLINE = Pattern.compile("\n");

    /**
     * Creates a new instance of the {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer} unless you're extending the class you shouldn't use this.
     * @see #INSTANCE
     */
    public DefaultMinecraftRenderer() {
    }

    @Override
    public Component link(@NotNull Component part, String link) {
        return part.clickEvent(ClickEvent.openUrl(link));
    }

    @Override
    @NotNull
    public Component strikethrough(@NotNull Component component) {
        return component.decoration(TextDecoration.STRIKETHROUGH, true);
    }

    @Override
    @NotNull
    public Component underline(@NotNull Component component) {
        return component.decoration(TextDecoration.UNDERLINED, true);
    }

    @Override
    @NotNull
    public Component italics(@NotNull Component component) {
        return component.decoration(TextDecoration.ITALIC, true);
    }

    @Override
    @NotNull
    public Component bold(@NotNull Component component) {
        return component.decoration(TextDecoration.BOLD, true);
    }

    @Override
    @NotNull
    public Component codeString(@NotNull Component component) {
        return component.color(NamedTextColor.DARK_GRAY);
    }

    @Override
    @NotNull
    public Component codeBlock(@NotNull Component component) {
        return component.color(NamedTextColor.DARK_GRAY);
    }

    @Override
    @NotNull
    public Component appendSpoiler(@NotNull Component component, @NotNull Component content) {
        return component.append(content.decoration(TextDecoration.OBFUSCATED, true)
                .color(NamedTextColor.DARK_GRAY).hoverEvent(HoverEvent.showText(content)));
    }

    private static final Component QUOTE_PREFIX = Component.text("| ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD);
    private static final TextReplacementConfig QUOTE_REPLACEMENT = TextReplacementConfig.builder()
            .match(PATTERN_NEWLINE).replacement(builder -> builder.append(QUOTE_PREFIX)).build();

    @Override
    @NotNull
    public Component appendQuote(@NotNull Component component, @NotNull Component content) {
        return Component.empty().append(QUOTE_PREFIX).append(component.replaceText(QUOTE_REPLACEMENT));
    }

    @Override
    @NotNull
    public Component appendEmoteMention(@NotNull Component component, @NotNull String name, @NotNull String id) {
        return component.append(Component.text(":" + name + ":"));
    }

    @Override
    @NotNull
    public Component appendChannelMention(@NotNull Component component, @NotNull String id) {
        return component.append(Component.text("<#" + id + ">"));
    }

    @Override
    @NotNull
    public Component appendUserMention(@NotNull Component component, @NotNull String id) {
        return component.append(Component.text("<@" + id + ">"));
    }

    @Override
    @NotNull
    public Component appendRoleMention(@NotNull Component component, @NotNull String id) {
        return component.append(Component.text("<@&" + id + ">"));
    }
}
