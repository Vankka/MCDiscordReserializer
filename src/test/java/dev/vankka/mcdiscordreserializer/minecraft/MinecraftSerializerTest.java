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

package dev.vankka.mcdiscordreserializer.minecraft;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MinecraftSerializerTest {

    private final MinecraftSerializer serializer = new MinecraftSerializer();

    @Test
    public void test() {
        Assertions.assertEquals(
                Component.text()
                        .append(
                                Component.text()
                                        .decorate(TextDecoration.UNDERLINED)
                                        .append(
                                                Component.text("underline").decorate(TextDecoration.UNDERLINED)
                                        )
                                )
                        .build(),
                serializer.serialize("__underline__")
        );


        Assertions.assertEquals(
                Component.text()
                        .append(
                                Component.text()
                                        .decorate(TextDecoration.UNDERLINED)
                                        .append(
                                                Component.text("underline ").decorate(TextDecoration.UNDERLINED)
                                        )
                                        .append(
                                                Component.text()
                                                        .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                                                        .append(
                                                                Component.text("bold").decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                                                        )
                                        )
                        )
                        .build(),
                serializer.serialize("__underline **bold**__")
        );
    }
}
