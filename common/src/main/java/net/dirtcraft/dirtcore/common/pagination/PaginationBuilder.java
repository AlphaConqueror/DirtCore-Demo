/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.pagination;

import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public final class PaginationBuilder implements Pagination.Builder {

    private int width = Pagination.WIDTH;
    private int resultsPerPage = Pagination.RESULTS_PER_PAGE;

    private char lineCharacter = Pagination.LINE_CHARACTER;
    private Style lineStyle = Pagination.LINE_STYLE;

    private Pagination.Renderer renderer = Pagination.DEFAULT_RENDERER;

    private char previousPageButtonCharacter = Pagination.PREVIOUS_PAGE_BUTTON_CHARACTER;
    private Style previousPageButtonStyle = Pagination.PREVIOUS_PAGE_BUTTON_STYLE;
    private char nextPageButtonCharacter = Pagination.NEXT_PAGE_BUTTON_CHARACTER;
    private Style nextPageButtonStyle = Pagination.NEXT_PAGE_BUTTON_STYLE;
    private boolean addEmptyLine = Pagination.ADD_EMPTY_LINE;

    @Override
    public Pagination.@NotNull Builder width(final int width) {
        this.width = width;
        return this;
    }

    @Override
    public Pagination.@NotNull Builder resultsPerPage(
            final @Range(from = 0, to = Integer.MAX_VALUE) int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
        return this;
    }

    @Override
    public Pagination.@NotNull Builder renderer(final Pagination.@NotNull Renderer renderer) {
        this.renderer = renderer;
        return this;
    }

    @Override
    public Pagination.@NotNull Builder line(final @NotNull Consumer<CharacterAndStyle> line) {
        line.accept(new CharacterAndStyle() {
            @Override
            public @NotNull CharacterAndStyle character(final char character) {
                PaginationBuilder.this.lineCharacter = character;
                return this;
            }

            @Override
            public @NotNull CharacterAndStyle style(final @NotNull Style style) {
                PaginationBuilder.this.lineStyle = style;
                return this;
            }
        });
        return this;
    }

    @Override
    public Pagination.@NotNull Builder previousButton(
            final @NotNull Consumer<CharacterAndStyle> previousButton) {
        previousButton.accept(new CharacterAndStyle() {
            @Override
            public @NotNull CharacterAndStyle character(final char character) {
                PaginationBuilder.this.previousPageButtonCharacter = character;
                return this;
            }

            @Override
            public @NotNull CharacterAndStyle style(final @NotNull Style style) {
                PaginationBuilder.this.previousPageButtonStyle = style;
                return this;
            }
        });
        return this;
    }

    @Override
    public Pagination.@NotNull Builder nextButton(
            final @NotNull Consumer<CharacterAndStyle> nextButton) {
        nextButton.accept(new CharacterAndStyle() {
            @Override
            public @NotNull CharacterAndStyle character(final char character) {
                PaginationBuilder.this.nextPageButtonCharacter = character;
                return this;
            }

            @Override
            public @NotNull CharacterAndStyle style(final @NotNull Style style) {
                PaginationBuilder.this.nextPageButtonStyle = style;
                return this;
            }
        });
        return this;
    }

    @Override
    public Pagination.@NotNull Builder addEmptyLine(final boolean flag) {
        this.addEmptyLine = flag;
        return this;
    }

    @Override
    public <T> @NotNull Pagination<T> build(final @NotNull Component title,
            final Pagination.Renderer.@NotNull RowRenderer<T> rowRenderer,
            final Pagination.@NotNull PageCommandFunction pageCommand) {
        return new PaginationImpl<>(this.width, this.resultsPerPage, this.renderer,
                this.lineCharacter, this.lineStyle, this.previousPageButtonCharacter,
                this.previousPageButtonStyle, this.nextPageButtonCharacter,
                this.nextPageButtonStyle, title, rowRenderer, pageCommand, this.addEmptyLine);
    }
}
