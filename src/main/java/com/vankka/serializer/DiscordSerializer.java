package com.vankka.serializer;

import java.util.ArrayList;
import java.util.List;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextDecoration;

public final class DiscordSerializer {

    // Serializes TextComponent (from a chat message) to Discord formatting (markdown)
    private String serialize(TextComponent textComponent) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Text> texts = getTexts(textComponent, new Text());

        for (Text text : texts) {
            if (text.isBold()) {
                stringBuilder.append("**");
            }

            if (text.isStrikethrough()) {
                stringBuilder.append("~~");
            }

            if (text.isItalic()) {
                stringBuilder.append("_");
            }

            if (text.isUnderline()) {
                stringBuilder.append("__");
            }

            stringBuilder.append(text.getContent());

            if (text.isUnderline()) {
                stringBuilder.append("__");
            }

            if (text.isItalic()) {
                stringBuilder.append("_");
            }

            if (text.isStrikethrough()) {
                stringBuilder.append("~~");
            }

            if (text.isBold()) {
                stringBuilder.append("**");
            }

            stringBuilder.append("\u200B"); // zero width space
        }

        return stringBuilder.toString();
    }

    private List<Text> getTexts(Component component, Text text) {
        List<Text> output = new ArrayList<>();
        if (component instanceof TextComponent) {
            text.setContent(((TextComponent) component).content());
        }

        TextDecoration.State bold = component.decoration(TextDecoration.BOLD);
        if (bold != TextDecoration.State.NOT_SET) {
            text.setBold(bold == TextDecoration.State.TRUE);
        }

        TextDecoration.State italic = component.decoration(TextDecoration.ITALIC);
        if (italic != TextDecoration.State.NOT_SET) {
            text.setItalic(italic == TextDecoration.State.TRUE);
        }

        TextDecoration.State underline = component.decoration(TextDecoration.UNDERLINE);
        if (underline != TextDecoration.State.NOT_SET) {
            text.setUnderline(underline == TextDecoration.State.TRUE);
        }

        TextDecoration.State strikethrough = component.decoration(TextDecoration.STRIKETHROUGH);
        if (strikethrough != TextDecoration.State.NOT_SET) {
            text.setStrikethrough(strikethrough == TextDecoration.State.TRUE);
        }

        output.add(text);

        component.children().forEach(child -> {
            Text next = text.clone();
            next.setContent("");

            output.addAll(getTexts(child, next));
        });

        return output;
    }

    private class Text {
        private String content;
        private boolean bold = false, strikethrough = false, underline = false, italic = false;

        void setContent(String content) {
            this.content = content;
        }

        void setBold(boolean bold) {
            this.bold = bold;
        }

        void setStrikethrough(boolean strikethrough) {
            this.strikethrough = strikethrough;
        }

        void setUnderline(boolean underline) {
            this.underline = underline;
        }

        void setItalic(boolean italic) {
            this.italic = italic;
        }

        String getContent() {
            return content;
        }

        boolean isBold() {
            return bold;
        }

        boolean isStrikethrough() {
            return strikethrough;
        }

        boolean isUnderline() {
            return underline;
        }

        boolean isItalic() {
            return italic;
        }

        public Text clone() {
            Text text = new Text();
            text.setContent(content);
            text.setBold(bold);
            text.setStrikethrough(strikethrough);
            text.setUnderline(underline);
            text.setItalic(italic);
            return text;
        }
    }
}