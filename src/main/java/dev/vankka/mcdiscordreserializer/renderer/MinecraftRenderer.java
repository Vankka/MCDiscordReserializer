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

package dev.vankka.mcdiscordreserializer.renderer;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;

/**
 * Interface for rendering formatting into Minecraft {@link net.kyori.text.Component}s.
 */
public interface MinecraftRenderer {

    /**
     * Renders the provided {@link Component} as strikethrough.
     *
     * @param component the {@link Component}
     * @return the strikethrough {@link Component}
     */
    Component strikethrough(Component component);

    /**
     * Renders the provided {@link Component} as underlined.
     *
     * @param component the {@link Component}
     * @return the underlined {@link Component}
     */
    Component underline(Component component);

    /**
     * Renders the provided {@link Component} as italics.
     *
     * @param component the {@link Component}
     * @return the italics {@link Component}
     */
    Component italics(Component component);

    /**
     * Renders the provided {@link Component} as bold.
     *
     * @param component the {@link Component}
     * @return the bold {@link Component}
     */
    Component bold(Component component);

    /**
     * Renders the provided {@link Component} as a spoiler.
     *
     * @param component the {@link Component}
     * @return the spoiler'ed {@link Component}
     */
    Component spoiler(Component component, TextComponent content);

    /**
     * Renders the provided {@link Component} as a code string.
     *
     * @param component the {@link Component}
     * @return the code stringed {@link Component}
     */
    Component codeString(Component component);

    /**
     * Renders the provided {@link Component} as a code block.
     *
     * @param component the {@link Component}
     * @return the code blocked {@link Component}
     */
    Component codeBlock(Component component);

    /**
     * Renders the provided {@link Component} as a quote.
     *
     * @param component the {@link Component}
     * @return the quoted {@link Component}
     */
    Component quote(Component component, Component content);

    /**
     * Renders a emote mention into a Minecraft {@link Component}.
     *
     * @param name The name of the emote
     * @param id   The id of the emote
     * @return The {@link Component}
     */
    Component emoteMention(Component component, String name, String id);

    /**
     * Renders a channel mention into a Minecraft {@link Component}.
     *
     * @param id The id of the channel
     * @return The {@link Component}
     */
    Component channelMention(Component component, String id);

    /**
     * Renders a user mention into a Minecraft {@link Component}.
     *
     * @param id The id of the user
     * @return The {@link Component}
     */
    Component userMention(Component component, String id);

    /**
     * Renders a role mention into a Minecraft {@link Component}.
     *
     * @param id The id of the role
     * @return The {@link Component}
     */
    Component roleMention(Component component, String id);
}
