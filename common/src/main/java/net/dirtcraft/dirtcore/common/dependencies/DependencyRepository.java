/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
