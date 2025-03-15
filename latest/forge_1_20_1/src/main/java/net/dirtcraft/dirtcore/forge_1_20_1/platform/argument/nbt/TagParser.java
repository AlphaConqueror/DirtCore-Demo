/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.nbt;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Pattern;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.Dynamic2CommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;

public class TagParser {

    public static final SimpleCommandExceptionType ERROR_TRAILING_DATA =
            new SimpleCommandExceptionType(new LiteralMessage("Unexpected trailing data"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY =
            new SimpleCommandExceptionType(new LiteralMessage("Expected key"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE =
            new SimpleCommandExceptionType(new LiteralMessage("Expected value"));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST =
            new Dynamic2CommandExceptionType(
                    (p_129366_, p_129367_) -> new LiteralMessage("Can't insert %s into list of %s",
                            p_129366_, p_129367_));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY =
            new Dynamic2CommandExceptionType(
                    (p_129357_, p_129358_) -> new LiteralMessage("Can't insert %s into %s",
                            p_129357_, p_129358_));
    public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY =
            new DynamicCommandExceptionType(
                    (p_129355_) -> new LiteralMessage("Invalid array type '%s'", p_129355_));
    public static final char ELEMENT_SEPARATOR = ',';
    public static final char NAME_VALUE_SEPARATOR = ':';
    private static final char LIST_OPEN = '[';
    private static final char LIST_CLOSE = ']';
    private static final char STRUCT_CLOSE = '}';
    private static final char STRUCT_OPEN = '{';
    private static final Pattern DOUBLE_PATTERN_NOSUFFIX =
            Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN =
            Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern FLOAT_PATTERN =
            Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    public TagParser(final StringReader pReader) {
        this.reader = pReader;
    }

    public static CompoundTag parseTag(final String pText) throws CommandSyntaxException {
        return (new TagParser(new StringReader(pText))).readSingleStruct();
    }

    public Tag readValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();

        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        }

        final char c0 = this.reader.peek();

        if (c0 == '{') {
            return this.readStruct();
        }

        return c0 == '[' ? this.readList() : this.readTypedValue();
    }

    public CompoundTag readStruct() throws CommandSyntaxException {
        this.expect('{');

        final CompoundTag compoundTag = new CompoundTag();

        this.reader.skipWhitespace();

        while (this.reader.canRead() && this.reader.peek() != '}') {
            final int i = this.reader.getCursor();
            final String s = this.readKey();

            if (s.isEmpty()) {
                this.reader.setCursor(i);
                throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
            }

            this.expect(':');
            compoundTag.put(s, this.readValue());

            if (!this.hasElementSeparator()) {
                break;
            }

            if (!this.reader.canRead()) {
                throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
            }
        }

        this.expect('}');
        return compoundTag;
    }

    CompoundTag readSingleStruct() throws CommandSyntaxException {
        final CompoundTag compoundtag = this.readStruct();

        this.reader.skipWhitespace();

        if (this.reader.canRead()) {
            throw ERROR_TRAILING_DATA.createWithContext(this.reader);
        }

        return compoundtag;
    }

    protected String readKey() throws CommandSyntaxException {
        this.reader.skipWhitespace();

        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
        }

        return this.reader.readString();
    }

    protected Tag readTypedValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();

        final int i = this.reader.getCursor();

        if (StringReader.isQuotedStringStart(this.reader.peek())) {
            return StringTag.valueOf(this.reader.readQuotedString());
        }

        final String s = this.reader.readUnquotedString();

        if (s.isEmpty()) {
            this.reader.setCursor(i);
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        }

        return this.type(s);
    }

