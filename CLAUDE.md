# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Techsenger Weaverbird is a framework built on JPMS (Java Platform Module System) that manages modular
components through dynamic module layers. The framework lives in the boot layer and manages other layers;
each **component** is deployed into its own `ModuleLayer` with a defined lifecycle (`added` → `resolved` →
`deployed` → `activated`), configured via a `Builder` API or XML (with EL expression support). Components
are started/stopped via **activators** (`ModuleActivator` services registered in `module-info`). See
`README.md` for the full conceptual model (directory layout, registry, text commands, remote control,
assembly plugin) — it's long and thorough, read it before making framework-level changes.

Requires **Java 25+** and **JavaFX 25+** (for the GUI console).

## Language

Everything in the project is written in English — README, documentation, Javadoc, code comments, commit
messages, etc. Always — regardless of what language the conversation with the assistant happens in.

## Scope of work

It is strictly forbidden to operate outside the project directory. This applies absolutely, to every kind
of operation — including read-only ones like `find` or search/grep — not just edits.

The only exception: the developer may explicitly name a directory outside the project directory for a
specific task. In that case, work must stay confined to the project directory plus exactly the
directory/directories the developer named — nothing else. Proactively suggesting or using any other
outside directory on your own is strictly forbidden.

If some source you need isn't in an allowed directory, ask the developer where to find it — never search
the rest of the machine for it on your own.

## Module layout (reactor)

Each is a separate Maven module (some are multi-module themselves); dependency direction flows top-to-bottom:

- **weaverbird-core** — the framework itself: component/module lifecycle, registry, config model (`ComponentConfig`),
  activators, EL context (`sys`/`env`/`config`/`info`/`utils`), path management (`PathManager`/`PathResolver`).
  This is the only module every other module depends on (directly or transitively).
- **weaverbird-repo** (`weaverbird-repo-core`, `weaverbird-repo-external`) — the module repository SPI/impl used
  to resolve and store JPMS modules the framework loads into layers.
- **weaverbird-net** (`weaverbird-net-shared`, `weaverbird-net-client`, `weaverbird-net-server`) — remote control
  over HTTP: all text commands go through this even for in-process consoles (`Console ⮂ CommandExecutor ⮂ Command
  ⮂ Client ⮂ Server ⮂ EndpointHandler ⮂ Framework API`). The channel is unencrypted — remote/cross-host use needs
  a VPN or SSH tunnel; don't add plaintext-sensitive data to it.
- **weaverbird-executor** — `Command`/`CommandExecutor`/`CommandService` SPI and the ~40 built-in text commands,
  plus `EndpointHandler`/`EndpointHandlerService` SPI for the server side.
- **weaverbird-cli** — terminal console (JLine-based) on top of `weaverbird-executor` + `weaverbird-net-client`.
- **weaverbird-gui** — JavaFX console (Console / Memory Log / Diagrams tabs); see MVVM pattern below.
- **weaverbird-assembly** (`weaverbird-assembly-maven-plugin`, `weaverbird-assembly-security`) — the Maven plugin
  that assembles runtimes/distributions (`assemble-runtime`, `assemble-dist`, `update` goals); this is what both
  IT modules and real distributions use to materialize a framework directory tree with `bin`/`config`/`repo`.
- **weaverbird-it** (`weaverbird-it-shared`, `weaverbird-it-activator`, `weaverbird-it-core`, `weaverbird-it-net`) —
  integration tests exercising a real assembled runtime.
- **weaverbird-demo** (`weaverbird-demo-cli`, `weaverbird-demo-gui`, `weaverbird-demo-starter`,
  `weaverbird-demo-jfx-*`) — runnable examples; several are excluded from Maven Central publishing
  (see `publishing.plugin.exclusions` in root `pom.xml`). Each `Demo.java`'s Javadoc explains how to run it
  (some via `exec-maven-plugin`, some only via generated `.sh`/`.bat` scripts).

Every module is a real JPMS module (`module-info.java` under `src/main/java`, and often a second one under
`src/test/java` for modular tests) — when adding a new package that must be visible outside its module, remember
to add `exports`/`opens`/`provides`/`uses` in `module-info.java`, not just make the class `public`.

## Commands

