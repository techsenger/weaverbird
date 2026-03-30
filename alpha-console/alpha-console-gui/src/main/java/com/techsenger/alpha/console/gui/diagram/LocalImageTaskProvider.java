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

package com.techsenger.alpha.console.gui.diagram;

import com.techsenger.tabshell.kit.core.file.FileInfo;
import com.techsenger.tabshell.kit.core.file.FileTaskProvider;
import com.techsenger.tabshell.kit.core.workertab.TabWorker;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Pavel Castornii
 */
class LocalImageTaskProvider implements FileTaskProvider<Image> {

    @Override
    public TabWorker<Image> createFileReader(FileInfo fileInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TabWorker<Void> createFileWriter(FileInfo fileInfo, Image content) {
        class WriterTask extends Task<Void> implements TabWorker<Void> {

            @Override
            protected Void call() throws Exception {
                BufferedImage bImage = SwingFXUtils.fromFXImage(content, null);
                var path = Paths.get(fileInfo.getPath());
                ImageIO.write(bImage, "png", path.toFile());
                return null;
            }

            @Override
            public boolean usesProgress() {
                return false;
            }
        }

        return new WriterTask();
    }
}
