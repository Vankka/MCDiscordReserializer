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

import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Options for {@link DiscordSerializer}s.
 */
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

    public DiscordSerializerOptions(
            boolean embedLinks,
            boolean escapeMarkdown,
            @NotNull Function<KeybindComponent, String> keybindProvider,
            @NotNull Function<TranslatableComponent, String> translationProvider
    ) {
        this.embedLinks = embedLinks;
        this.escapeMarkdown = escapeMarkdown;
        this.keybindProvider = keybindProvider;
        this.translationProvider = translationProvider;
    }

    /**
     * If links should be embedded.
     * @return if these options have embedded links enabled
     */
    public boolean isEmbedLinks() {
        return embedLinks;
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.discord.DiscordSerializerOptions}
     * based on this instance with embedLinks set to the provided value.
     * @param embedLinks {@code true} to use masked links for open_url components
     * @return the new instance
     */
    public DiscordSerializerOptions withEmbedLinks(boolean embedLinks) {
        return new DiscordSerializerOptions(embedLinks, escapeMarkdown, keybindProvider, translationProvider);
    }

    /**
     * If escaping markdown should be enabled.
     * @return if these options have escaping markdown enabled
     */
    public boolean isEscapeMarkdown() {
        return escapeMarkdown;
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.discord.DiscordSerializerOptions}
     * based on this instance with escapeMarkdown set to the provided value.
     * @param escapeMarkdown {@code true} to escape markdown characters inside text components
     * @return the new instance
     */
    public DiscordSerializerOptions withEscapeMarkdown(boolean escapeMarkdown) {
        return new DiscordSerializerOptions(embedLinks, escapeMarkdown, keybindProvider, translationProvider);
    }

    /**
     * The {@link net.kyori.adventure.text.KeybindComponent} to {@link String} converter for these options.
     * @return the keybindProvider for these options
     */
    public @NotNull Function<KeybindComponent, String> getKeybindProvider() {
        return keybindProvider;
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.discord.DiscordSerializerOptions}
     * based on this instance with keybindProvider set to the provided value.
     * @param keybindProvider a function for converting {@link KeybindComponent}s to Strings for Discord
     * @return the new instance
     */
    public DiscordSerializerOptions withKeybindProvider(Function<KeybindComponent, String> keybindProvider) {
        return new DiscordSerializerOptions(embedLinks, escapeMarkdown, keybindProvider, translationProvider);
    }

    /**
     * The {@link net.kyori.adventure.text.TranslatableComponent} to {@link String} converter for these options.
     * @return the keybindProvider for these options
     */
    public @NotNull Function<TranslatableComponent, String> getTranslationProvider() {
        return translationProvider;
    }


    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.discord.DiscordSerializerOptions}
     * based on this instance with translationProvider set to the provided value.
     * @param translationProvider a function for converting {@link TranslatableComponent}s to Strings for Discord
     * @return the new instance
     */
    public DiscordSerializerOptions withTranslationProvider(Function<TranslatableComponent, String> translationProvider) {
        return new DiscordSerializerOptions(embedLinks, escapeMarkdown, keybindProvider, translationProvider);
    }

    @Override
    public String toString() {
        return "DiscordSerializerOptions{" +
                "embedLinks=" + embedLinks +
                ", escapeMarkdown=" + escapeMarkdown +
                ", keybindProvider=" + keybindProvider +
                ", translationProvider=" + translationProvider +
                '}';
    }
}
