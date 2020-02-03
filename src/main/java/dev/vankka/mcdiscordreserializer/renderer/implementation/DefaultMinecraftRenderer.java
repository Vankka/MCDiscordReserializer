/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2020 Vankka
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
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;

/**
 * The default implementation for the {@link MinecraftRenderer}.
 */
public class DefaultMinecraftRenderer implements MinecraftRenderer {

    @Override
    public Component strikethrough(Component component) {
        return component.decoration(TextDecoration.STRIKETHROUGH, true);
    }

    @Override
    public Component underline(Component component) {
        return component.decoration(TextDecoration.UNDERLINED, true);
    }

    @Override
    public Component italics(Component component) {
        return component.decoration(TextDecoration.ITALIC, true);
    }

    @Override
    public Component bold(Component component) {
        return component.decoration(TextDecoration.BOLD, true);
    }

    @Override
    public Component spoiler(Component component, TextComponent content) {
        return component.append(content.decoration(TextDecoration.OBFUSCATED, true)
                .color(TextColor.DARK_GRAY).hoverEvent(HoverEvent.showText(content)));
    }

    @Override
    public Component codeString(Component component) {
        return component.color(TextColor.DARK_GRAY);
    }

    @Override
    public Component codeBlock(Component component) {
        return component.color(TextColor.DARK_GRAY);
    }

    @Override
    public Component quote(Component component) {
        return component
                .append(TextComponent.of("| ", TextColor.DARK_GRAY, TextDecoration.BOLD));
    }

    @Override
    public Component emoteMention(Component component, String name, String id) {
        return component.append(TextComponent.of(":" + name + ":"));
    }

    @Override
    public Component channelMention(Component component, String id) {
        return component.append(TextComponent.of("<#" + id + ">"));
    }

    @Override
    public Component userMention(Component component, String id) {
        return component.append(TextComponent.of("<@" + id + ">"));
    }

    @Override
    public Component roleMention(Component component, String id) {
        return component.append(TextComponent.of("<@&" + id + ">"));
    }
}
