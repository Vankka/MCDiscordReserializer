/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2018-2022 Vankka
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

package dev.vankka.mcdiscordreserializer.renderer.implementation;

import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializerOptions;
import dev.vankka.mcdiscordreserializer.renderer.NodeRenderer;
import dev.vankka.simpleast.core.TextStyle;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.StyleNode;
import dev.vankka.simpleast.core.node.TextNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The default implementation for escaping Discord markdown.
 */
public class DefaultDiscordEscapingRenderer implements NodeRenderer<String> {

    /**
     * The instance of {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultDiscordEscapingRenderer}.
     */
    public static final DefaultDiscordEscapingRenderer INSTANCE = new DefaultDiscordEscapingRenderer();

    /**
     * Creates a new instance of the {@link dev.vankka.mcdiscordreserializer.renderer.implementation.DefaultDiscordEscapingRenderer} unless you're extending the class you shouldn't use this.
     * @see #INSTANCE
     */
    public DefaultDiscordEscapingRenderer() {
    }

    private String render(String renderTo, Node<Object> node, boolean after) {
        if (node instanceof TextNode && !after) {
            return renderTo + ((TextNode<Object>) node).getContent();
        } else if (node instanceof StyleNode) {
            StringBuilder output = new StringBuilder(renderTo);
            for (Object style : ((StyleNode<?, ?>) node).getStyles()) {
                TextStyle textStyle = (TextStyle) style;
                TextStyle.Type styleType = textStyle.getType();
                if (styleType == TextStyle.Type.QUOTE && after) {
                    continue;
                }

                output.append(getChar(textStyle));
            }
            return output.toString();
        } else {
            return null;
        }
    }

    private String getChar(TextStyle textStyle) {
        switch (textStyle.getType()) {
            case BOLD: return "\\*\\*";
            case QUOTE: return "\\>";
            case ITALICS: return "\\" + (textStyle.getExtra().getOrDefault("asterisk", "true").equals("true") ? "*" : "_");
            case SPOILER: return "\\|\\|";
            case UNDERLINE: return "\\_\\_";
            case CODE_BLOCK: return "\\`\\`\\`";
            case CODE_STRING: return "\\`";
            default: return null;
        }
    }

    @Override
    public String render(@Nullable String renderTo, @NotNull Node<Object> node,
                         @NotNull MinecraftSerializerOptions<String> serializerOptions,
                         @NotNull Function<Node<Object>, String> renderWithChildren) {
        return render(renderTo, node, false);
    }


    @Override
    public String renderAfterChildren(@Nullable String renderTo, @NotNull Node<Object> node,
                                      @NotNull MinecraftSerializerOptions<String> serializerOptions,
                                      @NotNull Function<Node<Object>, String> renderWithChildren) {
        return render(renderTo, node, true);
    }
}
