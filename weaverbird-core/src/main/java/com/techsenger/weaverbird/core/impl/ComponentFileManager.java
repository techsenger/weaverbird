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

package com.techsenger.weaverbird.core.impl;

import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.toolkit.core.file.FilePermissionException;
import com.techsenger.toolkit.core.file.FileUtils;
import com.techsenger.weaverbird.core.api.Constants;
import com.techsenger.weaverbird.core.api.DefaultPathManager;
import com.techsenger.weaverbird.core.api.DefaultPathResolver;
import com.techsenger.weaverbird.core.api.PathManager;
import com.techsenger.weaverbird.core.api.PathResolver;
import com.techsenger.weaverbird.core.api.component.ComponentConfig;
import com.techsenger.weaverbird.core.api.component.ComponentConfigInfo;
import com.techsenger.weaverbird.core.api.component.ComponentConfigUtils;
import com.techsenger.weaverbird.core.api.component.ComponentException;
import com.techsenger.weaverbird.core.impl.component.ConfigXmlReader;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds and removes component files from framework directory.
 *
 * @author Pavel Castornii
 */
class ComponentFileManager {

    private static final Logger logger = LoggerFactory.getLogger(ComponentFileManager.class);

    private static class FileVisitorImpl extends SimpleFileVisitor<Path> {

        private final Path sourceDirectory;

        private final String destinationDirectory;

        private final ZipOutputStream zos;

        FileVisitorImpl(Path srcDir, String destDir, ZipOutputStream zos) {
            this.sourceDirectory = srcDir;
            this.destinationDirectory = destDir;
            this.zos = zos;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            //resolving path of the file relative to component base directory, for example, config/name/version
            Path relativePath = sourceDirectory.relativize(file);
            ZipEntry zipEntry = new ZipEntry(destinationDirectory + "/"
                    + relativePath.toString().replace(File.separatorChar, '/'));
            zos.putNextEntry(zipEntry);
            Files.copy(file, zos);
            zos.closeEntry();
            return FileVisitResult.CONTINUE;
        }
    }

    private final PathManager pathManager;

    private final PathResolver pathResolver;

    private final ComponentConfigInfo configInfo;

    private final ComponentConfigUtils configUtils;

    ComponentFileManager(PathManager pathManager, ComponentConfigInfo configInfo,
            ComponentConfigUtils configUtils) {
        this.pathManager = pathManager;
        this.pathResolver = pathManager.getPathResolver();
        this.configInfo = configInfo;
        this.configUtils = configUtils;
    }

