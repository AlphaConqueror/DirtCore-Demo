/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MoreFiles {

    private MoreFiles() {}

    public static Path createFileIfNotExists(final Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        return path;
    }

    public static Path createDirectoryIfNotExists(final Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return path;
        }

        try {
            Files.createDirectory(path);
        } catch (final FileAlreadyExistsException e) {
            // ignore
        }

        return path;
    }

    public static Path createDirectoriesIfNotExists(final Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return path;
        }

        try {
            Files.createDirectories(path);
        } catch (final FileAlreadyExistsException e) {
            // ignore
        }

        return path;
    }

    public static void deleteDirectory(final Path path) throws IOException {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return;
        }

        try (final DirectoryStream<Path> contents = Files.newDirectoryStream(path)) {
            for (final Path file : contents) {
                if (Files.isDirectory(file)) {
                    deleteDirectory(file);
                } else {
                    Files.delete(file);
                }
            }
        }

        Files.delete(path);
    }

}
