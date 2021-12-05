/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2018-2021 Vankka
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

import lombok.*;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Options for {@link DiscordSerializer}s.
 */
@RequiredArgsConstructor
@ToString
@Getter
@With
public final class DiscordSerializerOptions {

    /**
     * Creates the default {@link DiscordSerializerOptions}.
     * @return the default {@link DiscordSerializerOptions}.
     */
    public static DiscordSerializerOptions defaults() {
        return new DiscordSerializerOptions(false, true, KeybindComponent::keybind, TranslatableComponent::key);
    }

    /**
     * Makes messages format as [message content](url) when there is an open_url clickEvent (for embeds).
     */
    private final boolean embedLinks;

    /**
     * Escapes Discord formatting codes in the Minecraft message content.
     */
    private final boolean escapeMarkdown;

    /**
     * The translator for {@link KeybindComponent}s.
     */
    @NotNull
    private final Function<KeybindComponent, String> keybindProvider;

    /**
     * The translator for {@link TranslatableComponent}s.
     */
    @NotNull
    private final Function<TranslatableComponent, String> translationProvider;
}
