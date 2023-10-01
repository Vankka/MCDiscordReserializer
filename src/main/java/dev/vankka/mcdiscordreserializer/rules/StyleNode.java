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

package dev.vankka.mcdiscordreserializer.rules;

import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.TextNode;

import java.util.List;

public class StyleNode<RC, T> extends Node<RC> {

    private final List<T> styles;

    public StyleNode(List<T> styles) {
        this.styles = styles;
    }

    public List<T> getStyles() {
        return styles;
    }

    @Override
    public String toString() {
        return "StyleNode{" +
                "styles=" + styles +
                ", children=" + getChildren() + "}";
    }

    public static <RC> StyleNode<RC, Style> createWithText(String content, List<Style> styles) {
        StyleNode<RC, Style> styleNode = new StyleNode<>(styles);
        styleNode.addChild(new TextNode<>(content));
        return styleNode;
    }

    public interface Style {
        String name();
    }

    public static class CodeBlockStyle implements Style {

        private final String language;

        public CodeBlockStyle(String language) {
            this.language = language;
        }

        public String getLanguage() {
            return language;
        }

        @Override
        public String name() {
            return "CODE_BLOCK";
        }
    }

    public static class MentionStyle implements Style {

        private final Type type;
        private final String id;

        public MentionStyle(Type type, String id) {
            this.type = type;
            this.id = id;
        }

        public Type getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        @Override
        public String name() {
            return type.name() + "_NAME";
        }

        public enum Type {
            CHANNEL,
            USER,
            ROLE,
        }
    }

    public static class EmojiStyle implements Style {

        private final String id;
        private final String name;

        public EmojiStyle(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String name() {
            return "EMOJI_MENTION";
        }
    }

    public static class ItalicStyle implements Style {

        private final boolean italics;

        public ItalicStyle(boolean italics) {
            this.italics = italics;
        }

        public boolean isItalics() {
            return italics;
        }

        @Override
        public String name() {
            return "ITALICS";
        }
    }

    public static class ContentStyle implements Style {

        private final Type type;
        private final String content;

        public ContentStyle(Type type, String content) {
            this.type = type;
            this.content = content;
        }

        public Type getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String name() {
            return type.name();
        }

        public enum Type {
            QUOTE,
            SPOILER,
            LINK
        }
    }

    public enum Styles implements Style {
        /* Basic Markdown: */
        BOLD,
        UNDERLINE,
        STRIKETHROUGH,

        /* Discord special */
        CODE_STRING,
        MASKED_LINK,
        LIST
    }
}
