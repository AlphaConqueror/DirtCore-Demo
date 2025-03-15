/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.pagination;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.pagination.context.PaginationContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

/**
 * Pagination.
 *
 * @param <T> the content type
 */
public interface Pagination<T> {

    Supplier<Builder> DEFAULT_PAGINATOR =
            () -> Pagination.builder().width(45).renderer(new Pagination.Renderer() {
                @Override
                public @NonNull Component renderEmpty() {
                    return text("There are no entries present.", RED);
                }

                @Override
                public @NonNull Component renderUnknownPage(final int page, final int pages) {
                    return text(
                            "Unknown page selected (" + page + "). " + pages + " total pages" + ".",
                            RED);
                }
            }).resultsPerPage(3);

    /**
     * The default interface width.
     */
    int WIDTH = 55;
    /**
     * The default number of results per page.
     */
    int RESULTS_PER_PAGE = 6;
    /**
     * The default line character.
     */
    char LINE_CHARACTER = '-';
    /**
     * The default line style.
     */
    Style LINE_STYLE = Style.style(NamedTextColor.DARK_GRAY);
    /**
     * The default character for the previous page button.
     */
    char PREVIOUS_PAGE_BUTTON_CHARACTER = '\u00AB'; // «
    /**
     * The default style for the next page button.
     */
    Style PREVIOUS_PAGE_BUTTON_STYLE = Style.style(NamedTextColor.RED,
            HoverEvent.showText(Component.text("Previous Page", NamedTextColor.RED)));
    /**
     * The default character for the next page button.
     */
    char NEXT_PAGE_BUTTON_CHARACTER = '\u00BB'; // »
    /**
     * The default style for the next page button.
     */
    Style NEXT_PAGE_BUTTON_STYLE = Style.style(NamedTextColor.GREEN,
            HoverEvent.showText(Component.text("Next Page", NamedTextColor.GREEN)));
    /**
     * The default interface renderer.
     */
    Renderer DEFAULT_RENDERER = new Renderer() {
        @Override
        public String toString() {
            return "Pagination.DEFAULT_RENDERER";
        }
    };

    /**
     * The default add empty line flag.
     */
    boolean ADD_EMPTY_LINE = false;

    /**
     * Creates a pagination builder.
     *
     * @return a builder
     */
    static @NotNull Builder builder() {
        return new PaginationBuilder();
    }

    /**
     * Renders.
     *
     * @param content the content to render
     * @param page    the page number
     * @return the rendered results
     */
    @NotNull List<Component> render(@NonNull PaginationContext context,
            final @NotNull Collection<? extends T> content, int page);

    /**
     * Renders.
     *
     * @param content the content to render
     * @param insert  the components to insert after the header and before the main body
     * @param page    the page number
     * @return the rendered results
     */
    @NotNull List<Component> render(@NonNull PaginationContext context,
            @NonNull Collection<Component> insert, @NotNull Collection<? extends T> content,
            int page);

    /**
     * A pagination renderer.
     */
    interface Renderer {

        Component GRAY_LEFT_ROUND_BRACKET = Component.text("(", NamedTextColor.GRAY);
        Component GRAY_LEFT_SQUARE_BRACKET = Component.text("[", NamedTextColor.GRAY);
        Component GRAY_RIGHT_ROUND_BRACKET = Component.text(")", NamedTextColor.GRAY);
        Component GRAY_RIGHT_SQUARE_BRACKET = Component.text("]", NamedTextColor.GRAY);
        Component GRAY_FORWARD_SLASH = Component.text("/", NamedTextColor.GRAY);

        /**
         * Renders an empty result.
         *
         * <p>No header or footer are rendered.</p>
         *
         * @return the rendered component
         */
        default @NotNull Component renderEmpty() {
            return Component.text("No results match.", NamedTextColor.GRAY);
        }

        /**
         * Renders an unknown page.
         *
         * <p>No header or footer are rendered.</p>
         *
         * @param page  the unknown page
         * @param pages the total number of pages
         * @return the rendered component
         */
        default @NotNull Component renderUnknownPage(final int page, final int pages) {
            return Component.text("Unknown page selected. " + pages + " total pages.",
                    NamedTextColor.GRAY);
        }

