package com.techsenger.alpha.gui.log;

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
//package com.techsenger.alpha.console.gui.log;
//
//import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
//import com.techsenger.alpha.console.gui.style.ConsoleIcons;
//import com.techsenger.alpha.console.gui.textstyle.StyledText;
//import com.techsenger.alpha.console.gui.utils.FileLogFragment;
//import com.techsenger.mvvm4fx.core.HistoryPolicy;
//import com.techsenger.tabshell.core.TabShellViewModel;
//import com.techsenger.tabshell.core.tab.ShellTabKey;
//import com.techsenger.tabshell.material.icon.FontIcon;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.List;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * @author Pavel Castornii
// */
//public class FileLogTabViewModel extends AbstractLogTabViewModel {
//
//    private static final Logger logger = LoggerFactory.getLogger(FileLogTabViewModel.class);
//
//    private boolean ansiCoded;
//
//    public FileLogTabViewModel(TabShellViewModel tabShell, Path logPath, boolean ansiCoded) {
//        super(tabShell);
//        setIcon(new FontIcon(ConsoleIcons.FILE_LOG));
//        setTitle("File log");
//        setHistoryPolicy(HistoryPolicy.APPEARANCE);
//        setHistoryProvider(() -> tabShell.getHistoryManager()
//                .getHistory(FileLogTabHistory.class, FileLogTabHistory::new));
//        getFileInfo().setPath(logPath.toAbsolutePath().toString());
//        levelFilterButtonSelectedProperty().set(false);
//        this.ansiCoded = ansiCoded;
//    }
//
//    @Override
//    public ShellTabKey getKey() {
//        return ConsoleComponentKeys.FILE_LOG_TAB;
//    }
//
//    public void startService() {
//        var service = new FileLogTabService(this.getFileInfo(), ansiCoded);
//        service.valueProperty().addListener((ov, oldV, newV) -> {
//            if (newV != null) {
//                processLogFragments(newV);
//            }
//        });
//        this.submitWorker(service);
//    }
//
//    @Override
//    protected void refreshEvents() {
//
//    }
//
//    private void processLogFragments(List<FileLogFragment> fragments) {
//        List<StyledText> currentTexts = new ArrayList<>();
//        for (var fragment: fragments) {
//            if (fragment.getLevel() != null) {
//                var descriptor = this.resolveDescriptor(fragment.getLevel());
//                descriptor.increaseCount();
//                if (descriptor.buttonSelectedProperty().get()) {
//                    var cssStyle = getCssClassesByLevel().get(fragment.getLevel());
//                    var styledText = new StyledText(cssStyle, fragment.getText());
//                    currentTexts.add(styledText);
//                }
//            } else {
//                var styledText = new StyledText(null, fragment.getText());
//                currentTexts.add(styledText);
//            }
//        }
//        if (!currentTexts.isEmpty()) {
//            getTexts().next(currentTexts);
//        }
//    }
//};
