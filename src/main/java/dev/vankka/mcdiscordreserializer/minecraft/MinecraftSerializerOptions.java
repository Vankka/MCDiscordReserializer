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
import dev.vankka.mcdiscordreserializer.renderer.NodeRenderer;
import dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer;
import dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.parser.Parser;
import dev.vankka.simpleast.core.parser.Rule;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Options for {@link MinecraftSerializer}s.
 */
@RequiredArgsConstructor
@ToString
public class MinecraftSerializerOptions {

    /**
     * Creates the default {@link MinecraftSerializerOptions}.
     * @return the default {@link MinecraftSerializerOptions}.
     */
    public static MinecraftSerializerOptions defaults() {
        return new MinecraftSerializerOptions(new Parser<>(),
                DiscordMarkdownRules.createAllRulesForDiscord(true),
                Collections.emptyList(),
                false);
    }

    @Deprecated
    public MinecraftSerializerOptions withRenderer(MinecraftRenderer renderer) {
        return new MinecraftSerializerOptions(parser, rules, Collections.singletonList(renderer), debuggingEnabled);
    }

    @Deprecated
    public MinecraftRenderer getRenderer() {
        return renderers.stream()
                .filter(renderer -> renderer instanceof MinecraftRenderer)
                .map(renderer -> (MinecraftRenderer) renderer)
                .findFirst().orElse(DefaultMinecraftRenderer.INSTANCE);
    }

    /**
     * Creates a instance of {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions} with the given renderer added.
     *
     * @param renderer the renderer to add
     * @return the new instance of options
     * @throws java.lang.IllegalArgumentException if the renderer is already included in this options instance or is of type {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer}
     * @see List#add(Object)
     */
    public MinecraftSerializerOptions addRenderer(NodeRenderer renderer) {
        if (renderers.contains(renderer)) {
            throw new IllegalArgumentException("The provided renderer is already included in this options instance");
        }
        if (renderer instanceof DefaultMinecraftRenderer) {
            throw new IllegalArgumentException("DefaultMinecraftRenderer cannot be added to serializer options");
        }
        List<NodeRenderer> renderers = new ArrayList<>(this.renderers);
        renderers.add(renderer);
        return new MinecraftSerializerOptions(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Creates a instance of {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions} with the given renderer added at the given index, keep in mind a default renderer is always present.
     *
     * @param renderer the renderer to add
     * @param index the index to add the renderer at
     * @return the new instance of options
     * @throws java.lang.IllegalArgumentException if the renderer is already included in this options instance or is of type {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer}
     * @see List#add(int, Object)
     */
    public MinecraftSerializerOptions addRenderer(int index, NodeRenderer renderer) {
        if (renderers.contains(renderer)) {
            throw new IllegalArgumentException("The provided renderer is already included in this options instance");
        }
        if (renderer instanceof DefaultMinecraftRenderer) {
            throw new IllegalArgumentException("DefaultMinecraftRenderer cannot be added to serializer options");
        }
        List<NodeRenderer> renderers = new ArrayList<>(this.renderers);
        renderers.add(index, renderer);
        return new MinecraftSerializerOptions(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Creates a instance of {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions} without the given renderer.
     *
     * @param renderer the renderer to remove
     * @return the new instance of options
     * @throws java.lang.IllegalArgumentException if the renderer is not included in this options instance
     */
    public MinecraftSerializerOptions removeRenderer(NodeRenderer renderer) {
        if (!renderers.contains(renderer)) {
            throw new IllegalArgumentException("The provided renderer is not included in this options instance");
        }
        List<NodeRenderer> renderers = new ArrayList<>(this.renderers);
        renderers.remove(renderer);
        return new MinecraftSerializerOptions(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Returns the renderers for this options instance
     * @return the ordered unmodifiable list of
     */
    public List<NodeRenderer> getRenderers() {
        return Collections.unmodifiableList(renderers);
    }

    /**
     * The SimpleAST {@link Parser} to use to generate the abstract syntax tree.
     */
    @NonNull
    @With
    @Getter
    private final Parser<Object, Node<Object>, Object> parser;

    /**
     * The {@link dev.vankka.simpleast.core.parser.Rule Rules} for the {@link Parser}, null to use the {@link Parser Parsers} default rules.
     */
    @With
    @Getter
    private final List<Rule<Object, Node<Object>, Object>> rules;

    /**
     * The {@link dev.vankka.mcdiscordreserializer.renderer.NodeRenderer}s to use to render formatting for Minecraft.
     */
    @NonNull
    private final List<NodeRenderer> renderers;

    /**
     * Weather or not to use debug logging for the {@link Parser}.
     */
    @With
    @Getter
    private final boolean debuggingEnabled;
}
