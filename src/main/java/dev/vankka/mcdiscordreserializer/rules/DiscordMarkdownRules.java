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

package dev.vankka.mcdiscordreserializer.rules;

import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.TextNode;
import dev.vankka.simpleast.core.parser.ParseSpec;
import dev.vankka.simpleast.core.parser.Parser;
import dev.vankka.simpleast.core.parser.Rule;
import dev.vankka.simpleast.core.simple.SimpleMarkdownRules;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown rules for Discord formatting. The patterns here attempt to follow how the Discord client behaves as closely as possible.
 */
public final class DiscordMarkdownRules {

    private DiscordMarkdownRules() {}

    private static final Pattern PATTERN_EMOTE_MENTION = Pattern.compile("^<a?:(\\w+):(\\d+)>");
    private static final Pattern PATTERN_CHANNEL_MENTION = Pattern.compile("^<#(\\d+)>");
    private static final Pattern PATTERN_USER_MENTION = Pattern.compile("^<@!?(\\d+)>");
    private static final Pattern PATTERN_ROLE_MENTION = Pattern.compile("^<@&(\\d+)>");

    private static final Pattern PATTERN_BOLD = Pattern.compile("^\\*\\*(.+?)\\*\\*(?!\\*)");
    private static final Pattern PATTERN_UNDERLINE = Pattern.compile("^__(.+?)__(?!_)");
    private static final Pattern PATTERN_STRIKETHRU = Pattern.compile("^~~(.+?)~~");
    private static final Pattern PATTERN_SPOILER = Pattern.compile("^\\|\\|(.+?)\\|\\|");
    private static final Pattern PATTERN_CODE_STRING = Pattern.compile("^(?:`{2}(.+?)`{2}|`(.+?)`)");
    private static final Pattern PATTERN_QUOTE = Pattern.compile("^> (.+(?:\\n> .+)*)", Pattern.DOTALL);
    private static final Pattern PATTERN_CODE_BLOCK = Pattern.compile("^```(?:(\\S+?)\\n)?\\n*(.+?)\\n*```");

    private static final Pattern PATTERN_ITALICS = Pattern.compile(
            // only match _s surrounding words.
            "^\\b_" + "((?:__|\\\\[\\s\\S]|[^\\\\_])+?)_" + "\\b" +
                    "|" +
                    // Or match *s that are followed by a non-space:
                    "^\\*(?=\\S)(" +
                    // Match any of:
                    //  - `**`: so that bolds inside italics don't close the italics
                    //  - non-whitespace, non-* characters and then 0-2 whitespaces
                    "(?:\\*\\*|[^\\s*]\\s{0,2})+?" +
                    // followed by *, then non-*
                    ")\\*(?!\\*)"
    );

    // patched version of SimpleMarkdownRules.createText for quotes
    private static final Pattern PATTERN_TEXT = Pattern.compile("^[\\s\\S]+?(?=[^0-9A-Za-z\\s\\u00c0-\\uffff>]|\\n| {2,}\\n|\\w+:\\S|$)");
    private static final Pattern PATTERN_LINK = Pattern.compile("^(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]+\\.[-a-zA-Z0-9+&@#/%=~_|]+)");

