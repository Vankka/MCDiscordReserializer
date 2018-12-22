/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2018 Vankka
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

package com.vankka.reserializer.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Text class, for defining segments of text with formatting rules.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Text {
    private String content;
    private boolean bold;
    private boolean strikethrough;
    private boolean underline;
    private boolean italic;

    /**
     * Explicit constructor for generating Texts with String content.
     *
     * @param content
     *     The content of this text.
     */
    public Text(final String content) {
        this.content = content;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Text clone() {
        return new Text(content, bold, strikethrough, underline, italic);
    }
}
