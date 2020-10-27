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

package dev.vankka.mcdiscordreserializer.rules;

import dev.vankka.simpleast.core.TextStyle;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.StyleNode;
import dev.vankka.simpleast.core.node.TextNode;
import dev.vankka.simpleast.core.parser.ParseSpec;
import dev.vankka.simpleast.core.parser.Parser;
import dev.vankka.simpleast.core.parser.Rule;
import dev.vankka.simpleast.core.simple.SimpleMarkdownRules;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown rules for Discord formatting.
 */
public final class DiscordMarkdownRules {

    private DiscordMarkdownRules() {
    }

    private static final Pattern PATTERN_EMOTE_MENTION = Pattern.compile("<a?:(\\w+):(\\d+)>");
    private static final Pattern PATTERN_CHANNEL_MENTION = Pattern.compile("<#(\\d+)>");
    private static final Pattern PATTERN_USER_MENTION = Pattern.compile("<@!?(\\d+)>");
    private static final Pattern PATTERN_ROLE_MENTION = Pattern.compile("<@&(\\d+)>");

    private static final Pattern PATTERN_SPOILER = Pattern.compile("\\|\\|([\\s\\S]+?)\\|\\|");
    private static final Pattern PATTERN_CODE_STRING = Pattern.compile("`(.+?)`");
    private static final Pattern PATTERN_QUOTE = Pattern.compile("> (.+(?:\\n> .+)*)", Pattern.MULTILINE);
    private static final Pattern PATTERN_CODE_BLOCK = Pattern.compile("```(?:(\\S+?)[\\n ])?\\n*(?:(.+?))\\n*```");
    private static final Pattern PATTERN_MATCH_ALL = Pattern.compile("([\\w\\W]+)");

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's emote mentions.
     * <a href="https://discord.com/developers/docs/reference#message-formatting">Discord developer docs</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createEmoteMentionRule() {
        return new Rule<R, Node<R>, S>(PATTERN_EMOTE_MENTION) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                Map<String, String> extra = new HashMap<>();
                extra.put("name", matcher.group(1));
                extra.put("id", matcher.group(2));

                return ParseSpec.createTerminal(new StyleNode<>(new ArrayList<>(
                        Collections.singletonList(new TextStyle(TextStyle.Type.MENTION_EMOJI, extra)))), state);
            }
        };
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's channel mentions.
     * <a href="https://discord.com/developers/docs/reference#message-formatting">Discord developer docs</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createChannelMentionRule() {
        return createSimpleMentionRule(PATTERN_CHANNEL_MENTION, new TextStyle(TextStyle.Type.MENTION_CHANNEL));
    }


    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's user mentions.
     * <a href="https://discord.com/developers/docs/reference#message-formatting">Discord developer docs</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createUserMentionRule() {
        return createSimpleMentionRule(PATTERN_USER_MENTION, new TextStyle(TextStyle.Type.MENTION_USER));
    }


    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's role mentions.
     * <a href="https://discord.com/developers/docs/reference#message-formatting">Discord developer docs</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createRoleMentionRule() {
        return createSimpleMentionRule(PATTERN_ROLE_MENTION, new TextStyle(TextStyle.Type.MENTION_ROLE));
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's spoilers.
     * <a href="https://support.discord.com/hc/en-us/articles/360022320632-Spoiler-Tags-">Discord blog</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createSpoilerRule() {
        return new Rule<R, Node<R>, S>(PATTERN_SPOILER) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                Map<String, String> extra = new HashMap<>();
                extra.put("content", matcher.group(1));

                return ParseSpec.createTerminal(new StyleNode<>(new ArrayList<>(Collections.singletonList(new TextStyle(TextStyle.Type.SPOILER, extra)))), state);
            }
        };
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's code strings.
     * <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-">Discord blog</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createCodeStringRule() {
        return new Rule<R, Node<R>, S>(PATTERN_CODE_STRING) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                String content = matcher.group();
                return ParseSpec.createTerminal(StyleNode.Companion.createWithText(content.substring(1, content.length() - 1),
                        new ArrayList<>(Collections.singletonList(new TextStyle(TextStyle.Type.CODE_STRING)))), state);
            }
        };
    }

    private static <R, S> Rule<R, Node<R>, S> createSimpleMentionRule(Pattern pattern, TextStyle textStyle) {
        return new Rule<R, Node<R>, S>(pattern) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                textStyle.getExtra().put("id", matcher.group(1));

                // , matcher.start(1), matcher.end(1)
                return ParseSpec.createTerminal(new StyleNode<>(new ArrayList<>(Collections.singletonList(textStyle))), state);
            }
        };
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's quotes.
     * <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-">Discord blog</a>
     */
    public static <R> Rule<R, Node<R>, Object> createQuoteRule() {
        return new Rule<R, Node<R>, Object>(PATTERN_QUOTE) {
            @Override
            public Matcher match(CharSequence inspectionSource, String lastCapture, Object state) {
                if (state instanceof QuoteState && ((QuoteState) state).isInQuote) {
                    return null;
                } else {
                    return super.match(inspectionSource, lastCapture, state);
                }
            }

            @Override
            public ParseSpec<R, Node<R>, Object> parse(Matcher matcher, Parser parser, Object state) {
                Object newState = state instanceof QuoteState ? ((QuoteState) state).newQuoteState(true) : new QuoteState(true);

                Map<String, String> extra = new HashMap<>();
                extra.put("content", matcher.group(1).trim().replace("\n> ", "\n"));

                return ParseSpec.createTerminal(new StyleNode<>(Collections.singletonList(new TextStyle(TextStyle.Type.QUOTE, extra))), newState);
            }
        };
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's code blocks.
     * <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-">Discord blog</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createCodeBlockRule() {
        return new Rule<R, Node<R>, S>(PATTERN_CODE_BLOCK) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                Map<String, String> extra = new HashMap<>();
                extra.put("language", matcher.group(1));

                return ParseSpec.createTerminal(StyleNode.Companion.createWithText(matcher.group(2),
                        (Collections.singletonList(new TextStyle(TextStyle.Type.CODE_BLOCK, extra)))), state);
            }
        };
    }

    /**
     * Creates all the mention rules.
     *
     * @see #createEmoteMentionRule()
     * @see #createChannelMentionRule()
     * @see #createUserMentionRule()
     * @see #createRoleMentionRule()
     */
    public static <R, S> List<Rule<R, Node<R>, S>> createMentionRules() {
        List<Rule<R, Node<R>, S>> rules = new ArrayList<>();
        rules.add(createEmoteMentionRule());
        rules.add(createChannelMentionRule());
        rules.add(createUserMentionRule());
        rules.add(createRoleMentionRule());

        return rules;
    }


    /**
     * Creates all the style rules.
     *
     * @see #createQuoteRule()
     * @see #createCodeStringRule()
     * @see #createCodeBlockRule()
     * @see #createSpoilerRule()
     */
    public static <R> List<Rule<R, Node<R>, Object>> createStyleRules() {
        List<Rule<R, Node<R>, Object>> rules = new ArrayList<>();
        rules.add(createQuoteRule());
        rules.add(createSpoilerRule());
        rules.add(createCodeBlockRule());
        rules.add(createCodeStringRule());

        return rules;
    }

    /**
     * Creates all rules for Discord mentions and styles.
     *
     * @see #createMentionRules()
     * @see #createStyleRules()
     */
    public static <R> List<Rule<R, Node<R>, Object>> createDiscordMarkdownRules() {
        List<Rule<R, Node<R>, Object>> rules = new ArrayList<>();
        rules.addAll(createStyleRules());
        rules.addAll(createMentionRules());

        return rules;
    }

    /**
     * Creates a text rule that matches all characters, without cutting at special characters like
     * {@link dev.vankka.simpleast.core.simple.SimpleMarkdownRules#createTextRule()}.
     */
    public static <R, S> Rule<R, Node<R>, S> createMatchAllTextRule() {
        return new Rule<R, Node<R>, S>(PATTERN_MATCH_ALL) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                return ParseSpec.createTerminal(new TextNode<>(matcher.group()), state);
            }
        };
    }

    /**
     * Creates a set of rules for parsing Discord messages.
     *
     * @param includeText Should the text rule be included?
     * @return The rules for parsing Discord messages
     */
    public static <R> List<Rule<R, Node<R>, Object>> createAllRulesForDiscord(boolean includeText) {
        List<Rule<R, Node<R>, Object>> rules = new ArrayList<>();
        rules.addAll(SimpleMarkdownRules.createSimpleMarkdownRules(false));
        rules.addAll(createDiscordMarkdownRules());
        if (includeText) {
            rules.add(createMatchAllTextRule());
        }

        return rules;
    }

    /**
     * A state for quotes, used to not recursively parse quotes.
     */
    public static class QuoteState {
        private boolean isInQuote;

        /**
         * Creates a {@link dev.vankka.mcdiscordreserializer.rules.DiscordMarkdownRules.QuoteState}.
         */
        public QuoteState(boolean isInQuote) {
            this.isInQuote = isInQuote;
        }

        /**
         * Sets a new status for this quote state.
         */
        public QuoteState newQuoteState(boolean isInQuote) {
            this.isInQuote = isInQuote;
            return this;
        }
    }
}
