/*
 * Copyright 2018-2026 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.alpha.core.impl.war;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Pavel Castornii
 */
public final class WarModuleFinder implements ModuleFinder {

    public static WarModuleFinder of(Path war) throws IOException {
        return new WarModuleFinder(war);
    }

    private final FileSystem warfs;

    private final Path classes;

    private final ModuleReference mref;

    private final class WarModuleReader implements ModuleReader {

        private volatile boolean closed;

        private void ensureOpen() throws IOException {
            if (closed) {
                throw new IOException("ModuleReader is closed");
            }
        }

        public Optional<URI> find(String name) throws IOException {
            ensureOpen();
            if (!name.startsWith("/")) {
                Path entry = classes.resolve(name);
                if (Files.exists(entry)) {
                    return Optional.of(entry.toUri());
                }
            }
            return Optional.empty();
        }

        public Stream<String> list() throws IOException {
            ensureOpen();
            return Files.walk(classes)
                    .map(entry -> classes.relativize(entry).toString())
                    .filter(name -> name.length() > 0);
        }

        public void close() {
            closed = true;
        }
    }

    private WarModuleFinder(Path warfile) throws IOException {
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        FileSystem fs = FileSystems.newFileSystem(warfile, scl);
        Path classes = fs.getPath("/WEB-INF/classes");

        ModuleDescriptor descriptor;
        try (InputStream in =
            Files.newInputStream(classes.resolve("module-info.class"))) {
                        descriptor = ModuleDescriptor.read(in, () ->
            packages(classes));
        }

        this.warfs = fs;
        this.classes = classes;
        this.mref = new ModuleReference(descriptor, warfile.toUri()) {

            @Override
            public ModuleReader open() {
                return new WarModuleReader();
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("[module ");
                sb.append(descriptor().name());
                sb.append(", location=");
                sb.append(location());
                sb.append("]");
                return sb.toString();
            }
        };
    }

    @Override
    public Optional<ModuleReference> find(String name) {
        if (name.equals(mref.descriptor().name())) {
            return Optional.of(mref);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Set<ModuleReference> findAll() {
        return Set.of(mref);
    }

    private Set<String> packages(Path classes) {
        try {
            return Files.find(classes, Integer.MAX_VALUE,
                              (path, attrs) -> !attrs.isDirectory())
                    .map(entry -> classes.relativize(entry).toString())
                    .map(this::toPackageName)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private Optional<String> toPackageName(String name) {
        int index = name.lastIndexOf("/");
        if (index > 0) {
            return Optional.of(name.substring(0, index).replace('/', '.'));
        } else {
            return Optional.empty();
        }
    }
}
