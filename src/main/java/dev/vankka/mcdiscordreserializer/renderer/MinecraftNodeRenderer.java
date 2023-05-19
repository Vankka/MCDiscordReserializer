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

package dev.vankka.mcdiscordreserializer.renderer;

import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions;
import dev.vankka.simpleast.core.node.Node;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Interface for rendering {@link dev.vankka.simpleast.core.node.Node}s into Minecraft {@link net.kyori.adventure.text.Component}s.
 */
public interface MinecraftNodeRenderer extends NodeRenderer<Component> {

    /**
     * Renders the given {@link dev.vankka.simpleast.core.node.Node} onto the provided
     * {@link net.kyori.adventure.text.Component} using the given
     * {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions}.
     *
     * @param baseComponent the input component to apply the node to
     * @param node the node
     * @param serializerOptions the serializer options for this render
     * @param renderWithChildren a function to allow rendering a node recursively
     * @return the new component with the node applied to it
     */
    @Nullable
    Component render(@NotNull Component baseComponent,
                     @NotNull Node<Object> node,
                     @NotNull MinecraftSerializerOptions<Component> serializerOptions,
                     @NotNull Function<Node<Object>, Component> renderWithChildren);
}
