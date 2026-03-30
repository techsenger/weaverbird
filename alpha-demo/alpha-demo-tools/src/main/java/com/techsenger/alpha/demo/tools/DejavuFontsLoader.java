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

package com.techsenger.alpha.demo.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Pavel Castornii
 */
public final class DejavuFontsLoader {

    public static final String FONT_URL =
            "https://github.com/dejavu-fonts/dejavu-fonts/releases/download/version_2_37/dejavu-fonts-ttf-2.37.zip";

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println("Usage: java DejavuFontsLoader <output_folder>");
                return;
            }
            URL url = new URL(FONT_URL);
            Path outputPath = Paths.get(args[0]).resolve("dejavu-fonts-ttf-2.37.zip");
            try (InputStream in = url.openStream()) {
                Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Fonts saved successfully to " + outputPath);
            }
            unzip(outputPath);
        } catch (Exception e) {
            e.printStackTrace();
            //this loader is executed on the thread of the plugin,
            //so, using system.exit we kill it
            System.exit(1);
        }
    }

    public static void unzip(Path zipPath) throws IOException {
        Path zipParent = zipPath.getParent();

        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            String rootFolder = null;

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                int firstSlash = entryName.indexOf('/');

                if (firstSlash > 0) {
                    String folderName = entryName.substring(0, firstSlash);
                    if (rootFolder == null) {
                        rootFolder = folderName;
                    } else if (!rootFolder.equals(folderName)) {
                        throw new IOException("Zip file contains multiple entries");
                    }
                }
            }
            entries = zipFile.entries();
            Path outputPath = zipParent.resolve(rootFolder);

            for (ZipEntry entry : zipFile.stream().collect(Collectors.toList())) {
                Path entryDest = zipParent.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(entryDest);
                } else {
                    Files.createDirectories(entryDest.getParent());
                    Files.copy(zipFile.getInputStream(entry), entryDest, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            System.out.println("Fonts unzipped successfully to " + outputPath);
        }
    }

    private DejavuFontsLoader() {
        //empty
    }
}
