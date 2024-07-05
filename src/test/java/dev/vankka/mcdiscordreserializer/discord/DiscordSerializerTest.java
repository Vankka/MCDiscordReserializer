/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2021-2024 Vankka
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
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DiscordSerializerTest {

    private static final String SEPARATOR = "\u200B";

    private final DiscordSerializer serializer = DiscordSerializer.INSTANCE;
    private final DiscordSerializer serializerWithLinks = new DiscordSerializer(DiscordSerializerOptions.defaults().withMaskedLinks(true));

    @Test
    public void basicFormattingTest() {
        Assertions.assertEquals("**bold**", serializer.serialize(Component.text("bold").decorate(TextDecoration.BOLD)));
        Assertions.assertEquals("~~strikethrough~~", serializer.serialize(Component.text("strikethrough").decorate(TextDecoration.STRIKETHROUGH)));
        Assertions.assertEquals("_italic_", serializer.serialize(Component.text("italic").decorate(TextDecoration.ITALIC)));
        Assertions.assertEquals("__underline__", serializer.serialize(Component.text("underline").decorate(TextDecoration.UNDERLINED)));
    }

    @Test
    public void multiFormattingTest() {
        Assertions.assertEquals(
                "**__bold underline__**",
                serializer.serialize(
                        Component.text("bold underline")
                                .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                )
        );

        Assertions.assertEquals(
                "**bold**" + SEPARATOR + "**__bold underline__**",
                serializer.serialize(
                        Component.text()
                                .append(Component.text("bold").decorate(TextDecoration.BOLD))
                                .append(Component.text("bold underline").decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED))
                                .build()
                )
        );

        Assertions.assertEquals(
                "**bold**" + SEPARATOR + "**__bold underline__**",
                serializer.serialize(
                        Component.text()
                                .decorate(TextDecoration.BOLD)
                                .append(Component.text("bold"))
                                .append(Component.text("bold underline").decorate(TextDecoration.UNDERLINED))
                                .build()
                )
        );
    }

    @Test
    public void openUrlTest() {
        Assertions.assertEquals(
                "[Discord](<https://discord.com>)",
                serializerWithLinks.serialize(
                        Component.text()
                                .content("Discord")
                                .clickEvent(ClickEvent.openUrl("https://discord.com"))
                                .build()
                )
        );

        Assertions.assertEquals(
                "[Discord](<https://discord.com> \"hover\")",
                serializerWithLinks.serialize(
                        Component.text()
                                .content("Discord")
                                .clickEvent(ClickEvent.openUrl("https://discord.com"))
                                .hoverEvent(HoverEvent.showText(Component.text("hover")))
                                .build()
                )
        );

        Assertions.assertEquals(
                "**[Discord](<https://discord.com>)**",
                serializerWithLinks.serialize(
                        Component.text()
                                .content("Discord")
                                .decorate(TextDecoration.BOLD)
                                .clickEvent(ClickEvent.openUrl("https://discord.com"))
                                .build()
                )
        );
    }
}