        /**
         * Renders a header.
         *
         * @param title the title
         * @param page  the page
         * @param pages the total number of pages
         * @return the rendered component
         */
        default @NotNull Component renderHeader(final @NotNull Component title, final int page,
                final int pages) {
            return Component.text()
                    .append(Component.space())
                    .append(title)
                    .append(Component.space())
                    .append(GRAY_LEFT_ROUND_BRACKET)
                    .append(Component.text(page, NamedTextColor.WHITE))
                    .append(GRAY_FORWARD_SLASH)
                    .append(Component.text(pages, NamedTextColor.WHITE))
                    .append(GRAY_RIGHT_ROUND_BRACKET)
                    .append(Component.space()).build();
        }

        /**
         * Renders a previous page button.
         *
         * @param character  the button character
         * @param style      the button style
         * @param clickEvent the click event for the button
         * @return the rendered component
         */
        default @NotNull Component renderPreviousPageButton(final char character,
                final @NotNull Style style, final @NotNull ClickEvent clickEvent) {
            return Component.text()
                    .append(Component.space())
                    .append(GRAY_LEFT_SQUARE_BRACKET)
                    .append(Component.text(character, style.clickEvent(clickEvent)))
                    .append(GRAY_RIGHT_SQUARE_BRACKET)
                    .append(Component.space()).build();
        }

        /**
         * Renders a next page button.
         *
         * @param character  the button character
         * @param style      the button style
         * @param clickEvent the click event for the button
         * @return the rendered component
         */
        default @NotNull Component renderNextPageButton(final char character,
                final @NotNull Style style, final @NotNull ClickEvent clickEvent) {
            return Component.text()
                    .append(Component.space())
                    .append(GRAY_LEFT_SQUARE_BRACKET)
                    .append(Component.text(character, style.clickEvent(clickEvent)))
                    .append(GRAY_RIGHT_SQUARE_BRACKET)
                    .append(Component.space()).build();
        }

        /**
         * A row renderer.
         *
         * @param <T> the content type
         */
        @FunctionalInterface
        interface RowRenderer<T> {

            /**
             * Renders a row.
             *
             * @param value the value
             * @param index the index
             * @return the rendered row
             */
            @NotNull Collection<Component> renderRow(@NonNull final PaginationContext context,
                    final @Nullable T value, final int index);
        }
    }

    /**
     * A page command function.
     */
    @FunctionalInterface
    interface PageCommandFunction {

        /**
         * Gets the command to display the page.
         *
         * @param page the page
         * @return the command
         */
        @NotNull String pageCommand(final int page);
    }

    /**
     * A pagination builder.
     */
    interface Builder {

        /**
         * Sets the width.
         *
         * @param width the width
         * @return this builder
         */
        @NotNull Builder width(final int width);

        /**
         * Sets the number of results per page.
         *
         * @param resultsPerPage the number of results per page
         * @return this builder
         */
        @NotNull Builder resultsPerPage(
                final @Range(from = 0, to = Integer.MAX_VALUE) int resultsPerPage);

        /**
         * Sets the renderer.
         *
         * @param renderer the renderer
         * @return this builder
         */
        @NotNull Builder renderer(final @NotNull Renderer renderer);

        /**
         * Sets the line character and style.
         *
         * @param line the line consumer
         * @return this builder
         */
        @NotNull Builder line(final @NotNull Consumer<CharacterAndStyle> line);

        /**
         * Sets the previous button.
         *
         * @param previousButton the button consumer
         * @return this builder
         */
        @NotNull Builder previousButton(final @NotNull Consumer<CharacterAndStyle> previousButton);

        /**
         * Sets the next button.
         *
         * @param nextButton the button consumer
         * @return this builder
         */
        @NotNull Builder nextButton(final @NotNull Consumer<CharacterAndStyle> nextButton);

        /**
         * Sets add empty line flag
         *
         * @param flag the flag
         * @return this builder
         */
        @NotNull Builder addEmptyLine(final boolean flag);

        /**
         * Builds.
         *
         * @param title       the title
         * @param rowRenderer the row renderer
         * @param pageCommand the page command
         * @param <T>         the content type
         * @return pagination
         * @throws IllegalStateException if the title has not been set
         * @throws IllegalStateException if the row renderer has not been set
         */
        <T> @NotNull Pagination<T> build(final @NotNull Component title,
                final Renderer.@NotNull RowRenderer<T> rowRenderer,
                final @NotNull PageCommandFunction pageCommand);

        /**
         * A builder for a character and style pair.
         */
        interface CharacterAndStyle {

            /**
             * Sets the character.
             *
             * @param character the character
             * @return this builder
             */
            @NotNull CharacterAndStyle character(final char character);

            /**
             * Sets the style.
             *
             * @param style the style
             * @return this builder
             */
            @NotNull CharacterAndStyle style(final @NotNull Style style);
        }
    }
}
