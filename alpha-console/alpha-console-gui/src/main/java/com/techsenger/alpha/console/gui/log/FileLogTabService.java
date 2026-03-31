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
//import com.techsenger.alpha.console.gui.textstyle.StyledText;
//import com.techsenger.alpha.console.gui.utils.FileLogFragment;
//import com.techsenger.alpha.console.gui.utils.FileLogParser;
//import com.techsenger.tabshell.kit.core.file.FileInfo;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import javafx.concurrent.Task;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
///**
// *
// * @author Pavel Castornii
// */
//class FileLogTabService extends AbstractLogTabService<FileLogFragment> {
//
//    private static final Logger logger = LoggerFactory.getLogger(FileLogTabService.class);
//
//    private final Path logPath;
//
//    private final boolean ansiCoded;
//
//    private final FileLogParser parser;
//
//    FileLogTabService(FileInfo fileInfo, boolean ansiCoded) {
//        this.logPath = Paths.get(fileInfo.getPath());
//        this.ansiCoded = ansiCoded;
//        if (this.ansiCoded) {
//            parser = new FileLogParser();
//        } else {
//            parser = null;
//        }
//    }
//
//    @Override
//    protected Task<List<FileLogFragment>> createTask() {
//        return new Task() {
//
//            @Override
//            protected List<StyledText> call() throws Exception {
//                this.updateTitle("File Log Reader");
//                var waitingMessage = "Waiting for new log events";
//                this.updateMessage(waitingMessage);
//                try (var fileReader = new FileReader(logPath.toFile());
//                        var bufferedReader = new BufferedReader(fileReader)) {
//                    String logLine;
//                    var lineSeparator = "";
//                    var systemLineSeparator = "\n"; //richtextFX works only with \n
//                    var currentLogFragments = new ArrayList<FileLogFragment>();
//                    FileLogFragment fileLogFragment = null;
//                    var count = 0;
//                    while (true) {
//                        if (isCancelled()) {
//                            return null;
//                        }
//                        //we need the second loop to process all existing events
//                        while ((logLine = bufferedReader.readLine()) != null) {
//                            this.updateMessage("Processing log events");
//                            logLine = lineSeparator + logLine;
//                            if (ansiCoded) {
//                                var fragments = parser.parse(logLine);
//                                if (!fragments.isEmpty()) {
//                                    lineSeparator = systemLineSeparator;
//                                    currentLogFragments.addAll(fragments);
//                                    count++;
//                                }
//                            } else {
//                                fileLogFragment = new FileLogFragment(null, logLine);
//                                lineSeparator = systemLineSeparator;
//                                currentLogFragments.add(fileLogFragment);
//                                count++;
//                            }
//                            //count be 99 and 101 because texts are added in cycle above
//                            if (count >= BATCH_SIZE) {
//                                count = 0;
//                                this.updateValue(currentLogFragments);
//                                currentLogFragments = new ArrayList<>();
//                            }
//                        }
//                        //rest events that are outside batch_size
//                        if (!currentLogFragments.isEmpty()) {
//                            count = 0;
//                            this.updateValue(currentLogFragments);
//                            currentLogFragments = new ArrayList<>();
//                        }
//                        Thread.sleep(1000);
//                        this.updateMessage(waitingMessage);
//                        //wait until there is more of the file for us to read
//                        Thread.sleep(1000);
//                    }
//                } catch (InterruptedException ex) {
//                    logger.debug("Sleeping thread was interrupted", ex);
//                } catch (Exception ex) {
//                    logger.error("Error reading log file: {}", logPath, ex);
//                }
//                return null;
//            }
//        };
//    }
//}
