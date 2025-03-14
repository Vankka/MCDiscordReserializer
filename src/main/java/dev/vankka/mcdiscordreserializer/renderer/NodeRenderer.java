/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2021-2025 Vankka
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

package dev.vankka.mcdiscordreserializer.renderer;

import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions;
import dev.vankka.simpleast.core.node.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Interface for rendering {@link dev.vankka.simpleast.core.node.Node}s into the given type.
 * @param <O> the type.
 */
public interface NodeRenderer<O> {

    /**
     * Renders the given {@link dev.vankka.simpleast.core.node.Node} onto the provided
     * input using the given {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions}.
     *
     * @param renderTo the input to apply the node to
     * @param node the node
     * @param serializerOptions the serializer options for this render
     * @param renderWithChildren a function to allow rendering a node recursively
     * @return the renderTo input with the node applied to it
     */
    @Nullable
    O render(@NotNull O renderTo,
             @NotNull Node<Object> node,
             @NotNull MinecraftSerializerOptions<O> serializerOptions,
             @NotNull Function<Node<Object>, O> renderWithChildren);

    /**
     * Renders a given {@link dev.vankka.simpleast.core.node.Node} after children for it have been processed.
     *
     * @param renderTo the input to apply the node to
     * @param node the node
     * @param serializerOptions the serializer options for this render
     * @param renderWithChildren a function to allow rendering a node recursively
     * @return the renderTo input with the node applied to it
     * @see #render(Object, dev.vankka.simpleast.core.node.Node, dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions, java.util.function.Function)
     */
    @Nullable
    default O renderAfterChildren(@Nullable O renderTo,
                                  @NotNull Node<Object> node,
                                  @NotNull MinecraftSerializerOptions<O> serializerOptions,
                                  @NotNull Function<Node<Object>, O> renderWithChildren) {
        return null;
    }
}
