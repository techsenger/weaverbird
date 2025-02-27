/*
 * Copyright 2018-2025 Pavel Castornii.
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

package com.techsenger.alpha.core.launcher;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.SystemProperties;
import com.techsenger.alpha.api.component.Component;
import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.alpha.api.message.LoggerMessagePrinter;
import com.techsenger.alpha.api.registry.InstallResultEntry;
import com.techsenger.alpha.core.registry.DefaultRegistry;
import com.techsenger.alpha.spi.launcher.BootService;
import com.techsenger.alpha.spi.launcher.InstallService;
import com.techsenger.alpha.spi.launcher.LauncherProgressHandler;
import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.toolkit.core.file.FileUtils;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Pavel Castornii
 */
public final class Launcher {

    /*
     * This code must be run before any logger instantiation.
     */
    static {
        LogNameResolver.resolve();
    }

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    /**
     * Install task name.
     */
    private static final String INSTALL_TASK = "install";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        Framework.launch();
        if (args.length > 0 && args[0].equalsIgnoreCase(INSTALL_TASK)) {
            var installService = ServiceUtils.loadProvider(Launcher.class.getModule().getLayer(), false,
                    InstallService.class);
            if (installService.isPresent()) {
                installService.get().run();
            } else {
                executeInstallScript();
                Framework.shutdown();
            }
        } else {
            var bootService = ServiceUtils.loadProvider(Launcher.class.getModule().getLayer(), false,
                    BootService.class);
            if (bootService.isPresent()) {
                bootService.get().run();
            } else {
                executeBootScript();
            }
        }
    }

    /**
     * Starts boot executor component (that contains alpha-executor). This component must be already installed.
     * All other components must be started via script. So, this method always starts only one component.
     */
    private static void startExecutorComponent() {
        try {
            String executorComponent = System.getProperty(SystemProperties.EXECUTOR);
            if (executorComponent == null) {
                logger.error("Executor component is not defined via property {}", SystemProperties.EXECUTOR);
                return;
            }
            logger.info("Starting executor component");
            var nameAndVersion = Component.resolveNameAndVersion(executorComponent);
            String alias = System.getProperty(SystemProperties.EXECUTOR_ALIAS);
            Framework.getComponentManager()
                    .startComponent(nameAndVersion.getFirst(), nameAndVersion.getSecond(), alias);
            logger.info("Executor component started");
        } catch (Exception ex) {
            logger.error("Error starting executor component", ex);
        }
    }

    /**
     * Executes install script.
     */
    private static void executeInstallScript() {
        startExecutorComponent();
        CommandExecutor executor = Framework.getServiceManager().getCommandExecutor();
        var previousInstallResult = Framework.getRegistry().getInstallResult();
        if (previousInstallResult != null) {
            if (previousInstallResult.wasSuccessful()) {
                System.out.println("The program already installed. Exiting.");
                return;
            } else {
                System.out.println("Previous installation failed. Try to install from scratch.");
                return;
            }
        }
        String installScript = System.getProperty(SystemProperties.SCRIPT);
        if (installScript == null) {
            logger.warn("Installation script wasn't set via system properties");
            //installation script is optional
            return;
        }
        DefaultRegistry registry = (DefaultRegistry) Framework.getRegistry();
        try {

            Path scriptPath = Framework.getPathManager().getScriptDirectoryPath().resolve(installScript);
            logger.info("Executing installation script at path {}", scriptPath);
            if (!Files.exists(scriptPath)) {
                throw new IllegalArgumentException(StringUtils.format("Install script {} not found in {}",
                        installScript, scriptPath));
            }
            var commands = FileUtils.readFile(scriptPath, StandardCharsets.UTF_8);
            var loggerPrinter = new LoggerMessagePrinter(logger);
            var commandContext = executor.createContext(null);
            var results = executor.executeCommands(commands, commandContext, "installation", createProgressHandler(),
                    loggerPrinter.getWidth());
            results.forEach(r -> loggerPrinter.print(r.getMessages()));
            registry.setInstallResult(new InstallResultEntry(true));
            registry.save();
            logger.info("Executed installation script");

        } catch (Exception ex) {
            registry.setInstallResult(new InstallResultEntry(false));
            registry.save();
            logger.error("Error executing installation script", ex);
        }
    }

    /**
    * Executes boot script. We DON'T execute boot script in executor activator as if some component needs executor,
    * for example console, it won't be found as executor component won't be available until its activator finishes.
    */
    private static void executeBootScript() {
        startExecutorComponent();
        CommandExecutor executor = Framework.getServiceManager().getCommandExecutor();
        var installResult = Framework.getRegistry().getInstallResult();
        if (installResult == null) {
            System.out.println("The program is not installed. Exiting.");
            return;
        } else {
            if (!installResult.wasSuccessful()) {
                System.out.println("The program wasn't installed properly. Exiting.");
                return;
            }
        }
        String bootScript = System.getProperty(SystemProperties.SCRIPT);
        if (bootScript == null) {
            logger.warn("Boot script wasn't set via system properties");
            //boot script is optional
            return;
        }
        try {
            Path scriptPath = Framework.getPathManager().getScriptDirectoryPath().resolve(bootScript);
            logger.info("Executing boot script at path {}", scriptPath);
            if (!Files.exists(scriptPath)) {
                throw new IllegalArgumentException(StringUtils.format("Boot script {} not found in {}",
                        bootScript, scriptPath));
            }
            var commands = FileUtils.readFile(scriptPath, StandardCharsets.UTF_8);
            var loggerPrinter = new LoggerMessagePrinter(logger);
            var commandContext = executor.createContext(null);
            var results = executor.executeCommands(commands, commandContext, "boot", createProgressHandler(),
                    loggerPrinter.getWidth());
            results.forEach(r -> loggerPrinter.print(r.getMessages()));
            logger.info("Executed boot script");
        } catch (Exception ex) {
            logger.error("Error executing boot script", ex);
        }
    }

    private static LauncherProgressHandler createProgressHandler() {
        var handlerProvider = ServiceUtils.loadProvider(Launcher.class.getModule().getLayer(), false,
                LauncherProgressHandler.class);
        if (handlerProvider.isPresent()) {
            return handlerProvider.get();
        } else {
            return new DefaultLauncherProgressHandler();
        }
    }

    private Launcher() {
        //empty
    }
}