```
mvn clean install                 # build everything (multi-module reactor)
mvn -pl weaverbird-core install   # build a single module (and its dependents if -am is added)
mvn test -pl weaverbird-core      # unit tests only, one module
mvn verify -pl weaverbird-it/weaverbird-it-core   # runs integration tests (Failsafe) for that IT module
mvn test -Dtest=SomeTestClass#someMethod -pl weaverbird-core   # single test method
```

- Unit tests use JUnit 5 + AssertJ and run via Surefire during `test`/`install`.
- Integration tests live under `weaverbird-it/*` and run via Failsafe during `verify` (not `install`),
  driven by the `weaverbird-assembly-maven-plugin`'s `assemble-runtime` goal bound to `initialize`, which
  builds a real framework runtime under `target/framework` before the ITs execute against it. If you change
  core/component behavior, prefer running `verify` on the relevant `weaverbird-it-*` module, not just `test`.
- `weaverbird-it-activator` and `weaverbird-it-shared` must compile before `weaverbird-it-core`/`weaverbird-it-net`
  (declared as test-scope dependencies, not just module order).
- No local checkstyle/lint config in this repo — style rules come from the `com.techsenger.maven.root:maven-root`
  parent POM. License headers (Apache 2.0, `Copyright 2018-2026 Pavel Castornii.`) are present at the top of
  every Java file — copy the existing header verbatim into new files.
- There is no local CI test workflow; `.github/workflows/snapshot-deploy.yml` only deploys SNAPSHOT versions
  to a Repsy repository on push to `main` — it does not gate on tests.
- Version is a shared reactor `1.0.0-SNAPSHOT` set once in the root `pom.xml`.

## Architecture: components in GUI

The GUI is mid-refactor onto an MVVM pattern built on two external Techsenger libraries:
**patternfx** (`com.techsenger.patternfx.core`/`.mvvm`) for MVVM plumbing, and **shellfx**
(`com.techsenger.shellfx.core`/`.material`/`.icons`/`.shared`/`.layout`) for the shell/tab/dialog host
components. Naming convention used throughout `weaverbird-gui`, per screen/dialog/tab:

- `XxxView` — the JavaFX view (layout + bindings).
- `XxxViewModel` — observable state and behavior for the view.
- `XxxParams` — construction-time parameters passed in when a tab/dialog/page is created.
- `XxxPort` — a narrow interface a child view/composer uses to talk back up to its host (e.g.
  `ConsoleToolBarPort`, `CompletionPopupPort`) without depending on the whole parent.
- `XxxComposer` — wires a View + ViewModel (+ children) together; typically implements a shellfx interface
  like `HostTabComposer` (see `ConsoleTabComposer`, `DiagramTabComposer`, `LayerDialogComposer`).

When touching GUI code, match this pattern for the area you're editing (console, diagram, log, session,
settings, about) rather than introducing a different structure — this refactor is actively converging the
whole module onto it, so partial/mixed patterns in older files (e.g. plain `AbstractXxxView`/`AbstractXxxViewModel`
pairs without a Composer/Port) are legacy, not the target shape.

`weaverbird-assembly-maven-plugin/.../gui-config.xml` is the component XML config for assembling/running the
GUI console component itself (module list, directives) — check it when adding a new module dependency to
`weaverbird-gui` that needs framework-level directives (opens/reads/exports) at runtime.

## Architecture: ViewModel state in GUI

The **ViewModel is the source of truth** for a component's state, exposed as JavaFX properties that the View
binds to directly. Every ViewModel property falls into one of three shapes:

1. **Read and written from outside, fully owned by the ViewModel** (nothing else can change it
independently): a plain `Property`, exposed as three public methods — `xProperty()`, `getX()`, `setX()`:

```java
// ViewModel
private final ObjectProperty<Foo> foo = new SimpleObjectProperty<>();

public ObjectProperty<Foo> fooProperty() {
    return this.foo;
}

public Foo getFoo() {
    return this.foo.get();
}

public void setFoo(Foo foo) {
    this.foo.set(foo);
}
```

2. **Read only from outside, fully owned by the ViewModel:** a `ReadOnlyObjectWrapper` backing field,
exposed as two public methods — `xProperty()`, `getX()`:

