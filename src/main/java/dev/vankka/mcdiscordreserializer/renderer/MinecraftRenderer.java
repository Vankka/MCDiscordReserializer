/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2018-2020 Vankka
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
 * Interface for rendering formatting into Minecraft {@link net.kyori.text.Component}s.<br/>
 */
public interface MinecraftRenderer {

    /**
     * Renders the provided {@link Component} as strikethrough.
     *
     * @param part the {@link Component} to render as strikethrough
     * @return the strikethrough {@link Component}
     */
    Component strikethrough(Component part);

    /**
     * Renders the provided {@link Component} as underlined.
     *
     * @param part the {@link Component} to render as underlined
     * @return the underlined {@link Component}
     */
    Component underline(Component part);

    /**
     * Renders the provided {@link Component} as italics.
     *
     * @param part the {@link Component} to render as italics
     * @return the italics {@link Component}
     */
    Component italics(Component part);

    /**
     * Renders the provided {@link Component} as bold.
     *
     * @param part the {@link Component} to render as bold
     * @return the bold {@link Component}
     */
    Component bold(Component part);

    /**
     * Renders the provided {@link Component} as a code string.
     *
     * @param part the {@link Component} to render the code string to
     * @return the code stringed {@link Component}
     */
    Component codeString(Component part);

    /**
     * Renders the provided {@link Component} as a code block.
     *
     * @param part the {@link Component} to render as a code block
     * @return the code blocked {@link Component}
     */
    Component codeBlock(Component part);

    /**
     * Renders the spoiler and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render the spoiler to
     * @param content   the content of the spoiler
     * @return the spoiler'ed {@link Component}
     */
    Component appendSpoiler(Component component, Component content);

    /**
     * Renders the quote and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param content   the content of the quote
     * @return the {@link Component} with the quote rendered
     */
    Component appendQuote(Component component, Component content);

    /**
     * Renders a emote mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param name      the name of the emote
     * @param id        the id of the emote
     * @return the {@link Component} with emote rendered
     */
    Component appendEmoteMention(Component component, String name, String id);

    /**
     * Renders a channel mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param id        the id of the channel
     * @return the {@link Component} with the channel mention rendered
     */
    Component appendChannelMention(Component component, String id);

    /**
     * Renders a user mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param id        the id of the user
     * @return the {@link Component} with the user mention rendered
     */
    Component appendUserMention(Component component, String id);

    /**
     * Renders a role mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param id        the id of the role
     * @return the {@link Component} with the role mention rendered
     */
    Component appendRoleMention(Component component, String id);
}
