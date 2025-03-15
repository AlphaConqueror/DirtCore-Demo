/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.ArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContextBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.ParsedArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.context.SuggestionContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNodeLike;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.LiteralCommandNode;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.RootCommandNode;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.option.OptionCommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The core command dispatcher, for registering, parsing, and executing commands.
 *
 * @param <S> a custom "source" type, such as a user or originator of a command
 */
public class CommandDispatcher<P extends DirtCorePlugin, S extends Sender> {

    /**
     * The string required to separate individual arguments in an input string
     *
     * @see #ARGUMENT_SEPARATOR_CHAR
     */
    public static final String ARGUMENT_SEPARATOR = " ";

    /**
     * The char required to separate individual arguments in an input string
     *
     * @see #ARGUMENT_SEPARATOR
     */
    public static final char ARGUMENT_SEPARATOR_CHAR = ' ';
    public static final String OPTION_PREFIX = "--";
    public static final String USAGE_OPTIONAL_OPEN = "[";
    public static final String USAGE_OPTIONAL_CLOSE = "]";
    public static final String USAGE_REQUIRED_OPEN = "<";
    public static final String USAGE_REQUIRED_CLOSE = ">";

    private static final String USAGE_LITERAL_OPEN = "";
    private static final String USAGE_LITERAL_CLOSE = "";
    private static final String USAGE_OR = "|";

    private final Comparator<ParseResults<P, S>> PARSE_RESULTS_COMPARATOR = (a, b) -> {
        if (!a.getReader().canRead() && b.getReader().canRead()) {
            return -1;
        }

        if (a.getReader().canRead() && !b.getReader().canRead()) {
            return 1;
        }

        if (a.getExceptionMap().isEmpty() && !b.getExceptionMap().isEmpty()) {
            return -1;
        }

        if (!a.getExceptionMap().isEmpty() && b.getExceptionMap().isEmpty()) {
            return 1;
        }

        return 0;
    };

    private final P plugin;
    private final RootCommandNode<P, S> root;
    private ResultConsumer<P, S> consumer = (c, s, r) -> {};

    /**
     * Create a new {@link CommandDispatcher} with the specified root node.
     *
     * <p>This is often useful to copy existing or pre-defined command trees.</p>
     *
     * @param plugin the plugin
     * @param root   the existing {@link RootCommandNode} to use as the basis for this tree
     */
    public CommandDispatcher(final P plugin, final RootCommandNode<P, S> root) {
        this.plugin = plugin;
        this.root = root;
    }

    /**
     * Creates a new {@link CommandDispatcher} with an empty command tree.
     */
    public CommandDispatcher(final P plugin) {
        this(plugin, new RootCommandNode<>());
    }

    /**
     * Utility method for registering new commands.
     *
     * <p>This is a shortcut for calling {@link RootCommandNode#addChild(CommandNode)} after
     * building the provided {@code command}.</p>
     *
     * <p>As {@link RootCommandNode} can only hold literals, this method will only allow literal
     * arguments.</p>
     *
     * @param command an argument builder to add to this command tree
     */
    public void register(final ArgumentBuilder<P, S, ? extends ArgumentBuilder<P, S, ?>> command) {
        command.onBuild(this.root);
    }

    /**
     * Sets a callback to be informed of the result of every command.
     *
     * @param consumer the new result consumer to be called
     */
    public void setConsumer(final ResultConsumer<P, S> consumer) {
        this.consumer = consumer;
    }

    /**
     * Parses and executes a given command.
     *
     * <p>This is a shortcut to first {@link #parse(StringReader, Sender)} and then
     * {@link #execute(ParseResults)}.</p>
     *
     * <p>It is recommended to parse and execute as separate steps, as parsing is often the most
     * expensive step, and easiest to cache.</p>
     *
     * <p>If this command returns a value, then it successfully executed something. If it could
     * not parse the command, or the execution was a failure,
     * then an exception will be thrown. Most exceptions will be of type
     * {@link CommandSyntaxException}, but it is possible that a {@link RuntimeException}
     * may bubble up from the result of a command. The meaning behind the returned result is
     * arbitrary, and will depend
     * entirely on what command was performed.</p>
     *
     * <p>If the command passes through a node that is {@link CommandNode#isFork()} then it will
     * be 'forked'.
     * A forked command will not bubble up any {@link CommandSyntaxException}s, and the 'result'
     * returned will turn into
     * 'amount of successful commands executes'.</p>
     *
     * <p>After each and any command is ran, a registered callback given to
     * {@link #setConsumer(ResultConsumer)}
     * will be notified of the result and success of the command. You can use that method to
     * gather more meaningful
     * results than this method will return, especially when a command forks.</p>
     *
     * @param input  a command string to parse &amp; execute
     * @param source a custom "source" object, usually representing the originator of this command
     * @return a numeric result from a "command" that was performed
     * @throws CommandSyntaxException if the command failed to parse or execute
     * @throws RuntimeException       if the command failed to execute and was not handled
     *                                gracefully
     * @see #parse(String, Sender)
     * @see #parse(StringReader, Sender)
     * @see #execute(ParseResults)
     * @see #execute(StringReader, Sender)
     */
    public int execute(final String input, final S source) throws CommandSyntaxException {
        return this.execute(new StringReader(input), source);
    }