    private static <R> StyleNode<R, StyleNode.Style> styleNode(StyleNode.Style style) {
        return new StyleNode<>(new ArrayList<>(Collections.singletonList(style)));
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's emote mentions.
     * <a href="https://discord.com/developers/docs/reference#message-formatting">Discord developer docs</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createEmoteMentionRule() {
        return new Rule<R, Node<R>, S>(PATTERN_EMOTE_MENTION) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                String name = matcher.group(1);
                String id = matcher.group(2);

                return ParseSpec.createTerminal(styleNode(new StyleNode.EmojiStyle(id, name)), state);
            }
        };
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's channel mentions.
     * <a href="https://discord.com/developers/docs/reference#message-formatting">Discord developer docs</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createChannelMentionRule() {
        return createSimpleMentionRule(PATTERN_CHANNEL_MENTION, StyleNode.MentionStyle.Type.CHANNEL);
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's user mentions.
     * <a href="https://discord.com/developers/docs/reference#message-formatting">Discord developer docs</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createUserMentionRule() {
        return createSimpleMentionRule(PATTERN_USER_MENTION, StyleNode.MentionStyle.Type.USER);
    }


    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's role mentions.
     * <a href="https://discord.com/developers/docs/reference#message-formatting">Discord developer docs</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createRoleMentionRule() {
        return createSimpleMentionRule(PATTERN_ROLE_MENTION, StyleNode.MentionStyle.Type.ROLE);
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's bold.
     * <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-">Discord blog</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createBoldRule() {
        return createSimpleStyleRule(PATTERN_BOLD, StyleNode.Styles.BOLD);
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's underline.
     * <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-">Discord blog</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createUnderlineRule() {
        return createSimpleStyleRule(PATTERN_UNDERLINE, StyleNode.Styles.UNDERLINE);
    }

    public static <R, S> Rule<R, Node<R>, S> createSimpleStyleRule(Pattern pattern, StyleNode.Style style) {
        return new Rule<R, Node<R>, S>(pattern) {

            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                return ParseSpec.createNonterminal(styleNode(style), state, matcher.start(1), matcher.end(1));
            }
        };
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's italics.
     * <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-">Discord blog</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createItalicsRule() {
        return new Rule<R, Node<R>, S>(PATTERN_ITALICS) {

            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                int startIndex;
                int endIndex;
                String asteriskMatch = matcher.group(2);
                boolean asterisk = asteriskMatch != null && !asteriskMatch.isEmpty();
                if (asterisk) {
                    startIndex = matcher.start(2);
                    endIndex = matcher.end(2);
                } else {
                    startIndex = matcher.start(1);
                    endIndex = matcher.end(1);
                }

                StyleNode.ItalicStyle style = new StyleNode.ItalicStyle(asterisk);
                return ParseSpec.createNonterminal(new StyleNode<>(new ArrayList<>(Collections.singletonList(style))), state, startIndex, endIndex);
            }
        };
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's strikethrough.
     * <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-">Discord blog</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createStrikethruRule() {
        return createSimpleStyleRule(PATTERN_STRIKETHRU, StyleNode.Styles.STRIKETHROUGH);
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's spoilers.
     * <a href="https://support.discord.com/hc/en-us/articles/360022320632-Spoiler-Tags-">Discord blog</a>
     */
    public static <R, S> Rule<R, Node<R>, S> createSpoilerRule() {
        return new Rule<R, Node<R>, S>(PATTERN_SPOILER) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                String content = matcher.group(1);

                return ParseSpec.createTerminal(styleNode(new StyleNode.ContentStyle(StyleNode.ContentStyle.Type.SPOILER, content)), state);
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
                String content = matcher.group(1);
                if (content == null) {
                    content = matcher.group(2);
                }
                return ParseSpec.createTerminal(
                        StyleNode.createWithText(
                                content,
                                new ArrayList<>(Collections.singletonList(StyleNode.Styles.CODE_STRING))
                        ),
                        state
                );
            }
        };
    }

    private static <R, S> Rule<R, Node<R>, S> createSimpleMentionRule(Pattern pattern, StyleNode.MentionStyle.Type style) {
        return new Rule<R, Node<R>, S>(pattern) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                String id = matcher.group(1);
                StyleNode.MentionStyle mentionStyle = new StyleNode.MentionStyle(style, id);

                return ParseSpec.createTerminal(styleNode(mentionStyle), state);
            }
        };
    }

    /**
     * Creates a {@link dev.vankka.simpleast.core.parser.Rule} for Discord's quotes.
     * <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-">Discord blog</a>
     *
     * <p>You will need to use the specialized text rule from this class</p>
     * @see #createSpecialTextRule()
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
                String content = matcher.group(1).trim().replace("\n> ", "\n");

                return ParseSpec.createNonterminal(
                        styleNode(new StyleNode.ContentStyle(StyleNode.ContentStyle.Type.QUOTE, content)),
                        newState,
                        matcher.start(1),
                        matcher.end(1)
                );
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
                String language = matcher.group(1);
                return ParseSpec.createTerminal(
                        StyleNode.createWithText(
                                matcher.group(2),
                                new ArrayList<>(Collections.singletonList(new StyleNode.CodeBlockStyle(language)))
                        ),
                        state
                );
            }
        };
    }

    /**
     * Creates a special text rule for Discord, required only if using quotes.
     * @see #createQuoteRule()
     */
    public static <R, S> Rule<R, Node<R>, S> createSpecialTextRule() {
        return new Rule<R, Node<R>, S>(PATTERN_TEXT) {

            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                return ParseSpec.createTerminal(new TextNode<>(matcher.group()), state);
            }
        };
    }

    /**
     * Creates a link rule, for appending the url instead of styling text as an url.
     * @see dev.vankka.simpleast.core.simple.SimpleMarkdownRules#createLinkRule()
     */
    public static <R, S> Rule<R, Node<R>, S> createLinkRule() {
        return new Rule<R, Node<R>, S>(PATTERN_LINK) {
            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                String link = matcher.group(1);

                return ParseSpec.createTerminal(
                        styleNode(new StyleNode.ContentStyle(StyleNode.ContentStyle.Type.LINK, link)),
                        state
                );
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
     * Creates basic markdown rules.
     * @return escape, link, newline, bold, underline, italics and strikethru rules.
     */
    public static <R> List<Rule<R, Node<R>, Object>> createSimpleMarkdownRules() {
        List<Rule<R, Node<R>, Object>> rules = new ArrayList<>();
        rules.add(SimpleMarkdownRules.createEscapeRule());
        rules.add(createLinkRule());
        rules.add(SimpleMarkdownRules.createNewlineRule());
        rules.add(createBoldRule());
        rules.add(createUnderlineRule());
        rules.add(createItalicsRule());
        rules.add(createStrikethruRule());
        return rules;
    }

    /**
     * Creates a set of rules for parsing Discord messages.
     *
     * @param includeText Should the text rule be included?
     * @return The rules for parsing Discord messages
     */
    public static <R> List<Rule<R, Node<R>, Object>> createAllRulesForDiscord(boolean includeText) {
        List<Rule<R, Node<R>, Object>> rules = new ArrayList<>();
        rules.addAll(createSimpleMarkdownRules());
        rules.addAll(createDiscordMarkdownRules());
        if (includeText) {
            rules.add(createSpecialTextRule());
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
