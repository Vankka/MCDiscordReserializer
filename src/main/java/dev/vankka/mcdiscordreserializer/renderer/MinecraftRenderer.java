/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2018-2021 Vankka
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

import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions;
import dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules;
import dev.vankka.simpleast.core.TextStyle;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.StyleNode;
import dev.vankka.simpleast.core.node.TextNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Interface for rendering formatting {@link dev.vankka.simpleast.core.node.Node}s into Minecraft
 * {@link net.kyori.adventure.text.Component}s for standard {@link dev.vankka.simpleast.core.TextStyle}s.
 */
public interface MinecraftRenderer extends MinecraftNodeRenderer {

    @Override
    default Component render(@NotNull Component component,
                             @NotNull Node<Object> node,
                             @NotNull MinecraftSerializerOptions<Component> serializerOptions,
                             @NotNull Function<Node<Object>, Component> renderWithChildren) {
        if (node instanceof TextNode) {
            component = ((TextComponent) component).content(((TextNode<?>) node).getContent());
        } else if (node instanceof StyleNode) {
            List<TextStyle> styles = new ArrayList<>(((StyleNode<?, TextStyle>) node).getStyles());
            for (TextStyle style : styles) {
                switch (style.getType()) {
                    case STRIKETHROUGH:
                        component = strikethrough(component);
                        break;
                    case UNDERLINE:
                        component = underline(component);
                        break;
                    case ITALICS:
                        component = italics(component);
                        break;
                    case BOLD:
                        component = bold(component);
                        break;
                    case CODE_STRING:
                        component = codeString(component);
                        ((StyleNode<?, TextStyle>) node).getStyles().remove(style);
                        break;
                    case CODE_BLOCK:
                        component = codeBlock(component);
                        ((StyleNode<?, TextStyle>) node).getStyles().remove(style);
                        break;
                    case QUOTE:
                        TextComponent content = Component.empty();
                        for (Node<Object> objectNode : serializerOptions.getParser().parse(style.getExtra().get("content"),
                                new DiscordMarkdownRules.QuoteState(true),
                                serializerOptions.getRules(),
                                serializerOptions.isDebuggingEnabled())) {
                            content = content.append(renderWithChildren.apply(objectNode));
                        }

                        component = appendQuote(component, content);
                        break;
                    case SPOILER:
                        content = Component.empty();
                        for (Node<Object> objectNode : serializerOptions.getParser().parse(style.getExtra().get("content"),
                                null, serializerOptions.getRules(), serializerOptions.isDebuggingEnabled())) {
                            content = content.append(renderWithChildren.apply(objectNode));
                        }

                        component = appendSpoiler(component, content);
                        break;
                    case MENTION_EMOJI:
                        component = appendEmoteMention(component, style.getExtra().get("name"), style.getExtra().get("id"));
                        break;
                    case MENTION_CHANNEL:
                        component = appendChannelMention(component, style.getExtra().get("id"));
                        break;
                    case MENTION_USER:
                        component = appendUserMention(component, style.getExtra().get("id"));
                        break;
                    case MENTION_ROLE:
                        component = appendRoleMention(component, style.getExtra().get("id"));
                        break;
                    default:
                        break;
                }
                if (component == null) {
                    break;
                }
            }
        }

        return component;
    }

    /**
     * Renders the provided {@link Component} as strikethrough.
     *
     * @param part the {@link Component} to render as strikethrough
     * @return the strikethrough {@link Component} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component strikethrough(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as underlined.
     *
     * @param part the {@link Component} to render as underlined
     * @return the underlined {@link Component} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component underline(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as italics.
     *
     * @param part the {@link Component} to render as italics
     * @return the italics {@link Component} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component italics(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as bold.
     *
     * @param part the {@link Component} to render as bold
     * @return the bold {@link Component} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component bold(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as a code string.
     *
     * @param part the {@link Component} to render the code string to
     * @return the code stringed {@link Component} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component codeString(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as a code block.
     *
     * @param part the {@link Component} to render as a code block
     * @return the code blocked {@link Component} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component codeBlock(@NotNull Component part);

    /**
     * Renders the spoiler and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render the spoiler to
     * @param content   the content of the spoiler
     * @return the spoiler'ed {@link Component} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component appendSpoiler(@NotNull Component component, @NotNull Component content);

    /**
     * Adds the required formatting for quotes to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param content   the content of the quote
     * @return the {@link Component} with the quote rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component appendQuote(@NotNull Component component, @NotNull Component content);

    /**
     * Renders a emote mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param name      the name of the emote
     * @param id        the id of the emote
     * @return the {@link Component} with emote rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component appendEmoteMention(@NotNull Component component, @NotNull String name, @NotNull String id);

    /**
     * Renders a channel mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param id        the id of the channel
     * @return the {@link Component} with the channel mention rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component appendChannelMention(@NotNull Component component, @NotNull String id);

    /**
     * Renders a user mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param id        the id of the user
     * @return the {@link Component} with the user mention rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component appendUserMention(@NotNull Component component, @NotNull String id);

    /**
     * Renders a role mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param id        the id of the role
     * @return the {@link Component} with the role mention rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    Component appendRoleMention(@NotNull Component component, @NotNull String id);
}