    /**
     * Parses and executes a given command.
     *
     * <p>This is a shortcut to first {@link #parse(StringReader, Sender)} and then
     * {@link #execute(ParseResults)}.</p>
     *
     * <p>It is recommended to parse and execute as separate steps, as parsing is often the most
     * expensive step, and easiest to cache.</p>
     *
     * <p>If this command returns a value, then it successfully executed something. If it could
     * not parse the command, or the execution was a failure,
     * then an exception will be thrown. Most exceptions will be of type
     * {@link CommandSyntaxException}, but it is possible that a {@link RuntimeException}
     * may bubble up from the result of a command. The meaning behind the returned result is
     * arbitrary, and will depend
     * entirely on what command was performed.</p>
     *
     * <p>If the command passes through a node that is {@link CommandNode#isFork()} then it will
     * be 'forked'.
     * A forked command will not bubble up any {@link CommandSyntaxException}s, and the 'result'
     * returned will turn into
     * 'amount of successful commands executes'.</p>
     *
     * <p>After each and any command is ran, a registered callback given to
     * {@link #setConsumer(ResultConsumer)}
     * will be notified of the result and success of the command. You can use that method to
     * gather more meaningful
     * results than this method will return, especially when a command forks.</p>
     *
     * @param input  a command string to parse &amp; execute
     * @param source a custom "source" object, usually representing the originator of this command
     * @return a numeric result from a "command" that was performed
     * @throws CommandSyntaxException if the command failed to parse or execute
     * @throws RuntimeException       if the command failed to execute and was not handled
     *                                gracefully
     * @see #parse(String, Sender)
     * @see #parse(StringReader, Sender)
     * @see #execute(ParseResults)
     * @see #execute(String, Sender)
     */
    public int execute(final StringReader input, final S source) throws CommandSyntaxException {
        final ParseResults<P, S> parse = this.parse(input, source);
        return this.execute(parse);
    }

