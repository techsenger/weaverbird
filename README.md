# Techsenger Alpha

Techsenger Alpha is a framework built on top of the Java Platform Module System (JPMS) that manages modular components
through dynamic module layers. It provides a powerful API and versatile interfaces (CLI/GUI) with multiple built-in
commands — helping developers efficiently build and orchestrate modular systems.

## Table of Contents
* [Overview](#overview)
* [Use Cases](#use-cases)
* [Demo](#demo)
    * [CLI Console Demo](#demo-cli)
    * [GUI Console Demo](#demo-gui)
* [Requirements](#requirements)
* [Dependencies](#dependencies)
* [Usage](#usage)
    * [Framework](#usage-framework)
        * [Directory Layout](#usage-framework-directory)
        * [Registry](#usage-framework-registry)
        * [Installation Mechanism](#usage-framework-installation)
        * [Boot](#usage-framework-boot)
    * [Component](#usage-component)
        * [Life Cycle](#usage-component-life)
        * [Activator](#usage-component-activator)
        * [Configuration](#usage-component-config)
        * [Events](#usage-component-events)
        * [Services](#usage-component-services)
    * [Text Commands](#usage-commands)
    * [CLI Console](#usage-cli)
    * [GUI Console](#usage-gui)
        * [Shell](#usage-gui-shell)
        * [Memory Log](#usage-gui-memory-log)
        * [File Log](#usage-gui-file-log)
        * [Diagrams](#diagrams)
* [Code building](#code-building)
* [Running Demo](#running-demo)
* [License](#license)
* [Contributing](#contributing)
* [Support Us](#support-us)

## Overview <a name="overview"></a>

JPMS (Java Platform Module System), which was introduced in Java 9, along with modules, added the concept of module
layer. A layer can be defined as a group of modules that are loaded and managed together. Key features of a layer
include:

* Isolation - modules within a layer can be independent from other layers.
* Hierarchy - layers form a graph and can use modules from parent layers.
* Dynamic configuration - layers can be dynamically added and removed (except the boot layer).

Techsenger Alpha is a framework designed to work with module layers. The framework resides in the boot layer and handles
all the work of managing the other layers. To facilitate this, the concept of a component is introduced.

A component is a logical part of the system that can be dynamically added or removed. Each component is deployed in
a separate module layer and has a clearly defined lifecycle. The configuration of a component is specified via an XML
file (with plans to add a ConfigBuilder), which describes the component's modules (groupId, artifactId, version),
module directives (opens, reads, exports, etc.), repositories from which modules can be loaded, and other information.
For flexibility, the XML configuration supports properties, the `if` and `choose-when` constructs and EL
(Expression Language).

All loaded modules are stored in the framework's internal repository, which is by default a Maven repository.

The code within a component is started and stopped using activators — special services invoked by the framework during
the activation or deactivation of the component.

The framework operates in three modes: `standalone`, `client`, and `server`. The last two modes allow you to connect to
already running Alpha frameworks.

You can interact with the framework either through the API or through the text command mechanism. Text commands are a
convenient way to work with the framework. Out of the box, about 40 commands are provided for working with components,
sessions, gathering information, etc. At the same time, it is very easy to add your own commands. The framework will
automatically discover them in any layer (the module must provide a factory service) and integrate them into any
console. The framework can also execute command scripts, which consist of a series of commands, for example, from a file.

The framework provides two types of consoles: CLI and GUI. The CLI console allows working with text commands. The GUI
console additionally allows working with logs and generates diagrams with information about layers, modules, and
packages. These diagrams are especially useful when working with complex systems.

During the development of the framework, much attention was paid to flexibility and universality. For this reason,
all key elements of the framework — repository, command executor, client, server, console, etc. are designed as
services, allowing easy integration of custom implementations when needed. For example, even when a command script is
being executed, a service for tracking its progress can be added. This feature also allows for the creation of a splash
screen with a loading indicator.

Thus, four main responsibilities of the framework can be highlighted:

1. Component management
2. Text command mechanism
3. Log message viewing
4. Information provision

## Use Cases <a name="use-cases"></a>

The framework can be used for programs that:

* Have subsystems that can be dynamically added or removed.
* Support plugins, extensions, add-ons, etc., that can be dynamically loaded.
* Include a web server and web applications, where each web application is a module.
* Use modules that are loaded based on conditions, such as the operating system type, etc.

## Demo <a name="demo"></a>

### CLI Console Demo <a name="demo-cli"></a>
![Alpha CLI Console](./cli-demo.gif)

### GUI Console Demo <a name="demo-gui"></a>
![Alpha GUI Console](./gui-demo.gif)

## Requirements <a name="requirements"></a>

The entire framework is written in Java 11. However, Java 17+ is required for compiling and running the GUI console and
the demo application. Therefore, compilation is done with Java 17+, but the framework itself (excluding the GUI and demo)
can run on Java 11.0.23+.

## Dependencies <a name="dependencies"></a>

This project will be available on Maven Central in a few weeks

```
<dependency>
    <groupId>com.techsenger.alpha</groupId>
    <artifactId>alpha-api</artifactId>
    <version>${alpha.version}</version>
</dependency>
<dependency>
    <groupId>com.techsenger.alpha</groupId>
    <artifactId>alpha-spi</artifactId>
    <version>${alpha.version}</version>
</dependency>
```

## Usage <a name="usage"></a>

### Framework <a name="usage-framework"></a>

The framework is always located in the root layer. In production, this is the boot layer, but in tests, it can also be
a dynamically created layer. To work with the framework, you need to use the `Framework` class, which provides all the
necessary tools.

It is important to note that when launching the framework, the parameter `--add-modules ALL-DEFAULT` is used, which
loads all default JRE/JDK modules into the boot layer. This is necessary because JRE/JDK modules can only be added to
the boot layer.

#### Directory Layout <a name="usage-framework-directory"></a>

The framework provides a specific folder structure, see `PathManager`. All folders are divided into two groups:

1. Standard Folders — the contents of these folders are either unstructured or structured according to external
standards, such as the Maven repository standards.
2. Special Folders — the contents of these folders are structured according to the framework's rules.

```
bin     //standard
cache
config
data
doc
legal
log     //standard
repo    //standard
script  //standard
temp
```

Special folders have the following structure: at the root of the folder, framework files are located, while component
files are organized into two nested folders. The name of the first folder is the component name, and the name of the
second folder is the component version, see `PathResolver`.

For example:

```
config
│── Component A
│   │── 1.0.0
│   │   │── configuration.xml   //component file
│   │   │── settings.xml        //component file
│── log4j2.xml                  //framework file
```

#### Registry <a name="usage-framework-registry"></a>

The framework stores data about the installation and components in a registry, which is located in the file
`data/alpha-registry.xml`. If the client and server are located in the same folder, as in the `alpha-demo-net` example,
both the client and server instances of the framework use this file.

#### Installation Mechanism <a name="usage-framework-installation"></a>

The framework includes a simple optional installation mechanism. To use it, the `install` argument must be passed to the
`Launcher` in the `.sh`/`.bat` files.

There are two possible scenarios. If the framework finds the `InstallService` provider, this service is executed.
If the provider is not found, a script of commands is run, and then a record of the installation is made in the registry.

It is important to note that the absence of an installation record indicates that the installation process has not
been carried out. If a record of a failed installation is present, the user is prompted to start the installation from
scratch.

#### Boot <a name="usage-framework-boot"></a>

The framework is booted as follows. If the framework detects a `BootService` provider, this service is executed.
Otherwise, a command script is executed.

### Component <a name="usage-component"></a>

Each component has a name and a version. The name and version are separated by `:`. For example, the foo component
with version 1.0.0 is specified as `foo:1.0.0`, and the command to launch the component will be:

```
component:start foo:1.0.0
```

An unlimited number of instances of the same component can be created, as instances are identified in the system by
their `id`. Additionally, a component instance can be assigned an `alias` and accessed using this `alias`. This is
especially useful when referencing a component instance in command scripts.

#### Life Cycle <a name="usage-component-life"></a>

Each component can have the following states: `added`, `resolved`, `deployed`, `activated`.

**Added**. The component has been added, but its modules have not yet been loaded from external repositories. The
component can be added either manually or automatically.

To manually add a component, the following steps need to be performed:

1. Create the component's XML configuration.
2. Place the configuration in the special `config` folder.
3. Add the component to the AddedComponents registry of the framework.

The automatic addition of a component occurs when there is a component ZIP archive (with any extension).

The component archive is used for distributing the component. For example, if a program using the framework works with
plugins, each plugin is distributed via its archive. The component archive must always contain the component's
configuration. Additionally, it may contain JAR/WAR modules (which are not in the repository), component data, etc.

The component archive can be created using the `component:build` command, and the component can be added using the
`component:add` command.

**Resolved**. The component's modules have been loaded into the framework's repository, and the component can be deployed.
The resolution of the component is performed using the `component:resolve` command.

**Deployed**. A `ModuleLayer` has been created for the component, which contains all of the component's modules. However,
the component's code has not yet been executed. The deployment of the component is performed using the
`component:deploy` command.

**Activated**. All activators of the component's active modules have been called. The component's code is now being
executed. The activation of the component is performed using the `component:activate` command.

The commands mentioned above allow working with each component state individually; however, in many cases, this is
unnecessary. Therefore, there are combined commands that handle multiple states at once. For example, instead of using
the `component:add` and `component:resolve` commands separately, you can use a single command, `component:install`.

Table of commands and states:

<table>
    <tr>
        <td>Initial State</td>
        <td>Final State</td>
        <td>Command</td>
        <td>Combined Command</td>
    </tr>
    <tr>
        <td>—</td>
        <td>Added</td>
        <td>component:add</td>
        <td rowspan="2">component:install</td>
    </tr>
    <tr>
        <td>Added</td>
        <td>Resolved</td>
        <td>component:resolve</td>
    </tr>
    <tr>
        <td>Resolved</td>
        <td>Deployed</td>
        <td>component:deploy</td>
        <td rowspan="2">component:start</td>
    </tr>
    <tr>
        <td>Deployed</td>
        <td>Activated</td>
        <td>component:activate</td>
    </tr>
    <tr>
        <td>Activated</td>
        <td>Deployed</td>
        <td>component:deactivate</td>
        <td rowspan="2">component:stop</td>
    </tr>
    <tr>
        <td>Deployed</td>
        <td>Resolved</td>
        <td>component:undeploy</td>
    </tr>
    <tr>
        <td>Resolved</td>
        <td>Added</td>
        <td>component:unresolve</td>
        <td rowspan="2">component:uninstall</td>
    </tr>
    <tr>
        <td>Added</td>
        <td>—</td>
        <td>component:remove</td>
    </tr>
</table>

#### Activator <a name="usage-component-activator"></a>

Activators are module services that are invoked when a component is activated or deactivated. To create an activator,
follow these steps:

1. Implement the `ModuleActivator` interface in the module.
2. In the `module-info`, add `provides ... with ...`.

Important! The created activator will not be called if the configuration of the component does not specify that the
module is active:

```
<Module groupId="..." artifactId="..." version="..." active="true"/>
```
Thus, you can control through the component's configuration whether the module activators should be triggered or not.

When the component is activated, its activators are called in the order in which they are specified in the component's
configuration. During deactivation, they are called in reverse order (the last active module is deactivated first).

#### Configuration <a name="usage-component-config"></a>

At the moment, the configuration is set using an XML file. In the future, `ConfigBuilder` is planned to be added.

A configuration template with all supported tags:

```
<?xml version="1.0" encoding="UTF-8" ?>
<Configuration title="The Best Foo" name="foo" version="1.0.0" type="notBar">
    <Metadata>
        <Entry key="License" value="Apache 2"/>
    </Metadata>

    <Repositories>
        <Repository name="central" url="https://repo1.maven.org/maven2/"/>
    </Repositories>

    <Choose>
        <When test="${info['os.family'] == 'linux'}">
            <Property name="modVersion" value="2.0.0"/>
        </When>
        <When test="${info['os.family'] == 'mac'}">
            <Property name="modVersion" value="3.0.0"/>
        </When>
    </Choose>

    <Modules>
        <If test="${...}">
            <Module groupId="..." artifactId="..." version="..."/>
        </If>

        <Module groupId="..." artifactId="..." version="${config['modVersion']}" active="true">
            <Directives>
                <Directive type="opens/reads/exports" package="..." layer="..." module="..."/>
                <Directive type="allowsOpen/allowsRead/allowsExport" package="..." layer="..." module="..."/>
            </Directives>
        </Module>
    </Modules>
</Configuration>
```

For detailed information on the component configuration, refer to the `ComponentConfig` Javadoc. Here, we will
only cover the key points that should be noted.

**Expression Language**. All attributes can have EL expressions. By default, five maps are defined in the EL context:

```
sys — System.getProperties()
env — System.getenv()
config — This configuration properties
info — ComponentManager.getConfigInfo()
utils — ComponentManager.getConfigUtils
```

The last two maps are accessible through the `ComponentManager` and can be modified if necessary.

**Module Directive**. The module directives can be divided into two groups. The first group includes directives
specified in the `module-info`. The second group includes directives specified in the configuration.

Using directives in the configuration is necessary because, during module development, it is impossible to foresee
all possible use cases for the module. For example, if a module uses the API of a specification, it generally does
not know which implementation of that specification will be used. Therefore, when a particular implementation is used,
it may be necessary to add extra directives.

The layer attribute allows specifying the module's layer. If this attribute is not specified, the layer of this
component is used. To specify the framework and component layer, either the name `foo` or the name and version
`foo:1.0.0` is used. The name of the layer where the framework is located is `alpha-framework`.

The difference between the directives `opens`, `reads`, `exports` and the directives `allowsOpen`, `allowsRead`,
`allowsExport` is that the former are applied directly to the module specified in the `Module` tag, while the latter
are applied to the module specified in the `module` attribute. In other words, the first set of directives is applied
directly to the configured module, while the second set is applied to another module, usually from the parent layer.
For example, such a configuration:

```
<Directive type="allowsRead" package="..." layer="..." module="foo.module"/>
```
will result in the `reads` directive being added to the `foo.module` module.

It is important to note that directives are added through the layer controller. Since JPMS does not provide access
to the boot layer controller, you cannot add directives to the modules of this layer using `allows` directives in
the component configuration.

For a specific example of working with directives, see the configuration of the demo component — `alpha-console-gui`.

#### Events <a name="usage-component-events"></a>

The framework allows receiving events related to components. For this, `ModuleActivator` receives a `ModuleContext`,
from which a reference to the `Component` can be obtained. The `Component` allows adding a `ComponentObserver`. To
implement an observer, it is generally easier to use `AbstractComponentObserver`, which provides empty implementations
for all methods.

#### Services <a name="usage-component-services"></a>

Finding services within a single layer or in its parent layers is not an issue. However, when searching for services
that may not be in parent layers but instead in child layers or even outside the hierarchy, the task becomes more
complex. The reason for this is that such layers can be dynamically added and removed.

There are two solutions to this problem:

1. Use `ServiceTracker`.
2. Use `ComponentObserver`.

### Text Commands <a name="usage-commands"></a>

Text commands are a powerful tool for working with the framework; however, their use is optional. It is important
to note that all commands, both built-in and custom, are automatically added to the CLI and GUI consoles.

**Command Executor**. The execution of commands is handled by `CommandExecutor`, which receives a `String` containing
the commands as input. Each command is a separate class implementing the `Command` interface. The executor splits the
text into individual commands and processes each one. If a command is local, the executor handles it internally.
If a command is remote, the executor forwards it to the executor of the remote framework.

The command processing follows these steps: First, the class implementing the command is identified. Then, the
`CommandFactory` provider of the module containing the command is invoked. The `CommandFactory` creates an instance
of the command and returns it to the executor. Next, the command's parameters are parsed and their values are set
in the command instance. Finally, the command is executed.

It is important to note that `CommandExecutor` will find the `CommandFactory` in any layer.

**Custom Command**. To create a custom command, you need to do the following:

1. Implement the `Command` interface (it is recommended to inherit from `AbstractCommand`).
2. Create a `CommandFactory` in the module containing the command.
3. Add the command to the `CommandFactory`.

For examples, refer to the `alpha-commands` module.

**Command Scripts**. A command `String` can contain an unlimited number of commands separated by `;`. This allows for
the use of command scripts — files that contain text commands. Typically, scripts are stored in the `script` folder.

**Special Symbols**. When working with commands, the following special characters should be considered:

* `;` — used to separate commands.
* `#` — used for comments in scripts. It can only be the first character of a line.
* `!` — used as a prefix for commands when working in sessions, to execute the command locally.

### CLI Console <a name="usage-cli"></a>

The CLI console is a simple command-line interface. Unlike the GUI console, its capabilities are limited to
executing commands. The strength of the CLI console lies in its versatility—it can operate in environments without a
graphical interface.

The console supports a completer, which is invoked using the `Tab` key.

Help is available for all commands in the console. Therefore, we will highlight only two points:

1. The framework is shut down via the `framework:shutdown` command.
2. When working in a session, use the `!` prefix to exit a session, switch between sessions, etc.

For example, the following command will exit the session but will not close it.

```
demouser@localhost> !session:detach
> |
```

### GUI Console <a name="usage-gui"></a>

The GUI console has significantly more capabilities compared to the CLI console. It not only allows executing commands
but also provides features for viewing log messages and generating graphs for working with layers, modules, and packages.

#### Shell <a name="usage-gui-shell"></a>

The Shell is a component that allows executing text commands. It is similar to the GUI console with two exceptions:

1. To invoke the completer, you need to use the `Ctrl` + `Space` keyboard shortcut.
2. It provides a graphical interface for working with sessions.

#### Memory Log <a name="usage-gui-memory-log"></a>

The memory log stores all log events that occur in the system. Since it receives all messages directly from `log4j2`
rather than reading them from a file, it offers greater flexibility for viewing and filtering those messages.

Currently, the memory log retains all messages until it is cleared, so it cannot be used in production. It is
intended solely for testing and debugging purposes.

By default, the memory log is not active. To enable it, you must set the parameter
`com.techsenger.alpha.core.log.memory` to `true` in the `.sh` / `.bat` scripts.

#### File Log <a name="usage-gui-file-log"></a>

The file log is used to view messages from a file. The log message level is determined by the SGR function, which is
set in the `log4j2` configuration.

#### Diagrams <a name="diagrams"></a>

Diagrams are a useful tool that provides complete information about layers, modules, and packages. The importance of
this tool is especially heightened when working with complex systems.

Diagrams have their own settings, which can be viewed in the `Settings` dialog.

## Code Building <a name="code-building"></a>

To build the framework use standard Git and Maven commands:

    git clone https://github.com/techsenger/alpha
    cd alpha
    mvn clean install

## Running Demo <a name="running-demo"></a>

The project includes 4 binary demo archives (`.tar`) with CLI and GUI consoles in `standalone` and `client`, `server`
modes.

Each demo, in addition to framework components, includes two web components: a web server (Jetty 12 + Spring 6) and a
web application. When the demo is running, you can open a browser and check the page at `http://127.0.0.1:8080/`. At
the same time, it is important to note that the framework knows nothing about the web server or the web
application — for it, they are just components.

Demo archives are created during the project build process and placed in the following directories:

```
alpha-demo/alpha-demo-cli/target
alpha-demo/alpha-demo-gui/target
alpha-demo/alpha-demo-net/alpha-demo-net-cli/target
alpha-demo/alpha-demo-net/alpha-demo-net-gui/target
```
The `alpha-demo-net-cli` and `alpha-demo-net-gui` projects also demonstrate installation using `install.sh/.bat` files.
When this file is executed, all changes will occur within the framework directory. For details, refer to
`script/installation.script`.

To connect the server use the following command (name: admin, password: admin):

```
session:open demo -a 127.0.0.1:7900 -s -n admin
```

## License <a name="license"></a>

Techsenger Alpha is licensed under the Apache License, Version 2.0.

## Contributing <a name="contributing"></a>

We welcome all contributions. You can help by reporting bugs, suggesting improvements, or submitting pull requests
with fixes and new features. If you have any questions, feel free to reach out — we’ll be happy to assist you.

## Support Us <a name="support-us"></a>

You can support our open-source work through [GitHub Sponsors](https://github.com/sponsors/techsenger).
Your contribution helps us maintain projects, develop new features, and provide ongoing improvements.
Multiple sponsorship tiers are available, each offering different levels of recognition and benefits.

