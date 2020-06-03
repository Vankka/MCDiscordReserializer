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

package dev.vankka.mcdiscordreserializer.discord;

import dev.vankka.mcdiscordreserializer.text.Text;
import lombok.Getter;
import lombok.Setter;
import net.kyori.text.*;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.format.TextDecoration;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * DiscordSerializer, for serializing from Minecraft {@link Component}s to Discord messages.
 *
 * @author Vankka
 *
 * @see DiscordSerializerOptions
 * @see dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DiscordSerializer {

    /**
     * Default instance of the DiscordSerializer, incase that's all you need.
     * Using {@link DiscordSerializer#setDefaultOptions(DiscordSerializerOptions)} is not allowed.
     */
    public static final DiscordSerializer INSTANCE = new DiscordSerializer() {
        @Override
        public void setDefaultOptions(DiscordSerializerOptions defaultOptions) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }

        @SuppressWarnings("deprecation")
        @Override
        @Deprecated
        public void setKeybindProvider(Function<KeybindComponent, String> provider) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }

        @SuppressWarnings("deprecation")
        @Override
        @Deprecated
        public void setTranslationProvider(Function<TranslatableComponent, String> provider) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    /**
     * The default {@link DiscordSerializerOptions} to use for this serializer.
     */
    @Getter
    @Setter
    private DiscordSerializerOptions defaultOptions;
    private Function<KeybindComponent, String> keybindProvider;
    private Function<TranslatableComponent, String> translationProvider;

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
    public DiscordSerializer(DiscordSerializerOptions defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    /**
     * Constructor fore creating a serializer with translations provided with arguments.
     *
     * @param keybindProvider     The keybind provider.
     * @param translationProvider The translation provider.
     * @deprecated Use {@link #DiscordSerializer(DiscordSerializerOptions)}
     * {@link DiscordSerializerOptions#withKeybindProvider(Function)}
     * {@link DiscordSerializerOptions#withTranslationProvider(Function)}
     */
    @Deprecated
    public DiscordSerializer(Function<KeybindComponent, String> keybindProvider,
                             Function<TranslatableComponent, String> translationProvider) {
        this.defaultOptions = DiscordSerializerOptions.defaults();
        this.keybindProvider = keybindProvider;
        this.translationProvider = translationProvider;
    }

    /**
     * Returns the keybind provider for this serializer.
     *
     * @return keybind provider, a KeybindComponent to String function
     * @deprecated Use {@link #getDefaultOptions()} {@link DiscordSerializerOptions#getKeybindProvider()}
     */
    @Deprecated
    public Function<KeybindComponent, String> getKeybindProvider() {
        return keybindProvider;
    }

    /**
     * Sets the keybind provider for this serializer.
     *
     * @param provider a KeybindComponent to String function
     * @deprecated Use {@link #setDefaultOptions(DiscordSerializerOptions)} {@link DiscordSerializerOptions#withKeybindProvider(Function)}
     */
    @Deprecated
    public void setKeybindProvider(Function<KeybindComponent, String> provider) {
        keybindProvider = provider;
    }

    /**
     * Returns the translation provider for this serializer.
     *
     * @return keybind provider, a TranslatableComponent to String function
     * @deprecated Use {@link #getDefaultOptions()} {@link DiscordSerializerOptions#getTranslationProvider()}
     */
    @Deprecated
    public Function<TranslatableComponent, String> getTranslationProvider() {
        return translationProvider;
    }

    /**
     * Sets the translation provider for this serializer.
     *
     * @param provider a TranslationComponent to String function
     * @deprecated Use {@link #setDefaultOptions(DiscordSerializerOptions)} {@link DiscordSerializerOptions#withTranslationProvider(Function)}
     */
    @Deprecated
    public void setTranslationProvider(Function<TranslatableComponent, String> provider) {
        translationProvider = provider;
    }

    /**
     * Serializes a {@link Component} to Discord formatting (markdown) with this serializer's {@link DiscordSerializer#getDefaultOptions() default options}.<br/>
     * Use {@link DiscordSerializer#serialize(Component, DiscordSerializerOptions)} to fine tune the serialization options.
     *
     * @param component The text component from a Minecraft chat message
     * @return Discord markdown formatted String
     */
    public String serialize(final Component component) {
        DiscordSerializerOptions options = getDefaultOptions();
        if (keybindProvider != null) {
            options = options.withKeybindProvider(keybindProvider);
        }
        if (translationProvider != null) {
            options = options.withTranslationProvider(translationProvider);
        }
        return serialize(component, options);
    }

    /**
     * Serializes a Component (from a chat message) to Discord formatting (markdown).
     *
     * @param component     The text component from a Minecraft chat message
     * @param embedLinks    Makes messages format as [message content](url) when there is a open_url clickEvent (for embeds)
     * @return Discord markdown formatted String
     * @deprecated Use {@link #serialize(Component, DiscordSerializerOptions)} {@link DiscordSerializerOptions#withEmbedLinks(boolean)}
     */
    @Deprecated
    public String serialize(final Component component, boolean embedLinks) {
        return serialize(component, defaultOptions.withEmbedLinks(embedLinks));
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
    public String serialize(final Component component, final DiscordSerializerOptions serializerOptions) {
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
                content = content.replace("(?<!\\\\)(?:\\\\\\\\)*\\*", "\\*")
                        .replace("(?<!\\\\)(?:\\\\\\\\)*~", "\\~")
                        .replace("(?<!\\\\)(?:\\\\\\\\)*_", "\\_")
                        .replace("(?<!\\\\)(?:\\\\\\\\)*`", "\\`")
                        .replace("(?<!\\\\)(?:\\\\\\\\)*\\|", "\\|");
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

    private LinkedList<Text> getTexts(final List<Text> input, final Component component,
                                final Text text, final DiscordSerializerOptions serializerOptions) {
        LinkedList<Text> output = new LinkedList<>(input);

        String content;
        if (component instanceof KeybindComponent) {
            content = keybindProvider.apply((KeybindComponent) component);
        } else if (component instanceof ScoreComponent) {
            content = ((ScoreComponent) component).value();
        } else if (component instanceof SelectorComponent) {
            content = ((SelectorComponent) component).pattern();
        } else if (component instanceof TextComponent) {
            content = ((TextComponent) component).content();
        } else if (component instanceof TranslatableComponent) {
            content = translationProvider.apply(((TranslatableComponent) component));
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
}
