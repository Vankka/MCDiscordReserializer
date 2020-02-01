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

import dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules;
import dev.vankka.mcdiscordreserializer.renderer.SnowflakeRenderer;
import dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultSnowflakeRenderer;
import dev.vankka.simpleast.core.TextStyle;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.StyleNode;
import dev.vankka.simpleast.core.node.TextNode;
import dev.vankka.simpleast.core.parser.Parser;
import dev.vankka.simpleast.core.parser.Rule;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;

import java.util.ArrayList;
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
        public void setSnowflakeRenderer(SnowflakeRenderer snowflakeRenderer) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    private Parser<Object, Node<Object>, Object> parser;
    private SnowflakeRenderer snowflakeRenderer;

    /**
     * Constructor for creating a serializer with a {@link Parser}
     * and adds the {@link DiscordMarkdownRules#createAllRulesForDiscord(boolean)} with the text rule.
     * Uses {@link DefaultSnowflakeRenderer} for rendering snowflakes.
     */
    public MinecraftSerializer(boolean debugging) {
        this.parser = new Parser<>(debugging);
        this.snowflakeRenderer = new DefaultSnowflakeRenderer();
        parser.addRules(DiscordMarkdownRules.createAllRulesForDiscord(true));
    }

    /**
     * Constructor for creating a serializer with specified arguments (by default).
     *
     * @param parser The {@link Parser} used by the serializer
     */
    public MinecraftSerializer(Parser<Object, Node<Object>, Object> parser, SnowflakeRenderer snowflakeRenderer) {
        this.parser = parser;
        this.snowflakeRenderer = snowflakeRenderer;
    }

    /**
     * Constructor for creating a serializer with specified arguments (by default).
     *
     * @param parser            The {@link Parser} for this serializer
     * @param snowflakeRenderer The {@link SnowflakeRenderer} for this serializer
     * @param rules             Rules for the parser
     */
    public MinecraftSerializer(Parser<Object, Node<Object>, Object> parser, SnowflakeRenderer snowflakeRenderer, List<Rule<Object, Node<Object>, Object>> rules) {
        this(parser, snowflakeRenderer);
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
     * Returns the snowflake renderer for this serializer.
     *
     * @return the {@link SnowflakeRenderer}
     */
    public SnowflakeRenderer getSnowflakeRenderer() {
        return snowflakeRenderer;
    }

    /**
     * Sets the new snowflake renderer for this serializer.
     *
     * @param snowflakeRenderer the {@link SnowflakeRenderer}
     */
    public void setSnowflakeRenderer(SnowflakeRenderer snowflakeRenderer) {
        this.snowflakeRenderer = snowflakeRenderer;
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft TextComponent without debug logging.
     *
     * @param discordMessage a Discord markdown message
     * @return the Discord message formatted to a Minecraft TextComponent
     */
    public TextComponent serialize(final String discordMessage) {
        return serialize(discordMessage, false);
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft TextComponent.
     *
     * @param discordMessage a Discord markdown message
     * @param debugLogging   true to enable debug logging for the SimpleAST parser
     * @return the Discord message formatted to a Minecraft TextComponent
     */
    public TextComponent serialize(final String discordMessage, boolean debugLogging) {
        List<Component> components = new ArrayList<>();

        List<Node<Object>> nodes = parser.parse(discordMessage, null);
        for (Node<Object> node : nodes) {
            components.add(process(node, TextComponent.empty()));
        }

        return TextComponent.empty().children(components);
    }

    private TextComponent process(final Node<Object> node, TextComponent textComponent) {
        TextComponent component = TextComponent.empty()
                .mergeDecorations(textComponent)
                .mergeEvents(textComponent)
                .mergeColor(textComponent);

        if (node instanceof TextNode) {
            component = component.content(((TextNode) node).getContent());
        } else if (node instanceof StyleNode) {
            //noinspection unchecked
            List<TextStyle> styles = new ArrayList<>(((StyleNode) node).getStyles());
            for (TextStyle style : styles) {
                switch (style.getType()) {
                    case STRIKETHROUGH:
                        component = component.decoration(TextDecoration.STRIKETHROUGH, true);
                        break;
                    case UNDERLINE:
                        component = component.decoration(TextDecoration.UNDERLINED, true);
                        break;
                    case ITALICS:
                        component = component.decoration(TextDecoration.ITALIC, true);
                        break;
                    case BOLD:
                        component = component.decoration(TextDecoration.BOLD, true);
                        break;
                    case SPOILER:
                        TextComponent content = TextComponent.empty();
                        for (Node<Object> objectNode : parser.parse(style.getExtra().get("content"), null)) {
                            content = content.append(process(objectNode, component));
                        }

                        component = component.append(TextComponent.of(style.getExtra().get("content"))
                                .decoration(TextDecoration.OBFUSCATED, true).color(TextColor.DARK_GRAY)
                                .hoverEvent(HoverEvent.showText(content)));
                        break;
                    case CODE_STRING:
                    case CODE_BLOCK:
                        component = component.color(TextColor.DARK_GRAY);
                        styles.remove(style);
                        break;
                    case QUOTE:
                        component = component
                                .append(TextComponent.of("| ", TextColor.DARK_GRAY, TextDecoration.BOLD));
                        break;
                    case MENTION_EMOJI:
                        component = component.append(snowflakeRenderer.renderEmoteMention(style.getExtra().get("name"), style.getExtra().get("id")));
                        break;
                    case MENTION_CHANNEL:
                        component = component.append(snowflakeRenderer.renderChannelMention(style.getExtra().get("id")));
                        break;
                    case MENTION_USER:
                        component = component.append(snowflakeRenderer.renderUserMention(style.getExtra().get("id")));
                        break;
                    case MENTION_ROLE:
                        component = component.append(snowflakeRenderer.renderRoleMention(style.getExtra().get("id")));
                        break;
                    default:
                        break;
                }
            }
        }

        for (Node<Object> child : node.getChildren()) {
            component = component.append(process(child, component));
        }

        return component;
    }
}
