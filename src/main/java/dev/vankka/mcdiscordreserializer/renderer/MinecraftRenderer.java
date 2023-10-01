/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2018-2023 Vankka
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
import dev.vankka.mcdiscordreserializer.rules.StyleNode;
import dev.vankka.simpleast.core.node.Node;
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
            List<StyleNode.Style> styles = new ArrayList<>(((StyleNode<?, StyleNode.Style>) node).getStyles());
            for (StyleNode.Style style : styles) {
                if (style instanceof StyleNode.MentionStyle) {
                    StyleNode.MentionStyle mentionStyle = (StyleNode.MentionStyle) style;
                    String id = mentionStyle.getId();
                    switch (mentionStyle.getType()) {
                        case ROLE: {
                            component = appendRoleMention(component, id);
                            break;
                        }
                        case USER: {
                            component = appendUserMention(component, id);
                            break;
                        }
                        case CHANNEL: {
                            component = appendChannelMention(component, id);
                            break;
                        }
                    }
                } else if (style instanceof StyleNode.EmojiStyle) {
                    StyleNode.EmojiStyle emojiStyle = (StyleNode.EmojiStyle) style;
                    component = appendEmoteMention(component, emojiStyle.getId(), emojiStyle.getName());
                } else if (style instanceof StyleNode.CodeBlockStyle) {
                    StyleNode.CodeBlockStyle codeBlockStyle = (StyleNode.CodeBlockStyle) style;
                    component = codeBlock(component, codeBlockStyle.getLanguage());
                    ((StyleNode<?, StyleNode.Style>) node).getStyles().remove(style);
                } else if (style instanceof StyleNode.ContentStyle) {
                    StyleNode.ContentStyle contentStyle = (StyleNode.ContentStyle) style;
                    switch (contentStyle.getType()) {
                        case LINK: {
                            component = appendLink(component, contentStyle.getContent());
                            break;
                        }
                        case QUOTE: {
                            TextComponent content = Component.empty();
                            List<Node<Object>> nodes = serializerOptions.getParser().parse(
                                    contentStyle.getContent(),
                                    new DiscordMarkdownRules.QuoteState(true),
                                    serializerOptions.getRules(),
                                    serializerOptions.isDebuggingEnabled()
                            );
                            for (Node<Object> objectNode : nodes) {
                                content = content.append(renderWithChildren.apply(objectNode));
                            }

                            component = appendQuote(component, content);
                            break;
                        }
                        case SPOILER: {
                            TextComponent content = Component.empty();
                            List<Node<Object>> nodes = serializerOptions.getParser().parse(
                                    contentStyle.getContent(),
                                    null,
                                    serializerOptions.getRules(),
                                    serializerOptions.isDebuggingEnabled()
                            );
                            for (Node<Object> objectNode : nodes) {
                                content = content.append(renderWithChildren.apply(objectNode));
                            }

                            component = appendSpoiler(component, content);
                            break;
                        }
                    }
                } else if (style instanceof StyleNode.ItalicStyle) {
                    component = italics(component);
                } else if (style instanceof StyleNode.Styles) {
                    switch ((StyleNode.Styles) style) {
                        case CODE_STRING: {
                            component = codeString(component);
                            ((StyleNode<?, StyleNode.Style>) node).getStyles().remove(style);
                            break;
                        }
                        case BOLD: {
                            component = bold(component);
                            break;
                        }
                        case UNDERLINE: {
                            component = underline(component);
                            break;
                        }
                        case STRIKETHROUGH: {
                            component = strikethrough(component);
                            break;
                        }
                    }
                }
                if (component == null) {
                    break;
                }
            }
        }

        return component;
    }

    /**
     * Renders the provided {@link net.kyori.adventure.text.Component} with a link.
     *
     * @param part the {@link net.kyori.adventure.text.Component} to render with a link.
     * @return the {@link net.kyori.adventure.text.Component} with the link or {@code null} if this renderer does not process that kind of style
     */
    @Deprecated
    Component link(@NotNull Component part, String link);

    /**
     * Renders the link and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render the link to
     * @param link      the link
     * @return the {@link Component} with the link or {@code null} if this renderer does not process that kind of style
     */
    default Component appendLink(@NotNull Component component, String link) {
        Component linkComponent = Component.empty();
        linkComponent = link(linkComponent, link);
        if (linkComponent == null) {
            return null;
        }

        return component.append(linkComponent.append(Component.text(link)));
    }

    /**
     * Renders the provided {@link Component} as strikethrough.
     *
     * @param part the {@link Component} to render as strikethrough
     * @return the strikethrough {@link Component} or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component strikethrough(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as underlined.
     *
     * @param part the {@link Component} to render as underlined
     * @return the underlined {@link Component} or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component underline(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as italics.
     *
     * @param part the {@link Component} to render as italics
     * @return the italics {@link Component} or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component italics(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as bold.
     *
     * @param part the {@link Component} to render as bold
     * @return the bold {@link Component} or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component bold(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as a code string.
     *
     * @param part the {@link Component} to render the code string to
     * @return the code stringed {@link Component} or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component codeString(@NotNull Component part);

    /**
     * Renders the provided {@link Component} as a code block.
     *
     * @param part the {@link Component} to render as a code block
     * @param language the language of the code block
     * @return the code blocked {@link Component} or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    default Component codeBlock(@NotNull Component part, @Nullable String language) {
        return codeBlock(part);
    }

    /**
     * Renders the provided {@link Component} as a code block. Will not be executed if {@link #codeBlock(net.kyori.adventure.text.Component, String)} if is overridden.
     *
     * @param part the {@link Component} to render as a code block
     * @return the code blocked {@link Component} or {@code null} if this renderer does not process that kind of style
     * @see #codeBlock(net.kyori.adventure.text.Component, String)
     */
    @Nullable
    Component codeBlock(@NotNull Component part);

    /**
     * Renders the spoiler and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render the spoiler to
     * @param content   the content of the spoiler
     * @return the spoiler'ed {@link Component} or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component appendSpoiler(@NotNull Component component, @NotNull Component content);

    /**
     * Adds the required formatting for quotes to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param content   the content of the quote
     * @return the {@link Component} with the quote rendered or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component appendQuote(@NotNull Component component, @NotNull Component content);

    /**
     * Renders a emote mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param name      the name of the emote
     * @param id        the id of the emote
     * @return the {@link Component} with emote rendered or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component appendEmoteMention(@NotNull Component component, @NotNull String name, @NotNull String id);

    /**
     * Renders a channel mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param id        the id of the channel
     * @return the {@link Component} with the channel mention rendered or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component appendChannelMention(@NotNull Component component, @NotNull String id);

    /**
     * Renders a user mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param id        the id of the user
     * @return the {@link Component} with the user mention rendered or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component appendUserMention(@NotNull Component component, @NotNull String id);

    /**
     * Renders a role mention and appends it to the provided {@link Component}.
     *
     * @param component the {@link Component} to render to
     * @param id        the id of the role
     * @return the {@link Component} with the role mention rendered or {@code null} if this renderer does not process that kind of style
     */
    @Nullable
    Component appendRoleMention(@NotNull Component component, @NotNull String id);
}
