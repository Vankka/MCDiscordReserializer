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

package dev.vankka.mcdiscordreserializer.minecraft;

import dev.vankka.mcdiscordreserializer.renderer.MinecraftRenderer;
import dev.vankka.mcdiscordreserializer.renderer.NodeRenderer;
import dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultDiscordEscapingRenderer;
import dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.TextNode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

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
@SuppressWarnings("unused") // API
public class MinecraftSerializer {

    /**
     * Default instance of the MinecraftSerializer, incase that's all you need.
     * Using {@link MinecraftSerializer#setDefaultOptions(MinecraftSerializerOptions)} and
     * {@link MinecraftSerializer#setMarkdownDefaultOptions(MinecraftSerializerOptions)} are not allowed.
     */
    @NotNull
    public static final MinecraftSerializer INSTANCE = new MinecraftSerializer() {

        @Override
        public void setDefaultOptions(@NotNull MinecraftSerializerOptions<Component> defaultOptions) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }

        @Override
        public void setMarkdownDefaultOptions(@NotNull MinecraftSerializerOptions<String> markdownDefaultOptions) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    /**
     * The default {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions}
     * to use for this serializer.
     * @see #serialize(String)
     */
    @Getter
    @Setter
    @NotNull
    private MinecraftSerializerOptions<Component> defaultOptions;

    /**
     * The default {@link dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions}
     * to use for escaping markdown.
     * @see #escapeMarkdown(String, MinecraftSerializerOptions)
     */
    @Getter
    @Setter
    private MinecraftSerializerOptions<String> markdownDefaultOptions;

    /**
     * Constructor for creating a serializer, with {@link MinecraftSerializerOptions#defaults()}
     * and {@link MinecraftSerializerOptions#escapeDefaults()} as defaults.
     */
    public MinecraftSerializer() {
        this(MinecraftSerializerOptions.defaults(), MinecraftSerializerOptions.escapeDefaults());
    }

    /**
     * Constructor for creating a serializer, with the specified {@link MinecraftSerializerOptions} as defaults.
     *
     * @param defaultOptions the default serializer options (can be overridden on serialize)
     * @see MinecraftSerializerOptions#defaults()
     * @see MinecraftSerializerOptions#MinecraftSerializerOptions(dev.vankka.simpleast.core.parser.Parser, List, List, boolean)
     */
    public MinecraftSerializer(@NotNull MinecraftSerializerOptions<Component> defaultOptions,
                               @NotNull MinecraftSerializerOptions<String> markdownDefaultOptions) {
        this.defaultOptions = defaultOptions;
        this.markdownDefaultOptions = markdownDefaultOptions;
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft {@link Component} using this serializer's
     * {@link MinecraftSerializer#getDefaultOptions() default options}.<br/>
     * Use {@link MinecraftSerializer#serialize(String, MinecraftSerializerOptions)} to fine tune the serialization options.
     *
     * @param discordMessage a Discord markdown message
     * @return the Discord message formatted to a Minecraft TextComponent
     */
    @NotNull
    public Component serialize(@NotNull final String discordMessage) {
        MinecraftSerializerOptions<Component> options = getDefaultOptions();
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
    @NotNull
    public Component serialize(@NotNull final String discordMessage, @NotNull final MinecraftSerializerOptions<Component> serializerOptions) {
        List<Component> components = new ArrayList<>();

        List<Node<Object>> nodes = serializerOptions.getParser().parse(discordMessage, null, serializerOptions.getRules(), serializerOptions.isDebuggingEnabled());
        nodes = flattenTextNodes(nodes); // reduce the amount of single character nodes caused by special characters
        for (Node<Object> node : nodes) {
            components.add(addChild(node, Component.empty(), serializerOptions));
        }

        return Component.empty().children(components);
    }

    /**
     * Escapes the given Discord message of Discord markdown. Should include the entire message (not just a part) to be effective.
     *
     * @param discordMessage    the Discord message
     * @return the Discord markdown message with markdown escaped
     * @see MinecraftSerializerOptions#escapeDefaults()
     * @see dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializer#escapeMarkdown(String, MinecraftSerializerOptions)
     */
    public String escapeMarkdown(@NotNull final String discordMessage) {
        return escapeMarkdown(discordMessage, getMarkdownDefaultOptions());
    }

    /**
     * Escapes the given Discord message of Discord markdown. Should include the entire message (not just a part) to be effective.
     *
     * @param discordMessage    the Discord message
     * @param serializerOptions options for this escape
     * @return the Discord markdown message with markdown escaped
     * @see MinecraftSerializerOptions#escapeDefaults()
     * @see dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializer#escapeMarkdown(String)
     */
    public String escapeMarkdown(@NotNull final String discordMessage, @NotNull final MinecraftSerializerOptions<String> serializerOptions) {
        String output = "";

        List<Node<Object>> nodes = serializerOptions.getParser().parse(discordMessage, null, serializerOptions.getRules(), serializerOptions.isDebuggingEnabled());
        nodes = flattenTextNodes(nodes); // reduce the amount of single character nodes caused by special characters
        for (Node<Object> node : nodes) {
            output = addChild(node, output, serializerOptions);
        }

        return output;
    }

    private Component addChild(final Node<Object> node, final Component styleNode,
                               final MinecraftSerializerOptions<Component> serializerOptions) {
        Component component = Component.empty().mergeStyle(styleNode);
        Function<Node<Object>, Component> renderWithChildren = otherNode -> addChild(otherNode, component, serializerOptions);

        Component output = null;
        NodeRenderer<Component> render = null;
        for (NodeRenderer<Component> renderer : serializerOptions.getRenderers()) {
            Component currentOutput = renderer.render(component, node, serializerOptions, renderWithChildren);
            if (currentOutput != null) {
                output = currentOutput;
                render = renderer;
                break;
            }
        }
        if (output == null) {
            render = DefaultMinecraftRenderer.INSTANCE;
            output = render.render(component, node, serializerOptions, renderWithChildren);
            if (output == null) {
                throw new IllegalStateException("DefaultMinecraftRenderer returned a null component");
            }
        }

        Collection<Node<Object>> children = node.getChildren();
        if (children != null) {
            for (Node<Object> child : children) {
                output = output.append(addChild(child, output, serializerOptions));
            }
        }

        Component newOutput = render.renderAfterChildren(output, node, serializerOptions, renderWithChildren);
        if (newOutput != null) {
            output = newOutput;
        }

        return output;
    }

    private String addChild(final Node<Object> node, final String input,
                          final MinecraftSerializerOptions<String> serializerOptions) {
        Function<Node<Object>, String> renderWithChildren = otherNode -> addChild(otherNode, input, serializerOptions);

        String output = null;
        NodeRenderer<String> render = null;
        for (NodeRenderer<String> renderer : serializerOptions.getRenderers()) {
            output = renderer.render(output, node, serializerOptions, renderWithChildren);
            if (output != null) {
                render = renderer;
                break;
            }
        }
        if (output == null) {
            render = DefaultDiscordEscapingRenderer.INSTANCE;
            output = render.render(input, node, serializerOptions, renderWithChildren);
        }

        Collection<Node<Object>> children = node.getChildren();
        if (children != null) {
            for (Node<Object> child : children) {
                output = addChild(child, output, serializerOptions);
            }
        }

        String newOutput = render.renderAfterChildren(output, node, serializerOptions, renderWithChildren);
        if (newOutput != null) {
            output = newOutput;
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
