/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2021-2025 Vankka
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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentEncoder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * DiscordSerializer, for serializing from Minecraft {@link Component}s to Discord messages.
 *
 * @author Vankka
 *
 * @see DiscordSerializerOptions
 */
@SuppressWarnings("unused") // API
public class DiscordSerializer implements ComponentEncoder<Component, String> {

    private static final Pattern LINK_PATTERN = Pattern.compile("(https?://.*\\.[^ ]*)$");

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
    @Override
    public @NotNull String serialize(@NotNull final Component component) {
        DiscordSerializerOptions options = getDefaultOptions();
        return serialize(component, options);
    }

    @NotNull
    public String serialize(@NotNull final Component component, @NotNull final DiscordSerializerOptions serializerOptions) {
        ComponentFlattener flattener = serializerOptions.getFlattener();

        FlattenListener listener = new FlattenListener(serializerOptions);
        flattener.flatten(component, listener);

        StringBuilder stringBuilder = new StringBuilder();
        List<Text> texts = listener.getTexts();
        for (Text text : texts) {
            String content = text.getContent().toString();
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

            // Markdown doesn't apply inside links
            if (serializerOptions.isEscapeMarkdown() && !LINK_PATTERN.matcher(stringBuilder).find()) {
                content = content
                        .replace("*", "\\*")
                        .replace("~", "\\~")
                        .replace("_", "\\_")
                        .replace("`", "\\`")
                        .replace("|", "\\|");
            }

            String openUrl = text.getOpenUrl();
            if (serializerOptions.isMaskedLinks() && openUrl != null) {
                String display = text.getUrlHover();
                content = "[" + content + "](<" + openUrl + ">" + (display != null ? " \"" + display + "\"" : "") + ")";
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

            // Separator for formatting, since going from bold -> bold underline
            // would lead to "**bold****__bold underline__**" which doesn't work
            stringBuilder.append("\u200B"); // zero width space
        }
        int length = stringBuilder.length();
        return length < 1 ? "" : stringBuilder.substring(0, length - 1);
    }

    private static class FlattenListener implements FlattenerListener {

        private final Map<Style, Text> previousText = new HashMap<>();
        private final List<Text> texts = new ArrayList<>();
        private Text currentText = null;

        private final DiscordSerializerOptions serializerOptions;
        private final boolean gatherLinks;

        public FlattenListener(DiscordSerializerOptions serializerOptions) {
            this.serializerOptions = serializerOptions;
            this.gatherLinks = serializerOptions.isMaskedLinks();
        }

        public List<Text> getTexts() {
            if (currentText != null) {
                texts.add(currentText);
            }
            return texts;
        }

        @Override
        public void pushStyle(@NotNull Style style) {
            Boolean isBold = null, isItalic = null, isUnderline = null, isStrikethrough = null;

            Text text;
            if (currentText != null) {
                text = currentText.clone();
                text.getContent().setLength(0);
            } else {
                text = new Text();
            }

            TextDecoration.State bold = style.decoration(TextDecoration.BOLD);
            if (bold != TextDecoration.State.NOT_SET) {
                boolean wasBold = text.isBold();
                text.setBold(bold == TextDecoration.State.TRUE);
            }

            TextDecoration.State italic = style.decoration(TextDecoration.ITALIC);
            if (italic != TextDecoration.State.NOT_SET) {
                boolean wasItalic = text.isItalic();
                text.setItalic(italic == TextDecoration.State.TRUE);
            }

            TextDecoration.State underline = style.decoration(TextDecoration.UNDERLINED);
            if (underline != TextDecoration.State.NOT_SET) {
                boolean wasUnderline = text.isUnderline();
                text.setUnderline(underline == TextDecoration.State.TRUE);
            }

            TextDecoration.State strikethrough = style.decoration(TextDecoration.STRIKETHROUGH);
            if (strikethrough != TextDecoration.State.NOT_SET) {
                boolean wasStrikethrough = text.isStrikethrough();
                text.setStrikethrough(strikethrough == TextDecoration.State.TRUE);
            }

            ClickEvent clickEvent = style.clickEvent();
            if (gatherLinks && clickEvent != null && clickEvent.action() == ClickEvent.Action.OPEN_URL) {
                text.setOpenUrl(clickEvent.value());
            }

            HoverEvent<?> hoverEvent = style.hoverEvent();
            if (gatherLinks && hoverEvent != null && hoverEvent.action() == HoverEvent.Action.SHOW_TEXT) {
                FlattenToTextOnly flatten = new FlattenToTextOnly();
                serializerOptions.getFlattener().flatten((Component) hoverEvent.value(), flatten);
                text.setUrlHover(flatten.getContent());
            }

            if (currentText == null) {
                currentText = text;
            } else if (!text.formattingMatches(currentText)) {
                // If formatting is different in any way, switch to a new text part because
                // "**bold __bold underline** underline__" does not work
                texts.add(currentText);
                previousText.put(style, currentText.clone());
                currentText = text;
            }
        }

        @Override
        public void popStyle(@NotNull Style style) {
            Text pop = previousText.remove(style);
            if (pop != null) {
                texts.add(currentText);
                currentText = pop;
                currentText.getContent().setLength(0);
            }
        }

        @Override
        public void component(@NotNull String text) {
            if (currentText == null) {
                currentText = new Text();
            }
            currentText.appendContent(text);
        }
    }

    private static class FlattenToTextOnly implements FlattenerListener {

        private final StringBuilder builder = new StringBuilder();

        @Override
        public void component(@NotNull String text) {
            builder.append(text);
        }

        public String getContent() {
            return builder.toString();
        }
    }

    private static class Text {

        private final StringBuilder content = new StringBuilder();
        private boolean bold;
        private boolean strikethrough;
        private boolean underline;
        private boolean italic;

        private String openUrl;
        private String urlHover;

        public Text() {}

        private Text(
                StringBuilder content,
                boolean bold,
                boolean strikethrough,
                boolean underline,
                boolean italic,
                String openUrl,
                String urlHover
        ) {
            this.content.append(content);
            this.bold = bold;
            this.strikethrough = strikethrough;
            this.underline = underline;
            this.italic = italic;
            this.openUrl = openUrl;
            this.urlHover = urlHover;
        }

        public StringBuilder getContent() {
            return content;
        }

        public void appendContent(String content) {
            this.content.append(content);
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

        public String getOpenUrl() {
            return openUrl;
        }

        public void setOpenUrl(String openUrl) {
            this.openUrl = openUrl;
        }

        public String getUrlHover() {
            return urlHover;
        }

        public void setUrlHover(String urlHover) {
            this.urlHover = urlHover;
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
                    && italic == other.italic
                    && Objects.equals(openUrl, other.openUrl)
                    && Objects.equals(urlHover, other.urlHover);
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public Text clone() {
            return new Text(content, bold, strikethrough, underline, italic, openUrl, urlHover);
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
                    && content.toString().contentEquals(text.content)
                    && Objects.equals(openUrl, text.openUrl)
                    && Objects.equals(urlHover, text.urlHover);
        }

        @Override
        public int hashCode() {
            return Objects.hash(content, bold, strikethrough, underline, italic, openUrl, urlHover);
        }
    }

}