    protected Tag readList() throws CommandSyntaxException {
        return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1))
                && this.reader.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
    }

    private Tag type(final String pValue) {
        try {
            if (FLOAT_PATTERN.matcher(pValue).matches()) {
                return FloatTag.valueOf(Float.parseFloat(pValue.substring(0, pValue.length() - 1)));
            }

            if (BYTE_PATTERN.matcher(pValue).matches()) {
                return ByteTag.valueOf(Byte.parseByte(pValue.substring(0, pValue.length() - 1)));
            }

            if (LONG_PATTERN.matcher(pValue).matches()) {
                return LongTag.valueOf(Long.parseLong(pValue.substring(0, pValue.length() - 1)));
            }

            if (SHORT_PATTERN.matcher(pValue).matches()) {
                return ShortTag.valueOf(Short.parseShort(pValue.substring(0, pValue.length() - 1)));
            }

            if (INT_PATTERN.matcher(pValue).matches()) {
                return IntTag.valueOf(Integer.parseInt(pValue));
            }

            if (DOUBLE_PATTERN.matcher(pValue).matches()) {
                return DoubleTag.valueOf(
                        Double.parseDouble(pValue.substring(0, pValue.length() - 1)));
            }

            if (DOUBLE_PATTERN_NOSUFFIX.matcher(pValue).matches()) {
                return DoubleTag.valueOf(Double.parseDouble(pValue));
            }

            if ("true".equalsIgnoreCase(pValue)) {
                return ByteTag.ONE;
            }

            if ("false".equalsIgnoreCase(pValue)) {
                return ByteTag.ZERO;
            }
        } catch (final NumberFormatException ignored) {}

        return StringTag.valueOf(pValue);
    }

    private Tag readListTag() throws CommandSyntaxException {
        this.expect('[');
        this.reader.skipWhitespace();

        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        }

        final ListTag listTag = new ListTag();
        TagType<?> tagtype = null;

        while (this.reader.peek() != ']') {
            final int i = this.reader.getCursor();
            final Tag tag = this.readValue();
            final TagType<?> t = tag.getType();

            if (tagtype == null) {
                tagtype = t;
            } else if (t != tagtype) {
                this.reader.setCursor(i);
                throw ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, t.getName(),
                        tagtype.getName());
            }

            listTag.add(tag);

            if (!this.hasElementSeparator()) {
                break;
            }

            if (!this.reader.canRead()) {
                throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            }
        }

        this.expect(']');
        return listTag;
    }

    private Tag readArrayTag() throws CommandSyntaxException {
        this.expect('[');

        final int i = this.reader.getCursor();
        final char c0 = this.reader.read();

        this.reader.read();
        this.reader.skipWhitespace();

        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else if (c0 == 'B') {
            return new ByteArrayTag(this.readArray(ByteArrayTag.TYPE, ByteTag.TYPE));
        } else if (c0 == 'L') {
            return new LongArrayTag(this.readArray(LongArrayTag.TYPE, LongTag.TYPE));
        } else if (c0 == 'I') {
            return new IntArrayTag(this.readArray(IntArrayTag.TYPE, IntTag.TYPE));
        }

        this.reader.setCursor(i);
        throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(c0));
    }

    @SuppressWarnings("unchecked")
    private <T extends Number> List<T> readArray(final TagType<?> pArrayType,
            final TagType<?> pElementType) throws CommandSyntaxException {
        final List<T> list = Lists.newArrayList();

        while (true) {
            if (this.reader.peek() != ']') {
                final int i = this.reader.getCursor();
                final Tag tag = this.readValue();
                final TagType<?> tagtype = tag.getType();

                if (tagtype != pElementType) {
                    this.reader.setCursor(i);
                    throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, tagtype.getName(),
                            pArrayType.getName());
                }

                if (pElementType == ByteTag.TYPE) {
                    list.add((T) (Byte) ((NumericTag) tag).getAsByte());
                } else if (pElementType == LongTag.TYPE) {
                    list.add((T) (Long) ((NumericTag) tag).getAsLong());
                } else {
                    list.add((T) (Integer) ((NumericTag) tag).getAsInt());
                }

                if (this.hasElementSeparator()) {
                    if (!this.reader.canRead()) {
                        throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
                    }

                    continue;
                }
            }

            this.expect(']');
            return list;
        }
    }

    private boolean hasElementSeparator() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    private void expect(final char pExpected) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(pExpected);
    }
}