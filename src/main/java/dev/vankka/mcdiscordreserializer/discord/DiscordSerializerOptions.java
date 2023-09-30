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

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.flattener.ComponentFlattener;
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
    @SuppressWarnings("deprecation")
    public static DiscordSerializerOptions defaults() {
        return new DiscordSerializerOptions(
                false,
                true,
                ComponentFlattener.builder()
                        .mapper(TextComponent.class, TextComponent::content)
                        .mapper(ScoreComponent.class, ScoreComponent::value)
                        .mapper(SelectorComponent.class, SelectorComponent::pattern)
                        .build()
        );
    }

    private final boolean maskedLinks;

    private final boolean escapeMarkdown;

    @NotNull
    private final ComponentFlattener flattener;

    public DiscordSerializerOptions(
            boolean embedLinks,
            boolean escapeMarkdown,
            @NotNull Function<KeybindComponent, String> keybindProvider,
            @NotNull Function<TranslatableComponent, String> translationProvider
    ) {
        this(
                embedLinks,
                escapeMarkdown,
                ComponentFlattener.builder()
                        .mapper(KeybindComponent.class, keybindProvider)
                        .mapper(TranslatableComponent.class, translationProvider)
                        .build()
        );
    }

    public DiscordSerializerOptions(
            boolean maskedLinks,
            boolean escapeMarkdown,
            @NotNull ComponentFlattener flattener
    ) {
        this.maskedLinks = maskedLinks;
        this.escapeMarkdown = escapeMarkdown;
        this.flattener = flattener;
    }

    public boolean isMaskedLinks() {
        return maskedLinks;
    }

    public DiscordSerializerOptions withMaskedLinks(boolean maskedLinks) {
        return new DiscordSerializerOptions(maskedLinks, escapeMarkdown, flattener);
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
        return new DiscordSerializerOptions(maskedLinks, escapeMarkdown, flattener);
    }

    public @NotNull ComponentFlattener getFlattener() {
        return flattener;
    }

    public DiscordSerializerOptions withFlattener(ComponentFlattener flattener) {
        return new DiscordSerializerOptions(maskedLinks, escapeMarkdown, flattener);
    }

    /**
     * If links should be embedded.
     * @return if these options have embedded links enabled
     * @deprecated Naming change, now maskedLinks, {@link #isMaskedLinks()}
     */
    @Deprecated
    public boolean isEmbedLinks() {
        return maskedLinks;
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.discord.DiscordSerializerOptions}
     * based on this instance with embedLinks set to the provided value.
     * @param embedLinks {@code true} to use masked links for open_url components
     * @return the new instance
     * @deprecated Naming change, now maskedLinks, {@link #withMaskedLinks(boolean)}
     */
    @Deprecated
    public DiscordSerializerOptions withEmbedLinks(boolean embedLinks) {
        return new DiscordSerializerOptions(embedLinks, escapeMarkdown, flattener);
    }

    /**
     * The {@link net.kyori.adventure.text.KeybindComponent} to {@link String} converter for these options.
     * @return the keybindProvider for these options
     * @deprecated replaced with flattener, {@link #getFlattener()}
     */
    @Deprecated
    public @NotNull Function<KeybindComponent, String> getKeybindProvider() {
        return component -> "";
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.discord.DiscordSerializerOptions}
     * based on this instance with keybindProvider set to the provided value.
     * @param keybindProvider a function for converting {@link KeybindComponent}s to Strings for Discord
     * @return the new instance
     * @deprecated replaced with flattener, {@link #withFlattener(net.kyori.adventure.text.flattener.ComponentFlattener)}
     */
    @Deprecated
    public DiscordSerializerOptions withKeybindProvider(Function<KeybindComponent, String> keybindProvider) {
        return new DiscordSerializerOptions(maskedLinks, escapeMarkdown, flattener.toBuilder().mapper(KeybindComponent.class, keybindProvider).build());
    }

    /**
     * The {@link net.kyori.adventure.text.TranslatableComponent} to {@link String} converter for these options.
     * @return the keybindProvider for these options
     * @deprecated replaced with flattener, {@link #getFlattener()}
     */
    @Deprecated
    public @NotNull Function<TranslatableComponent, String> getTranslationProvider() {
        return component -> "";
    }


    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.discord.DiscordSerializerOptions}
     * based on this instance with translationProvider set to the provided value.
     * @param translationProvider a function for converting {@link TranslatableComponent}s to Strings for Discord
     * @return the new instance
     * @deprecated replaced with flattener, {@link #withFlattener(net.kyori.adventure.text.flattener.ComponentFlattener)}
     */
    @Deprecated
    public DiscordSerializerOptions withTranslationProvider(Function<TranslatableComponent, String> translationProvider) {
        return new DiscordSerializerOptions(maskedLinks, escapeMarkdown, flattener.toBuilder().mapper(TranslatableComponent.class, translationProvider).build());
    }

    @Override
    public String toString() {
        return "DiscordSerializerOptions{" +
                "maskedLinks=" + maskedLinks +
                ", escapeMarkdown=" + escapeMarkdown +
                ", flattener=" + flattener +
                '}';
    }
}
