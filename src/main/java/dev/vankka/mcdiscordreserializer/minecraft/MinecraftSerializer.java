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
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.TextNode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * MinecraftSerializer, for serializing from Discord messages to Minecraft {@link Component}s.
 *
 * @author Vankka
 *
 * @see MinecraftSerializerOptions
 * @see MinecraftRenderer
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MinecraftSerializer {

    /**
     * Default instance of the MinecraftSerializer, incase that's all you need.
     * Using {@link MinecraftSerializer#setDefaultOptions(MinecraftSerializerOptions)} is not allowed.
     */
    public static final MinecraftSerializer INSTANCE = new MinecraftSerializer() {

        @Override
        public void setDefaultOptions(MinecraftSerializerOptions defaultOptions) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    /**
     * The default {@link MinecraftSerializerOptions} to use for this serializer.
     */
    @Getter
    @Setter
    private MinecraftSerializerOptions defaultOptions;

    /**
     * Constructor for creating a serializer, with {@link MinecraftSerializerOptions#defaults()} as defaults.
     */
    public MinecraftSerializer() {
        this(MinecraftSerializerOptions.defaults());
    }

    /**
     * Constructor for creating a serializer, with the specified {@link MinecraftSerializerOptions} as defaults.
     *
     * @param defaultOptions the default serializer options (can be overridden on serialize)
     * @see MinecraftSerializerOptions#defaults()
     * @see MinecraftSerializerOptions#MinecraftSerializerOptions(dev.vankka.simpleast.core.parser.Parser, List, List, boolean)
     */
    public MinecraftSerializer(@NonNull MinecraftSerializerOptions defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft {@link Component} using this serializer's
     * {@link MinecraftSerializer#getDefaultOptions() default options}.<br/>
     * Use {@link MinecraftSerializer#serialize(String, MinecraftSerializerOptions)} to fine tune the serialization options.
     *
     * @param discordMessage a Discord markdown message
     * @return the Discord message formatted to a Minecraft TextComponent
     */
    public Component serialize(@NonNull final String discordMessage) {
        MinecraftSerializerOptions options = getDefaultOptions();
        return serialize(discordMessage, options);
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft {@link Component}.
     *
     * @param discordMessage    a Discord markdown message
     * @param serializerOptions The options to use for this serialization
     * @return the Discord message formatted to a Minecraft TextComponent
     * @see MinecraftSerializerOptions#defaults()
     * @see MinecraftSerializerOptions#MinecraftSerializerOptions(dev.vankka.simpleast.core.parser.Parser, List, List, boolean)
     */
    public Component serialize(@NonNull final String discordMessage, @NonNull final MinecraftSerializerOptions serializerOptions) {
        List<Component> components = new ArrayList<>();

        List<Node<Object>> nodes = serializerOptions.getParser().parse(discordMessage, null, serializerOptions.getRules(), serializerOptions.isDebuggingEnabled());
        nodes = flattenTextNodes(nodes); // reduce the amount of single character nodes caused by special characters
        for (Node<Object> node : nodes) {
            components.add(addChild(node, Component.empty(), serializerOptions));
        }

        return Component.empty().children(components);
    }

    private Component addChild(@NonNull final Node<Object> node, @NonNull final Component styleNode,
                               @NonNull final MinecraftSerializerOptions serializerOptions) {
        Component component = Component.empty().mergeStyle(styleNode);
        Function<Node<Object>, Component> renderWithChildren = otherNode -> addChild(otherNode, component, serializerOptions);

        Component output = null;
        for (NodeRenderer renderer : serializerOptions.getRenderers()) {
            output = renderer.render(component, node, serializerOptions, renderWithChildren);
            if (output != null) {
                break;
            }
        }
        if (output == null) {
            output = DefaultMinecraftRenderer.INSTANCE.render(component, node, serializerOptions, renderWithChildren);
        }

        Collection<Node<Object>> children = node.getChildren();
        if (children != null) {
            for (Node<Object> child : children) {
                output = output.append(addChild(child, output, serializerOptions));
            }
        }

        return output;
    }

    @SuppressWarnings("unchecked")
    private <R, T extends Node<R>> List<T> flattenTextNodes(List<T> nodes) {
        List<T> newNodes = new ArrayList<>();
        TextNode<T> previousNode = null;
        for (T node : nodes) {
            List<Node<R>> children = node.getChildren();
            if (!children.isEmpty()) {
                if (previousNode != null) {
                    newNodes.add((T) previousNode);
                    previousNode = null;
                }

                List<T> childNodes = flattenTextNodes((List<T>) children);
                node.getChildren().clear();
                node.getChildren().addAll(childNodes);
                newNodes.add(node);
                continue;
            }
            if (!(node instanceof TextNode)) {
                if (previousNode != null) {
                    newNodes.add((T) previousNode);
                    previousNode = null;
                }
                newNodes.add(node);
                continue;
            }

            if (previousNode == null) {
                previousNode = (TextNode<T>) node;
            } else {
                previousNode = new TextNode<>(previousNode.getContent() + ((TextNode<?>) node).getContent());
            }
        }
        if (previousNode != null) {
            newNodes.add((T) previousNode);
        }
        return newNodes;
    }
}
