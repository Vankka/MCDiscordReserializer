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
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * MinecraftSerializer, for serializing from Discord messages to Minecraft {@link Component}s.
 *
 * @author Vankka
 *
 * @see MinecraftSerializerOptions
 * @see MinecraftRenderer
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MinecraftSerializer {

    /**
     * Default instance of the MinecraftSerializer, incase that's all you need.
     * Using {@link MinecraftSerializer#setDefaultOptions(MinecraftSerializerOptions)} is not allowed.
     */
    public static final MinecraftSerializer INSTANCE = new MinecraftSerializer() {

        @Override
        public void setDefaultOptions(MinecraftSerializerOptions defaultOptions) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }

        @SuppressWarnings("deprecation")
        @Override
        @Deprecated
        public void setParser(Parser<Object, Node<Object>, Object> parser) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }

        @SuppressWarnings("deprecation")
        @Override
        @Deprecated
        public void setRenderer(MinecraftRenderer renderer) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    /**
     * The default {@link MinecraftSerializerOptions} to use for this serializer.
     */
    @Getter
    @Setter
    private MinecraftSerializerOptions defaultOptions;
    private Parser<Object, Node<Object>, Object> parser;
    private MinecraftRenderer renderer;

    /**
     * Constructor for creating a serializer, with {@link MinecraftSerializerOptions#defaults()} as defaults.
     */
    public MinecraftSerializer() {
        this(MinecraftSerializerOptions.defaults());
    }

    /**
     * Constructor for creating a serializer, with the specified {@link MinecraftSerializerOptions} as defaults.
     *
     * @param defaultOptions the default serializer options (can be overridden on serialize)
     * @see MinecraftSerializerOptions#defaults()
     * @see MinecraftSerializerOptions#MinecraftSerializerOptions(Parser, List, MinecraftRenderer, boolean)
     */
    public MinecraftSerializer(@NonNull MinecraftSerializerOptions defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    /**
     * Constructor for creating a serializer with a {@link Parser}
     * and adds the {@link DiscordMarkdownRules#createAllRulesForDiscord(boolean)} with the text rule.
     * Uses {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer} for rendering parts of messages.
     * @deprecated Use {@link #MinecraftSerializer(MinecraftSerializerOptions)}
     */
    @Deprecated
    public MinecraftSerializer(boolean debugging) {
        this.parser = new Parser<>(debugging);
        this.renderer = new DefaultMinecraftRenderer();
        parser.addRules(DiscordMarkdownRules.createAllRulesForDiscord(true));
    }

    /**
     * Constructor for creating a serializer with specified arguments (by default).
     *
     * @param parser The {@link Parser} used by the serializer
     * @deprecated Use {@link #MinecraftSerializer(MinecraftSerializerOptions)}
     */
    @Deprecated
    public MinecraftSerializer(@NonNull Parser<Object, Node<Object>, Object> parser, @NonNull MinecraftRenderer renderer) {
        this.parser = parser;
        this.renderer = renderer;
    }

    /**
     * Constructor for creating a serializer with specified arguments (by default).
     *
     * @param parser            The {@link Parser} for this serializer
     * @param renderer          The {@link MinecraftRenderer} for this serializer
     * @param rules             Rules for the parser
     * @deprecated Use {@link #MinecraftSerializer(MinecraftSerializerOptions)}
     */
    @Deprecated
    public MinecraftSerializer(Parser<Object, Node<Object>, Object> parser, MinecraftRenderer renderer, List<Rule<Object, Node<Object>, Object>> rules) {
        this(parser, renderer);
        parser.addRules(rules);
    }

    /**
     * Returns the current parser used by this serializer.
     *
     * @return the {@link Parser}
     * @deprecated Use {@link #getDefaultOptions()} {@link MinecraftSerializerOptions#getParser()}
     */
    @Deprecated
    public Parser<Object, Node<Object>, Object> getParser() {
        return parser;
    }

    /**
     * Sets the new parser for this serializer.
     *
     * @param parser the new {@link Parser} for this serializer.
     * @deprecated Use {@link #setDefaultOptions(MinecraftSerializerOptions)} {@link MinecraftSerializerOptions#withParser(Parser)}
     */
    @Deprecated
    public void setParser(Parser<Object, Node<Object>, Object> parser) {
        this.parser = parser;
    }

    /**
     * Returns the renderer for this serializer.
     *
     * @return the {@link MinecraftRenderer}
     * @deprecated Use {@link #getDefaultOptions()} {@link MinecraftSerializerOptions#getRenderer()}
     */
    @Deprecated
    public MinecraftRenderer getRenderer() {
        return renderer;
    }

    /**
     * Sets the new renderer for this serializer.
     *
     * @param renderer the {@link MinecraftRenderer}
     * @deprecated Use {@link #setDefaultOptions(MinecraftSerializerOptions)} {@link MinecraftSerializerOptions#withRenderer(MinecraftRenderer)}
     */
    @Deprecated
    public void setRenderer(@NonNull MinecraftRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft {@link Component} using this serializer's
     * {@link MinecraftSerializer#getDefaultOptions() default options}.<br/>
     * Use {@link MinecraftSerializer#serialize(String, MinecraftSerializerOptions)} to fine tune the serialization options.
     *
     * @param discordMessage a Discord markdown message
     * @return the Discord message formatted to a Minecraft TextComponent
     */
    public Component serialize(@NonNull final String discordMessage) {
        MinecraftSerializerOptions options = getDefaultOptions();
        if (parser != null) {
            options.withParser(parser);
        }
        if (renderer != null) {
            options.withRenderer(renderer);
        }
        return serialize(discordMessage, options);
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft TextComponent.
     *
     * @param discordMessage a Discord markdown message
     * @param debugLogging   true to enable debug logging for the SimpleAST {@link Parser}
     * @return the Discord message formatted to a Minecraft TextComponent
     * @deprecated Use {@link #serialize(String, MinecraftSerializerOptions)} {@link MinecraftSerializerOptions#withDebuggingEnabled(boolean)}
     */
    @Deprecated
    public Component serialize(@NonNull final String discordMessage, boolean debugLogging) {
        MinecraftSerializerOptions options = getDefaultOptions();
        options = options.withDebuggingEnabled(debugLogging);
        if (parser != null) {
            options.withParser(parser);
        }
        if (renderer != null) {
            options.withRenderer(renderer);
        }
        return serialize(discordMessage, options);
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft {@link Component}.
     *
     * @param discordMessage    a Discord markdown message
     * @param serializerOptions The options to use for this serialization
     * @return the Discord message formatted to a Minecraft TextComponent
     * @see MinecraftSerializerOptions#defaults()
     * @see MinecraftSerializerOptions#MinecraftSerializerOptions(Parser, List, MinecraftRenderer, boolean)
     */
    public Component serialize(@NonNull final String discordMessage, @NonNull final MinecraftSerializerOptions serializerOptions) {
        List<Component> components = new ArrayList<>();

        List<Node<Object>> nodes = serializerOptions.getParser().parse(discordMessage, null, serializerOptions.getRules(), serializerOptions.isDebuggingEnabled());
        for (Node<Object> node : nodes) {
            components.add(addChild(node, TextComponent.empty(), serializerOptions.getRenderer()));
        }

        return TextComponent.empty().children(components);
    }

    private Component addChild(@NonNull final Node<Object> node, @NonNull final Component rootComponent, @NonNull final MinecraftRenderer renderer) {
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
                    case CODE_STRING:
                        component = renderer.codeString(component);
                        ((StyleNode<?, TextStyle>) node).getStyles().remove(style);
                        break;
                    case CODE_BLOCK:
                        component = renderer.codeBlock(component);
                        ((StyleNode<?, TextStyle>) node).getStyles().remove(style);
                        break;
                    case QUOTE:
                        TextComponent content = TextComponent.empty();
                        for (Node<Object> objectNode : parser.parse(style.getExtra().get("content"), null)) {
                            content = content.append(addChild(objectNode, component, renderer));
                        }

                        component = renderer.appendQuote(component, content);
                        break;
                    case SPOILER:
                        content = TextComponent.empty();
                        for (Node<Object> objectNode : parser.parse(style.getExtra().get("content"), null)) {
                            content = content.append(addChild(objectNode, component, renderer));
                        }

                        component = renderer.appendSpoiler(component, content);
                        break;
                    case MENTION_EMOJI:
                        component = renderer.appendEmoteMention(component, style.getExtra().get("name"), style.getExtra().get("id"));
                        break;
                    case MENTION_CHANNEL:
                        component = renderer.appendChannelMention(component, style.getExtra().get("id"));
                        break;
                    case MENTION_USER:
                        component = renderer.appendUserMention(component, style.getExtra().get("id"));
                        break;
                    case MENTION_ROLE:
                        component = renderer.appendRoleMention(component, style.getExtra().get("id"));
                        break;
                    default:
                        break;
                }
            }
        }

        Collection<Node<Object>> children = node.getChildren();
        if (children != null) {
            for (Node<Object> child : children) {
                component = component.append(addChild(child, component, renderer));
            }
        }

        return component;
    }
}
