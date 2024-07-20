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

package dev.vankka.mcdiscordreserializer.minecraft;

import dev.vankka.mcdiscordreserializer.renderer.NodeRenderer;
import dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer;
import dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.parser.Parser;
import dev.vankka.simpleast.core.parser.Rule;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Options for {@link MinecraftSerializer}s.
 * @param <O> the type of the result produced with the renderers
 * @see dev.vankka.mcdiscordreserializer.renderer.MinecraftNodeRenderer
 * @see dev.vankka.mcdiscordreserializer.renderer.MinecraftRenderer
 * @see dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules
 */
public class MinecraftSerializerOptions<O> {

    /**
     * Creates the default {@link MinecraftSerializerOptions} for serialization.
     * @return the default {@link MinecraftSerializerOptions}.
     */
    public static MinecraftSerializerOptions<Component> defaults() {
        return new MinecraftSerializerOptions<>(
                new Parser<>(),
                DiscordMarkdownRules.createAllRulesForDiscord(true),
                Collections.emptyList(),
                false
        );
    }

    /**
     * The SimpleAST {@link Parser} to use to generate the abstract syntax tree.
     */
    @NotNull
    private final Parser<Object, Node<Object>, Object> parser;

    /**
     * The {@link dev.vankka.simpleast.core.parser.Rule Rules} for the {@link Parser},
     * {@code null} to use the {@link Parser Parsers} default rules.
     */
    @Nullable
    private final List<Rule<Object, Node<Object>, Object>> rules;

    /**
     * The {@link dev.vankka.mcdiscordreserializer.renderer.NodeRenderer}s to use to render formatting for Minecraft.
     */
    @NotNull
    private final List<NodeRenderer<O>> renderers;

    /**
     * Weather or not to use debug logging for the {@link Parser}.
     */
    private final boolean debuggingEnabled;

    public MinecraftSerializerOptions(
            @NotNull Parser<Object, Node<Object>, Object> parser,
            @Nullable List<Rule<Object, Node<Object>, Object>> rules,
            @NotNull List<NodeRenderer<O>> renderers,
            boolean debuggingEnabled
    ) {
        this.parser = parser;
        this.rules = rules != null ? Collections.unmodifiableList(rules) : null;
        this.renderers = Collections.unmodifiableList(renderers);
        this.debuggingEnabled = debuggingEnabled;
    }

    /**
     * Gets the parser for these options.
     * @return the parser
     */
    public @NotNull Parser<Object, Node<Object>, Object> getParser() {
        return parser;
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions}
     * based on this instance with parser set to the provided value.
     * @param parser a parser for parsing a string into abstract syntax tree
     * @return the new instance
     */
    public MinecraftSerializerOptions<O> withParser(@NotNull Parser<Object, Node<Object>, Object> parser) {
        return new MinecraftSerializerOptions<>(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Gets the rules for creating the abstract syntax tree to Minecraft for these options.
     * @return the parser
     */
    public @Nullable List<Rule<Object, Node<Object>, Object>> getRules() {
        return rules;
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions}
     * based on this instance with parser set to the provided value.
     * @param rules the rules for creating the abstract syntax tree
     * @return the new instance
     */
    public MinecraftSerializerOptions<O> withRules(@NotNull List<Rule<Object, Node<Object>, Object>> rules) {
        return new MinecraftSerializerOptions<>(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions}
     * based on this instance with the given renderer added.
     *
     * @param renderer the renderer to add
     * @return the new instance of options
     * @throws java.lang.IllegalArgumentException if the renderer is already included in this options instance
     * or is of type {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer}
     * @see List#add(Object)
     */
    @NotNull
    public MinecraftSerializerOptions<O> addRenderer(@NotNull NodeRenderer<O> renderer) {
        if (renderers.contains(renderer)) {
            throw new IllegalArgumentException("The provided renderer is already included in this options instance");
        }
        if (renderer.getClass().equals(DefaultMinecraftRenderer.class)) {
            throw new IllegalArgumentException("DefaultMinecraftRenderer cannot be added to serializer options");
        }
        List<NodeRenderer<O>> renderers = new ArrayList<>(this.renderers);
        renderers.add(renderer);
        return new MinecraftSerializerOptions<>(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions}
     * based on this instance with the given renderer added at the given index, keep in mind a default renderer is always present.
     *
     * @param renderer the renderer to add
     * @param index the index to add the renderer at
     * @return the new instance of options
     * @throws java.lang.IllegalArgumentException if the renderer is already included in this options instance
     * or is of type {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer}
     * @see List#add(int, Object)
     */
    @NotNull
    public MinecraftSerializerOptions<O> addRenderer(int index, @NotNull NodeRenderer<O> renderer) {
        if (renderers.contains(renderer)) {
            throw new IllegalArgumentException("The provided renderer is already included in this options instance");
        }
        if (renderer.getClass().equals(DefaultMinecraftRenderer.class)) {
            throw new IllegalArgumentException("DefaultMinecraftRenderer cannot be added to serializer options");
        }
        List<NodeRenderer<O>> renderers = new ArrayList<>(this.renderers);
        renderers.add(index, renderer);
        return new MinecraftSerializerOptions<>(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Creates a new instance of {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions}
     * based on this instance without the given renderer.
     *
     * @param renderer the renderer to remove
     * @return the new instance of options
     * @throws java.lang.IllegalArgumentException if the renderer is not included in this options instance
     */
    public MinecraftSerializerOptions<O> removeRenderer(NodeRenderer<O> renderer) {
        if (!renderers.contains(renderer)) {
            throw new IllegalArgumentException("The provided renderer is not included in this options instance");
        }
        List<NodeRenderer<O>> renderers = new ArrayList<>(this.renderers);
        renderers.remove(renderer);
        return new MinecraftSerializerOptions<>(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Returns the renderers for this options instance.
     * @return the ordered unmodifiable list of
     */
    @NotNull
    public List<NodeRenderer<O>> getRenderers() {
        return renderers;
    }

    public boolean isDebuggingEnabled() {
        return debuggingEnabled;
    }

    public MinecraftSerializerOptions<O> withDebuggingEnabled(boolean debuggingEnabled) {
        return new MinecraftSerializerOptions<>(parser, rules, renderers, debuggingEnabled);
    }

    @Override
    public String toString() {
        return "MinecraftSerializerOptions{" +
                "parser=" + parser +
                ", rules=" + rules +
                ", renderers=" + renderers +
                ", debuggingEnabled=" + debuggingEnabled +
                '}';
    }
}