```java
// ViewModel
private final ReadOnlyObjectWrapper<Foo> foo = new ReadOnlyObjectWrapper<>();

public ReadOnlyObjectProperty<Foo> fooProperty() {
    return this.foo.getReadOnlyProperty();
}

public Foo getFoo() {
    return this.foo.get();
}

ReadOnlyObjectWrapper<Foo> fooWrapper() {
    return this.foo;
}

```

3. **Read only from outside, with a request setter — for state a View or the platform owns, not the
ViewModel (the `wrapper`/`source` naming pattern, its setter marked `@RequestSetter`).** Some ViewModel
state isn't decided by the ViewModel itself — it's decided by the real widget or the OS (a window's
width/height/x/y, maximized/minimized, a `TableView`'s selection) — and the *requested* value and the
*actual* value can legitimately diverge (the platform clamps, ignores, or rejects the request). This is why
real JavaFX's own `Window#widthProperty()` is `ReadOnlyDoubleProperty` with a separate best-effort
`setWidth(double)`, not a plain `DoubleProperty`. For this kind of state:

- Expose it as a `ReadOnly*Property` on the Port/ViewModel — never a plain writable `Property`. A writable
  property lets a caller write an optimistic value straight in; if the platform then silently rejects the
  request (no compensating change event fires, because nothing about the real state actually changed), that
  optimistic value sits there being wrong forever — a "lying property," which is worse than the
  two-way-binding reentrancy problem it might look like it's avoiding.
- Back it with a `ReadOnly*Wrapper` field, and add a package-private `xWrapper()` accessor — documented as a
  framework contract ("written directly by the View... direct invocation by user code results in undefined
  behavior") — that the paired View `.bind()`s straight to the real widget/`Stage` property. When there is no
  independent widget truth to bind to at all (confirmed only asynchronously, e.g. by a manager after it
  finishes an operation), the View instead writes the wrapper directly, once, at the point the real outcome
  is known.
- Give the public setter (`setWidth`, `setSelectedItem`, ...) a matching package-private `xSource()`
  accessor returning an `ObservableSource<T>`. The setter only calls `xSource.next(value)` — it never touches
  the wrapper itself. The View subscribes to `xSource()` and performs the real widget/platform call; the
  outcome flows back solely through the wrapper. This keeps exactly one writer per property in each
  direction, so there's no reentrancy/cycle to guard against and no way to bypass whatever side effect the
  request needs to trigger. Annotate the setter itself with `@RequestSetter`, marking it as best-effort rather
  than a guaranteed state change.
- Don't reach for `wrapper`/`source` for state the ViewModel fully owns itself, even if its setter carries a
  side effect or validation (a derived flag to update, a check that throws) — as long as the setter is the
  *only* way in, a plain `Property` is correct and a `ReadOnly` wrapper only makes the API harder to use for
  no safety benefit. Reserve `wrapper`/`source` for state a second, independent party (the View, the
  platform) can also change out from under the ViewModel.
- **Naming applies uniformly, with or without a paired wrapper.** A command source with no matching
  `ReadOnly*Wrapper` at all (e.g. a ViewModel unconditionally telling the View "re-render this now," with no
  independent "actual" value to diverge from) is still named `<action>Source`, with a same-named accessor
  `<action>Source()` — never `request<Verb>`/`getRequest<Verb>()`.

```java
// ViewModel
private final ReadOnlyObjectWrapper<Item> selectedItem = new ReadOnlyObjectWrapper<>();

private final ObservableSource<Item> selectedItemSource = new SimpleObservableSource<>();

public ReadOnlyObjectProperty<Item> selectedItemProperty() {
    return this.selectedItem.getReadOnlyProperty();
}

public Item getSelectedItem() {
    return this.selectedItem.get();
}

@RequestSetter
public void setSelectedItem(Item item) {
    this.selectedItemSource.next(item);
}

ReadOnlyObjectWrapper<Item> selectedItemWrapper() {
    return this.selectedItem;
}

ObservableSource<Item> selectedItemSource() {
    return this.selectedItemSource;
}
```

```java
// View
viewModel.selectedItemWrapper().bind(this.table.getSelectionModel().selectedItemProperty());
viewModel.selectedItemSource().addListener((item) -> {
    if (item == null) {
        this.table.getSelectionModel().clearSelection();
    } else {
        this.table.getSelectionModel().select(item);
    }
});
```

4. **Collections fully owned by the ViewModel:** an `ObservableList`/`ObservableSet`/`ObservableMap` follows
the same read-only-from-outside idea without needing a wrapper at all: keep a private modifiable collection,
expose an unmodifiable view over it to the View (`FXCollections.unmodifiableObservableList(...)`) — callers
observe structural changes directly — and, if a subclass needs to mutate the collection itself, expose the
modifiable collection to subclasses through a `protected` accessor rather than widening the public one.

## Module directives gotcha

JPMS directives can come from two places: `module-info.java` (compile-time) and the component XML config
(runtime, via `<Directive type="opens/reads/exports".../>` for the module itself, or
`type="requestsOpen/requestsRead/requestsExport"` to apply a directive to *another* module, usually one in
a parent layer). The boot layer is special: it has no accessible `LayerController`, so `requests*` directives
targeting boot-layer modules must go through the "relay" trick described in `README.md` (boot module opens a
package to the framework core module via `--add-opens` at JVM startup; the core module relays with
`Module.addOpens()` since it created every layer). Don't assume a missing `opens`/`reads` can always be fixed
purely in `module-info.java` — for boot-layer modules it may need this relay approach or a config `Directive`.

## Nullability

Types use `com.techsenger.annotations.Nullable`/`@Unmodifiable`; NullAway is enforced at compile time
(`OnlyNullMarked` mode) — only annotate/guard packages that are explicitly null-marked, don't add blanket
null checks elsewhere.

## Member ordering

Not caught by Checkstyle (no `DeclarationOrder` module in the config) — must be applied by hand on every
edit, not just when writing new files.

Within a class/interface, members are sorted by three nested keys, each answering a different question and
breaking ties in the one before it:

1. **Scope — static vs. instance.** Does this member belong to the class or to the object? All static
   members form one block, placed entirely before all instance members. This is absolute — e.g. a
   `private static` method is placed above a `public` constructor, because scope outranks visibility.
2. **Role — types → fields → (constructors →) methods.** This is a dependency order, not an arbitrary
   bucket: nested types define the vocabulary that fields are declared with, fields hold the state that
   methods/constructors operate on. So within the static block: nested static types → static fields →
   static methods. Within the instance block: nested instance types → instance fields → constructors →
   instance methods.
3. **Visibility — `public` → `protected` → package-private → `private`.** Within one role (e.g. "instance
   methods"), the public contract comes before implementation detail.

So the full sequence in one class is: static nested types (public→private) → static fields
(public→private) → static methods (public→private) → nested instance types (public→private) → instance
fields (public→private) → constructors (public→private) → instance methods (public→private).

**Field grouping, within one role+visibility bucket.** The three criteria above leave ties: in a `Port` or
`ViewModel` interface/class, `getX()`/`isX()`, `setX()`, and `xProperty()` for the same field are normally
all `public` instance methods, so nothing above orders them relative to each other or to the next field's
trio. **When they share the same access modifier, group them by field** instead of batching all getters,
then all setters, then all property accessors as three separate blocks — `getX()` → `setX()` (if present) →
`xProperty()` (if present), then move on to the next field's trio:

```java
double getWidth();

ReadOnlyDoubleProperty widthProperty();

double getHeight();

ReadOnlyDoubleProperty heightProperty();
```

not

```java
double getWidth();

double getHeight();

ReadOnlyDoubleProperty widthProperty();

ReadOnlyDoubleProperty heightProperty();
```

Field order otherwise follows whichever order is already established (typically the backing fields'
declaration order in the concrete `XxxViewModel`).

If the field's methods don't share one access modifier — e.g. a `wrapper()`/`source()` pair is
package-private while `getX()`/`xProperty()` are public (see the `wrapper`/`source` pattern above), or a
setter is `protected` while its getter is `public` — visibility still wins: keep each method in its own
visibility block, don't pull a lower-visibility method up next to a public one just to keep the trio
together. The package-private `xWrapper()`/`xSource()` pair still groups with each other, just in the
package-private block further down the class, not next to the public `getX()`/`xProperty()`.

## Naming convention

Component classes follow: `[UniqueName][Role][Element]`, e.g. `AlertDialogView`, `EditorTabViewModel`,
`InfoPopupParams`, `ToolBarPort`. Role examples: `Tab`, `Window`, `Popup`, `Area`, `Panel`, `ToolBar`. Element
examples: `View`, `ViewModel`, `Params`, `Port`, `History`.

`Composer` methods split into two categories — keep this distinction when adding new component types:
- Lifecycle-managing: `open*`/`close*` (create+add / remove+destroy) and `show*`/`hide*`.
- Structural-only (no lifecycle): `add*`/`remove*`/`replace*`.

| Component | Create + Add | Remove + Destroy | Add Only | Remove Only |
|---|---|---|---|---|
| Window | `openWindow(params)` | `closeWindow(window)` | `addWindow(window)` | `removeWindow(window)` |
| Tab | `openTab(params)` | `closeTab(tab)` | `addTab(tab)` | `removeTab(tab)` |
| Dialog | `openDialog(params)` | `closeDialog(dialog)` | `addDialog(dialog)` | `removeDialog(dialog)` |
| Popup | `openPopup(params)` | `closePopup(popup)` | `addPopup(popup)` | `removePopup(popup)` |
| Page | `openPage(params)` | `closePage(page)` | `addPage(page)` | `removePage(page)` |
| Area | `openArea(params)` | `closeArea(area)` | `addArea(area)` | `removeArea(area)` |

Prefer `create*`-delegating implementations of `open*`/`show*` where the pattern already uses them — it keeps
the created component swappable.

A `Map`-typed field/parameter/local/getter/setter is named `<values>By<key>` (e.g. `Map<FileType, Boolean>
selectionsByType`, `getSelectionsByType()`/`setSelectionsByType(...)`), not `<key><values>` (e.g.
`typeSelections`). The `by`-form reads directly as "which value, keyed by which type of key" at the
declaration site, without having to look at the generic type arguments to tell which side is the key.

State mirroring from ViewModel to View goes through the View binding to the ViewModel's properties directly,
in `bind()` (a plain `.bind()`) or `addListeners()` (`ValueUtils.callAndAddListener`/manual listeners, and any
`wrapper`/`source` property — see Architecture above). The private method that actually pushes a value onto
the real widget/platform uses the `update<X>` verb, e.g. `updateWidth`, `updateMaximized`, `updateTheme` — it
is private and triggered by the View's own listener/binding, never called publicly from outside the View:

- Plain property: `ValueUtils.callAndAddListener(viewModel.xProperty(), (ov, oldV, newV) -> updateX(newV));`
- `wrapper`/`source` property: `updateX(viewModel.getX()); viewModel.xSource().addListener((value) ->
  updateX(value));` — the explicit initial `updateX(...)` call is required because an `ObservableSource` has no
  "current value" to replay to a new subscriber the way a `Property` does.

Command methods — View methods that perform an action rather than mirror ViewModel state — still use an
appropriate action verb, such as `showX`, `hideX`, `scrollToFile`, `selectFile`, `clearX`, or `refreshMenu`.

## Javadoc

Document the contract — what the member does and why a caller would use it — never how it's implemented.
If a sentence just narrates the method body in prose (the steps it performs, the fields it touches along
the way), that's implementation detail the reader can already see by opening the method; cut it. A reader
deciding whether/how to call the member should get what they need without opening its body: what it
returns or does, plus any non-obvious constraint, side effect, or precondition — not a walkthrough.

Target 120-240 characters for the main description (the summary sentence plus any `<p>` continuation)
only — this range does not apply to `@param`/`@return`/`@throws`/`@see`/`{@link}` tags at all. Under 120
characters a javadoc rarely earns its place over just reading the signature; over 240 it has usually
drifted into narrating implementation or into a multi-paragraph essay — shorten it, or split the method
instead of padding its doc further. Avoid `{@link}`/cross-references to other classes or methods where
possible, especially to types from a different module/dependency (patternfx, shellfx, toolkit) — those
references rot silently when the referenced API changes and are harder to notice/fix than one in the same
file.

Accessor methods (get, set, is, xxxProperty) — should not have Javadoc unless they provide information
that is not obvious from the method name and type.

Tags follow a different, simpler rule: keep each tag's description as short as it can be while still
saying what it needs to; the only hard limit on it is the 120-char line length itself (wrap per the
indentation rule below if one line isn't enough). There is no minimum length for a tag.

**Tag coverage.** On `public`/`protected` methods, document every element that appears in the signature —
each `@param`, every checked exception via `@throws`, and so on — as tersely as the 120-char line allows;
a missing tag reads as an oversight on API surface other code depends on. Add `@return` only when the
return value carries semantics beyond what the method name and return type already say — nullability, a
sentinel/special value, which object state it reflects, resource ownership, mutability, a specific format,
or a constraint on the value. Skip `@return` when it would just restate the method name and type (e.g.
`getName()` returning `String` needs no `@return the name`).

On package-private/private methods, add `@param`/`@throws`/etc. only when that specific tag is actually
worth calling out (a non-obvious constraint, a surprising exception) — omitting the routine ones is fine.

Each `@param`, `@return`, `@throws`, and similar tag starts on a new line. The first line holds the tag,
the parameter name (if any), and the start of the description. When the description doesn't fit the
120-char line limit, continue it on the following line(s) using a **fixed 4-space indent relative to the
leading `*`**, not aligned under the start of the description — a fixed indent stays correct regardless of
how long the tag/parameter name is, so it never needs re-indenting when a name changes length:

```java
/**
 * Resolves a file.
 *
 * @param path controls whether the operation should be performed in a
 *     lightweight mode. If {@code false}, additional validation is performed.
 * @param destination specifies the destination used to resolve relative
 *     paths and determine where the resulting files should be stored.
 * @return the resolved file.
 * @throws IOException if the file cannot be resolved.
 */
```

Do not write javadoc on an overriding (`@Override`) method — rule of thumb: the comment belongs only on
the interface method or the parent class method being overridden, not duplicated on every override.

## Code style (Checkstyle)

Checkstyle runs on every `mvn package`/`install` (see Commands above) via the `com.techsenger.checkstyle.config`
artifact (Sun checks-derived, `severity=error`) — a violation fails the build, not just a lint warning. Treat
every rule below as binding when writing or editing Java. `module-info.java` files are exempt from all of it.
Run just this check with `mvn checkstyle:check`, or skip it entirely with `-Dcheckstyle.plugin.skip=true`
or `-P unit-tests`/`-P integration-tests`.

- **Layout**: 120-char line limit, no tabs, no trailing whitespace, file must end with a newline, exactly
  one blank line between the license header and the `package` declaration, no more than 5000 lines/file.
- **Methods**: max 250 non-empty lines, max 8 parameters — both signal you should split the method/introduce
  a parameter object rather than push past them.
- **Imports**: no star imports, no unused imports, no redundant imports.
- **Naming**: standard Java conventions — `PascalCase` types, `camelCase` methods/fields/params/locals,
  `UPPER_SNAKE_CASE` for non-private constants (private constants are exempt, so `private static final` in
  `camelCase`/mixed case is fine).
- **Braces & blocks**: braces required on every `if`/`for`/`while`/etc. (no single-statement bodies without
  `{}`), no empty blocks, no nested blocks, standard left/right-curly placement.
- **Whitespace**: standard spacing around operators/generics/casts/parens (`GenericWhitespace`, `ParenPad`,
  `TypecastParenPad`, `WhitespaceAround`, `WhitespaceAfter`, `NoWhitespaceBefore`/`After`, `OperatorWrap`).
- **Coding**: no empty statements, `equals`/`hashCode` always overridden together, no assignments inside
  expressions (`InnerAssignment`), one variable declaration per statement (no `int a, b;`), simplify boolean
  expressions/returns (`return x == y;` not `if (x == y) return true; else return false;`).
- **Class design**: a class with only private constructors must be `final`; utility classes (only static
  members) must have a private constructor (matches the existing `private Foo() { // empty }` pattern
  already used throughout, e.g. `NavigatorFileIconProvider`); fields should be `private` with accessors
  (`VisibilityModifier`), not exposed directly.
- **Misc**: array brackets on the type, not the variable (`String[] args`, not `String args[]`); long
  literals use uppercase `L` (`100L`, not `100l`).
- **No `TODO` comments** (`TodoComment` module) — since severity is `error`, a matching comment fails the
  build. Don't add new ones; open a tracked issue or just do the work instead.