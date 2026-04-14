package com.techsenger.weaverbird.gui.file;

///*
// * Copyright 2018-2026 Pavel Castornii.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.techsenger.weaverbird.console.gui.file;
//
//import com.techsenger.weaverbird.console.gui.keys.ConsoleComponentKeys;
//import com.techsenger.weaverbird.console.gui.style.ConsoleIcons;
//import com.techsenger.mvvm4fx.core.HistoryPolicy;
//import com.techsenger.tabshell.core.TabShellViewModel;
//import com.techsenger.tabshell.core.dialog.DialogScope;
//import com.techsenger.tabshell.core.menu.SimpleMenuItemHelper;
//import com.techsenger.tabshell.core.tab.ShellTabKey;
//import com.techsenger.tabshell.kit.core.file.FileInfo;
//import com.techsenger.tabshell.kit.core.file.FileType;
//import com.techsenger.tabshell.kit.core.file.LocalTextFileTaskProvider;
//import com.techsenger.tabshell.kit.core.menu.FileMenuKeys;
//import com.techsenger.tabshell.kit.dialog.alert.AlertDialogType;
//import com.techsenger.tabshell.kit.dialog.alert.AlertDialogViewModel;
//import com.techsenger.tabshell.kit.text.editor.AbstractEditorTabViewModel;
//import com.techsenger.tabshell.material.icon.FontIcon;
//import com.techsenger.toolkit.core.file.FileUtils;
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import javafx.stage.FileChooser;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.stream.XMLStreamException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.xml.sax.SAXException;
//
///**
// *
// * @author Pavel Castornii
// */
//public class FileTabViewModel extends AbstractEditorTabViewModel {
//
//    public static List<FileChooser.ExtensionFilter> createOpenExtensionFilters() {
//        return List.of(
//                SupportedFileType.XML.getExtensionFilter(),
//                SupportedFileType.SCRIPT.getExtensionFilter(),
//                SupportedFileType.TEXT.getExtensionFilter(),
//                new FileChooser.ExtensionFilter("Any file (*.*)", "*.*"));
//    }
//
//    private static final Logger logger = LoggerFactory.getLogger(FileTabViewModel.class);
//
//    private final FileType fileType;
//
//    public FileTabViewModel(TabShellViewModel tabShell, FileType fileType, File file) {
//        super(tabShell, new FileInfo(file.getAbsolutePath(), file.getName(),
//                FileUtils.getExtension(file.getName()), file.length(), false, StandardCharsets.UTF_8),
//                new LocalTextFileTaskProvider());
//        this.fileType = fileType;
//        setHistoryPolicy(HistoryPolicy.APPEARANCE);
//        setHistoryProvider(() -> tabShell.getHistoryManager().getHistory(FileTabHistory.class, FileTabHistory::new));
//        if (this.fileType == SupportedFileType.XML) {
//            this.setIcon(new FontIcon(ConsoleIcons.XML_FILE));
//        } else if (this.fileType == SupportedFileType.SCRIPT) {
//            this.setIcon(new FontIcon(ConsoleIcons.SCRIPT_FILE));
//        } else {
//            this.setIcon(new FontIcon(ConsoleIcons.TEXT_FILE));
//        }
//        this.contentModifiedProperty().addListener((ov, oldValue, newValue) -> {
//            this.titleProperty().set(this.resolveTabTitle(this.titleProperty().get()));
//        });
//
//        addMenuItemHelpers(
//            new SimpleMenuItemHelper(FileMenuKeys.SAVE) {
//                @Override
//                public Boolean getItemValid() {
//                    return getFileInfo().getPath() != null;
//                }
//            }
//        );
//    }
//
//    @Override
//    public ShellTabKey getKey() {
//        return ConsoleComponentKeys.FILE_TAB;
//    }
//
//    @Override
//    public List<FileChooser.ExtensionFilter> getExtensionFilters() {
//        return List.of(this.fileType.getExtensionFilter());
//    }
//
//    @Override
//    public String resolveDefaultExtension(FileChooser.ExtensionFilter filter) {
//        return this.fileType.getExtension();
//    }
//
//    @Override
//    public String getDefaultExtension() {
//        return this.fileType.getExtension();
//    }
//
//    public FileType getFileType() {
//        return fileType;
//    }
//
//    @Override
//    public FileHelper<?> getComponentHelper() {
//        return (FileHelper) super.getComponentHelper();
//    }
//
//    protected void openConvertedXmlTab(String selectedText) {
//        try {
//            var content = convertXml(selectedText);
//            var convertedViewModel = new ConvertedXmlTabViewModel(content);
//            getComponentHelper().openConvertedXmlTab(convertedViewModel);
//            setBottomPaneVisible(true);
//        } catch (Exception ex) {
//            if (!(ex instanceof XmlConvertingException)) {
//                logger.error("Error converting xml code", ex);
//            } else {
//                var dialog = new AlertDialogViewModel(DialogScope.TAB, AlertDialogType.ERROR, ex.getMessage());
//                getComponentHelper().openAlertDialog(dialog);
//            }
//        }
//    }
//
//    private String convertXml(String input) throws XmlConvertingException, SAXException, IOException,
//            ParserConfigurationException, XMLStreamException {
//        if (input == null || input.trim().length() == 0) {
//            throw new XmlConvertingException("No input data. Select xml code with dependencies");
//        }
//        boolean inputMavenDependencies = false;
//        String result = null;
//        if (input.contains("<dependency>")) {
//            inputMavenDependencies = true;
//            result = new MavenDependencyConverter().convert(input);
//        } else if (input.contains("<Module")) {
//            inputMavenDependencies = false;
//            result = new AlphaDependencyConverter().convert(input);
//        } else {
//            throw new XmlConvertingException("Can't resolve type of input dependencies - Alpha or Maven");
//        }
//        return result;
//    }
//}
