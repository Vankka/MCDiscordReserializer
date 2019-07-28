/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2019 Vankka
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

package me.vankka.reserializer.renderer.implementation;

import me.vankka.reserializer.renderer.SnowflakeRenderer;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;

/**
 * The default implementation for the {@link SnowflakeRenderer}.
 */
public class DefaultSnowflakeRenderer implements SnowflakeRenderer {
    @Override
    public Component renderEmoteMention(String name, String id) {
        return TextComponent.of(":" + name + ":");
    }

    @Override
    public Component renderChannelMention(String id) {
        return TextComponent.of("<#" + id + ">");
    }

    @Override
    public Component renderUserMention(String id) {
        return TextComponent.of("<@" + id + ">");
    }

    @Override
    public Component renderRoleMention(String id) {
        return TextComponent.of("<@&" + id + ">");
    }
}
