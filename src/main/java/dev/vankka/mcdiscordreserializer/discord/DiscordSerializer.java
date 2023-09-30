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

package dev.vankka.mcdiscordreserializer.discord;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * DiscordSerializer, for serializing from Minecraft {@link Component}s to Discord messages.
 *
 * @author Vankka
 *
 * @see DiscordSerializerOptions
 * @see dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules
 */
@SuppressWarnings("unused") // API
public class DiscordSerializer {

    /**
     * Default instance of the DiscordSerializer, incase that's all you need.
     * Using {@link DiscordSerializer#setDefaultOptions(DiscordSerializerOptions)} is not allowed.
     */
    public static final DiscordSerializer INSTANCE = new DiscordSerializer() {
        @Override
        public void setDefaultOptions(@NotNull DiscordSerializerOptions defaultOptions) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    /**
     * The default {@link DiscordSerializerOptions} to use for this serializer.
     */
    @NotNull
    private DiscordSerializerOptions defaultOptions;

    /**
     * Constructor for creating a serializer, which {@link DiscordSerializerOptions#defaults()} as defaults.
     */
    public DiscordSerializer() {
        this(DiscordSerializerOptions.defaults());
    }

    /**
     * Constructor for creating a serializer, with the specified {@link DiscordSerializerOptions} as defaults.
     *
     * @param defaultOptions the default serializer options (can be overridden on serialize)
     * @see DiscordSerializerOptions#defaults()
     * @see DiscordSerializerOptions#DiscordSerializerOptions(boolean, boolean, Function, Function)
     */
    public DiscordSerializer(@NotNull DiscordSerializerOptions defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    /**
     * Gets the default options for this serializer.
     * @return the default options for this serializer
     */
    public @NotNull DiscordSerializerOptions getDefaultOptions() {
        return defaultOptions;
    }

    /**
     * Sets the default options for this serializer.
     * @param defaultOptions the new default options
     */
    public void setDefaultOptions(@NotNull DiscordSerializerOptions defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    /**
     * Serializes a {@link Component} to Discord formatting (markdown) with this serializer's {@link DiscordSerializer#getDefaultOptions() default options}.<br/>
     * Use {@link DiscordSerializer#serialize(Component, DiscordSerializerOptions)} to fine tune the serialization options.
     *
     * @param component The text component from a Minecraft chat message
     * @return Discord markdown formatted String
     */
    public String serialize(@NotNull final Component component) {
        DiscordSerializerOptions options = getDefaultOptions();
        return serialize(component, options);
    }

    /**
     * Serializes Component (from a chat message) to Discord formatting (markdown).
     *
     * @param component         The text component from a Minecraft chat message
     * @param serializerOptions The options to use for this serialization
     * @return Discord markdown formatted String
     * @see DiscordSerializerOptions#defaults()
     * @see DiscordSerializerOptions#DiscordSerializerOptions(boolean, boolean, Function, Function)
     */
    @NotNull
    public String serialize(@NotNull final Component component, @NotNull final DiscordSerializerOptions serializerOptions) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Text> texts = getTexts(new LinkedList<>(), component, new Text(), serializerOptions);
        for (Text text : texts) {
            String content = text.getContent();
            if (content.isEmpty()) {
                // won't work
                continue;
            }

            if (text.isBold()) {
                stringBuilder.append("**");
            }
            if (text.isStrikethrough()) {
                stringBuilder.append("~~");
            }
            if (text.isItalic()) {
                stringBuilder.append("_");
            }
            if (text.isUnderline()) {
                stringBuilder.append("__");
            }

            if (serializerOptions.isEscapeMarkdown()) {
                content = content
                        .replace("*", "\\*")
                        .replace("~", "\\~")
                        .replace("_", "\\_")
                        .replace("`", "\\`")
                        .replace("|", "\\|");
            }

            stringBuilder.append(content);

            if (text.isUnderline()) {
                stringBuilder.append("__");
            }
            if (text.isItalic()) {
                stringBuilder.append("_");
            }
            if (text.isStrikethrough()) {
                stringBuilder.append("~~");
            }
            if (text.isBold()) {
                stringBuilder.append("**");
            }

            stringBuilder.append("\u200B"); // zero width space
        }
        int length = stringBuilder.length();
        return length < 1 ? "" : stringBuilder.substring(0, length - 1);
    }

    private LinkedList<Text> getTexts(@NotNull final List<Text> input, @NotNull final Component component,
                                      @NotNull final Text text, @NotNull final DiscordSerializerOptions serializerOptions) {
        LinkedList<Text> output = new LinkedList<>(input);

        String content;
        if (component instanceof KeybindComponent) {
            content = serializerOptions.getKeybindProvider().apply((KeybindComponent) component);
        } else if (component instanceof ScoreComponent) {
            content = ((ScoreComponent) component).value();
        } else if (component instanceof SelectorComponent) {
            content = ((SelectorComponent) component).pattern();
        } else if (component instanceof TextComponent) {
            content = ((TextComponent) component).content();
        } else if (component instanceof TranslatableComponent) {
            content = serializerOptions.getTranslationProvider().apply(((TranslatableComponent) component));
        } else {
            content = "";
        }

        ClickEvent clickEvent = component.clickEvent();
        if (serializerOptions.isEmbedLinks() && clickEvent != null && clickEvent.action() == ClickEvent.Action.OPEN_URL) {
            text.setContent("[" + content + "](" + clickEvent.value() + ")");
        } else {
            text.setContent(content);
        }

        TextDecoration.State bold = component.decoration(TextDecoration.BOLD);
        if (bold != TextDecoration.State.NOT_SET) {
            text.setBold(bold == TextDecoration.State.TRUE);
        }
        TextDecoration.State italic = component.decoration(TextDecoration.ITALIC);
        if (italic != TextDecoration.State.NOT_SET) {
            text.setItalic(italic == TextDecoration.State.TRUE);
        }
        TextDecoration.State underline = component.decoration(TextDecoration.UNDERLINED);
        if (underline != TextDecoration.State.NOT_SET) {
            text.setUnderline(underline == TextDecoration.State.TRUE);
        }
        TextDecoration.State strikethrough = component.decoration(TextDecoration.STRIKETHROUGH);
        if (strikethrough != TextDecoration.State.NOT_SET) {
            text.setStrikethrough(strikethrough == TextDecoration.State.TRUE);
        }

        if (!output.isEmpty()) {
            Text previous = output.getLast();
            // if the formatting matches (color was different), merge the text objects to reduce length
            if (text.formattingMatches(previous)) {
                output.removeLast();
                text.setContent(previous.getContent() + text.getContent());
            }
        }
        output.add(text);

        for (Component child : component.children()) {
            Text next = text.clone();
            next.setContent("");
            output = getTexts(output, child, next, serializerOptions);
        }

        return output;
    }

    private static class Text {

        private String content;
        private boolean bold;
        private boolean strikethrough;
        private boolean underline;
        private boolean italic;

        public Text() {}

        private Text(String content, boolean bold, boolean strikethrough, boolean underline, boolean italic) {
            this.content = content;
            this.bold = bold;
            this.strikethrough = strikethrough;
            this.underline = underline;
            this.italic = italic;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isBold() {
            return bold;
        }

        public void setBold(boolean bold) {
            this.bold = bold;
        }

        public boolean isStrikethrough() {
            return strikethrough;
        }

        public void setStrikethrough(boolean strikethrough) {
            this.strikethrough = strikethrough;
        }

        public boolean isUnderline() {
            return underline;
        }

        public void setUnderline(boolean underline) {
            this.underline = underline;
        }

        public boolean isItalic() {
            return italic;
        }

        public void setItalic(boolean italic) {
            this.italic = italic;
        }

        /**
         * Checks if the formatting matches between this and another Text object.
         *
         * @param other The other Text object.
         * @return true if the formatting matches the other Text object.
         */
        public boolean formattingMatches(Text other) {
            return other != null
                    && bold == other.bold
                    && strikethrough == other.strikethrough
                    && underline == other.underline
                    && italic == other.italic;
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public Text clone() {
            return new Text(content, bold, strikethrough, underline, italic);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Text text = (Text) o;
            return bold == text.bold
                    && strikethrough == text.strikethrough
                    && underline == text.underline
                    && italic == text.italic
                    && Objects.equals(content, text.content);
        }

        @Override
        public int hashCode() {
            return Objects.hash(content, bold, strikethrough, underline, italic);
        }
    }

}
