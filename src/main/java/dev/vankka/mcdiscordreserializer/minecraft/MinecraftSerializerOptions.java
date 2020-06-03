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

package dev.vankka.mcdiscordreserializer.minecraft;

import dev.vankka.mcdiscordreserializer.renderer.MinecraftRenderer;
import dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer;
import dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.parser.Parser;
import dev.vankka.simpleast.core.parser.Rule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

import java.util.List;

/**
 * Options for {@link MinecraftSerializer}s.
 */
@RequiredArgsConstructor
@ToString
@Getter
@With
public class MinecraftSerializerOptions {

    /**
     * Creates the default {@link MinecraftSerializerOptions}.
     * @return the default {@link MinecraftSerializerOptions}.
     */
    public static MinecraftSerializerOptions defaults() {
        return new MinecraftSerializerOptions(new Parser<>(),
                DiscordMarkdownRules.createAllRulesForDiscord(true), new DefaultMinecraftRenderer(),
                false);
    }

    /**
     * The SimpleAST {@link Parser} to use to generate the abstract syntax tree.
     */
    private final Parser<Object, Node<Object>, Object> parser;

    /**
     * The {@link dev.vankka.simpleast.core.parser.Rule Rules} for the {@link Parser}, null to use the {@link Parser Parsers} default rules.
     */
    private final List<Rule<Object, Node<Object>, Object>> rules;

    /**
     * The {@link MinecraftRenderer} to use to render formatting for Minecraft.
     */
    private final MinecraftRenderer renderer;

    /**
     * Weather or not to use debug logging for the {@link Parser}.
     */
    private final boolean debuggingEnabled;
}
