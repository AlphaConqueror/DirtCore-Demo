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

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Locale;
import net.dirtcraft.dirtcore.common.dependencies.relocation.Relocation;
import net.dirtcraft.dirtcore.common.dependencies.relocation.RelocationHelper;

/**
 * The dependencies used by DirtCore.
 */
public enum Dependency {

    ASM("org.ow2.asm", "asm", "9.1"),
    ASM_COMMONS("org.ow2.asm", "asm-commons", "9.1"),
    JAR_RELOCATOR("me.lucko", "jar-relocator", "1.7"),
    ADVENTURE_API("net{}kyori", "adventure-api", "4.16.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    ADVENTURE_KEY("net{}kyori", "adventure-key", "4.16.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    ADVENTURE_PLATFORM_API("net{}kyori", "adventure-platform-api", "4.1.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    ADVENTURE_TEXT_MINIMESSAGE("net{}kyori", "adventure-text-minimessage", "4.16.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    ADVENTURE_TEXT_SERIALIZER_BUNGEECORD("net{}kyori", "adventure-text-serializer-bungeecord",
            "4.1.0", Relocation.of("adventure", "net{}kyori{}adventure")),
    ADVENTURE_TEXT_SERIALIZER_GSON("net{}kyori", "adventure-text-serializer-gson", "4.16.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    ADVENTURE_TEXT_SERIALIZER_GSON_LEGACY("net{}kyori",
            "adventure-text-serializer-gson-legacy-impl", "4.10.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    ADVENTURE_TEXT_SERIALIZER_JSON("net{}kyori", "adventure-text-serializer-json", "4.16.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    ADVENTURE_TEXT_SERIALIZER_LEGACY("net{}kyori", "adventure-text-serializer-legacy", "4.16.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    EXAMINATION_API("net{}kyori", "examination-api", "1.3.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    EXAMINATION_STRING("net{}kyori", "examination-string", "1.3.0",
            Relocation.of("adventure", "net{}kyori{}adventure")),
    EVENT("net{}kyori", "event-api", "3.0.0", Relocation.of("event", "net{}kyori{}event")),
    HIBERNATE_CORE("org{}hibernate", "hibernate-core", "5.2.1.Final"),
    HIBERNATE_COMMONS_ANNOTATIONS("org.hibernate.common", "hibernate-commons-annotations",
            "5.0.1.Final"),
    ANTLR("antlr", "antlr", "2.7.7"),
    FASTERXML_CLASSMATE("com.fasterxml", "classmate", "1.3.0"),
    DOM4J("dom4j", "dom4j", "1.6.1"),
    JAVASSIST("org.javassist", "javassist", "3.30.2-GA"),
    OPTION("net{}kyori", "option", "1.0.0"),
    GSON("com.google.code.gson", "gson", "2.8.0"),
    CAFFEINE("com{}github{}ben-manes{}caffeine", "caffeine", "2.9.0",
            Relocation.of("caffeine", "com{}github{}benmanes{}caffeine")),
    OKIO("com{}squareup{}" + RelocationHelper.OKIO_STRING, RelocationHelper.OKIO_STRING, "3.9.0"),
    OKIO_JVM("com{}squareup{}" + RelocationHelper.OKIO_STRING, RelocationHelper.OKIO_JVM_STRING,
            "3.0.0"),
    OKHTTP("com{}squareup{}" + RelocationHelper.OKHTTP3_STRING, "okhttp", "4.10.0"),
    KOTLIN("org{}jetbrains{}kotlin", "kotlin-stdlib", "1.6.20"),
    NEOVISIONARIES("com.neovisionaries", "nv-websocket-client", "2.14"),
    TROVE4J("net.sf.trove4j", "trove4j", "3.0.3"),
    JACKSON_CORE("com.fasterxml.jackson.core", "jackson-core", "2.14.1"),
    JACKSON_DATABIND("com.fasterxml.jackson.core", "jackson-databind", "2.14.1"),
    JACKSON_ANNOTATIONS("com.fasterxml.jackson.core", "jackson-annotations", "2.14.1"),
    JBOSS_LOGGING("org.jboss.logging", "jboss-logging", "3.3.0.Final"),
    GERONIMO("org.apache.geronimo.specs", "geronimo-jta_1.1_spec", "1.1.1"),
    BYTEBUDDY("net{}bytebuddy", "byte-buddy", "1.10.22",
            Relocation.of("bytebuddy", "net{}bytebuddy")),
    COMMONS_COLLECTIONS4("org{}apache{}commons", "commons-collections4", "4.4"),
    JAVAX_PERSISTENCE("javax{}persistence", "javax.persistence-api", "2.2"),
    JAKARTA_XML_BIND("jakarta.xml.bind", "jakarta.xml.bind-api", "2.3.2"),
    JDA("net{}dv8tion", "JDA", "5.0.0-beta.18", Relocation.of("jda", "net{}dv8tion{}jda")),
    MARIADB_DRIVER("org{}mariadb{}jdbc", "mariadb-java-client", "3.1.3",
            Relocation.of("mariadb", "org{}mariadb{}jdbc")),
    MYSQL_DRIVER("mysql", "mysql-connector-java", "8.0.23", Relocation.of("mysql", "com{}mysql")),
    HIKARI("com{}zaxxer", "HikariCP", "4.0.3", Relocation.of("hikari", "com{}zaxxer{}hikari")),
    SLF4J_SIMPLE("org.slf4j", "slf4j-simple", "1.7.30"),
    SLF4J_API("org.slf4j", "slf4j-api", "1.7.30"),
    CONFIGURATE_CORE("org{}spongepowered", "configurate-core", "3.7.2",
            Relocation.of("configurate", "ninja{}leaping{}configurate")),
    CONFIGURATE_HOCON("org{}spongepowered", "configurate-hocon", "3.7.2",
            Relocation.of("configurate", "ninja{}leaping{}configurate"),
            Relocation.of("hocon", "com{}typesafe{}config")),
    HOCON_CONFIG("com{}typesafe", "config", "1.4.1",
            Relocation.of("hocon", "com{}typesafe{}config")),
    PERSISTENCE_API("javax{}persistence", "javax.persistence-api", "2.2",
            Relocation.of("persistence", "javax{}persistence"));

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";
    private final String mavenRepoPath;
    private final String version;
    private final List<Relocation> relocations;

    Dependency(final String groupId, final String artifactId, final String version) {
        this(groupId, artifactId, version, new Relocation[0]);
    }

    Dependency(final String groupId, final String artifactId, final String version,
            final Relocation... relocations) {
        this.mavenRepoPath = String.format(MAVEN_FORMAT, rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId), version, rewriteEscaping(artifactId), version);
        this.version = version;
        this.relocations = ImmutableList.copyOf(relocations);
    }

    private static String rewriteEscaping(final String s) {
        return s.replace("{}", ".");
    }

    public String getFileName(final String classifier) {
        final String name = this.name().toLowerCase(Locale.ROOT).replace('_', '-');
        final String extra = classifier == null || classifier.isEmpty() ? "" : "-" + classifier;

        return name + "-" + this.version + extra + ".jar";
    }

    public List<Relocation> getRelocations() {
        return this.relocations;
    }

    String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

}
