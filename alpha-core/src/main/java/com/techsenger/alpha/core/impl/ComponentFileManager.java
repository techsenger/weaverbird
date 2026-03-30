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

package com.techsenger.alpha.core.impl;

import com.techsenger.alpha.core.api.Constants;
import com.techsenger.alpha.core.api.DefaultPathManager;
import com.techsenger.alpha.core.api.DefaultPathResolver;
import com.techsenger.alpha.core.api.PathManager;
import com.techsenger.alpha.core.api.PathResolver;
import com.techsenger.alpha.core.api.component.ComponentConfig;
import com.techsenger.alpha.core.api.component.ComponentConfigInfo;
import com.techsenger.alpha.core.api.component.ComponentConfigUtils;
import com.techsenger.alpha.core.api.component.ComponentException;
import com.techsenger.alpha.core.impl.component.ConfigXmlReader;
import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.toolkit.core.file.FilePermissionException;
import com.techsenger.toolkit.core.file.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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
            ZipEntry zipEntry = new ZipEntry(destinationDirectory + "/" + relativePath.toString());
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
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            addDirectoryToArchive(pathResolver.resolveConfigDirectory(config),
                    DefaultPathManager.CONFIG_DIRECTORY_NAME, zos);
            addDirectoryToArchive(pathResolver.resolveDataDirectory(config),
                    DefaultPathManager.DATA_DIRECTORY_NAME, zos);
            addDirectoryToArchive(pathResolver.resolveDocumentDirectory(config),
                    DefaultPathManager.DOCUMENT_DIRECTORY_NAME, zos);
            addDirectoryToArchive(pathResolver.resolveLegalDirectory(config),
                    DefaultPathManager.LEGAL_DIRECTORY_NAME, zos);
            zos.finish();
            byte[] zipBytes = baos.toByteArray();
            var archivePath = directoryPath.resolve(config.getName() + "-" + config.getVersion() + "." + extension);
            Files.write(archivePath, zipBytes);
            return archivePath;
        }  catch (Exception ex) {
            throw new ComponentException(StringUtils.format("Error building {}{}{}", config.getName(),
                    Constants.NAME_VERSION_SEPARATOR, config.getVersion()), ex);
        }
    }

    /**
     * Component adding is done in two steps, as we need to read config, before copying files.
     *
     * @param zipArchive
     * @return
     */
    public ComponentConfig readConfig(ZipFile zipFile) throws ComponentException {
        var configPath = DefaultPathManager.CONFIG_DIRECTORY_NAME + "/" + DefaultPathResolver.CONFIG_FILE_NAME;
        var entry = zipFile.getEntry(configPath);
        if (entry == null) {
            throw new ComponentException(StringUtils.format("Component config not found at {}", zipFile));
        }
        try (InputStream inputStream = zipFile.getInputStream(entry)) {
            var reader = new ConfigXmlReader();
            var config = reader.read(inputStream, this.configInfo, this.configUtils);
            return config;
        } catch (Exception ex) {
            throw new ComponentException(StringUtils.format("Error reading config at {}", zipFile), ex);
        }
    }

    public void addComponent(ComponentConfig config, Path zipPath, ZipFile zipfile)
            throws ComponentException, IOException {
        //for simplicity we extract all files
        var filesByPath = extractAllFiles(zipfile);

        var configPath = pathResolver.resolveConfigDirectory(config);
        var dataPath = pathResolver.resolveDataDirectory(config);
        var docPath = pathResolver.resolveDocumentDirectory(config);
        var legalPath = pathResolver.resolveLegalDirectory(config);

        var configPathStr = DefaultPathManager.CONFIG_DIRECTORY_NAME + "/";
        var dataPathStr = DefaultPathManager.DATA_DIRECTORY_NAME + "/";
        var docPathStr = DefaultPathManager.DOCUMENT_DIRECTORY_NAME + "/";
        var legalPathStr = DefaultPathManager.LEGAL_DIRECTORY_NAME + "/";
        var repoPathStr = DefaultPathManager.REPOSITORY_DIRECTORY_NAME + "/";

        //we add only concrete folders
        for (var entry : filesByPath.entrySet()) {
            if (entry.getKey().startsWith(configPathStr)) {
                writeArchiveFile(entry.getKey(), entry.getValue(), configPathStr, configPath);
            } else if (entry.getKey().startsWith(dataPathStr)) {
                writeArchiveFile(entry.getKey(), entry.getValue(), dataPathStr, dataPath);
            } else if (entry.getKey().startsWith(docPathStr)) {
                writeArchiveFile(entry.getKey(), entry.getValue(), docPathStr, docPath);
            } else if (entry.getKey().startsWith(legalPathStr)) {
                writeArchiveFile(entry.getKey(), entry.getValue(), legalPathStr, legalPath);
            } else if (entry.getKey().startsWith(repoPathStr)) {
                writeArchiveFileToRepo(entry.getKey(), entry.getValue());
            } else {
                throw new ComponentException(StringUtils.format("Illegal file {} in component archive at {}",
                        entry.getKey(), zipPath));
            }
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

    /**
     * Extracts all files to map.
     *
     * @param zipFile
     * @return
     * @throws IOException
     */
    private Map<String, byte[]> extractAllFiles(ZipFile zipFile) throws IOException {
        Map<String, byte[]> fileContents = new HashMap<>();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            try (InputStream is = zipFile.getInputStream(entry)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    baos.write(buffer, 0, length);
                }
                fileContents.put(entry.getName(), baos.toByteArray());
            }
        }
        return fileContents;
    }

    /**
     * Writes archived file content to framework directory. Attention - this method is not used for directories.
     *
     * @param filePathStr the path of the file in zip archive
     * @param content
     * @param dirName the name of the directory in the archive that keeps this file (for example 'config/')
     * @param destDir full path to destination folder
     * @throws IOException
     */
    private void writeArchiveFile(String filePathStr, byte[] content, String dirName, Path destDir)
            throws IOException {
        //removing 'config/'
        filePathStr = filePathStr.substring(dirName.length());
        if (File.separator.equals("\\")) {
            //windows
            filePathStr = filePathStr.replace("/", "\\");
        }
        var filePath = destDir.resolve(filePathStr);
        Files.createDirectories(filePath.getParent());
        try (OutputStream os = Files.newOutputStream(filePath)) {
            os.write(content);
        }
        logger.debug("Wrote {} from archive to {}", filePathStr, filePath);
    }

    /**
     * Writes files to repo. It is assumed that maven repo is used.
     *
     * @param filePathStr
     * @param content
     * @throws IOException
     */
    private void writeArchiveFileToRepo(String filePathStr, byte[] content) throws IOException {
        if (File.separator.equals("\\")) {
            //windows
            filePathStr = filePathStr.replace("/", "\\");
        }
        //resolve to root because filePathStr starts with "repo/..."
        var filePath = this.pathManager.getRootDirectory().resolve(filePathStr);
        Files.createDirectories(filePath.getParent());
        try (OutputStream os = Files.newOutputStream(filePath)) {
            os.write(content);
        }
        logger.debug("Wrote {} from archive to repo: {}", filePathStr, filePath);
    }
}
