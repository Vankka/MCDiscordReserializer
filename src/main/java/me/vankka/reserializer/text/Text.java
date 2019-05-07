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

package me.vankka.reserializer.text;

import lombok.*;

/**
 * Text class, for defining segments of text with formatting rules.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@EqualsAndHashCode
public class Text {
    private String content;
    private boolean bold;
    private boolean strikethrough;
    private boolean underline;
    private boolean italic;

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
}