    /**
     * Executes a given pre-parsed command.
     *
     * <p>If this command returns a value, then it successfully executed something. If the
     * execution was a failure,
     * then an exception will be thrown.
     * Most exceptions will be of type {@link CommandSyntaxException}, but it is possible that a
     * {@link RuntimeException}
     * may bubble up from the result of a command. The meaning behind the returned result is
     * arbitrary, and will depend
     * entirely on what command was performed.</p>
     *
     * <p>If the command passes through a node that is {@link CommandNode#isFork()} then it will
     * be 'forked'.
     * A forked command will not bubble up any {@link CommandSyntaxException}s, and the 'result'
     * returned will turn into
     * 'amount of successful commands executes'.</p>
     *
     * <p>After each and any command is ran, a registered callback given to
     * {@link #setConsumer(ResultConsumer)}
     * will be notified of the result and success of the command. You can use that method to
     * gather more meaningful
     * results than this method will return, especially when a command forks.</p>
     *
     * @param parse the result of a successful {@link #parse(StringReader, Sender)}
     * @return a numeric result from a "command" that was performed.
     * @throws CommandSyntaxException if the command failed to parse or execute
     * @throws RuntimeException       if the command failed to execute and was not handled
     *                                gracefully
     * @see #parse(String, Sender)
     * @see #parse(StringReader, Sender)
     * @see #execute(String, Sender)
     * @see #execute(StringReader, Sender)
     */
    public int execute(final ParseResults<P, S> parse) throws CommandSyntaxException {
        if (parse.getReader().canRead()) {
            if (parse.getExceptionMap().size() == 1) {
                throw parse.getExceptions().iterator().next();
            }

            if (parse.getContext().getRange().isEmpty()) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()
                        .createWithContext(parse.getReader());
            }

            final ImmutableStringReader reader = parse.getReader();

            if (reader.canRead(2) && reader.getRemaining().startsWith(OPTION_PREFIX)) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownOption()
                        .createWithContext(reader);
            }

            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()
                    .createWithContext(reader);
        }

        int result = 0;
        int successfulForks = 0;
        boolean forked = false;
        boolean foundCommand = false;
        final String command = parse.getReader().getString();
        final CommandContext<P, S> original = parse.getContext().build(command);
        List<CommandContext<P, S>> contexts = Collections.singletonList(original);
        ArrayList<CommandContext<P, S>> next = null;

        while (contexts != null) {
            for (final CommandContext<P, S> context : contexts) {
                final CommandContext<P, S> child = context.getChild();

                if (child != null) {
                    forked |= context.isForked();
                    if (child.hasNodes()) {
                        foundCommand = true;
                        final RedirectModifier<P, S> modifier = context.getRedirectModifier();
                        if (modifier == null) {
                            if (next == null) {
                                next = new ArrayList<>(1);
                            }
                            next.add(child.copyFor(context.getSource()));
                        } else {
                            try {
                                final Collection<S> results = modifier.apply(context);
                                if (!results.isEmpty()) {
                                    if (next == null) {
                                        next = new ArrayList<>(results.size());
                                    }
                                    for (final S source : results) {
                                        next.add(child.copyFor(source));
                                    }
                                }
                            } catch (final CommandSyntaxException ex) {
                                this.consumer.onCommandComplete(context, false, 0);
                                if (!forked) {
                                    throw ex;
                                }
                            }
                        }
                    }
                } else if (context.getCommand() != null) {
                    if (context.getSource().isConsole() && !context.canConsoleUse()) {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherConsoleUsageException()
                                .createWithContext(parse.getReader());
                    }

                    foundCommand = true;

                    try {
                        final int value = context.getCommand().run(context);
                        result += value;
                        this.consumer.onCommandComplete(context, true, value);
                        successfulForks++;
                    } catch (final CommandSyntaxException ex) {
                        this.consumer.onCommandComplete(context, false, 0);
                        if (!forked) {
                            throw ex;
                        }
                    }
                }
            }

            contexts = next;
            next = null;
        }

        if (!foundCommand) {
            this.consumer.onCommandComplete(original, false, 0);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()
                    .createWithContext(parse.getReader());
        }

        return forked ? successfulForks : result;
    }

    /**
     * Parses a given command.
     *
     * <p>The result of this method can be cached, and it is advised to do so where appropriate.
     * Parsing is often the
     * most expensive step, and this allows you to essentially "precompile" a command if it will
     * be ran often.</p>
     *
     * <p>If the command passes through a node that is {@link CommandNode#isFork()} then the
     * resulting context will be marked as 'forked'.
     * Forked contexts may contain child contexts, which may be modified by the
     * {@link RedirectModifier} attached to the fork.</p>
     *
     * <p>Parsing a command can never fail, you will always be provided with a new
     * {@link ParseResults}.
     * However, that does not mean that it will always parse into a valid command. You should
     * inspect the returned results
     * to check for validity. If its {@link ParseResults#getReader()}
     * {@link StringReader#canRead()} then it did not finish
     * parsing successfully. You can use that position as an indicator to the user where the
     * command stopped being valid.
     * You may inspect {@link ParseResults#getExceptionMap()} if you know the parse failed, as it
     * will explain why it could
     * not find any valid commands. It may contain multiple exceptions, one for each "potential
     * node" that it could have visited,
     * explaining why it did not go down that node.</p>
     *
     * <p>When you eventually call {@link #execute(ParseResults)} with the result of this method,
     * the above error checking
     * will occur. You only need to inspect it yourself if you wish to handle that yourself.</p>
     *
     * @param command a command string to parse
     * @param source  a custom "source" object, usually representing the originator of this command
     * @return the result of parsing this command
     * @see #parse(StringReader, Sender)
     * @see #execute(ParseResults)
     * @see #execute(String, Sender)
     */
    public ParseResults<P, S> parse(final String command, final S source) {
        return this.parse(new StringReader(command), source);
    }

    /**
     * Parses a given command.
     *
     * <p>The result of this method can be cached, and it is advised to do so where appropriate.
     * Parsing is often the
     * most expensive step, and this allows you to essentially "precompile" a command if it will
     * be ran often.</p>
     *
     * <p>If the command passes through a node that is {@link CommandNode#isFork()} then the
     * resulting context will be marked as 'forked'.
     * Forked contexts may contain child contexts, which may be modified by the
     * {@link RedirectModifier} attached to the fork.</p>
     *
     * <p>Parsing a command can never fail, you will always be provided with a new
     * {@link ParseResults}.
     * However, that does not mean that it will always parse into a valid command. You should
     * inspect the returned results
     * to check for validity. If its {@link ParseResults#getReader()}
     * {@link StringReader#canRead()} then it did not finish
     * parsing successfully. You can use that position as an indicator to the user where the
     * command stopped being valid.
     * You may inspect {@link ParseResults#getExceptionMap()} if you know the parse failed, as it
     * will explain why it could
     * not find any valid commands. It may contain multiple exceptions, one for each "potential
     * node" that it could have visited,
     * explaining why it did not go down that node.</p>
     *
     * <p>When you eventually call {@link #execute(ParseResults)} with the result of this method,
     * the above error checking
     * will occur. You only need to inspect it yourself if you wish to handle that yourself.</p>
     *
     * @param command a command string to parse
     * @param source  a custom "source" object, usually representing the originator of this command
     * @return the result of parsing this command
     * @see #parse(String, Sender)
     * @see #execute(ParseResults)
     * @see #execute(String, Sender)
     */
    public ParseResults<P, S> parse(final StringReader command, final S source) {
        final CommandContextBuilder<P, S> context =
                new CommandContextBuilder<>(this.plugin, this, source, this.root,
                        command.getCursor());
        return this.parseNodes(this.root, command, context);
    }

    /**
     * Gets all possible executable commands following the given node.
     *
     * <p>You may use {@link #getRoot()} as a target to get all usage data for the entire command
     * tree.</p>
     *
     * <p>The returned syntax will be in "simple" form: {@code <param>} and {@code literal}.
     * "Optional" nodes will be
     * listed as multiple entries: the parent node, and the child nodes.
     * For example, a required literal "foo" followed by an optional param "int" will be two
     * nodes:</p>
     * <ul>
     *     <li>{@code foo}</li>
     *     <li>{@code foo <int>}</li>
     * </ul>
     *
     * <p>The path to the specified node will <b>not</b> be prepended to the output, as there can
     * theoretically be many
     * ways to reach a given node. It will only give you paths relative to the specified node,
     * not absolute from root.</p>
     *
     * @param node       target node to get child usage strings for
     * @param source     a custom "source" object, usually representing the originator of this
     *                   command
     * @param restricted if true, commands that the {@code source} cannot access will not be
     *                   mentioned
     * @return list of full usage strings under the target node
     */
    public List<String> getAllUsage(final CommandNode<P, S> node, final S source,
            final boolean restricted) {
        final ArrayList<String> result = new ArrayList<>();
        this.getAllUsage(node, source, result, "", restricted);
        return result;
    }

    public List<String> getAllUsage(final S source, final boolean restricted) {
        return this.getAllUsage(this.getRoot(), source, restricted);
    }

    public List<String> getSmartUsage(final CommandNode<P, S> node, final S source) {
        if (!node.canUse(source)) {
            return Collections.emptyList();
        }

        final boolean optional = node.getCommand() != null;
        final List<String> usages = new ArrayList<>();

        if (optional) {
            final Collection<OptionCommandNode<P, S>> options =
                    node.getOptions().stream().filter(c -> c.canUse(source))
                            .collect(Collectors.toList());

            for (final OptionCommandNode<P, S> option : options) {
                usages.add(option.getUsageText());
            }
        }

        node.getChildren().stream().sorted(CommandNode::compareTo)
                .map(child -> this.getSmartUsage(child, source, optional, false))
                .filter(set -> !set.isEmpty()).forEach(usages::addAll);

        return usages.isEmpty() && node.getRedirect() != null ? this.getSmartUsage(
                node.getRedirect(), source) : usages;
    }

    public List<String> getFullSmartUsage(final CommandNode<P, S> node, final S source) {
        final List<String> list = new ArrayList<>();
        this.getFullSmartUsage(node, source, "", list);
        return list;
    }

    public List<String> getFullSmartUsage(final S source) {
        final List<String> list = new ArrayList<>();
        this.getFullSmartUsage(this.getRoot(), source, "", list);
        return list;
    }

    /**
     * Gets suggestions for a parsed input string on what comes next.
     *
     * <p>As it is ultimately up to custom argument types to provide suggestions, it may be an
     * asynchronous operation,
     * for example getting in-game data or player names etc. As such, this method returns a
     * future and no guarantees
     * are made to when or how the future completes.</p>
     *
     * <p>The suggestions provided will be in the context of the end of the parsed input string,
     * but may suggest
     * new or replacement strings for earlier in the input string. For example, if the end of the
     * string was
     * {@code foobar} but an argument preferred it to be {@code minecraft:foobar}, it will
     * suggest a replacement for that
     * whole segment of the input.</p>
     *
     * @param parse the result of a {@link #parse(StringReader, Sender)}
     * @return a future that will eventually resolve into a {@link Suggestions} object
     */
    public CompletableFuture<Suggestions> getCompletionSuggestions(final ParseResults<P, S> parse) {
        return this.getCompletionSuggestions(parse, parse.getReader().getTotalLength());
    }

    // TODO: Fix option suggestions
    public CompletableFuture<Suggestions> getCompletionSuggestions(final ParseResults<P, S> parse,
            final int cursor) {
        final CommandContextBuilder<P, S> context = parse.getContext();
        final SuggestionContext<P, S> nodeBeforeCursor = context.findSuggestionContext(cursor);
        final CommandNode<P, S> parent = nodeBeforeCursor.parent;
        final Set<String> contextOptions = context.getOptions();
        final Map<String, ParsedArgument<?>> contextArguments = context.getArguments();
        int start = Math.min(nodeBeforeCursor.startPos, cursor);

        // fix start when parsing options
        for (final String optionName : contextOptions) {
            final ParsedArgument<?> parsedOption = contextArguments.get(optionName);

            if (parsedOption == null) {
                continue;
            }

            final int end = parsedOption.getRange().getEnd() + 1;

            if (end > start) {
                start = end;
            }
        }

        final String fullInput = parse.getReader().getString();
        final String truncatedInput = fullInput.substring(0, cursor);
        final String truncatedInputLowerCase = truncatedInput.toLowerCase(Locale.ROOT);
        final List<CompletableFuture<Suggestions>> futures = new ArrayList<>();

        if (contextOptions.isEmpty()) {
            // only suggest child nodes when there are no options
            for (final CommandNode<P, S> node : parent.getChildren()) {
                CompletableFuture<Suggestions> future = Suggestions.empty();

                if (this.showCompletion(node, context)) {
                    try {
                        future = node.listSuggestions(this.plugin, context.build(truncatedInput),
                                new SuggestionsBuilder(truncatedInput, truncatedInputLowerCase,
                                        start));
                    } catch (final CommandSyntaxException ignored) {}
                }

                futures.add(future);
            }
        }

        if (parent.canUse(context.getSource()) && parent.getCommand() != null) {
            // remove options that we have seen already
            final Set<OptionCommandNode<P, S>> options = parent.getOptions().stream()
                    .filter(option -> !contextOptions.contains(option.getName()))
                    .collect(Collectors.toSet());


            for (final OptionCommandNode<P, S> option : options) {
                CompletableFuture<Suggestions> future = Suggestions.empty();

                if (this.showCompletion(option, context)) {
                    try {
                        future = option.listSuggestions(this.plugin, context.build(truncatedInput),
                                new SuggestionsBuilder(truncatedInput, truncatedInputLowerCase,
                                        start));
                    } catch (final CommandSyntaxException ignored) {}
                }

                futures.add(future);
            }
        }

        final CompletableFuture<Suggestions> result = new CompletableFuture<>();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            final List<Suggestions> suggestions = new ArrayList<>();

            for (final CompletableFuture<Suggestions> future : futures) {
                suggestions.add(future.join());
            }

            result.complete(Suggestions.merge(fullInput, suggestions));
        });

        return result;
    }

    /**
     * Gets the root of this command tree.
     *
     * <p>This is often useful as a target of a {@link ArgumentBuilder#redirect(CommandNode)},
     * {@link #getAllUsage(CommandNode, Sender, boolean)} or
     * {@link #getSmartUsage(CommandNode, Sender)}.
     * You may also use it to clone the command tree via
     * {@link #CommandDispatcher(DirtCorePlugin, RootCommandNode)}.</p>
     *
     * @return root of the command tree
     */
    public RootCommandNode<P, S> getRoot() {
        return this.root;
    }

    /**
     * Finds a valid path to a given node on the command tree.
     *
     * <p>There may theoretically be multiple paths to a node on the tree, especially with the
     * use of forking or redirecting.
     * As such, this method makes no guarantees about which path it finds. It will not look at
     * forks or redirects,
     * and find the first instance of the target node on the tree.</p>
     *
     * <p>The only guarantee made is that for the same command tree and the same version of this
     * library, the result of
     * this method will <b>always</b> be a valid input for {@link #findNode(Collection)}, which
     * should return the same node
     * as provided to this method.</p>
     *
     * @param target the target node you are finding a path for
     * @return a path to the resulting node, or an empty list if it was not found
     */
    public Collection<String> getPath(final CommandNode<P, S> target) {
        final List<List<CommandNode<P, S>>> nodes = new ArrayList<>();
        this.addPaths(this.root, nodes, new ArrayList<>());

        for (final List<CommandNode<P, S>> list : nodes) {
            if (list.get(list.size() - 1) == target) {
                final List<String> result = new ArrayList<>(list.size());
                for (final CommandNode<P, S> node : list) {
                    if (node != this.root) {
                        result.add(node.getName());
                    }
                }
                return result;
            }
        }

        return Collections.emptyList();
    }

    /**
     * Finds a node by its path
     *
     * <p>Paths may be generated with {@link #getPath(CommandNode)}, and are guaranteed (for the
     * same tree, and the
     * same version of this library) to always produce the same valid node by this method.</p>
     *
     * <p>If a node could not be found at the specified path, then {@code null} will be returned
     * .</p>
     *
     * @param path a generated path to a node
     * @return the node at the given path, or null if not found
     */
    public CommandNode<P, S> findNode(final Collection<String> path) {
        CommandNode<P, S> node = this.root;

        for (final String name : path) {
            node = node.getChild(name);

            if (node == null) {
                return null;
            }
        }

        return node;
    }

    /**
     * Scans the command tree for potential ambiguous commands.
     *
     * <p>This is a shortcut for
     * {@link CommandNode#findAmbiguities(DirtCorePlugin, AmbiguityConsumer)} on
     * {@link #getRoot()}.</p>
     *
     * <p>Ambiguities are detected by testing every {@link CommandNode#getExamples()} on one node
     * verses every sibling
     * node. This is not fool proof, and relies a lot on the providers of the used argument types
     * to give good examples.</p>
     *
     * @param consumer a callback to be notified of potential ambiguities
     */
    public void findAmbiguities(final AmbiguityConsumer<P, S> consumer) {
        this.root.findAmbiguities(this.plugin, consumer);
    }

    private void getFullSmartUsage(final CommandNode<P, S> node, final S source, String prefix,
            final List<String> result) {
        if (!node.canUse(source)) {
            return;
        }

        final boolean optional = node.getCommand() != null;
        final List<String> usages = this.getSmartUsage(node, source, optional, false);

        if (!usages.isEmpty()) {
            for (String s : usages) {
                if (!prefix.isEmpty()) {
                    s = prefix + ARGUMENT_SEPARATOR + s;
                }

                result.add(s);
            }
        }

        if (optional) {
            final Collection<OptionCommandNode<P, S>> options =
                    node.getOptions().stream().filter(c -> c.canUse(source))
                            .collect(Collectors.toList());

            for (final OptionCommandNode<P, S> option : options) {
                result.add(prefix.isEmpty() ? option.getUsageText()
                        : prefix + ARGUMENT_SEPARATOR + option.getUsageText());
            }
        }

        prefix = prefix.isEmpty() ? node.getUsageText()
                : prefix + ARGUMENT_SEPARATOR + node.getUsageText();

        if (node.getRedirect() != null) {
            final String redirect = node.getRedirect() == this.root ? "..."
                    : "-> " + node.getRedirect().getUsageText();
            result.add(prefix.isEmpty() ? node.getUsageText() + ARGUMENT_SEPARATOR + redirect
                    : prefix + ARGUMENT_SEPARATOR + redirect);
        } else if (!node.getChildren().isEmpty()) {
            for (final CommandNode<P, S> child : node.getChildren()) {
                this.getFullSmartUsage(child, source, prefix, result);
            }
        }
    }

    @NonNull
    private List<String> getSmartUsage(final CommandNode<P, S> node, final S source,
            final boolean optional, final boolean deep) {
        if (!node.canUse(source)) {
            return Collections.emptyList();
        }

        final String self = node instanceof LiteralCommandNode ? USAGE_LITERAL_OPEN + node.getName()
                + USAGE_LITERAL_CLOSE
                : (optional ? USAGE_OPTIONAL_OPEN + node.getName() + USAGE_OPTIONAL_CLOSE
                        : USAGE_REQUIRED_OPEN + node.getName() + USAGE_REQUIRED_CLOSE);
        final boolean childOptional = node.getCommand() != null;
        final String open = childOptional ? USAGE_OPTIONAL_OPEN : USAGE_REQUIRED_OPEN;
        final String close = childOptional ? USAGE_OPTIONAL_CLOSE : USAGE_REQUIRED_CLOSE;

        if (!deep) {
            if (node.getRedirect() != null) {
                final String redirect = node.getRedirect() == this.getRoot() ? "..."
                        : "-> " + node.getRedirect().getName();
                return Collections.singletonList(self + ARGUMENT_SEPARATOR + redirect);
            }

            final List<String> usageList = new ArrayList<>();
            final Collection<CommandNode<P, S>> children =
                    node.getChildren().stream().filter(c -> c.canUse(source))
                            .collect(Collectors.toList());

            if (children.size() == 1) {
                final List<String> usage =
                        this.getSmartUsage(children.iterator().next(), source, childOptional,
                                false);
                usage.stream().map(s -> self + ARGUMENT_SEPARATOR + s).forEach(usageList::add);
            } else if (children.size() > 1) {
                final Map<CommandNode<P, S>, List<String>> usageMap = new HashMap<>();

                for (final CommandNode<P, S> child : children) {
                    final List<String> usage =
                            this.getSmartUsage(child, source, childOptional, false);

                    if (!usage.isEmpty()) {
                        usageMap.put(child, usage);
                    }
                }

                if (usageMap.size() == 1) {
                    final List<String> usage = usageMap.values().iterator().next();

                    if (childOptional) {
                        usage.stream().map(s -> self + ARGUMENT_SEPARATOR + USAGE_OPTIONAL_OPEN + s
                                + USAGE_OPTIONAL_CLOSE).forEach(usageList::add);
                    } else {
                        usage.stream().map(s -> self + ARGUMENT_SEPARATOR + USAGE_REQUIRED_OPEN + s
                                + USAGE_REQUIRED_CLOSE).forEach(usageList::add);
                    }
                } else if (usageMap.size() > 1) {
                    final StringBuilder builder = new StringBuilder(open);
                    int count = 0;

                    for (final CommandNode<P, S> child : usageMap.keySet().stream().sorted()
                            .collect(Collectors.toList())) {
                        if (count > 0) {
                            builder.append(USAGE_OR);
                        }

                        builder.append(child.getName());
                        count++;
                    }

                    if (count > 0) {
                        builder.append(close);

                        final String forwardUsage = this.getForwardUsage(usageMap.values());

                        if (forwardUsage != null) {
                            builder.append(' ')
                                    .append(forwardUsage);
                        }

                        usageList.add(self + ARGUMENT_SEPARATOR + builder);
                    }
                }
            }

            if (node.getCommand() != null) {
                final Collection<OptionCommandNode<P, S>> options =
                        node.getOptions().stream().filter(c -> c.canUse(source))
                                .collect(Collectors.toList());

                for (final OptionCommandNode<P, S> option : options) {
                    usageList.add(self + ARGUMENT_SEPARATOR + option.getUsageText());
                }
            }

            if (!(children.isEmpty() && usageList.isEmpty())) {
                return usageList;
            }
        }

        if (node.getCommand() != null) {
            return Collections.singletonList(self);
        }

        return Collections.emptyList();
    }

    /**
     * Returns if the completion for the node should be shown.
     *
     * @param node    the command node
     * @param context the command context
     * @return if the completion for the node should be shown
     */
    private boolean showCompletion(final CommandNode<P, S> node,
            final CommandContextBuilder<P, S> context) {
        return node.canUse(context.getSource()) && (node.getCommand() != null || node.getChildren()
                .stream().anyMatch(child -> this.showCompletion(child, context)));
    }

    /**
     * Returns if the completion for the node should be shown.
     *
     * @param option  the option command node
     * @param context the command context
     * @return if the completion for the node should be shown
     */
    private boolean showCompletion(final OptionCommandNode<P, S> option,
            final CommandContextBuilder<P, S> context) {
        return option.canUse(context.getSource());
    }

    private ParseResults<P, S> parseNodes(final CommandNode<P, S> node,
            final StringReader originalReader, final CommandContextBuilder<P, S> contextSoFar) {
        final S source = contextSoFar.getSource();
        Map<CommandNodeLike<S>, CommandSyntaxException> errors = null;
        List<ParseResults<P, S>> potentials = null;
        final int cursor = originalReader.getCursor();

        if (originalReader.canRead(2) && originalReader.getRemaining().startsWith(OPTION_PREFIX)) {
            if (node.getCommand() != null) {
                // only parse for options when there is a command present
                final ParseResults<P, S> parseOptions =
                        this.parseOptions(source, node.getOptions(), originalReader, contextSoFar);
                potentials = new ArrayList<>(1);
                potentials.add(parseOptions);
            }
        } else {
            for (final CommandNode<P, S> child : node.getRelevantNodes(originalReader)) {
                if (!child.canUse(source)) {
                    continue;
                }

                final CommandContextBuilder<P, S> context = contextSoFar.copy();
                final StringReader reader = new StringReader(originalReader);

                try {
                    try {
                        child.parse(this.plugin, reader, context);
                    } catch (final RuntimeException ex) {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException()
                                .createWithContext(reader, ex.getMessage());
                    }

                    if (reader.canRead() && reader.peek() != ARGUMENT_SEPARATOR_CHAR) {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherExpectedArgumentSeparator()
                                .createWithContext(reader);
                    }
                } catch (final CommandSyntaxException ex) {
                    if (errors == null) {
                        errors = new LinkedHashMap<>();
                    }

                    errors.put(child, ex);
                    reader.setCursor(cursor);
                    continue;
                }

                context.withCommand(child.getCommand());

                if (reader.canRead(child.getRedirect() == null ? 2 : 1)) {
                    reader.skip();

                    if (child.getRedirect() != null) {
                        final CommandContextBuilder<P, S> childContext =
                                new CommandContextBuilder<>(this.plugin, this, source,
                                        child.getRedirect(), reader.getCursor());
                        final ParseResults<P, S> parse =
                                this.parseNodes(child.getRedirect(), reader, childContext);

                        context.withChild(parse.getContext());
                        return new ParseResults<>(context, parse.getReader(),
                                parse.getExceptionMap());
                    }

                    if (potentials == null) {
                        potentials = new ArrayList<>(1);
                    }

                    potentials.add(this.parseNodes(child, reader, context));
                } else {
                    if (potentials == null) {
                        potentials = new ArrayList<>(1);
                    }

                    potentials.add(new ParseResults<>(context, reader, Collections.emptyMap()));
                }
            }
        }

        if (potentials != null) {
            if (potentials.size() > 1) {
                potentials.sort(this.PARSE_RESULTS_COMPARATOR);
            }

            return potentials.get(0);
        }

        return new ParseResults<>(contextSoFar, originalReader,
                errors == null ? Collections.emptyMap() : errors);
    }

    private ParseResults<P, S> parseOptions(final S source,
            final Collection<? extends OptionCommandNode<P, S>> options,
            final StringReader originalReader, final CommandContextBuilder<P, S> contextSoFar) {
        Map<CommandNodeLike<S>, CommandSyntaxException> errors = null;
        List<ParseResults<P, S>> potentials = null;
        final int cursor = originalReader.getCursor();

        for (final OptionCommandNode<P, S> option : options) {
            final String name = OPTION_PREFIX + option.getName();

            // skip if we can't use or aren't relevant
            if (!(option.canUse(source) && originalReader.canRead(name.length())
                    && originalReader.getRemaining().toLowerCase(Locale.ROOT)
                    .startsWith(name.toLowerCase()))) {
                continue;
            }

            final CommandContextBuilder<P, S> context = contextSoFar.copy();
            final StringReader reader = new StringReader(originalReader);

            try {
                try {
                    option.parse(this.plugin, reader, context);
                } catch (final RuntimeException ex) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException()
                            .createWithContext(reader, ex.getMessage());
                }

                if (reader.canRead() && reader.peek() != ARGUMENT_SEPARATOR_CHAR) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherExpectedArgumentSeparator()
                            .createWithContext(reader);
                }
            } catch (final CommandSyntaxException ex) {
                if (errors == null) {
                    errors = new LinkedHashMap<>();
                }

                errors.put(option, ex);
                reader.setCursor(cursor);
                continue;
            }

            if (potentials == null) {
                potentials = new ArrayList<>(1);
            }

            if (reader.canRead(2)) {
                reader.skip();

                final Set<OptionCommandNode<P, S>> newOptions = new HashSet<>(options);
                // remove current option, we have already seen it
                newOptions.remove(option);

                final ParseResults<P, S> parse =
                        this.parseOptions(source, newOptions, reader, context);
                potentials.add(parse);
            } else {
                potentials.add(new ParseResults<>(context, reader, Collections.emptyMap()));
            }
        }

        if (potentials != null) {
            if (potentials.size() > 1) {
                potentials.sort(this.PARSE_RESULTS_COMPARATOR);
            }

            return potentials.get(0);
        }

        return new ParseResults<>(contextSoFar, originalReader,
                errors == null ? Collections.emptyMap() : errors);
    }

    private void getAllUsage(final CommandNode<P, S> node, final S source,
            final ArrayList<String> result, final String prefix, final boolean restricted) {
        if (restricted && !node.canUse(source)) {
            return;
        }

        if (node.getCommand() != null) {
            result.add(prefix);
        }

        if (node.getRedirect() != null) {
            final String redirect = node.getRedirect() == this.root ? "..."
                    : "-> " + node.getRedirect().getUsageText();
            result.add(prefix.isEmpty() ? node.getUsageText() + ARGUMENT_SEPARATOR + redirect
                    : prefix + ARGUMENT_SEPARATOR + redirect);
        } else if (!node.getChildren().isEmpty()) {
            for (final CommandNode<P, S> child : node.getChildren()) {
                this.getAllUsage(child, source, result, prefix.isEmpty() ? child.getUsageText()
                        : prefix + ARGUMENT_SEPARATOR + child.getUsageText(), restricted);
            }
        }
    }

    @Nullable
    private String getForwardUsage(@NonNull final Collection<List<String>> usages) {
        // all usages have to be of size 1
        if (!usages.stream().allMatch(set -> set.size() == 1)) {
            return null;
        }

        final List<String> usageList =
                usages.stream().map(list -> list.get(0)).collect(Collectors.toList());
        String forwardUsage = null;

        for (final String s : usageList) {
            // must be "node1 node2"
            if (s.length() < 3) {
                return null;
            }

            final int index = s.indexOf(' ');

            // must be separated by a space
            if (index < 0) {
                return null;
            }

            final String split = s.substring(index + 1);

            if (forwardUsage == null) {
                forwardUsage = split;
            } else if (!forwardUsage.equals(split)) {
                // all children do not have the same usage
                return null;
            }
        }

        return forwardUsage;
    }

    private void addPaths(final CommandNode<P, S> node, final List<List<CommandNode<P, S>>> result,
            final List<CommandNode<P, S>> parents) {
        final List<CommandNode<P, S>> current = new ArrayList<>(parents);
        current.add(node);
        result.add(current);

        for (final CommandNode<P, S> child : node.getChildren()) {
            this.addPaths(child, result, current);
        }
    }

    private final Predicate<CommandNode<P, S>> hasCommand =
            input -> input != null && (input.getCommand() != null || input.getChildren().stream()
                    .anyMatch(CommandDispatcher.this.hasCommand));
}
