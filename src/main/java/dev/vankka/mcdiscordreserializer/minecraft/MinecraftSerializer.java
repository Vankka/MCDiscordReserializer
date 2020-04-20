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

package dev.vankka.mcdiscordreserializer.minecraft;

import dev.vankka.mcdiscordreserializer.renderer.MinecraftRenderer;
import dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer;
import dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules;
import dev.vankka.simpleast.core.TextStyle;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.StyleNode;
import dev.vankka.simpleast.core.node.TextNode;
import dev.vankka.simpleast.core.parser.Parser;
import dev.vankka.simpleast.core.parser.Rule;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * MinecraftSerializer, for serializing from Discord messages to MC TextComponents.
 *
 * @author Vankka
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MinecraftSerializer {

    /**
     * Default instance of the MinecraftSerializer, incase that's all you need.
     * Using {@link MinecraftSerializer#setParser(Parser)} is not allowed.
     */
    public static final MinecraftSerializer INSTANCE = new MinecraftSerializer(false) {
        @Override
        public void setParser(Parser<Object, Node<Object>, Object> parser) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }

        @Override
        public void setRenderer(MinecraftRenderer renderer) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    private Parser<Object, Node<Object>, Object> parser;
    private MinecraftRenderer renderer;

    /**
     * Constructor for creating a serializer with a {@link Parser}
     * and adds the {@link DiscordMarkdownRules#createAllRulesForDiscord(boolean)} with the text rule.
     * Uses {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer} for rendering parts of messages.
     */
    public MinecraftSerializer(boolean debugging) {
        this.parser = new Parser<>(debugging);
        this.renderer = new DefaultMinecraftRenderer();
        parser.addRules(DiscordMarkdownRules.createAllRulesForDiscord(true));
    }

    /**
     * Constructor for creating a serializer with specified arguments (by default).
     *
     * @param parser The {@link Parser} used by the serializer
     */
    public MinecraftSerializer(Parser<Object, Node<Object>, Object> parser, MinecraftRenderer renderer) {
        this.parser = parser;
        this.renderer = renderer;
    }

    /**
     * Constructor for creating a serializer with specified arguments (by default).
     *
     * @param parser            The {@link Parser} for this serializer
     * @param renderer          The {@link MinecraftRenderer} for this serializer
     * @param rules             Rules for the parser
     */
    public MinecraftSerializer(Parser<Object, Node<Object>, Object> parser, MinecraftRenderer renderer, List<Rule<Object, Node<Object>, Object>> rules) {
        this(parser, renderer);
        parser.addRules(rules);
    }

    /**
     * Returns the current parser used by this serializer.
     *
     * @return the {@link Parser}
     */
    public Parser<Object, Node<Object>, Object> getParser() {
        return parser;
    }

    /**
     * Sets the new parser for this serializer.
     *
     * @param parser the new {@link Parser} for this serializer.
     */
    public void setParser(Parser<Object, Node<Object>, Object> parser) {
        this.parser = parser;
    }

    /**
     * Returns the renderer for this serializer.
     *
     * @return the {@link MinecraftRenderer}
     */
    public MinecraftRenderer getRenderer() {
        return renderer;
    }

    /**
     * Sets the new renderer for this serializer.
     *
     * @param renderer the {@link MinecraftRenderer}
     */
    public void setRenderer(MinecraftRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft TextComponent without debug logging.
     *
     * @param discordMessage a Discord markdown message
     * @return the Discord message formatted to a Minecraft TextComponent
     */
    public Component serialize(final String discordMessage) {
        return serialize(discordMessage, false);
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft TextComponent.
     *
     * @param discordMessage a Discord markdown message
     * @param debugLogging   true to enable debug logging for the SimpleAST parser
     * @return the Discord message formatted to a Minecraft TextComponent
     */
    public Component serialize(final String discordMessage, boolean debugLogging) {
        List<Component> components = new ArrayList<>();

        List<Node<Object>> nodes = parser.parse(discordMessage, null);
        for (Node<Object> node : nodes) {
            components.add(process(node, TextComponent.empty()));
        }

        return TextComponent.empty().children(components);
    }

    private Component process(final Node<Object> node, Component rootComponent) {
        Component component = TextComponent.empty()
                .mergeDecorations(rootComponent)
                .mergeEvents(rootComponent)
                .mergeColor(rootComponent);

        if (node instanceof TextNode) {
            component = ((TextComponent) component).content(((TextNode<?>) node).getContent());
        } else if (node instanceof StyleNode) {
            List<TextStyle> styles = new ArrayList<>(((StyleNode<?, TextStyle>) node).getStyles());
            for (TextStyle style : styles) {
                switch (style.getType()) {
                    case STRIKETHROUGH:
                        component = renderer.strikethrough(component);
                        break;
                    case UNDERLINE:
                        component = renderer.underline(component);
                        break;
                    case ITALICS:
                        component = renderer.italics(component);
                        break;
                    case BOLD:
                        component = renderer.bold(component);
                        break;
                    case SPOILER:
                        TextComponent content = TextComponent.empty();
                        for (Node<Object> objectNode : parser.parse(style.getExtra().get("content"), null)) {
                            content = content.append(process(objectNode, component));
                        }

                        component = renderer.spoiler(component, content);
                        break;
                    case CODE_STRING:
                        component = renderer.codeString(component);
                        ((StyleNode<?, TextStyle>) node).getStyles().remove(style);
                        break;
                    case CODE_BLOCK:
                        component = renderer.codeBlock(component);
                        ((StyleNode<?, TextStyle>) node).getStyles().remove(style);
                        break;
                    case QUOTE:
                        content = TextComponent.empty();
                        for (Node<Object> objectNode : parser.parse(style.getExtra().get("content"), null)) {
                            content = content.append(process(objectNode, component));
                        }

                        component = renderer.quote(component, content);
                        break;
                    case MENTION_EMOJI:
                        component = renderer.emoteMention(component, style.getExtra().get("name"), style.getExtra().get("id"));
                        break;
                    case MENTION_CHANNEL:
                        component = renderer.channelMention(component, style.getExtra().get("id"));
                        break;
                    case MENTION_USER:
                        component = renderer.userMention(component, style.getExtra().get("id"));
                        break;
                    case MENTION_ROLE:
                        component = renderer.roleMention(component, style.getExtra().get("id"));
                        break;
                    default:
                        break;
                }
            }
        }

        Collection<Node<Object>> children = node.getChildren();
        if (children != null) {
            for (Node<Object> child : children) {
                component = component.append(process(child, component));
            }
        }

        return component;
    }
}
