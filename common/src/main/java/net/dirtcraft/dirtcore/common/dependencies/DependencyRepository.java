/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.dependencies;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Represents a repository which contains {@link Dependency}s.
 */
public enum DependencyRepository {

    /**
     * Maven Central mirror repository.
     *
     * <p>This is used to reduce the load on repo.maven.org.</p>
     *
     * <p>Although Maven Central is technically a CDN, it is meant for developer use,
     * not end-user products. It is trivial and not very expensive for us to provide a
     * mirror, which will absorb any traffic caused by DirtCore.</p>
     *
     * <p>DirtCore will fallback to the real-thing if the mirror ever goes offline.
     * Retrieved content is validated with a checksum, so there is no risk to integrity.</p>
     */
    MAVEN_CENTRAL_MIRROR("https://libraries.luckperms.net/") {
        @Override
        protected URLConnection openConnection(final Dependency dependency) throws IOException {
            final URLConnection connection = super.openConnection(dependency);
            connection.setRequestProperty("User-Agent", "luckperms");

            // Set a connect/read timeout, so if the mirror goes offline we can fall back
            // to Maven Central within a reasonable time.
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));

            return connection;
        }
    },

    /**
     * Maven Central.
     */
    MAVEN_CENTRAL("https://repo1.maven.org/maven2/");

    private final String url;

    DependencyRepository(final String url) {
        this.url = url;
    }

    /**
     * Downloads the raw bytes of the {@code dependency}.
     *
     * @param dependency the dependency to download
     * @return the downloaded bytes
     * @throws DependencyDownloadException if unable to download
     */
    public byte[] downloadRaw(final Dependency dependency) throws DependencyDownloadException {
        try {
            final URLConnection connection = this.openConnection(dependency);
            try (final InputStream in = connection.getInputStream()) {
                final byte[] bytes = ByteStreams.toByteArray(in);
                if (bytes.length == 0) {
                    throw new DependencyDownloadException("Empty stream");
                }
                return bytes;
            }
        } catch (final Exception e) {
            throw new DependencyDownloadException(e);
        }
    }

    /**
     * Downloads the raw bytes of the {@code dependency}.
     *
     * @param dependency the dependency to download
     * @return the downloaded bytes
     * @throws DependencyDownloadException if unable to download
     */
    public byte[] download(final Dependency dependency) throws DependencyDownloadException {
        return this.downloadRaw(dependency);
    }

    /**
     * Downloads the the {@code dependency} to the {@code file}, ensuring the
     * downloaded bytes match the checksum.
     *
     * @param dependency the dependency to download
     * @param file       the file to write to
     * @throws DependencyDownloadException if unable to download
     */
    public void download(final Dependency dependency,
            final Path file) throws DependencyDownloadException {
        try {
            Files.write(file, this.download(dependency));
        } catch (final IOException e) {
            throw new DependencyDownloadException(e);
        }
    }

    /**
     * Opens a connection to the given {@code dependency}.
     *
     * @param dependency the dependency to download
     * @return the connection
     * @throws IOException if unable to open a connection
     */
    protected URLConnection openConnection(final Dependency dependency) throws IOException {
        final URL dependencyUrl = new URL(this.url + dependency.getMavenRepoPath());
        return dependencyUrl.openConnection();
    }

}