    public Path build(ComponentConfig config, Path directoryPath, String extension) throws ComponentException {
        var archivePath = directoryPath.resolve(config.getName() + "-" + config.getVersion() + "." + extension);
        try {
            try (ZipOutputStream zos = new ZipOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(archivePath)))) {
                addDirectoryToArchive(pathResolver.resolveConfigDirectory(config),
                        resolveArchiveDirectory(DefaultPathManager.CONFIG_DIRECTORY_NAME, config), zos);
                addDirectoryToArchive(pathResolver.resolveDataDirectory(config),
                        resolveArchiveDirectory(DefaultPathManager.DATA_DIRECTORY_NAME, config), zos);
                addDirectoryToArchive(pathResolver.resolveDocumentDirectory(config),
                        resolveArchiveDirectory(DefaultPathManager.DOCUMENT_DIRECTORY_NAME, config), zos);
                addDirectoryToArchive(pathResolver.resolveLegalDirectory(config),
                        resolveArchiveDirectory(DefaultPathManager.LEGAL_DIRECTORY_NAME, config), zos);
            }
            return archivePath;
        } catch (Exception ex) {
            //a partially written archive must not stay on disk
            try {
                Files.deleteIfExists(archivePath);
            } catch (IOException deleteEx) {
                ex.addSuppressed(deleteEx);
            }
            throw new ComponentException(StringUtils.format("Error building {}{}{}", config.getName(),
                    Constants.NAME_VERSION_SEPARATOR, config.getVersion()), ex);
        }
    }

    /**
     * Reads the component config from the archive. Component adding is done in two steps, as the config must be
     * read before copying files. The archive structure mirrors the framework structure, so the config is located
     * at {@code config/<name>/<version>/configuration.xml}; the name and version in this path must match the config.
     *
     * @param zipFile the component archive
     * @return the config of the component
     * @throws ComponentException if there is no config, more than one config, or the config doesn't match its path
     */
    public ComponentConfig readConfig(ZipFile zipFile) throws ComponentException {
        var configEntries = zipFile.stream()
                .filter(e -> !e.isDirectory() && isConfigEntryName(e.getName()))
                .toList();
        if (configEntries.isEmpty()) {
            throw new ComponentException(StringUtils.format("Component config not found at {}", zipFile.getName()));
        }
        if (configEntries.size() > 1) {
            throw new ComponentException(StringUtils.format("Component archive {} contains more than one config: {}",
                    zipFile.getName(), configEntries.stream().map(ZipEntry::getName).toList()));
        }
        var entry = configEntries.get(0);
        ComponentConfig config;
        try (InputStream inputStream = zipFile.getInputStream(entry)) {
            var reader = new ConfigXmlReader();
            config = reader.read(inputStream, this.configInfo, this.configUtils);
        } catch (Exception ex) {
            throw new ComponentException(StringUtils.format("Error reading config at {}", zipFile.getName()), ex);
        }
        var expectedName = resolveArchiveDirectory(DefaultPathManager.CONFIG_DIRECTORY_NAME, config) + "/"
                + DefaultPathResolver.CONFIG_FILE_NAME;
        if (!expectedName.equals(entry.getName())) {
            throw new ComponentException(StringUtils.format("Component config {} in archive {} doesn't match its "
                    + "name and version, expected {}", entry.getName(), zipFile.getName(), expectedName));
        }
        return config;
    }

    /**
     * Copies archive files to the framework directory. All archive entries are validated before the first file is
     * written, so an invalid archive doesn't leave anything on disk.
     *
     * @param config the config read from this archive
     * @param zipPath the path of the archive
     * @param zipfile the opened archive
     * @throws ComponentException if the archive contains an illegal entry
     * @throws IOException if a file cannot be written
     */
    public void addComponent(ComponentConfig config, Path zipPath, ZipFile zipfile)
            throws ComponentException, IOException {
        var root = pathManager.getRootDirectory().toAbsolutePath().normalize();
        var allowedDirectories = List.of(
                pathResolver.resolveConfigDirectory(config),
                pathResolver.resolveDataDirectory(config),
                pathResolver.resolveDocumentDirectory(config),
                pathResolver.resolveLegalDirectory(config),
                pathManager.getRepositoryDirectory()).stream()
                .map(p -> p.toAbsolutePath().normalize())
                .toList();

        //the archive structure mirrors the framework structure, so entries are validated and then written as is
        var entries = new ArrayList<ZipEntry>();
        for (var entry : zipfile.stream().filter(e -> !e.isDirectory()).toList()) {
            var target = root.resolve(entry.getName()).normalize();
            if (allowedDirectories.stream().noneMatch(d -> !target.equals(d) && target.startsWith(d))) {
                throw new ComponentException(StringUtils.format("Illegal file {} in component archive at {}",
                        entry.getName(), zipPath));
            }
            entries.add(entry);
        }
        for (var entry : entries) {
            var target = root.resolve(entry.getName()).normalize();
            Files.createDirectories(target.getParent());
            try (InputStream is = zipfile.getInputStream(entry)) {
                Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            }
            logger.debug("Wrote {} from archive to {}", entry.getName(), target);
        }
    }

    public void addComponent(ComponentConfig config, String xmlConfig) throws ComponentException, IOException {
        var configPath = pathResolver.resolveConfigFile(config);
        Files.createDirectories(configPath.getParent());
        FileUtils.writeFile(configPath, xmlConfig, StandardCharsets.UTF_8);
    }

    public void removeComponent(ComponentConfig config) throws ComponentException {
        try {
            deleteDirectory(this.pathResolver.resolveCacheDirectory(config));
            //deleting cache/component-name directory
            deleteDirectoryIfEmpty(this.pathManager.getCacheDirectory(), config);

            deleteDirectory(this.pathResolver.resolveConfigDirectory(config));
            deleteDirectoryIfEmpty(this.pathManager.getConfigDirectory(), config);

            deleteDirectory(this.pathResolver.resolveDataDirectory(config));
            deleteDirectoryIfEmpty(this.pathManager.getDataDirectory(), config);

            deleteDirectory(this.pathResolver.resolveDocumentDirectory(config));
            deleteDirectoryIfEmpty(this.pathManager.getDocumentDirectory(), config);

            deleteDirectory(this.pathResolver.resolveLegalDirectory(config));
            deleteDirectoryIfEmpty(this.pathManager.getLegalDirectory(), config);

            deleteDirectory(this.pathResolver.resolveTempDirectory(config));
            deleteDirectoryIfEmpty(this.pathManager.getTempDirectory(), config);
        } catch (Exception ex) {
            throw new ComponentException(StringUtils.format("Couldn't remove files of {}{}{}", config.getName(),
                    Constants.NAME_VERSION_SEPARATOR, config.getVersion()));
        }
    }

    private void deleteDirectory(Path path) throws FilePermissionException {
        if (Files.exists(path)) {
            FileUtils.deleteDirectory(path.toFile());
            logger.debug("Deleted {}", path);
        }
    }

    private void deleteDirectoryIfEmpty(Path baseDirectory, ComponentConfig config) {
        var path = baseDirectory.resolve(config.getName());
        var directory = path.toFile();
        if (directory.isDirectory() && directory.list().length == 0) {
            directory.delete();
        }
    }

    /**
     * The main idea that we will walk component directory, for example config/somecomponent/1.0.0 but file path
     * will be resolved relative to specific directory.
     *
     * @param srcDir
     * @param destDir
     * @param zos
     * @throws IOException
     */
    private void addDirectoryToArchive(Path srcDir, String destDir, ZipOutputStream zos) throws IOException {
        if (Files.exists(srcDir)) {
            Files.walkFileTree(srcDir, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
                new FileVisitorImpl(srcDir, destDir, zos));
        }
    }

    private String resolveArchiveDirectory(String directoryName, ComponentConfig config) {
        return directoryName + "/" + config.getName() + "/" + config.getVersion().getFull();
    }

    /**
     * Checks that the name looks like {@code config/<name>/<version>/configuration.xml}.
     */
    private boolean isConfigEntryName(String entryName) {
        var parts = entryName.split("/");
        return parts.length == 4
                && DefaultPathManager.CONFIG_DIRECTORY_NAME.equals(parts[0])
                && DefaultPathResolver.CONFIG_FILE_NAME.equals(parts[3]);
    }
}
