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

package me.vankka.reserializer.minecraft;

import me.vankka.simpleast.core.TextStyle;
import me.vankka.simpleast.core.node.Node;
import me.vankka.simpleast.core.node.StyleNode;
import me.vankka.simpleast.core.node.TextNode;
import me.vankka.simpleast.core.parser.Parser;
import me.vankka.simpleast.core.parser.Rule;
import me.vankka.simpleast.core.simple.SimpleMarkdownRules;
import net.kyori.text.TextComponent;
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
    public static final MinecraftSerializer INSTANCE = new MinecraftSerializer() {
        @Override
        public void setParser(Parser<Object, Node<Object>, Object> parser) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    private Parser<Object, Node<Object>, Object> parser;

    /**
     * Constructor for creating a serializer with a {@link Parser}
     * and adds the {@link SimpleMarkdownRules#createSimpleMarkdownRules(boolean)} with the text rule.
     */
    public MinecraftSerializer() {
        this.parser = new Parser<>();
        parser.addRules(SimpleMarkdownRules.createSimpleMarkdownRules(true));
    }

    /**
     * Constructor for creating a serializer with specified arguments (by default).
     *
     * @param parser The {@link Parser} used by the serializer
     */
    public MinecraftSerializer(Parser<Object, Node<Object>, Object> parser) {
        this.parser = parser;
    }

    /**
     * Constructor for creating a serializer with specified arguments (by default).
     *
     * @param parser The {@link Parser} used by the serializer
     * @param rules The rules added to the parser
     */
    public MinecraftSerializer(Parser<Object, Node<Object>, Object> parser, List<Rule<Object, Node<Object>, Object>> rules) {
        this(parser);
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
        TextComponent textComponent = TextComponent.of("");

        List<Node<Object>> nodes = parser.parse(discordMessage, null, debugLogging);
        for (Node<Object> node : nodes) {
            textComponent = textComponent.append(process(node, new ArrayList<>()));
        }

        return textComponent;
    }

    private TextComponent process(final Node<Object> node, final List<TextStyle> styles) {
        TextComponent component = TextComponent.of("");

        if (node instanceof TextNode) {
            component = component.content(((TextNode) node).getContent());
        } else if (node instanceof StyleNode) {
            //noinspection unchecked
            styles.addAll(((StyleNode) node).getStyles());
        }

        for (TextStyle style : styles) {
            switch (style) {
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
                default:
                    break;
            }
        }

        for (Node<Object> child : node.getChildren()) {
            component = component.append(process(child, styles));
        }

        return component;
    }
}
