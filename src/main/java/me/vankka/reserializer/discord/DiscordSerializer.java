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
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextDecoration;

import java.beans.ConstructorProperties;
import java.util.LinkedList;
import java.util.List;

/**
 * DiscordSerializer, for serializing from MC TextComponents to Discord messages.
 *
 * @author Vankka
 */
public final class DiscordSerializer {
    private DiscordSerializer() {}
    
    /**
     * Serializes TextComponent (from a chat message) to Discord formatting (markdown).
     *
     * @param textComponent The text component from a Minecraft chat message
     * @return Discord markdown formatted String
     */
    // Serializes TextComponent (from a chat message) to Discord formatting (markdown)
    public static String serialize(TextComponent textComponent) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Text> texts = getTexts(textComponent, new Text());
        for (Text text : texts) {
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
            stringBuilder.append(text.getContent());
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
        return stringBuilder.toString();
    }

    private static List<Text> getTexts(Component component, Text text) {
        List<Text> output = new LinkedList<>();
        if (component instanceof TextComponent) {
            text.setContent(((TextComponent) component).content());
        } else {
            text.setContent("");
        }
        TextDecoration.State bold = component.decoration(TextDecoration.BOLD);
        if (bold != TextDecoration.State.NOT_SET) {
            text.setBold(bold == TextDecoration.State.TRUE);
        }
        TextDecoration.State italic = component.decoration(TextDecoration.ITALIC);
        if (italic != TextDecoration.State.NOT_SET) {
            text.setItalic(italic == TextDecoration.State.TRUE);
        }
        TextDecoration.State underline = component.decoration(TextDecoration.UNDERLINE);
        if (underline != TextDecoration.State.NOT_SET) {
            text.setUnderline(underline == TextDecoration.State.TRUE);
        }
        TextDecoration.State strikethrough = component.decoration(TextDecoration.STRIKETHROUGH);
        if (strikethrough != TextDecoration.State.NOT_SET) {
            text.setStrikethrough(strikethrough == TextDecoration.State.TRUE);
        }
        output.add(text);
        component.children().forEach(child -> {
            Text next = text.clone();
            next.setContent("");
            output.addAll(getTexts(child, next));
        });
        return output;
    }
}
