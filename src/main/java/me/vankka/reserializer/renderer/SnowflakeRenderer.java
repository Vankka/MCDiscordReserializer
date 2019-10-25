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

package me.vankka.reserializer.renderer;

import net.kyori.text.Component;

/**
 * Interface for rendering snowflakes (ids) into Minecraft {@link Component}s.
 */
public interface SnowflakeRenderer {

    /**
     * Renders a emote mention into a Minecraft {@link Component}.
     *
     * @param name The name of the emote
     * @param id   The id of the emote
     * @return The {@link Component}
     */
    Component renderEmoteMention(String name, String id);

    /**
     * Renders a channel mention into a Minecraft {@link Component}.
     *
     * @param id The id of the channel
     * @return The {@link Component}
     */
    Component renderChannelMention(String id);

    /**
     * Renders a user mention into a Minecraft {@link Component}.
     *
     * @param id The id of the user
     * @return The {@link Component}
     */
    Component renderUserMention(String id);

    /**
     * Renders a role mention into a Minecraft {@link Component}.
     *
     * @param id The id of the role
     * @return The {@link Component}
     */
    Component renderRoleMention(String id);
}
