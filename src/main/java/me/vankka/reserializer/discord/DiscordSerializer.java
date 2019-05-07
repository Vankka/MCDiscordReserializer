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

package me.vankka.reserializer.discord;

import me.vankka.reserializer.text.Text;
import net.kyori.text.*;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.format.TextDecoration;

import java.util.LinkedList;
import java.util.List;

/**
 * DiscordSerializer, for serializing from MC TextComponents to Discord messages.
 *
 * @author Vankka
 */
@SuppressWarnings("unused")
public final class DiscordSerializer {
    private DiscordSerializer() {
    }

    /**
     * Serializes TextComponent (from a chat message) to Discord formatting (markdown) without embed links.
     * Use {@link DiscordSerializer#serialize(TextComponent, boolean)} to serialize with embed links.
     *
     * @param textComponent The text component from a Minecraft chat message
     * @return Discord markdown formatted String
     */
    public static String serialize(final TextComponent textComponent) {
        return serialize(textComponent, false);
    }

    /**
     * Serializes TextComponent (from a chat message) to Discord formatting (markdown).
     *
     * @param textComponent The text component from a Minecraft chat message
     * @param embedLinks    Makes messages format as [message content](url) when there is a open_url clickEvent (for embeds)
     * @return Discord markdown formatted String
     */
    @SuppressWarnings("WeakerAccess")
    public static String serialize(final TextComponent textComponent, boolean embedLinks) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Text> texts = getTexts(new LinkedList<>(), textComponent, new Text(), embedLinks);
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

    private static List<Text> getTexts(final List<Text> input, final Component component,
                                       final Text text, final boolean embedLinks) {
        List<Text> output = new LinkedList<>(input);
        String content;
        if (component instanceof KeybindComponent) {
            content = ((KeybindComponent) component).keybind();
        } else if (component instanceof ScoreComponent) {
            content = ((ScoreComponent) component).value();
        } else if (component instanceof SelectorComponent) {
            content = ((SelectorComponent) component).pattern();
        } else if (component instanceof TextComponent) {
            content = ((TextComponent) component).content();
        } else if (component instanceof TranslatableComponent) {
            content = ((TranslatableComponent) component).key();
        } else {
            content = "";
        }
        ClickEvent clickEvent = component.clickEvent();
        if (embedLinks && clickEvent != null && clickEvent.action() == ClickEvent.Action.OPEN_URL) {
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
            Text previous = output.get(output.size() - 1);
            // if the formatting matches (color was different), merge the text objects to reduce length
            if (text.formattingMatches(previous)) {
                output.remove(previous);
                text.setContent(previous.getContent() + text.getContent());
            }
        }
        output.add(text);
        for (Component child : component.children()) {
            Text next = text.clone();
            next.setContent("");
            output = getTexts(output, child, next, embedLinks);
        }
        return output;
    }
}
